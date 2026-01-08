package br.com.ifrs.meuifpoa.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ifrs.meuifpoa.model.Nota
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NotasViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()

    private val _notas = MutableStateFlow<List<Nota>?>(null)
    val notas: StateFlow<List<Nota>?> = _notas

    fun carregarNotas() {
        viewModelScope.launch {
            mAuth.currentUser?.uid?.let { userId ->
                try {
                    val document = db.collection("usuarios").document(userId).get(Source.DEFAULT).await()
                    val perfil = document.toObject(br.com.ifrs.meuifpoa.model.Perfil::class.java)
                    _notas.value = perfil?.notas ?: emptyList()
                } catch (e: Exception) {
                    // Tratar erro
                    e.printStackTrace()
                    _notas.value = emptyList()
                }
            } ?: run {
                _notas.value = emptyList()
            }
        }
    }
}
