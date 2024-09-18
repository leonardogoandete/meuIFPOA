package br.com.ifrs.meuifpoa.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputLayout;

import br.com.ifrs.meuifpoa.R;

public class PasswordDialog {

    private final Context context;
    private final OnPasswordEnteredListener listener;

    public PasswordDialog(Context context, OnPasswordEnteredListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void show() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_sync_sigaa, null);
        TextInputLayout senhaSigaa = view.findViewById(R.id.textInputSenhaSyncSigaa);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view)
                .setPositiveButton("OK", (dialog, which) -> {
                    String senha = senhaSigaa.getEditText().getText().toString().trim();
                    if (!senha.isEmpty()) {
                        listener.onPasswordEntered(senha);
                    } else {
                        Toast.makeText(context, "Digite sua senha", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    public interface OnPasswordEnteredListener {
        void onPasswordEntered(String senha);
    }
}
