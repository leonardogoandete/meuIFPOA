package br.com.ifrs.meuifpoa.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import br.com.ifrs.meuifpoa.R;
import br.com.ifrs.meuifpoa.databinding.ActivityLoginBinding;
import br.com.ifrs.meuifpoa.model.Perfil;
import br.com.ifrs.meuifpoa.utils.Constants;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    /** Binding para a Activity. */
    private ActivityLoginBinding binding;
    /** Instância do Firebase Auth. */
    private FirebaseAuth mAuth;
    /** Cliente para o login com Google. */
    private SignInClient oneTapClient;
    /** Requisição para o login com Google. */
    private BeginSignInRequest signInRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configurarGoogleSignIn();
        binding.btnGoogleLogin.setOnClickListener(v -> iniciarGoogleSignIn());
    }

    /** Configura o Google Sign-In. */
    private void configurarGoogleSignIn() {
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                .setAutoSelectEnabled(false)
                .build();
        oneTapClient = Identity.getSignInClient(this);
    }

    /** Método para iniciar o login com Google. */
    private void iniciarGoogleSignIn() {
        if (mAuth.getCurrentUser() != null) {
            // Já autenticado, prossegue para a próxima tela
            tratarLoginBemSucedido();
            return;
        }

        mostrarProgressBar();
        binding.btnGoogleLogin.setEnabled(false);

        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(
                                result.getPendingIntent().getIntentSender()).build();
                        signInLauncher.launch(intentSenderRequest);
                    } catch (Exception e) {
                        handleError("Erro ao iniciar IntentSenderRequest", e);
                    }
                })
                .addOnFailureListener(this, e -> {
                    handleError("Falha ao iniciar o Sign-In", e);
                });
    }

    /** Launcher para o resultado do login com Google. */
    private final ActivityResultLauncher<IntentSenderRequest> signInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
                binding.btnGoogleLogin.setEnabled(true);

                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    handleGoogleSignInResult(result.getData());
                } else {
                    mostrarMensagemErro(getString(R.string.erro_login_google));
                }
            });

    /** Método para tratar o resultado do login com Google. */
    private void handleGoogleSignInResult(Intent data) {
        try {
            SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
            String idToken = credential.getGoogleIdToken();
            String email = credential.getId();

            if (email.endsWith(Constants.DOMINIO_EMAIL)) {
                if (idToken != null) {
                    AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                    autenticarComFirebase(firebaseCredential);
                } else {
                    mostrarErroLoginGoogle();
                }
            } else {
                mostrarErroEmailInvalido();
            }
        } catch (Exception e) {
            handleError("Erro ao processar resultado do login com Google", e);
        }
    }

    /** Autentica com Firebase usando o ID Token fornecido. */
    private void autenticarComFirebase(AuthCredential firebaseCredential) {
        mAuth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this, task -> {
                    esconderProgressBar();
                    if (task.isSuccessful()) {
                        salvarDadosUsuario(mAuth.getUid());
                        obterTokenFirebase();
                    } else {
                        mostrarErroLoginGoogle();
                    }
                });
    }

    /** Obtém o token de autenticação do Firebase após o login. */
    private void obterTokenFirebase() {
        FirebaseUser usuario = mAuth.getCurrentUser();
        if (usuario != null) {
            usuario.getIdToken(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            tratarLoginBemSucedido();
                        } else {
                            mostrarMensagemErro(getString(R.string.erro_obter_token_firebase));
                        }
                    });
        }
    }

    /** Tratamento de login bem-sucedido. */
    private void tratarLoginBemSucedido() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    /** Salva os dados do usuário no Firestore. */
    private void salvarDadosUsuario(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser usuario = mAuth.getCurrentUser();
        if (usuario != null) {
            db.collection("usuarios").document(uid).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().exists()) {
                                Perfil user = new Perfil(usuario.getDisplayName(), usuario.getEmail(), null, null, null, null, null);
                                db.collection("usuarios").document(uid).set(user)
                                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Documento criado com sucesso!"))
                                        .addOnFailureListener(e -> Log.w(TAG, "Erro ao criar o documento", e));
                            }
                        } else {
                            Log.w(TAG, "Erro ao verificar o documento", task.getException());
                        }
                    });
        }
    }

    /** Exibe a barra de progresso. */
    private void mostrarProgressBar() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnGoogleLogin.setEnabled(false);
    }

    /** Esconde a barra de progresso. */
    private void esconderProgressBar() {
        binding.progressBar.setVisibility(View.GONE);
        binding.btnGoogleLogin.setEnabled(true);
    }

    /** Exibe uma mensagem de erro. */
    private void mostrarMensagemErro(String mensagem) {
        Snackbar.make(binding.getRoot(), mensagem, Snackbar.LENGTH_SHORT).show();
    }

    /** Mostra erro de login com Google. */
    private void mostrarErroLoginGoogle() {
        esconderProgressBar();
        binding.btnGoogleLogin.setEnabled(true);
        mostrarMensagemErro(getString(R.string.erro_login_google));
    }

    /** Mostra erro de email inválido. */
    private void mostrarErroEmailInvalido() {
        esconderProgressBar();
        binding.btnGoogleLogin.setEnabled(true);
        mostrarMensagemErro(getString(R.string.erro_email_invalido));
    }

    /** Lida com erros genéricos durante o login. */
    private void handleError(String mensagem, Exception e) {
        Log.e(TAG, mensagem, e);
        esconderProgressBar();
        binding.btnGoogleLogin.setEnabled(true);
        mostrarMensagemErro(getString(R.string.erro_login_google));
    }
}
