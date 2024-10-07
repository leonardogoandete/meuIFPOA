package br.com.ifrs.meuifpoa.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import br.com.ifrs.meuifpoa.R;
import br.com.ifrs.meuifpoa.databinding.ActivityLoginBinding;

/**
 * A atividade de login do aplicativo.
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    /**
     * Método chamado quando a atividade é criada.
     *
     * @param savedInstanceState O estado salvo da instância.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        configurarListeners();
    }

    /**
     * Método chamado quando a atividade é destruída.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    /**
     * Configura os listeners para os botões e textos da interface.
     */
    private void configurarListeners() {
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etCpf.getText().toString().trim();
            String senha = binding.etSenha.getText().toString().trim();

            if (validarEntrada(email, senha)) {
                realizarLogin(email, senha);
            }
        });

        binding.textNaoTemConta.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistroActivity.class);
            startActivity(intent);
            finish();
        });

        binding.textEsqueciSenha.setOnClickListener(v -> mostrarDialogoEsqueciSenha());
    }

    /**
     * Valida a entrada do usuário.
     *
     * @param email O e-mail inserido pelo usuário.
     * @param senha A senha inserida pelo usuário.
     * @return true se a entrada for válida, false caso contrário.
     */
    private boolean validarEntrada(String email, String senha) {
        boolean valido = true;

        if (email.isEmpty()) {
            binding.etCpf.setError("Digite seu e-mail");
            valido = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etCpf.setError("E-mail inválido");
            valido = false;
        }

        if (senha.isEmpty()) {
            binding.etSenha.setError("Digite sua senha");
            valido = false;
        }

        return valido;
    }



    /**
     * Realiza o login do usuário.
     *
     * @param email O e-mail do usuário.
     * @param senha A senha do usuário.
     */
    private void realizarLogin(String email, String senha) {
        binding.progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, tarefa -> {
                    binding.progressBar.setVisibility(View.GONE);

                    if (tarefa.isSuccessful()) {
                        tratarLoginBemSucedido();
                    } else {
                        Snackbar.make(binding.getRoot(), R.string.msg_login_error, Snackbar.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * Trata o login bem-sucedido do usuário.
     */
    private void tratarLoginBemSucedido() {
        FirebaseUser usuario = mAuth.getCurrentUser();
        if (usuario != null) {
            usuario.getIdToken(true).addOnCompleteListener(tarefaToken -> {
                if (tarefaToken.isSuccessful()) {
                    SharedPreferences preferencias = getSharedPreferences("loginSigaa", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferencias.edit();

                    StringBuffer token = new StringBuffer("Bearer ");
                    token.append(tarefaToken.getResult().getToken());
                    editor.putString("token", token.toString());
                    editor.apply();

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("selectedItemId", R.id.homeFragment); // ou qualquer outro ID
                    startActivity(intent);
                    finish();
                } else {
                    Snackbar.make(binding.getRoot(), R.string.msg_login_error, Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }


    /**
     * Mostra o diálogo para recuperação de senha.
     */
    private void mostrarDialogoEsqueciSenha() {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View viewDialogo = inflater.inflate(R.layout.dialog_esqueci_senha, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(viewDialogo)
                .setPositiveButton(R.string.btnEnviarEmailRecuperarSenha, (dialog, which) -> {
                    tratarRecuperacaoSenha(viewDialogo);
                })
                .setNegativeButton(R.string.btnCancelarEmailRecuperarSenha, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }


    /**
     * Trata a recuperação de senha do usuário.
     *
     * @param viewDialogo A view do diálogo de recuperação de senha.
     */
    private void tratarRecuperacaoSenha(View viewDialogo) {
        TextInputLayout layoutEmail = viewDialogo.findViewById(R.id.textInputEmailRecuperacao);
        String email = layoutEmail.getEditText().getText().toString().trim();

        if (!email.isEmpty()) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(tarefa -> {
                        if (tarefa.isSuccessful()) {
                            Snackbar.make(binding.getRoot(), R.string.msg_email_recuperar_senha_sucesso, Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(binding.getRoot(), R.string.msg_email_recuperar_senha_erro, Snackbar.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Snackbar.make(binding.getRoot(), R.string.msg_email_recuperar_senha_erro, Snackbar.LENGTH_SHORT).show();
        }
    }
}
