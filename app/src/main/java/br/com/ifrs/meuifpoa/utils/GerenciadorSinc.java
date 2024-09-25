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
        if (aoSucesso == null) {
            aoSucesso = () -> {};
        }

        if (usuario != null) {
            long dataUltimaSincronizacao = obterDataUltimaSincronizacao(contexto);
            long tempoAtual = System.currentTimeMillis();

            Log.d(TAG, "Tempo desde última sincronização: " + (tempoAtual - dataUltimaSincronizacao));
            if (tempoAtual - dataUltimaSincronizacao >= QUINZE_DIAS_EM_MILLIS) {
                Log.d(TAG, "Tempo desde última sincronização excede 15 dias, solicitando senha.");
                Runnable finalAoSucesso = aoSucesso;
                new PasswordDialog(contexto, senha -> sincronizarDados(contexto, senha, finalAoSucesso)).show();
            } else {
                Log.d(TAG, "Sincronização recente, prosseguindo sem solicitar senha.");
                aoSucesso.run();
            }
        } else {
            exibirMensagem(contexto, "Usuário não autenticado");
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
                                    exibirMensagem(contexto, "CPF não encontrado");
                                }
                            } else {
                                exibirMensagem(contexto, "Documento não encontrado");
                            }
                        } else {
                            exibirMensagem(contexto, "Erro ao obter perfil do servidor");
                        }
                    })
                    .addOnFailureListener(e -> exibirMensagem(contexto, "Falha na conexão: " + e.getMessage()));
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
                    aoSucesso.run();
                } else {
                    exibirMensagem(contexto, "Erro ao sincronizar dados");
                    Log.e(TAG, "Erro ao sincronizar dados: " + resposta.body());
                }
            }

            @Override
            public void onFailure(Call<SyncResponse> chamada, Throwable t) {
                exibirMensagem(contexto, "Falha na conexão: " + t.getMessage());
                Log.e(TAG, "Falha na conexão: " + t.getMessage(), t);
            }
        });
    }

    public static long obterDataUltimaSincronizacao(Context contexto) {
        return getSharedPrefs(contexto, "syncPrefs").getLong("ultimaSincronizacao", 0);
    }

    public static void salvarDataUltimaSincronizacao(Context contexto, long timestamp) {
        getSharedPrefs(contexto, "syncPrefs").edit().putLong("ultimaSincronizacao", timestamp).apply();
    }

    public static void limpar(Context contexto) {
        getSharedPrefs(contexto, "syncPrefs").edit().clear().apply();
        getSharedPrefs(contexto, "loginSigaa").edit().clear().apply();
        Log.d(TAG, "GerenciadorSincronizacao limpo");
    }

    private static SharedPreferences getSharedPrefs(Context context, String prefsName) {
        return context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
    }

    private static void exibirMensagem(Context contexto, String mensagem) {
        Toast.makeText(contexto, mensagem, Toast.LENGTH_SHORT).show();
    }
}
