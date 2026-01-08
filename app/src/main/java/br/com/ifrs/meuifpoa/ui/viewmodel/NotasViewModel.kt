package br.com.ifrs.meuifpoa.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ifrs.meuifpoa.model.Nota
import br.com.ifrs.meuifpoa.model.Perfil
import br.com.ifrs.meuifpoa.retrofit.service.SyncService
import br.com.ifrs.meuifpoa.utils.SyncManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NotasViewModel(
    private val db: FirebaseFirestore,
    private val mAuth: FirebaseAuth,
    private val syncManager: SyncManager,
    private val syncService: SyncService
) : ViewModel() {

    private val _notas = MutableStateFlow<List<Nota>?>(null)
    val notas: StateFlow<List<Nota>?> = _notas

    fun carregarNotas() {
        viewModelScope.launch {
            mAuth.currentUser?.uid?.let { userId ->
                carregarNotasDoCache(userId)

                if (syncManager.deveSincronizar()) {
                    try {
                        val response = syncService.sincronizar()
                        if (response.erro == null) {
                            syncManager.registrarSincronizacaoConcluida()
                            carregarNotasDoCache(userId) // Recarrega após a sincronização
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } ?: run {
                _notas.value = emptyList()
            }
        }
    }

    private suspend fun carregarNotasDoCache(userId: String) {
        try {
            val document = db.collection("usuarios").document(userId).get(Source.CACHE).await()
            if (document.exists()) {
                val perfil = document.toObject(Perfil::class.java)
                _notas.value = perfil?.notas ?: emptyList()
            } else {
                _notas.value = emptyList()
            }
        } catch (e: Exception) {
            _notas.value = emptyList()
        }
    }
}