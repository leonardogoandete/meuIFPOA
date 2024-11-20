package br.com.ifrs.meuifpoa.ui.fragment;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.ifrs.meuifpoa.R;
import br.com.ifrs.meuifpoa.adapter.recycler.LinhaNotasAdapter;
import br.com.ifrs.meuifpoa.databinding.FragmentNotasBinding;
import br.com.ifrs.meuifpoa.model.Nota;
import br.com.ifrs.meuifpoa.model.Perfil;
import br.com.ifrs.meuifpoa.utils.GerenciadorSinc;

/**
 * Fragmento que exibe as notas do usuário.
 */
public class NotasFragment extends Fragment {

    private static final String TAG = "NotasFragment";
    private FragmentNotasBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private boolean dadosJaExibidos = false;

    /**
     * Método chamado para inflar o layout do fragmento.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Método chamado após a criação da view do fragmento.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser usuario = mAuth.getCurrentUser();
        if (usuario == null) {
            exibirMensagemErro(getString(R.string.msg_deve_estar_logado));
            return;
        }

        obterNotasDoCache();

        if (isNetworkAvailable()) {
            usuario.getIdToken(true).addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    String token = task.getResult().getToken();
                    if (token != null) {
                        GerenciadorSinc gerenciadorSinc = new GerenciadorSinc();
                        gerenciadorSinc.verificarERequisitarSenha(getContext(), this::sincronizarComServidor);
                        Log.d(TAG, "Token de autenticação obtido com sucesso.");
                    } else {
                        Log.e(TAG, "Token de autenticação nulo.");
                    }
                } else {
                    Log.e(TAG, "Erro ao obter token de autenticação.", task.getException());
                }
            });
        }

        binding.listViewNotas.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.listViewNotas.setLayoutManager(layoutManager);
    }

    /**
     * Método chamado quando o fragmento se torna visível ao usuário.
     */
    private void obterNotasDoCache() {
        if (binding == null) return;
        mostrarCarregamento(true);

        db.collection("usuarios")
                .document(mAuth.getUid())
                .get(Source.CACHE)
                .addOnCompleteListener(task -> {
                    mostrarCarregamento(false);
                    if (task.isSuccessful()) {
                        DocumentSnapshot documento = task.getResult();
                        if (documento != null && documento.exists()) {
                            Perfil perfil = documento.toObject(Perfil.class);
                            if (perfil != null && perfil.getNotas() != null) {
                                organizarNotasPorSemestre(perfil.getNotas());
                                dadosJaExibidos = true;
                            } else {
                                exibirMensagemErro(getString(R.string.msg_nao_ha_notas));
                            }
                        } else {
                            exibirMensagemErro(getString(R.string.msg_perfil_nao_encontrado));
                        }
                    } else {
                        Log.e(TAG, "Erro ao obter notas do cache", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    mostrarCarregamento(false);
                    Log.e(TAG, "Erro ao obter notas do cache", e);
                });
    }

    /**
     * Método chamado para sincronizar as notas com o servidor.
     */
    private void sincronizarComServidor() {
        if (dadosJaExibidos || binding == null) return;
        mostrarCarregamento(true);

        db.collection("usuarios")
                .document(mAuth.getUid())
                .get(Source.SERVER)
                .addOnCompleteListener(task -> {
                    mostrarCarregamento(false);
                    if (task.isSuccessful()) {
                        DocumentSnapshot documento = task.getResult();
                        if (documento != null && documento.exists()) {
                            Perfil perfil = documento.toObject(Perfil.class);
                            if (perfil != null && perfil.getNotas() != null) {
                                organizarNotasPorSemestre(perfil.getNotas());
                            } else {
                                exibirMensagemErro(getString(R.string.msg_nao_ha_notas));
                            }
                        } else {
                            exibirMensagemErro(getString(R.string.msg_perfil_nao_encontrado));
                        }
                    } else {
                        Log.e(TAG, "Erro ao sincronizar com o servidor", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    mostrarCarregamento(false);
                    Log.e(TAG, "Erro ao sincronizar com o servidor", e);
                });
    }

    /**
     * Método chamado para organizar as notas por semestre e exibi-las.
     */
    private void organizarNotasPorSemestre(List<Nota> notas) {
        Map<String, List<Nota>> notasPorSemestre = new HashMap<>();

        // Organiza as notas por semestre
        for (Nota nota : notas) {
            String semestre = nota.getSemestre(); // Supondo que cada Nota tem um campo "semestre"
            if (!notasPorSemestre.containsKey(semestre)) {
                notasPorSemestre.put(semestre, new ArrayList<>());
            }
            notasPorSemestre.get(semestre).add(nota);
        }

        // Ordena os semestres em ordem crescente (do mais antigo para o mais novo)
        List<String> semestresOrdenados = new ArrayList<>(notasPorSemestre.keySet());
        semestresOrdenados.sort((s1, s2) -> s2.compareTo(s1));

        // Cria a lista de itens para exibição com apenas os semestres inicialmente
        List<Object> itensParaExibir = new ArrayList<>();
        for (String semestre : semestresOrdenados) {
            itensParaExibir.add(semestre); // Adiciona apenas o título do semestre
        }

        exibirNotas(itensParaExibir, notasPorSemestre);
    }

    /**
     * Método chamado para exibir as notas na tela.
     */
    private void exibirNotas(List<Object> itens, Map<String, List<Nota>> notasPorSemestre) {
        LinhaNotasAdapter notasAdapter = new LinhaNotasAdapter(itens, notasPorSemestre);
        binding.listViewNotas.setAdapter(notasAdapter);
    }

    /**
     * Método chamado para exibir ou ocultar o carregamento.
     */
    private void mostrarCarregamento(boolean visivel) {
        if (binding != null) {
            if (visivel) {
                binding.containerProgressBarNotas.setVisibility(View.VISIBLE);
                binding.listViewNotas.setVisibility(View.GONE);
            } else {
                binding.containerProgressBarNotas.setVisibility(View.GONE);
                binding.listViewNotas.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Método chamado para exibir uma mensagem de erro.
     */
    private void exibirMensagemErro(String mensagem) {
        Snackbar.make(binding.getRoot(), mensagem, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Método chamado para verificar se há conexão com a internet.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(getContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Método chamado quando o fragmento é destruído.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
