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
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import br.com.ifrs.meuifpoa.R;
import br.com.ifrs.meuifpoa.utils.GerenciadorSinc;
import br.com.ifrs.meuifpoa.databinding.FragmentPerfilBinding;
import br.com.ifrs.meuifpoa.model.Perfil;

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

        // Usando GerenciadorSinc para verificar e requisitar a senha
        GerenciadorSinc GerenciadorSinc = new GerenciadorSinc();
        GerenciadorSinc.verificarERequisitarSenha(getContext(), this::obterDadosPerfilDoFirestore);

        binding.btnSairPerfil.setOnClickListener(v -> {
            mAuth.signOut();
            removerFotoPerfil();
            GerenciadorSinc.limpar(getContext());

            // Navega para o fragmento de login
            Navigation.findNavController(view).navigate(R.id.noticiasFragment);
        });
    }

    private void inicializarFirebase() {
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        // Configura o Firestore com persistência offline
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);
    }

    private void obterDadosPerfilDoFirestore() {
        FirebaseUser usuarioAtual = mAuth.getCurrentUser();
        if (usuarioAtual == null) {
            Snackbar.make(getView(), R.string.msg_titulo_deve_estar_logado, Snackbar.LENGTH_SHORT).show();
            return;
        }

        db.collection("usuarios")
                .document(usuarioAtual.getUid())
                .get(Source.DEFAULT)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documento = task.getResult();
                        if (documento.exists()) {
                            Perfil perfil = documento.toObject(Perfil.class);
                            if (perfil != null) {
                                configurarPerfil(perfil);
                                carregarFotoPerfil();
                                exibirElementosPerfil();
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
                    mostrarErro("Falha na conexão: " + e.getMessage());
                });
    }

    private void configurarPerfil(Perfil perfil) {
        binding.txtViewValorNome.setText(perfil.getNomeDocente() != null ? perfil.getNomeDocente() : "Nome não disponível");
        binding.txtViewValorMatricula.setText(perfil.getMatricula() != null ? perfil.getMatricula() : "Matrícula não disponível");
        binding.txtViewValorCurso.setText(perfil.getCurso() != null ? perfil.getCurso() : "Curso não disponível");
        binding.txtViewValorNivel.setText(perfil.getNivel() != null ? perfil.getNivel() : "Nível não disponível");
        binding.txtViewValorSituacao.setText(perfil.getStatus() != null ? perfil.getStatus() : "Status não disponível");
        binding.txtViewValorIngresso.setText(perfil.getAnoIngresso() != null ? perfil.getAnoIngresso() : "Ano de ingresso não disponível");
    }

    private void mostrarErro(String mensagem) {
        Toast.makeText(getContext(), mensagem, Toast.LENGTH_SHORT).show();
    }

    private void carregarFotoPerfil() {
        FirebaseUser usuarioAtual = mAuth.getCurrentUser();
        if (usuarioAtual == null) {
            return;
        }

        File arquivoLocal = new File(getContext().getFilesDir(), CAMINHO_IMAGEM_LOCAL);

        if (arquivoLocal.exists()) {
            Log.d(TAG, "Imagem carregada do cache local");
            exibirImagemLocal(arquivoLocal);
        } else {
            StorageReference fotoRef = storage.getReference().child("perfil/" + usuarioAtual.getUid() + ".jpg");

            fotoRef.getFile(arquivoLocal).addOnSuccessListener(taskSnapshot -> {
                Log.d(TAG, "Download concluído");
                exibirImagemLocal(arquivoLocal);
            }).addOnFailureListener(exception -> {
                Log.e(TAG, "Erro ao baixar a imagem", exception);
                binding.imgPerfil.setImageResource(R.drawable.ifrs_poa_logo);
            });
        }
    }

    private void exibirImagemLocal(File arquivo) {
        if (arquivo.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(arquivo.getAbsolutePath());
            binding.imgPerfil.setImageBitmap(bitmap);
        } else {
            binding.imgPerfil.setImageResource(R.drawable.ifrs_poa_logo);
        }
    }

    private void removerFotoPerfil() {
        File arquivoLocal = new File(getContext().getFilesDir(), CAMINHO_IMAGEM_LOCAL);
        if (arquivoLocal.exists()) {
            boolean excluido = arquivoLocal.delete();
            if (excluido) {
                Log.d(TAG, "Foto de perfil removida com sucesso");
            } else {
                Log.e(TAG, "Falha ao remover a foto de perfil");
            }
        }
    }

    private void exibirElementosPerfil() {
        binding.imgPerfil.setVisibility(View.VISIBLE);
        binding.linearLayoutNome.setVisibility(View.VISIBLE);
        binding.linearLayoutMatricula.setVisibility(View.VISIBLE);
        binding.linearLayoutCurso.setVisibility(View.VISIBLE);
        binding.linearLayoutNivel.setVisibility(View.VISIBLE);
        binding.linearLayoutSituacao.setVisibility(View.VISIBLE);
        binding.linearLayoutIngresso.setVisibility(View.VISIBLE);
        binding.btnSairPerfil.setVisibility(View.VISIBLE);
    }
}
