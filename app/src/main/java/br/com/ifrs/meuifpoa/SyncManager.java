package br.com.ifrs.meuifpoa;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

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
    private static final String PREFS_NAME = "sync_prefs";
    private static final String LAST_SYNC_DATE_KEY = "last_sync_date";
    private static final long CINQUENTA_DIAS_MILLIS = 50 * 24 * 60 * 60 * 1000L; // Exemplo de 50 dias

    public static void verificarERequisitarSenha(Context contexto, Runnable callback) {
        SharedPreferences prefs = contexto.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long dataUltimaSincronizacao = prefs.getLong(LAST_SYNC_DATE_KEY, 0);
        long tempoAtual = System.currentTimeMillis();

        // Ajuste o período conforme necessário
        if (tempoAtual - dataUltimaSincronizacao >= CINQUENTA_DIAS_MILLIS) {
            mostrarDialogoSenha(contexto, callback);
        } else {
            // Se não precisa solicitar a senha, apenas execute o callback
            if (callback != null) {
                callback.run();
            }
        }
    }

    public static void salvarDataUltimaSincronizacao(Context contexto) {
        SharedPreferences prefs = contexto.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(LAST_SYNC_DATE_KEY, System.currentTimeMillis());
        editor.apply();
    }

    private static void mostrarDialogoSenha(Context contexto, Runnable callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
        builder.setTitle("Senha Necessária");

        final EditText input = new EditText(contexto);
        input.setHint("Digite sua senha");
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String senha = input.getText().toString();
            if (!senha.isEmpty()) {
                // Senha fornecida, iniciar a sincronização com o servidor
                if (callback != null) {
                    callback.run();
                }
                // Salvar data da última sincronização
                salvarDataUltimaSincronizacao(contexto);
            } else {
                // Senha não fornecida, mostrar mensagem de erro
                Toast.makeText(contexto, "Digite sua senha", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}

