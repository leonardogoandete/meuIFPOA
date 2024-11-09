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

import java.util.List;

import br.com.ifrs.meuifpoa.R;
import br.com.ifrs.meuifpoa.adapter.recycler.LinhaNotasAdapter;
import br.com.ifrs.meuifpoa.databinding.FragmentNotasBinding;
import br.com.ifrs.meuifpoa.model.Nota;
import br.com.ifrs.meuifpoa.model.Perfil;

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
            // Caso o usuário não esteja autenticado, exibe uma mensagem
            exibirMensagemErro(getString(R.string.msg_deve_estar_logado));
            return;
        }

        // Primeira tentativa: busca os dados do cache
        obterNotasDoCache();

        // Sincroniza com o servidor se estiver online
        if (isNetworkAvailable()) {
            usuario.getIdToken(true).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    sincronizarComServidor();
                } else {
                    Log.e(TAG, "Erro ao obter token de autenticação.", task.getException());
                }
            });
        }

        // Configurar o RecyclerView
        binding.listViewNotas.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.listViewNotas.setLayoutManager(layoutManager);
    }

    /**
     * Busca as notas do cache local.
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
                                exibirNotas(perfil.getNotas());
                                dadosJaExibidos = true; // Sinaliza que os dados já foram exibidos
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
     * Sincroniza os dados com o servidor.
     */
    private void sincronizarComServidor() {
        if (dadosJaExibidos || binding == null) return; // Verifica se os dados já foram exibidos para evitar atualização redundante
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
                                exibirNotas(perfil.getNotas());
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
     * Exibe as notas no RecyclerView.
     *
     * @param notas A lista de notas a ser exibida.
     */
    private void exibirNotas(List<Nota> notas) {
        LinhaNotasAdapter notasAdapter = new LinhaNotasAdapter(notas);
        binding.listViewNotas.setAdapter(notasAdapter);
    }

    /**
     * Mostra ou oculta o ProgressBar.
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
     * Exibe uma mensagem de erro usando um Snackbar.
     */
    private void exibirMensagemErro(String mensagem) {
        Snackbar.make(binding.getRoot(), mensagem, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Verifica se há conexão com a internet.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(getContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
