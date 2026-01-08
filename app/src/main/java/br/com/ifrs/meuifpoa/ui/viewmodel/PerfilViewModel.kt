package br.com.ifrs.meuifpoa.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class PerfilViewModel(
    private val db: FirebaseFirestore,
    private val mAuth: FirebaseAuth,
    private val syncManager: SyncManager,
    private val syncService: SyncService
) : ViewModel() {

    private val _perfil = MutableStateFlow<Perfil?>(null)
    val perfil: StateFlow<Perfil?> = _perfil

    fun carregarPerfil() {
        viewModelScope.launch {
            mAuth.currentUser?.uid?.let { userId ->
                carregarPerfilDoCache(userId)

                if (syncManager.deveSincronizar()) {
                    try {
                        val response = syncService.sincronizar()
                        if (response.erro == null) {
                            syncManager.registrarSincronizacaoConcluida()
                            carregarPerfilDoCache(userId) // Recarrega após a sincronização
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private suspend fun carregarPerfilDoCache(userId: String) {
        try {
            val document = db.collection("usuarios").document(userId).get(Source.CACHE).await()
            if (document.exists()) {
                _perfil.value = document.toObject(Perfil::class.java)
            }
        } catch (e: Exception) {
            // O cache pode estar vazio ou indisponível
        }
    }

    fun sair(context: Context) {
        mAuth.signOut()
        syncManager.limparPreferenciasDeSincronizacao(context)
    }
}