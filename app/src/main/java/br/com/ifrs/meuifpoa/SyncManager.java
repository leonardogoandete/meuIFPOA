package br.com.ifrs.meuifpoa;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import br.com.ifrs.meuifpoa.retrofit.SyncRetrofit;
import br.com.ifrs.meuifpoa.retrofit.service.SyncService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncManager {

    private static final String TAG = "SyncManager";
    private static final long QUINZE_DIAS_EM_MILLIS = 15 * 24 * 60 * 60 * 1000L;

    public static void verificarERequisitarSenha(Context contexto, Runnable onSuccess) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser usuario = mAuth.getCurrentUser();
        if (usuario != null) {
            long dataUltimaSincronizacao = getLastSyncDate(contexto);
            long tempoAtual = System.currentTimeMillis();

            Log.d(TAG, "Tempo desde última sincronização: " + (tempoAtual - dataUltimaSincronizacao));
            if (tempoAtual - dataUltimaSincronizacao >= QUINZE_DIAS_EM_MILLIS) {
                Log.d(TAG, "Tempo desde última sincronização excede 15 dias, solicitando senha.");
                new SyncPasswordDialog(contexto, senha -> sincronizarDados(contexto, senha, onSuccess)).show();
            } else {
                Log.d(TAG, "Sincronização recente, prosseguindo sem solicitar senha.");
                if (onSuccess != null) {
                    onSuccess.run();
                }
            }
        } else {
            Snackbar.make(null, "Você precisa estar logado para sincronizar os dados.", Snackbar.LENGTH_SHORT).show();
        }
    }

    private static void sincronizarDados(Context contexto, String senha, Runnable onSuccess) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (mAuth.getUid() != null) {
            db.collection("usuarios").document(mAuth.getUid()).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                String cpf = document.getString("cpf");
                                if (cpf != null) {
                                    Log.d(TAG, "CPF encontrado: " + cpf);
                                    sincronizarDadosSigaa(contexto, senha, onSuccess);
                                } else {
                                    Toast.makeText(contexto, "CPF não encontrado", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(contexto, "Documento não encontrado", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(contexto, "Erro ao obter perfil do servidor", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(contexto, "Falha na conexão: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private static void sincronizarDadosSigaa(Context contexto, String senha, Runnable onSuccess) {
        SharedPreferences preferencias = contexto.getSharedPreferences("loginSigaa", Context.MODE_PRIVATE);
        String token = preferencias.getString("token", "");
        SyncService syncService = new SyncRetrofit().getSyncService();

        Log.d(TAG, "Iniciando sincronização com token: " + token);
        Call<Void> call = syncService.sincronizar(token, senha);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Sincronização realizada com sucesso");
                    saveLastSyncDate(contexto, System.currentTimeMillis());
                    Snackbar.make(null, R.string.msg_sync_sucesso, Snackbar.LENGTH_SHORT).show();
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                } else {
                    Snackbar.make(null, R.string.msg_sync_erro, Snackbar.LENGTH_SHORT).show();
                    Log.e(TAG, "Erro ao sincronizar dados: " + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(contexto, "Falha na conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Falha na conexão: " + t.getMessage(), t);
            }
        });
    }

    public static long getLastSyncDate(Context contexto) {
        SharedPreferences prefs = contexto.getSharedPreferences("syncPrefs", Context.MODE_PRIVATE);
        return prefs.getLong("lastSyncDate", 0);
    }

    public static void saveLastSyncDate(Context contexto, long timestamp) {
        SharedPreferences prefs = contexto.getSharedPreferences("syncPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("lastSyncDate", timestamp);
        editor.apply();
    }
}
