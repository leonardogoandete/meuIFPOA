package br.com.ifrs.meuifpoa;

import android.content.Context;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class SyncPasswordDialog {

    private final Context contexto;
    private final OnPasswordEnteredListener listener;

    public SyncPasswordDialog(Context contexto, OnPasswordEnteredListener listener) {
        this.contexto = contexto;
        this.listener = listener;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
        builder.setTitle("Senha Necessária");

        final EditText input = new EditText(contexto);
        input.setHint("Digite sua senha");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String senha = input.getText().toString();
            if (!senha.isEmpty()) {
                listener.onPasswordEntered(senha);
            } else {
                Toast.makeText(contexto, "Digite sua senha", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    public interface OnPasswordEnteredListener {
        void onPasswordEntered(String senha);
    }
}

