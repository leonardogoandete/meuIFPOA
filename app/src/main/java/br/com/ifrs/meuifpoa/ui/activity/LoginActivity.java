package br.com.ifrs.meuifpoa.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
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

/**
 * A classe `LoginActivity` é responsável por gerenciar a tela de login do aplicativo.
 * Ela permite que o usuário faça login com sua conta do Google e salva o token de autenticação
 * para uso posterior.
 */
public class LoginActivity extends AppCompatActivity {

    /** Binding para a Activity. */
    private ActivityLoginBinding binding;
    /** Instância do Firebase Auth. */
    private FirebaseAuth mAuth;
    /** Cliente para o login com Google. */
    private SignInClient oneTapClient;
    /** Requisição para o login com Google. */
    private BeginSignInRequest signInRequest;

    // Launcher para o resultado do login com Google
    private final ActivityResultLauncher<IntentSenderRequest> signInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    handleGoogleSignInResult(result.getData());
                } else {
                    esconderProgressBar();
                    mostrarMensagemErro(getString(R.string.erro_login_google));
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configurarGoogleSignIn();
        binding.btnGoogleLogin.setOnClickListener(v -> iniciarGoogleSignIn());

        checkAndRefreshToken(); // Verifica e renova o token no início, se necessário
    }

    /**
     * Configura o Google Sign-In.
     */
    private void configurarGoogleSignIn() {
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(false) // Permite escolha de conta
                        .build())
                .setAutoSelectEnabled(true)
                .build();

        oneTapClient = Identity.getSignInClient(this);
    }

    /**
     * Método para iniciar o login com Google.
     */
    private void iniciarGoogleSignIn() {
        mostrarProgressBar();
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(
                                result.getPendingIntent().getIntentSender()).build();
                        signInLauncher.launch(intentSenderRequest);
                    } catch (Exception e) {
                        esconderProgressBar();
                        mostrarMensagemErro(getString(R.string.erro_login_google));
                    }
                })
                .addOnFailureListener(this, e -> {
                    esconderProgressBar();
                    mostrarMensagemErro(getString(R.string.erro_login_google));
                });
    }

    /**
     * Método para tratar o resultado do login com Google.
     *
     * @param data Intent com os dados do login.
     */
    private void handleGoogleSignInResult(Intent data) {
        try {
            SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
            String idToken = credential.getGoogleIdToken();
            String email = credential.getId();

            if (email.endsWith(Constants.DOMINIO_EMAIL)) {
                if (idToken != null) {
                    AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                    mAuth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {
                                    salvarDadosUsuario(mAuth.getUid());
                                    obterTokenFirebase();
                                } else {
                                    esconderProgressBar();
                                    mostrarMensagemErro(getString(R.string.erro_login_google));
                                }
                            });
                } else {
                    esconderProgressBar();
                    mostrarMensagemErro(getString(R.string.erro_login_google));
                }
            } else {
                esconderProgressBar();
                mostrarMensagemErro(getString(R.string.erro_email_invalido));
            }
        } catch (Exception e) {
            esconderProgressBar();
            mostrarMensagemErro(getString(R.string.erro_login_google));
        }
    }

    /**
     * Método para obter o token de autenticação do Firebase após o login.
     */
    private void obterTokenFirebase() {
        FirebaseUser usuario = mAuth.getCurrentUser();
        if (usuario != null) {
            usuario.getIdToken(true)
                    .addOnCompleteListener(task -> {
                        esconderProgressBar();
                        if (task.isSuccessful()) {
                            String firebaseToken = task.getResult().getToken();
                            salvarToken(firebaseToken);
                            tratarLoginBemSucedido();
                        } else {
                            mostrarMensagemErro(getString(R.string.erro_obter_token_firebase));
                        }
                    });
        } else {
            esconderProgressBar();
        }
    }

    /**
     * Método para salvar o token de autenticação para uso posterior.
     *
     * @param token Token de autenticação a ser salvo.
     */
    private void salvarToken(String token) {
        SharedPreferences preferencias = getSharedPreferences("loginSigaa", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("token", "Bearer " + token);
        editor.apply();
    }

    /**
     * Método para tratar o login bem-sucedido e iniciar a atividade principal.
     */
    private void tratarLoginBemSucedido() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Método para salvar os dados do usuário no Firestore.
     *
     * @param uid Identificador único do usuário.
     */
    private void salvarDadosUsuario(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser usuario = mAuth.getCurrentUser();

        if (usuario != null) {
            db.collection("usuarios").document(uid).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                Log.d("TAG", "Documento já existe para o usuário.");
                            } else {
                                Perfil user = new Perfil(mAuth.getCurrentUser().getDisplayName(), mAuth.getCurrentUser().getEmail(),null, null, null, null, null);
                                db.collection("usuarios").document(uid).set(user)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("TAG", "Documento criado com sucesso!");
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.w("TAG", "Erro ao criar o documento", e);
                                        });
                            }
                        } else {
                            Log.w("TAG", "Erro ao verificar o documento", task.getException());
                        }
                    });
        }
    }

    /** Launcher para o resultado do login silencioso com Google. */
    private final ActivityResultLauncher<IntentSenderRequest> silentSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    handleGoogleSignInResult(result.getData());
                } else {
                    mostrarMensagemErro(getString(R.string.erro_login_google));
                }
            });

    /**
     * Realiza um login silencioso usando o Google Sign-In para renovar o token de autenticação.
     * Caso o token de ID não seja renovado automaticamente, esse método tenta obter um novo token sem interação do usuário.
     * Se falhar, redireciona o usuário para um novo login completo.
     */
    private void silentSignIn() {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build();
                        silentSignInLauncher.launch(intentSenderRequest);
                    } catch (Exception e) {
                        mostrarMensagemErro(getString(R.string.erro_login_google));
                    }
                })
                .addOnFailureListener(this, e -> iniciarGoogleSignIn());
    }

    /**
     * Verifica se o token de autenticação é válido e, se necessário, tenta renová-lo usando silent sign-in.
     */
    private void checkAndRefreshToken() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // O usuário está autenticado; podemos tentar obter o token atualizado
            currentUser.getIdToken(true).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Token atualizado com sucesso
                    String updatedToken = task.getResult().getToken();
                    salvarToken(updatedToken);
                } else {
                    // Falha ao obter token; tenta silent sign-in
                    silentSignIn();
                }
            });
        } else {
            // Usuário não autenticado, tenta silent sign-in para revalidar o token
            silentSignIn();
        }
    }


    /** Método para exibir a barra de progresso. */
    private void mostrarProgressBar() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnGoogleLogin.setEnabled(false);
    }

    /** Método para esconder a barra de progresso. */
    private void esconderProgressBar() {
        binding.progressBar.setVisibility(View.GONE);
        binding.btnGoogleLogin.setEnabled(true);
    }

    /**
     * Método para exibir mensagens de erro.
     *
     * @param mensagem Mensagem de erro a ser exibida.
     */
    private void mostrarMensagemErro(String mensagem) {
        Snackbar.make(binding.getRoot(), mensagem, Snackbar.LENGTH_SHORT).show();
    }
}
