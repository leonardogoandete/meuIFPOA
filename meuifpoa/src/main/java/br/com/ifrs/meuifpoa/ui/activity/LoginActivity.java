package br.com.ifrs.meuifpoa.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
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
 * A classe LoginActivity é responsável por gerenciar o fluxo de login do usuário com Google Sign-In
 * e autenticação no Firebase.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001; // Código de solicitação para o Sign-In do Google

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;

    /**
     * Método chamado quando a atividade é criada.
     *
     * @param savedInstanceState Contém o estado salvo da atividade se ela foi previamente fechada.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        configurarGoogleSignIn();

        // Configura o listener para o botão de login com Google
        binding.btnGoogleLogin.setOnClickListener(v -> iniciarGoogleSignIn());
    }

    /**
     * Configura as opções de login com Google.
     */
    private void configurarGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    /**
     * Inicia o processo de login com Google.
     * Sempre exibe a seleção de conta antes de prosseguir com o login.
     */
    private void iniciarGoogleSignIn() {
        mostrarProgressBar();
        binding.btnGoogleLogin.setEnabled(false);

        // Força logout para garantir que a escolha de conta seja sempre exibida
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    /**
     * Método chamado quando o resultado de uma atividade é recebido.
     *
     * @param requestCode Código de solicitação passado na chamada para startActivityForResult.
     * @param resultCode Código de resultado retornado pela atividade filha.
     * @param data Dados de retorno da atividade filha.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                if (account != null) {
                    // Verificação do domínio do email
                    String email = account.getEmail();
                    if (email != null && email.endsWith(Constants.DOMINIO_EMAIL)) {
                        firebaseAuthWithGoogle(account.getIdToken());
                    } else {
                        // Email com domínio inválido
                        mostrarMensagemErro("O domínio do email não é permitido.");
                        esconderProgressBar();
                        binding.btnGoogleLogin.setEnabled(true);
                    }
                }
            } catch (ApiException e) {
                handleError("Erro ao fazer login com Google", e);
            }
        }
    }

    /**
     * Autentica o usuário com Firebase usando o token de ID fornecido pelo Google.
     *
     * @param idToken Token de ID recebido após o login com Google.
     */
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    esconderProgressBar();
                    if (task.isSuccessful()) {
                        tratarLoginBemSucedido();
                        salvarDadosUsuario(mAuth.getCurrentUser().getUid());
                    } else {
                        mostrarMensagemErro(getString(R.string.erro_login_google));
                    }
                });
    }

    /**
     * Trata o login bem-sucedido redirecionando o usuário para a atividade principal.
     */
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

    /**
     * Exibe a barra de progresso.
     */
    private void mostrarProgressBar() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnGoogleLogin.setEnabled(false);
    }

    /**
     * Esconde a barra de progresso.
     */
    private void esconderProgressBar() {
        binding.progressBar.setVisibility(View.GONE);
        binding.btnGoogleLogin.setEnabled(true);
    }

    /**
     * Exibe uma mensagem de erro usando um Snackbar.
     *
     * @param mensagem Mensagem de erro a ser exibida.
     */
    private void mostrarMensagemErro(String mensagem) {
        Snackbar.make(binding.getRoot(), mensagem, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Lida com erros ocorridos durante o processo de login e autenticação.
     *
     * @param mensagem Mensagem de erro a ser exibida.
     * @param e Exceção lançada durante o processo.
     */
    private void handleError(String mensagem, Exception e) {
        Log.e(TAG, mensagem, e);
        esconderProgressBar();
        binding.btnGoogleLogin.setEnabled(true);
        mostrarMensagemErro(getString(R.string.erro_login_google));
    }
}
