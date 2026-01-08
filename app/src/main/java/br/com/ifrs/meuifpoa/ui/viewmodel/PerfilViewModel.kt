package br.com.ifrs.meuifpoa.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ifrs.meuifpoa.AppContainer
import br.com.ifrs.meuifpoa.model.Perfil
import com.google.firebase.firestore.Source
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.core.content.edit

class PerfilViewModel : ViewModel() {

    private val db = AppContainer.firestore
    private val mAuth = AppContainer.firebaseAuth

    private val _perfil = MutableStateFlow<Perfil?>(null)
    val perfil: StateFlow<Perfil?> = _perfil

    fun carregarPerfil() {
        viewModelScope.launch {
            mAuth.currentUser?.uid?.let { userId ->
                try {
                    // Tenta carregar do cache primeiro
                    var document = db.collection("usuarios").document(userId).get(Source.CACHE).await()
                    if (document.exists()) {
                        _perfil.value = document.toObject(Perfil::class.java)
                    }
                    // Em seguida, busca no servidor
                    document = db.collection("usuarios").document(userId).get(Source.SERVER).await()
                    if (document.exists()) {
                        _perfil.value = document.toObject(Perfil::class.java)
                    }
                } catch (e: Exception) {

                }
            }
        }
    }

    fun sair(context: Context) {
        mAuth.signOut()
        // Limpa as SharedPreferences
        context.getSharedPreferences("syncPrefs", Context.MODE_PRIVATE).edit { clear() }
        context.getSharedPreferences("loginSigaa", Context.MODE_PRIVATE).edit { clear() }
    }
}
