package br.com.ifrs.meuifpoa.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import br.com.ifrs.meuifpoa.R;
import br.com.ifrs.meuifpoa.databinding.FragmentPerfilBinding;
import br.com.ifrs.meuifpoa.model.Perfil;
import br.com.ifrs.meuifpoa.utils.GerenciadorSinc;

public class PerfilFragment extends Fragment {

    private static final String TAG = "PerfilFragment";
    private static final String CAMINHO_IMAGEM_LOCAL = "perfil.jpg";
    private FragmentPerfilBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializarFirebase();

        GerenciadorSinc GerenciadorSinc = new GerenciadorSinc();
        GerenciadorSinc.verificarERequisitarSenha(getContext(), this::obterDadosPerfilDoFirestore);

        mostrarSincronizacao(true);

        binding.btnSairPerfil.setOnClickListener(v -> {
            mAuth.signOut();
            limparDadosPerfil();
            removerFotoPerfil();
            GerenciadorSinc.limpar(getContext());

            Navigation.findNavController(view).navigate(R.id.noticiasFragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void inicializarFirebase() {
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void obterDadosPerfilDoFirestore() {
        FirebaseUser usuarioAtual = mAuth.getCurrentUser();
        if (usuarioAtual == null || binding == null) {
            return;
        }

        limparDadosPerfil(); // Limpa o perfil atual antes de carregar os novos dados

        db.collection("usuarios")
                .document(usuarioAtual.getUid())
                .get(Source.DEFAULT)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && binding != null) {
                        DocumentSnapshot documento = task.getResult();
                        if (documento.exists()) {
                            Perfil perfil = documento.toObject(Perfil.class);
                            if (perfil != null) {
                                configurarPerfil(perfil);
                                carregarFotoPerfil();
                                mostrarSincronizacao(false);
                            } else {
                                mostrarErro("Perfil não encontrado");
                            }
                        } else {
                            mostrarErro("Documento não encontrado");
                        }
                    } else {
                        mostrarErro("Erro ao obter perfil do servidor");
                    }
                })
                .addOnFailureListener(e -> {
                    if (binding != null) {
                        mostrarErro("Falha na conexão: " + e.getMessage());
                    }
                });
    }

    private void configurarPerfil(Perfil perfil) {
        if (binding == null) {
            return;
        }
        binding.txtViewValorNome.setText(perfil.getNomeDocente() != null ? perfil.getNomeDocente() : getString(R.string.dado_nao_disponivel));
        binding.txtViewValorMatricula.setText(perfil.getMatricula() != null ? perfil.getMatricula() : getString(R.string.dado_nao_disponivel));
        binding.txtViewValorCurso.setText(perfil.getCurso() != null ? perfil.getCurso() : getString(R.string.dado_nao_disponivel));
        binding.txtViewValorNivel.setText(perfil.getNivel() != null ? perfil.getNivel() : getString(R.string.dado_nao_disponivel));
        binding.txtViewValorSituacao.setText(perfil.getStatus() != null ? perfil.getStatus() : getString(R.string.dado_nao_disponivel));
        binding.txtViewValorIngresso.setText(perfil.getAnoIngresso() != null ? perfil.getAnoIngresso() : getString(R.string.dado_nao_disponivel));
        exibirElementosPerfil(true);
    }

    private void mostrarErro(String mensagem) {
        Toast.makeText(getContext(), mensagem, Toast.LENGTH_SHORT).show();
        Log.e(TAG, mensagem);
    }

    private void limparDadosPerfil() {
        if (binding == null) {
            return;
        }
        binding.txtViewValorNome.setText("");
        binding.txtViewValorMatricula.setText("");
        binding.txtViewValorCurso.setText("");
        binding.txtViewValorNivel.setText("");
        binding.txtViewValorSituacao.setText("");
        binding.txtViewValorIngresso.setText("");
        binding.imgPerfil.setImageResource(R.drawable.ifrs_poa_logo);
        exibirElementosPerfil(false);
    }

    private void carregarFotoPerfil() {
        FirebaseUser usuarioAtual = mAuth.getCurrentUser();
        if (usuarioAtual == null || binding == null) {
            return;
        }

        File arquivoLocal = new File(getContext().getFilesDir(), CAMINHO_IMAGEM_LOCAL);

        if (arquivoLocal.exists()) {
            Log.d(TAG, "Imagem carregada do cache local");
            exibirImagemLocal(arquivoLocal);
        } else {
            StorageReference fotoRef = storage.getReference().child("perfil/" + usuarioAtual.getUid() + ".jpg");

            fotoRef.getFile(arquivoLocal).addOnSuccessListener(taskSnapshot -> {
                if (binding == null) return;  // Verifica o binding antes de acessar a UI
                Log.d(TAG, "Download concluído");
                exibirImagemLocal(arquivoLocal);
            }).addOnFailureListener(exception -> {
                if (binding == null) return;  // Verifica o binding antes de acessar a UI
                Log.e(TAG, "Erro ao baixar a imagem", exception);
                binding.imgPerfil.setImageResource(R.drawable.ifrs_poa_logo);
            });
        }
    }


    private void exibirImagemLocal(File arquivo) {
        if (binding == null) {
            return;
        }

        if (arquivo.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(arquivo.getAbsolutePath());
            binding.imgPerfil.setImageBitmap(bitmap);
        } else {
            binding.imgPerfil.setImageResource(R.drawable.ifrs_poa_logo);
        }
    }


    private void removerFotoPerfil() {
        File arquivoLocal = new File(getContext().getFilesDir(), CAMINHO_IMAGEM_LOCAL);
        if (arquivoLocal.exists() && arquivoLocal.delete()) {
            Log.d(TAG, "Foto de perfil removida com sucesso");
        } else {
            Log.e(TAG, "Falha ao remover a foto de perfil");
        }
    }

    private void exibirElementosPerfil(boolean exibir) {
        if (binding == null) {
            return;
        }
        int visibilidade = exibir ? View.VISIBLE : View.GONE;
        binding.imgPerfil.setVisibility(visibilidade);
        binding.linearLayoutNome.setVisibility(visibilidade);
        binding.linearLayoutMatricula.setVisibility(visibilidade);
        binding.linearLayoutCurso.setVisibility(visibilidade);
        binding.linearLayoutNivel.setVisibility(visibilidade);
        binding.linearLayoutSituacao.setVisibility(visibilidade);
        binding.linearLayoutIngresso.setVisibility(visibilidade);
        binding.btnSairPerfil.setVisibility(visibilidade);
    }

    private void mostrarSincronizacao(boolean exibir) {
        if (binding == null) {
            return;
        }
        binding.alertSinconizando.setVisibility(exibir ? View.VISIBLE : View.GONE);
    }
}
