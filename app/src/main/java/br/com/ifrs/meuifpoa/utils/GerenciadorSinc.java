package br.com.ifrs.meuifpoa.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import br.com.ifrs.meuifpoa.R;
import br.com.ifrs.meuifpoa.model.SyncResponse;
import br.com.ifrs.meuifpoa.retrofit.SyncRetrofit;
import br.com.ifrs.meuifpoa.retrofit.service.SyncService;
import br.com.ifrs.meuifpoa.ui.dialog.PasswordDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GerenciadorSinc {

    private static final String TAG = "GerenciadorSincronizacao";
    private static final long QUINZE_DIAS_EM_MILLIS = 15 * 24 * 60 * 60 * 1000L;

    public static void verificarERequisitarSenha(Context contexto, Runnable aoSucesso) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser usuario = mAuth.getCurrentUser();
        if (usuario != null) {
            long dataUltimaSincronizacao = obterDataUltimaSincronizacao(contexto);
            long tempoAtual = System.currentTimeMillis();

            Log.d(TAG, "Tempo desde última sincronização: " + (tempoAtual - dataUltimaSincronizacao));
            if (tempoAtual - dataUltimaSincronizacao >= QUINZE_DIAS_EM_MILLIS) {
                Log.d(TAG, "Tempo desde última sincronização excede 15 dias, solicitando senha.");
                new PasswordDialog(contexto, senha -> sincronizarDados(contexto, senha, aoSucesso)).show();
            } else {
                Log.d(TAG, "Sincronização recente, prosseguindo sem solicitar senha.");
                if (aoSucesso != null) {
                    aoSucesso.run();
                }
            }
        } else {
            Log.d(TAG, "Usuário não autenticado, não é possível sincronizar dados.");
            Toast.makeText(contexto, "Usuário não autenticado", Toast.LENGTH_SHORT).show();
        }
    }

    private static void sincronizarDados(Context contexto, String senha, Runnable aoSucesso) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String uid = mAuth.getUid();
        if (uid != null) {
            db.collection("usuarios").document(uid).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documento = task.getResult();
                            if (documento != null && documento.exists()) {
                                String cpf = documento.getString("cpf");
                                if (cpf != null) {
                                    Log.d(TAG, "CPF encontrado: " + cpf);
                                    sincronizarDadosSigaa(contexto, senha, aoSucesso);
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

    private static void sincronizarDadosSigaa(Context contexto, String senha, Runnable aoSucesso) {
        SharedPreferences preferencias = contexto.getSharedPreferences("loginSigaa", Context.MODE_PRIVATE);
        String token = preferencias.getString("token", "");
        SyncService syncService = new SyncRetrofit().getSyncService();

        Log.d(TAG, "Iniciando sincronização com token: " + token);
        Call<SyncResponse> chamada = syncService.sincronizar(token, senha);
        chamada.enqueue(new Callback<SyncResponse>() {
            @Override
            public void onResponse(Call<SyncResponse> chamada, Response<SyncResponse> resposta) {
                if (resposta.isSuccessful()) {
                    Log.d(TAG, "Sincronização realizada com sucesso");
                    salvarDataUltimaSincronizacao(contexto, System.currentTimeMillis());
                    //Toast.makeText(contexto, R.string.msg_sync_sucesso, Toast.LENGTH_SHORT).show();
                    if (aoSucesso != null) {
                        aoSucesso.run();
                    }
                } else {
                    Toast.makeText(contexto, R.string.msg_sync_erro, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Erro ao sincronizar dados: " + resposta.body());
                }
            }

            @Override
            public void onFailure(Call<SyncResponse> chamada, Throwable t) {
                Toast.makeText(contexto, "Falha na conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Falha na conexão: " + t.getMessage(), t);
            }
        });
    }

    // Obtém a data da última sincronização
    public static long obterDataUltimaSincronizacao(Context contexto) {
        SharedPreferences prefs = contexto.getSharedPreferences("syncPrefs", Context.MODE_PRIVATE);
        return prefs.getLong("ultimaSincronizacao", 0);
    }

    // Guarda a data da última sincronização
    public static void salvarDataUltimaSincronizacao(Context contexto, long timestamp) {
        SharedPreferences prefs = contexto.getSharedPreferences("syncPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("ultimaSincronizacao", timestamp);
        editor.apply();
    }

    public static void limpar(Context contexto) {
        // Limpa as preferências relacionadas à sincronização
        SharedPreferences preferenciasSincronizacao = contexto.getSharedPreferences("syncPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorSincronizacao = preferenciasSincronizacao.edit();
        editorSincronizacao.clear();  // Limpa todas as entradas de sincronização
        editorSincronizacao.apply();

        // Limpa as preferências relacionadas ao login Sigaa
        SharedPreferences preferenciasLogin = contexto.getSharedPreferences("loginSigaa", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorLogin = preferenciasLogin.edit();
        editorLogin.clear();  // Limpa todas as entradas de login Sigaa
        editorLogin.apply();

        // Se houver outras tarefas de limpeza específicas, adicione aqui
        Log.d(TAG, "GerenciadorSincronizacao limpo");
    }

}
