package br.com.ifrs.meuifpoa.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

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
    }

    // Configurações para o Google Sign-In
    /** Configura o Google Sign-In. */
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

    // Iniciar login com Google
    /** Metodo para iniciar o login com Google. */
    private void iniciarGoogleSignIn() {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(
                                result.getPendingIntent().getIntentSender()).build();
                        signInLauncher.launch(intentSenderRequest);
                    } catch (Exception e) {
                        mostrarMensagemErro(getString(R.string.erro_login_google));
                    }
                })
                .addOnFailureListener(this, e -> mostrarMensagemErro(getString(R.string.erro_login_google)));
    }


    // Resultado do login com Google
    /** Método para tratar o resultado do login com Google.
        * @param data Intent com os dados do login. */
    private void handleGoogleSignInResult(Intent data) {
        try {
            SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
            String idToken = credential.getGoogleIdToken();
            String email = credential.getId();

            // Verifica se o email é do domínio permitido
            // Add Dialog loading ao logar:
            // https://www.youtube.com/watch?v=oRiHBLsVQgA
            if (email.endsWith(Constants.DOMINIO_EMAIL)) {
                if (idToken != null) {
                    AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                    mAuth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {
                                    salvarDadosUsuario(mAuth.getUid());
                                    obterTokenFirebase();
                                } else {
                                    mostrarMensagemErro(getString(R.string.erro_login_google));
                                }
                            });
                } else {
                    mostrarMensagemErro(getString(R.string.erro_login_google));
                }
            } else {
                mostrarMensagemErro(getString(R.string.erro_email_invalido));
            }
        } catch (Exception e) {
            mostrarMensagemErro(getString(R.string.erro_login_google));
        }
    }

    // Obter o token do Firebase após login
    /** Método para obter o token de autenticação do Firebase após o login. */
    private void obterTokenFirebase() {
        FirebaseUser usuario = mAuth.getCurrentUser();
        if (usuario != null) {
            usuario.getIdToken(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String firebaseToken = task.getResult().getToken();
                            salvarToken(firebaseToken);
                            tratarLoginBemSucedido();
                        } else {
                            mostrarMensagemErro(getString(R.string.erro_obter_token_firebase));
                        }
                    });
        }
    }

    // Salvar o token para uso posterior
    /** Método para salvar o token de autenticação para uso posterior.
        * @param token Token de autenticação a ser salvo. */
    private void salvarToken(String token) {
        SharedPreferences preferencias = getSharedPreferences("loginSigaa", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("token", "Bearer " + token);
        editor.apply();
    }

    // Tratar login bem-sucedido e iniciar nova atividade
    /** Método para tratar o login bem-sucedido e iniciar a atividade principal. */
    private void tratarLoginBemSucedido() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //Salvar dados do Usuário no FireStore
    /** Método para salvar os dados do usuário no Firestore.
        * @param uid Identificador único do usuário. */
    private void salvarDadosUsuario(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser usuario = mAuth.getCurrentUser();

        if (usuario != null) {
            // Referência para o documento do usuário
            db.collection("usuarios").document(uid).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Verifica se o documento já existe
                            if (task.getResult().exists()) {
                                Log.d("TAG", "Documento já existe para o usuário.");
                            } else {
                                // Se o documento não existir, cria um novo
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

    // Exibir mensagens de erro
    /** Método para exibir mensagens de erro.
        * @param mensagem Mensagem de erro a ser exibida. */
    private void mostrarMensagemErro(String mensagem) {
        Snackbar.make(binding.getRoot(), mensagem, Snackbar.LENGTH_SHORT).show();
    }
}
