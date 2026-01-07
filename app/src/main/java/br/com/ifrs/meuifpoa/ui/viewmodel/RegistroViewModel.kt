package br.com.ifrs.meuifpoa.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.caelum.stella.validation.CPFValidator
import br.com.ifrs.meuifpoa.model.Registro
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegistroViewModel : ViewModel() {

    private val mAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _registroError = MutableStateFlow<String?>(null)
    val registroError: StateFlow<String?> = _registroError

    private val _registroSuccess = MutableStateFlow(false)
    val registroSuccess: StateFlow<Boolean> = _registroSuccess

    fun registrar(nome: String, cpf: String, email: String, senha: String) {
        if (!validarEntradas(nome, cpf, email, senha)) {
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _registroError.value = null
            try {
                val authResult = mAuth.createUserWithEmailAndPassword(email, senha).await()
                val uid = authResult.user?.uid
                if (uid != null) {
                    salvarDadosNoFirestore(uid, nome, cpf, email)
                    _registroSuccess.value = true
                } else {
                    _registroError.value = "Erro ao obter ID do usuário."
                }
            } catch (e: Exception) {
                _registroError.value = "Erro ao criar usuário: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun salvarDadosNoFirestore(uid: String, nome: String, cpf: String, email: String) {
        val registro = Registro(uid, nome, cpf, email)
        db.collection("usuarios").document(uid).set(registro)
    }

    private fun validarEntradas(nome: String, cpf: String, email: String, senha: String): Boolean {
        if (nome.isEmpty()) {
            _registroError.value = "Nome obrigatório"
            return false
        }
        if (cpf.isEmpty() || !CPFValidator().invalidMessagesFor(cpf).isEmpty()) {
            _registroError.value = if (cpf.isEmpty()) "CPF obrigatório" else "CPF inválido"
            return false
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _registroError.value = if (email.isEmpty()) "E-mail obrigatório" else "E-mail inválido"
            return false
        }
        if (senha.isEmpty() || senha.length < 6) {
            _registroError.value = if (senha.isEmpty()) "Senha obrigatória" else "Senha deve ter no mínimo 6 caracteres"
            return false
        }
        return true
    }

    fun clearError() {
        _registroError.value = null
    }
}
