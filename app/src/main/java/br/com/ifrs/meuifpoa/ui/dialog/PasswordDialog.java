package br.com.ifrs.meuifpoa.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import br.com.ifrs.meuifpoa.R;

public class PasswordDialog {

    private final Context context;
    private final OnPasswordEnteredListener listener;
    private AlertDialog dialog;
    private Button positiveButton;
    private LinearLayout progressBarContainer;
    private TextInputLayout senhaSigaa;

    public PasswordDialog(Context context, OnPasswordEnteredListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void show() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_sync_sigaa, null);
        senhaSigaa = view.findViewById(R.id.textInputSenhaSyncSigaa);
        progressBarContainer = view.findViewById(R.id.containerProgressBarSync);
        ProgressBar progressBar = view.findViewById(R.id.progressBarSync);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view)
                .setPositiveButton("OK", null)  // Vamos configurar o clique do botão manualmente mais tarde
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        dialog = builder.create();
        dialog.show();

        positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            String senha = senhaSigaa.getEditText().getText().toString().trim();
            if (!senha.isEmpty()) {
                // Mostra o ProgressBar e esconde o campo de senha
                mostrarCarregando(true);
                // Chama o listener para continuar com a sincronização
                listener.onPasswordEntered(senha);
            } else {
                Toast.makeText(context, "Digite sua senha", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para exibir/esconder o ProgressBar e controlar os componentes
    private void mostrarCarregando(boolean carregando) {
        if (carregando) {
            progressBarContainer.setVisibility(View.VISIBLE);
            senhaSigaa.setVisibility(View.GONE);
            positiveButton.setEnabled(false);
        } else {
            progressBarContainer.setVisibility(View.GONE);
            senhaSigaa.setVisibility(View.VISIBLE);
            positiveButton.setEnabled(true);
        }
    }

    public interface OnPasswordEnteredListener {
        void onPasswordEntered(String senha);
    }
}
