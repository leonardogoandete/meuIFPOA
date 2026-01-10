package br.com.ifrs.meuifpoa.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class PerfilViewModel(
    private val syncService: SyncService
) : ViewModel() {

    private val _perfil = MutableStateFlow<Perfil?>(null)
    val perfil: StateFlow<Perfil?> = _perfil.asStateFlow()

    fun carregarPerfil() {
        viewModelScope.launch {
            val userId = Firebase.auth.currentUser?.uid
            if (userId == null) {
                // Tratar o caso de usuário não logado
                return@launch
            }

            carregarPerfilDoCache(userId)

            // TODO: Implement sync logic
            try {
                val response = withContext(Dispatchers.IO) {
                    // TODO: Obter a senha do SIGAA de forma segura
                    syncService.sincronizar("SENHA_AQUI")
                }
                if (response.erro == null) {
                    // TODO: Register sync completed
                    carregarPerfilDoCache(userId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun carregarPerfilDoCache(userId: String) {
        try {
            val document = withContext(Dispatchers.IO) {
                Firebase.firestore.collection("usuarios").document(userId).get()
            }
            if (document.exists) {
                _perfil.value = document.data<Perfil>()
            }
        } catch (e: Exception) {
            // O cache pode estar vazio ou indisponível
        }
    }

    fun sair() {
        viewModelScope.launch {
            Firebase.auth.signOut()
            // TODO: Clear sync preferences
        }
    }
}