package br.com.ifrs.meuifpoa.ui.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ifrs.meuifpoa.R
import br.com.ifrs.meuifpoa.model.Perfil
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false,
)

class LoginViewModel(
    private val mAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun iniciarLogin(context: Context) {
        val credentialManager = CredentialManager.create(context)
        val serverClientId = context.getString(R.string.default_web_client_id)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val result = credentialManager.getCredential(context as Activity, request)
                val credential = result.credential

                Log.d("LoginViewModel", "Credential type returned: ${credential::class.java.name}")

                var idToken: String? = null

                if (credential is GoogleIdTokenCredential) {
                    idToken = credential.idToken
                } else if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    // Fallback for devices that return a CustomCredential
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        idToken = googleIdTokenCredential.idToken
                    } catch (e: Exception) {
                        handleError("Erro ao extrair token da credencial customizada: ${e.message}")
                    }
                }

                if (idToken != null) {
                    withContext(Dispatchers.IO) {
                        firebaseAuthWithGoogle(idToken)
                    }
                } else {
                    handleError("Credencial inesperada ou inválida: ${credential::class.java.name}")
                }

            } catch (e: GetCredentialException) {
                handleError("Falha ao obter credencial: ${e.message}")
            } catch (e: Exception) {
                handleError("Ocorreu um erro inesperado: ${e.message}")
            }
        }
    }

    private suspend fun firebaseAuthWithGoogle(idToken: String) {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            mAuth.signInWithCredential(credential).await()
            val user = mAuth.currentUser
            if (user != null) {
                salvarDadosUsuario(user)
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(loginSuccess = true, isLoading = false)
                }
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

    private suspend fun handleError(message: String) {
        withContext(Dispatchers.Main) {
            _uiState.value = _uiState.value.copy(error = message, isLoading = false)
        }
    }

    fun clearError() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(error = null)
        }
    }
}