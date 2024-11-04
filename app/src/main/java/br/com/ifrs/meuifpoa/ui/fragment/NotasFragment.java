package br.com.ifrs.meuifpoa.ui.fragment;

import android.app.AlertDialog;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.List;

import br.com.ifrs.meuifpoa.R;
import br.com.ifrs.meuifpoa.adapter.recycler.LinhaNotasAdapter;
import br.com.ifrs.meuifpoa.databinding.FragmentNotasBinding;
import br.com.ifrs.meuifpoa.model.Nota;
import br.com.ifrs.meuifpoa.model.Perfil;
import br.com.ifrs.meuifpoa.utils.GerenciadorSinc;

/**
 * O tipo NotasFragment representa um fragmento que exibe as notas do usuário.
 */
public class NotasFragment extends Fragment {

    private static final String TAG = "NotasFragment";
    private FragmentNotasBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    /**
     * Método chamado para criar a visualização do fragmento.
     *
     * @param inflater  O LayoutInflater usado para inflar qualquer visualização no fragmento.
     * @param container O ViewGroup pai ao qual a visualização do fragmento será anexada.
     * @param savedInstanceState Se não for nulo, este fragmento está sendo reconstruído a partir de um estado salvo anterior.
     * @return A visualização raiz do fragmento.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inicializar o View Binding
        binding = FragmentNotasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Método chamado imediatamente após onCreateView(LayoutInflater, ViewGroup, Bundle) ter retornado.
     *
     * @param view A visualização retornada por onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState Se não for nulo, este fragmento está sendo reconstruído a partir de um estado salvo anterior.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        FirebaseUser usuario = mAuth.getCurrentUser();
        if (usuario == null) {
            //exibirErro("Usuário não autenticado.");
            return;
        }

        usuario.getIdToken(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();
                if (token != null) {
                    // Sincronização opcional com o servidor após verificar a senha
                    GerenciadorSinc GerenciadorSinc = new GerenciadorSinc();
                    GerenciadorSinc.verificarERequisitarSenha(getContext(), this::obterNotasDoFirestore);
                    Log.d(TAG, "Token de autenticação obtido com sucesso.");
                } else {
                    Log.e(TAG, "Token de autenticação nulo.");
                }
            } else {
                Log.e(TAG, "Erro ao obter token de autenticação.", task.getException());
            }
        });

        // Configurar o RecyclerView
        binding.listViewNotas.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.listViewNotas.setLayoutManager(layoutManager);

        // Obter notas do Firestore
        obterNotasDoFirestore();
    }

    /**
     * Obtém as notas do Firestore e as exibe no RecyclerView.
     */
    private void obterNotasDoFirestore() {
        if (binding == null) return;
        // Exibe o ProgressBar antes de iniciar o carregamento
        mostrarCarregamento(true);

        if (mAuth.getUid() == null) {
            exibirMensagemErro(getString(R.string.msg_deve_estar_logado));
            mostrarCarregamento(false);
            return;
        }

        db.collection("usuarios")
                .document(mAuth.getUid())
                .get(Source.DEFAULT)
                .addOnCompleteListener(task -> {
                    // Oculta o ProgressBar ao finalizar o carregamento
                    mostrarCarregamento(false);

                    if (task.isSuccessful()) {
                        Perfil perfil = task.getResult().toObject(Perfil.class);
                        if (perfil != null) {
                            List<Nota> notasServidor = perfil.getNotas();
                            if (notasServidor != null && !notasServidor.isEmpty()) {
                                LinhaNotasAdapter notasAdapter = new LinhaNotasAdapter(notasServidor);
                                binding.listViewNotas.setAdapter(notasAdapter);
                            }else {
                                exibirMensagemErro(getString(R.string.msg_nao_ha_notas));
                            }
                        } else {
                            exibirMensagemErro(getString(R.string.msg_perfil_nao_encontrado));
                        }
                    } else {
                        exibirMensagemErro(getString(R.string.erro_sync_conexao));
                    }
                })
                .addOnFailureListener(e -> {
                    // Oculta o ProgressBar ao ocorrer uma falha
                    mostrarCarregamento(false);
                    exibirMensagemErro(getString(R.string.erro_sync_conexao));
                });
    }


    /**
     * Mostra ou oculta o ProgressBar.
     *
     * @param visivel true para mostrar o ProgressBar, false para ocultar.
     */
    private void mostrarCarregamento(boolean visivel) {
        if (binding != null) {  // Verifique se o binding ainda está disponível
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
     *
     * @param mensagem A mensagem de erro a ser exibida.
     */
    private void exibirMensagemErro(String mensagem) {
        Snackbar.make(binding.getRoot(), mensagem, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Método chamado quando a visualização do fragmento é destruída.
     * Limpa o binding para evitar vazamento de memória.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
