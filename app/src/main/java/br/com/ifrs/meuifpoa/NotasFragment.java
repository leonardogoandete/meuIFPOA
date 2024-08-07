package br.com.ifrs.meuifpoa;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.List;

import br.com.ifrs.meuifpoa.adapter.recycler.LinhaNotasAdapter;
import br.com.ifrs.meuifpoa.model.Nota;
import br.com.ifrs.meuifpoa.retrofit.SyncRetrofit;
import br.com.ifrs.meuifpoa.retrofit.service.SyncService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotasFragment extends Fragment {

    RecyclerView recyclerViewListNotas;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notas, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewListNotas = view.findViewById(R.id.listViewNotas);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        verificarERequisitarSenha(getContext());  // Adicionando aqui para verificar a senha ao abrir o app

        obterNotasDoFirestore();
        recyclerViewListNotas.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewListNotas.setLayoutManager(layoutManager);
    }

    private void obterNotasDoFirestore() {
        if (mAuth.getUid() == null) {
            Snackbar.make(getView(), R.string.msg_titulo_deve_estar_logado, Snackbar.LENGTH_SHORT).show();
            return;
        }

        db.collection("notas")
                .document(mAuth.getUid())
                .collection("disciplinas")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Nota> notasServidor = task.getResult().toObjects(Nota.class);
                        if (!notasServidor.isEmpty()) {
                            LinhaNotasAdapter notasAdapter = new LinhaNotasAdapter(notasServidor);
                            recyclerViewListNotas.setAdapter(notasAdapter);
                        } else {
                            Toast.makeText(getContext(), "Sem notas disponíveis", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Erro ao obter notas do servidor", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Falha na conexão", Toast.LENGTH_SHORT).show();
                });
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
