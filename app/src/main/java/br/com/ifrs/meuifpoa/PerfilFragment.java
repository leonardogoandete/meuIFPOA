package br.com.ifrs.meuifpoa;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
import java.io.FileOutputStream;
import java.io.IOException;

import br.com.ifrs.meuifpoa.databinding.FragmentPerfilBinding;
import br.com.ifrs.meuifpoa.model.Perfil;
import br.com.ifrs.meuifpoa.retrofit.SyncRetrofit;
import br.com.ifrs.meuifpoa.retrofit.service.SyncService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        verificarERequisitarSenha(getContext());  // Adicionando aqui para verificar a senha ao abrir o app

        obterDadosPerfilDoFirestore();

        binding.btnSairPerfil.setOnClickListener(v -> {
            mAuth.signOut();
            // Navega para o fragmento de login
            Navigation.findNavController(view).navigate(R.id.noticiasFragment);
            // Para encerrar a aplicação, você pode usar:
            // requireActivity().finishAffinity();
        });
    }

    private void obterDadosPerfilDoFirestore() {
        if (mAuth.getUid() == null) {
            Snackbar.make(getView(), R.string.msg_titulo_deve_estar_logado, Snackbar.LENGTH_SHORT).show();
            return;
        }

        db.collection("usuarios").document(mAuth.getUid()).get()
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
        String userId = mAuth.getUid();
        if (userId == null) {
            return; // User not logged in, handle this case appropriately
        }

        // Crie uma referência ao arquivo no Firebase Storage
        StorageReference fotoRef = FirebaseStorage.getInstance().getReference().child("perfil/" + userId + ".jpg");

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

    private void mostrarDialogoSenha(final Context contexto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
        builder.setTitle("Senha Necessária");

        final EditText input = new EditText(contexto);
        input.setHint("Digite sua senha");
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String senha = input.getText().toString();
            if (!senha.isEmpty()) {
                // Senha fornecida, iniciar a sincronização com o servidor
                sincronizarDados(senha);
            } else {
                // Senha não fornecida, mostrar mensagem de erro
                Toast.makeText(contexto, "Digite sua senha", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void verificarERequisitarSenha(Context contexto) {
        FirebaseUser usuario = mAuth.getCurrentUser();
        if (usuario != null) {
            long dataUltimaSincronizacao = SyncManager.getLastSyncDate(contexto);
            long tempoAtual = System.currentTimeMillis();
            long quinzeSegundosEmMillis = 15 * 1000L;

            Log.d("SyncManager", "Data da última sincronização: " + dataUltimaSincronizacao);
            Log.d("SyncManager", "Tempo atual: " + tempoAtual);
            Log.d("SyncManager", "Diferença: " + (tempoAtual - dataUltimaSincronizacao));

            if (tempoAtual - dataUltimaSincronizacao >= quinzeSegundosEmMillis) {
                mostrarDialogoSenha(contexto);
            }
        } else {
            Toast.makeText(contexto, "Você precisa estar logado para sincronizar os dados.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sincronizarDados(String senha) {
        // Primeiro pegar o CPF do Firestore
        if (mAuth.getUid() != null) {
            db = FirebaseFirestore.getInstance();
            db.collection("usuarios").document(mAuth.getUid()).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                String cpf = document.getString("cpf");
                                if (cpf != null) {
                                    // Sincronizar dados com o servidor
                                    sincronizarDadosSigaa(senha);
                                } else {
                                    Toast.makeText(getContext(), "CPF não encontrado", Toast.LENGTH_SHORT).show();
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
    }

    private void sincronizarDadosSigaa(String senha) {
        SharedPreferences preferencias = getContext().getSharedPreferences("loginSigaa", Context.MODE_PRIVATE);
        String token = preferencias.getString("token", "");
        SyncService syncService = new SyncRetrofit().getSyncService();

        Call<Void> call = syncService.sincronizar(token, senha);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Atualize a data da última sincronização
                    SyncManager.saveLastSyncDate(getContext(), System.currentTimeMillis());
                    Snackbar.make(getView(), "Sincronização realizada com sucesso", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(getView(), "Erro ao sincronizar dados", Snackbar.LENGTH_SHORT).show();
                    Log.e("SyncManager", "Erro ao sincronizar dados: " + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Snackbar.make(getView(), "Falha na conexão: " + t.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
