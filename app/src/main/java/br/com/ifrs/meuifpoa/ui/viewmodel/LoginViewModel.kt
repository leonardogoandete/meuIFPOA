package br.com.ifrs.meuifpoa.ui.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ifrs.meuifpoa.R
import br.com.ifrs.meuifpoa.model.Perfil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false,
    val googleSignInIntent: Intent? = null
)

class LoginViewModel : ViewModel() {

    private val mAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private lateinit var googleSignInClient: GoogleSignInClient

    fun configureGoogleSignIn(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun startGoogleSignIn() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            googleSignInClient.signOut().await()
            _uiState.value = _uiState.value.copy(googleSignInIntent = googleSignInClient.signInIntent)
        }
    }

    fun onGoogleSignInResult(data: Intent?) {
        viewModelScope.launch {
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                } else {
                    handleError("Conta do Google não encontrada.")
                }
            } catch (e: ApiException) {
                handleError("Falha no login com Google: ${e.message}")
            }
        }
    }

    private suspend fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        try {
            mAuth.signInWithCredential(credential).await()
            val user = mAuth.currentUser
            if (user != null) {
                salvarDadosUsuario(user)
                _uiState.value = _uiState.value.copy(loginSuccess = true, isLoading = false)
            } else {
                handleError("Usuário do Firebase não encontrado após login.")
            }
        } catch (e: Exception) {
            handleError("Falha na autenticação com Firebase: ${e.message}")
        }
    }

    private suspend fun salvarDadosUsuario(user: FirebaseUser) {
        val docRef = db.collection("usuarios").document(user.uid)
        val snapshot = docRef.get().await()
        if (!snapshot.exists()) {
            val perfil = Perfil(nomeDocente = user.displayName, curso = user.email)
            docRef.set(perfil).await()
        }
    }

    private fun handleError(message: String) {
        _uiState.value = _uiState.value.copy(error = message, isLoading = false, googleSignInIntent = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun resetGoogleSignInIntent() {
        _uiState.value = _uiState.value.copy(googleSignInIntent = null)
    }
}
