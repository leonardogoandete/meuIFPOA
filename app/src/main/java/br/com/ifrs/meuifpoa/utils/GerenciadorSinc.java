package br.com.ifrs.meuifpoa.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.ifrs.meuifpoa.R;
import br.com.ifrs.meuifpoa.model.SyncResponse;
import br.com.ifrs.meuifpoa.retrofit.SyncRetrofit;
import br.com.ifrs.meuifpoa.retrofit.service.SyncService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A classe `GerenciadorSinc` é responsável por gerenciar a sincronização de dados do aplicativo.
 * Ela verifica a necessidade de sincronização, solicita a senha do usuário e realiza a sincronização
 * com o servidor.
 */
public class GerenciadorSinc {

    private static final String TAG = "GerenciadorSincronizacao";
    private static final long QUINZE_DIAS_EM_MILLIS = 15 * 24 * 60 * 60 * 1000L;

    /**
     * Verifica se a sincronização é necessária e solicita a senha do usuário, se necessário.
     *
     * @param contexto  O contexto da aplicação.
     * @param aoSucesso Runnable a ser executado em caso de sucesso.
     */
    public static void verificarERequisitarSenha(Context contexto, Runnable aoSucesso) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser usuario = mAuth.getCurrentUser();

        if (usuario != null) {
            long dataUltimaSincronizacao = obterDataUltimaSincronizacao(contexto);
            long tempoAtual = System.currentTimeMillis();

            Log.d(TAG, "Tempo desde última sincronização: " + (tempoAtual - dataUltimaSincronizacao));
            if (tempoAtual - dataUltimaSincronizacao >= QUINZE_DIAS_EM_MILLIS) {
                Log.d(TAG, "Tempo desde última sincronização excede 15 dias, solicitando senha.");

                AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                LayoutInflater inflater = LayoutInflater.from(contexto);
                View dialogView = inflater.inflate(R.layout.dialog_sync_sigaa, null);
                TextInputLayout senhaInput = dialogView.findViewById(R.id.textInputSenhaSyncSigaa);
                LinearLayout progressBarContainer = dialogView.findViewById(R.id.containerProgressBarSync);

                builder.setView(dialogView)
                        .setPositiveButton("OK", null)  // Não fechar o diálogo automaticamente
                        .setNegativeButton("Cancelar", (dialogInterface, which) -> dialogInterface.dismiss());

                AlertDialog dialog = builder.create();

                dialog.setOnShowListener(dialogInterface -> {
                    Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                    positiveButton.setOnClickListener(v -> {
                        String senha = senhaInput.getEditText().getText().toString().trim();

                        if (!senha.isEmpty()) {
                            iniciarSincronizacao(contexto, senha, dialogView, dialog, progressBarContainer, aoSucesso, positiveButton);
                        } else {
                            senhaInput.setError("Senha não pode ser vazia");
                        }
                    });

                    negativeButton.setOnClickListener(v -> dialog.dismiss());
                });

                dialog.show();
            } else {
                Log.d(TAG, "Sincronização recente, prosseguindo sem solicitar senha.");
                aoSucesso.run();
            }
        } else {
            exibirMensagem(contexto, "Usuário não autenticado");
        }
    }

    /**
     * Inicia o processo de sincronização com o servidor.
     *
     * @param contexto             O contexto da aplicação.
     * @param senha                A senha do usuário.
     * @param dialogView           A view do diálogo.
     * @param dialog               O diálogo de sincronização.
     * @param progressBarContainer O container da barra de progresso.
     * @param aoSucesso            Runnable a ser executado em caso de sucesso.
     * @param positiveButton       O botão positivo do diálogo.
     */
    private static void iniciarSincronizacao(Context contexto, String senha, View dialogView, AlertDialog dialog, LinearLayout progressBarContainer, Runnable aoSucesso, Button positiveButton) {
        TextInputLayout senhaSigaa = dialogView.findViewById(R.id.textInputSenhaSyncSigaa);
        progressBarContainer.setVisibility(View.VISIBLE);
        senhaSigaa.setVisibility(View.GONE);

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
                    exibirMensagem(contexto, "Sincronização realizada com sucesso");
                    dialog.dismiss();
                    aoSucesso.run();
                } else {
                    Log.e(TAG, "Erro ao sincronizar dados: " + resposta.message());
                    tratarErroSincronizacao(contexto, progressBarContainer, senhaSigaa, positiveButton, "Erro ao sincronizar dados, tente novamente!" + resposta.message());
                }
            }

            @Override
            public void onFailure(Call<SyncResponse> chamada, Throwable t) {
                tratarErroSincronizacao(contexto, progressBarContainer, senhaSigaa, positiveButton, "Falha na conexão: " + t.getMessage());
            }
        });
    }

    /**
     * Trata os erros ocorridos durante a sincronização.
     *
     * @param contexto             O contexto da aplicação.
     * @param progressBarContainer O container da barra de progresso.
     * @param senhaSigaa           O campo de entrada da senha.
     * @param positiveButton       O botão positivo do diálogo.
     * @param mensagemErro         A mensagem de erro a ser exibida.
     */
    private static void tratarErroSincronizacao(Context contexto, LinearLayout progressBarContainer, TextInputLayout senhaSigaa, Button positiveButton, String mensagemErro) {
        exibirMensagem(contexto, mensagemErro);
        Log.e(TAG, mensagemErro);
        progressBarContainer.setVisibility(View.GONE);
        senhaSigaa.setVisibility(View.VISIBLE);
        positiveButton.setEnabled(true);  // Reabilita o botão para tentar novamente
    }

    /**
     * Exibe uma mensagem ao usuário.
     *
     * @param contexto O contexto da aplicação.
     * @param mensagem A mensagem a ser exibida.
     */
    private static void exibirMensagem(Context contexto, String mensagem) {
        Toast.makeText(contexto, mensagem, Toast.LENGTH_LONG).show();
    }

    /**
     * Obtém a data da última sincronização.
     *
     * @param contexto O contexto da aplicação.
     * @return A data da última sincronização em milissegundos.
     */
    public static long obterDataUltimaSincronizacao(Context contexto) {
        return getSharedPrefs(contexto, "syncPrefs").getLong("ultimaSincronizacao", 0);
    }

    /**
     * Salva a data da última sincronização.
     *
     * @param contexto   O contexto da aplicação.
     * @param timestamp  O timestamp da última sincronização.
     */
    public static void salvarDataUltimaSincronizacao(Context contexto, long timestamp) {
        getSharedPrefs(contexto, "syncPrefs").edit().putLong("ultimaSincronizacao", timestamp).apply();
    }

    /**
     * Limpa os dados de sincronização e login.
     *
     * @param contexto O contexto da aplicação.
     */
    public static void limpar(Context contexto) {
        getSharedPrefs(contexto, "syncPrefs").edit().clear().apply();
        getSharedPrefs(contexto, "loginSigaa").edit().clear().apply();
        Log.d(TAG, "GerenciadorSincronizacao limpo");
    }

    /**
     * Obtém as preferências compartilhadas.
     *
     * @param context   O contexto da aplicação.
     * @param prefsName O nome das preferências.
     * @return As preferências compartilhadas.
     */
    private static SharedPreferences getSharedPrefs(Context context, String prefsName) {
        return context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
    }
}

