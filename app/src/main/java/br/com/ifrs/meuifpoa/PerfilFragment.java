package br.com.ifrs.meuifpoa;

import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import br.com.ifrs.meuifpoa.databinding.FragmentPerfilBinding;
import br.com.ifrs.meuifpoa.model.Perfil;

public class PerfilFragment extends Fragment {

    private static final String TAG = "PerfilFragment";
    private static final String LOCAL_IMAGE_PATH = "perfil.jpg";

    private FragmentPerfilBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        // Configura o Firestore com persistência offline
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);

        // Usando SyncManager para verificar e requisitar a senha
        SyncManager syncManager = new SyncManager();
        syncManager.verificarERequisitarSenha(getContext(), this::obterDadosPerfilDoFirestore);

        binding.btnSairPerfil.setOnClickListener(v -> {
            mAuth.signOut();
            // Navega para o fragmento de login
            Navigation.findNavController(view).navigate(R.id.noticiasFragment);
        });
    }

    private void obterDadosPerfilDoFirestore() {
        FirebaseUser usuarioAtual = mAuth.getCurrentUser();
        if (usuarioAtual == null) {
            Snackbar.make(getView(), R.string.msg_titulo_deve_estar_logado, Snackbar.LENGTH_SHORT).show();
            return;
        }

        db.collection("usuarios").document(usuarioAtual.getUid()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Perfil perfil = document.toObject(Perfil.class);
                            if (perfil != null) {
                                configuraPerfil(perfil);
                                carregarFotoPerfil();
                            } else {
                                Toast.makeText(getContext(), "Perfil não encontrado", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Documento não encontrado", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Erro ao obter perfil do servidor", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Falha na conexão: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void configuraPerfil(Perfil perfil) {
        binding.txtViewValorNome.setText(perfil.getNomeDocente() != null ? perfil.getNomeDocente() : "Nome não disponível");
        binding.txtViewValorMatricula.setText(perfil.getMatricula() != null ? perfil.getMatricula() : "Matrícula não disponível");
        binding.txtViewValorCurso.setText(perfil.getCurso() != null ? perfil.getCurso() : "Curso não disponível");
        binding.txtViewValorNivel.setText(perfil.getNivel() != null ? perfil.getNivel() : "Nível não disponível");
        binding.txtViewValorSituacao.setText(perfil.getStatus() != null ? perfil.getStatus() : "Status não disponível");
        binding.txtViewValorIngresso.setText(perfil.getAnoIngresso() != null ? perfil.getAnoIngresso() : "Ano de ingresso não disponível");
    }

    private void carregarFotoPerfil() {
        FirebaseUser usuarioAtual = mAuth.getCurrentUser();
        if (usuarioAtual == null) {
            return; // Usuário não está logado, trate isso adequadamente
        }

        // Crie uma referência ao arquivo no Firebase Storage
        StorageReference fotoRef = storage.getReference().child("perfil/" + usuarioAtual.getUid() + ".jpg");

        // Caminho local onde a imagem será salva
        File localFile = new File(getContext().getFilesDir(), LOCAL_IMAGE_PATH);

        // Baixar o arquivo do Firebase Storage
        fotoRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
            // Download completo, exibe a imagem
            Log.d(TAG, "Download concluído");
            exibirImagemLocal(localFile);
        }).addOnFailureListener(exception -> {
            Log.e(TAG, "Erro ao baixar a imagem", exception);
            // Exibe uma imagem padrão ou um placeholder
            binding.imgPerfil.setImageResource(R.drawable.ifrs_poa_logo); // Placeholder
        });
    }

    private void exibirImagemLocal(File file) {
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            binding.imgPerfil.setImageBitmap(bitmap);
        } else {
            binding.imgPerfil.setImageResource(R.drawable.ifrs_poa_logo); // Placeholder
        }
    }
}
