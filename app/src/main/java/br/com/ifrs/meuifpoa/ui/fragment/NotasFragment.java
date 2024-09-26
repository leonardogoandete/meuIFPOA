package br.com.ifrs.meuifpoa.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.List;

import br.com.ifrs.meuifpoa.adapter.recycler.LinhaNotasAdapter;
import br.com.ifrs.meuifpoa.databinding.FragmentNotasBinding;
import br.com.ifrs.meuifpoa.model.Nota;
import br.com.ifrs.meuifpoa.utils.GerenciadorSinc;

public class NotasFragment extends Fragment {

    private FragmentNotasBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inicializar o View Binding
        binding = FragmentNotasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Usando GerenciadorSinc para verificar e requisitar a senha
        GerenciadorSinc GerenciadorSinc = new GerenciadorSinc();
        GerenciadorSinc.verificarERequisitarSenha(getContext(), this::obterNotasDoFirestore);

        // Configurar o RecyclerView
        binding.listViewNotas.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.listViewNotas.setLayoutManager(layoutManager);

        // Obter notas do Firestore
        obterNotasDoFirestore();
    }

    private void obterNotasDoFirestore() {
        if (binding == null) return;
        // Exibe o ProgressBar antes de iniciar o carregamento
        mostrarCarregamento(true);

        if (mAuth.getUid() == null) {
            exibirMensagemErro("Você deve estar logado.");
            mostrarCarregamento(false);
            return;
        }

        db.collection("notas")
                .document(mAuth.getUid())
                .collection("disciplinas")
                .get(Source.DEFAULT)
                .addOnCompleteListener(task -> {
                    // Oculta o ProgressBar ao finalizar o carregamento
                    mostrarCarregamento(false);

                    if (task.isSuccessful()) {
                        List<Nota> notasServidor = task.getResult().toObjects(Nota.class);
                        if (!notasServidor.isEmpty()) {
                            LinhaNotasAdapter notasAdapter = new LinhaNotasAdapter(notasServidor);
                            binding.listViewNotas.setAdapter(notasAdapter);
                        } else {
                            exibirMensagemErro("Sem notas disponíveis");
                        }
                    } else {
                        exibirMensagemErro("Erro ao obter notas do servidor");
                    }
                })
                .addOnFailureListener(e -> {
                    // Oculta o ProgressBar ao ocorrer uma falha
                    mostrarCarregamento(false);
                    exibirMensagemErro("Falha na conexão");
                });
    }

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


    private void exibirMensagemErro(String mensagem) {
        Snackbar.make(binding.getRoot(), mensagem, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Limpar o binding para evitar vazamento de memória
        binding = null;
    }
}
