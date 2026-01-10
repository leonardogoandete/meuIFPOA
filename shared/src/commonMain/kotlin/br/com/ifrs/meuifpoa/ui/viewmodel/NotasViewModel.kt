package br.com.ifrs.meuifpoa.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ifrs.meuifpoa.model.Nota
import br.com.ifrs.meuifpoa.model.Perfil
import br.com.ifrs.meuifpoa.client.service.SyncService
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotasViewModel(
    private val syncService: SyncService
) : ViewModel() {

    private val _notas = MutableStateFlow<List<Nota>?>(null)
    val notas: StateFlow<List<Nota>?> = _notas.asStateFlow()

    fun carregarNotas() {
        viewModelScope.launch {
            val userId = Firebase.auth.currentUser?.uid
            if (userId == null) {
                _notas.value = emptyList()
                return@launch
            }

            carregarNotasDoCache(userId)

            // TODO: Implement sync logic
            try {
                val response = withContext(Dispatchers.IO) {
                    // TODO: Obter a senha do SIGAA de forma segura
                    syncService.sincronizar("SENHA_AQUI")
                }
                if (response.erro == null) {
                    // TODO: Register sync completed
                    carregarNotasDoCache(userId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun carregarNotasDoCache(userId: String) {
        try {
            val document = withContext(Dispatchers.IO) {
                Firebase.firestore.collection("usuarios").document(userId).get()
            }
            if (document.exists) {
                val perfil = document.data<Perfil>()
                _notas.value = perfil.notas
            } else {
                _notas.value = emptyList()
            }
        } catch (e: Exception) {
            _notas.value = emptyList()
        }
    }
}