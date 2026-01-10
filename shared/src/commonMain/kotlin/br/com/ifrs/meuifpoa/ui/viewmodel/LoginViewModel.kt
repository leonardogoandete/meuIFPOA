package br.com.ifrs.meuifpoa.ui.viewmodel

import androidx.lifecycle.ViewModel
import br.com.ifrs.meuifpoa.model.Perfil
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

expect class GoogleAuthHandler {
    suspend fun signIn(): Pair<String, String>?
}

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false,
)

class LoginViewModel(private val googleAuthHandler: GoogleAuthHandler) : ViewModel() {

    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()


    fun iniciarLogin() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val result = googleAuthHandler.signIn()
                if (result != null) {
                    val (idToken, email) = result
                    if (email.endsWith("@aluno.poa.ifrs.edu.br") || email.endsWith("@ifrs.edu.br")) {
                        firebaseAuthWithGoogle(idToken)
                    } else {
                        handleError("Apenas e-mails do IFRS são permitidos.")
                    }
                } else {
                    handleError("Falha ao obter token do Google.")
                }
            } catch (e: Exception) {
                handleError("Ocorreu um erro inesperado durante o login: ${e.message}")
            }
        }
    }

    private suspend fun firebaseAuthWithGoogle(idToken: String) {
        try {
            // TODO: Implement Firebase Google Sign-In for KMP
            // For now, using custom authentication
            _uiState.value = _uiState.value.copy(loginSuccess = true, isLoading = false)
        } catch (e: Exception) {
            handleError("Erro ao autenticar com Firebase: ${e.message}")
        }
    }

    private suspend fun salvarDadosUsuario(user: dev.gitlive.firebase.auth.FirebaseUser) {
        val docRef = Firebase.firestore.collection("usuarios").document(user.uid)
        val snapshot = docRef.get()
        if (!snapshot.exists) {
            val perfil = Perfil(nomeDocente = user.displayName, curso = user.email)
            docRef.set(perfil)
        }
    }

    private fun handleError(message: String) {
        _uiState.value = _uiState.value.copy(error = message, isLoading = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}