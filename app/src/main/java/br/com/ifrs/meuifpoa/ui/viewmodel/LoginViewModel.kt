package br.com.ifrs.meuifpoa.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {

    private val mAuth = FirebaseAuth.getInstance()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    fun realizarLogin(email: String, senha: String, context: Context) {
        if (email.isEmpty() || senha.isEmpty()) {
            _loginError.value = "E-mail e senha não podem ser vazios"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _loginError.value = null
            try {
                mAuth.signInWithEmailAndPassword(email, senha).await()
                tratarLoginBemSucedido(context)
                _loginSuccess.value = true
            } catch (e: Exception) {
                _loginError.value = "Falha no login: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun tratarLoginBemSucedido(context: Context) {
        val usuario = mAuth.currentUser
        usuario?.getIdToken(true)?.await()?.token?.let { token ->
            val preferencias = context.getSharedPreferences("loginSigaa", Context.MODE_PRIVATE)
            preferencias.edit()
                .putString("token", "Bearer $token")
                .apply()
        }
    }

    fun clearError() {
        _loginError.value = null
    }
}
