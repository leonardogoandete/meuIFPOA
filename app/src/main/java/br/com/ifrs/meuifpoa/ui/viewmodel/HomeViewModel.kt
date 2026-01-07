package br.com.ifrs.meuifpoa.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ifrs.meuifpoa.model.Documento.DocumentoRequest
import br.com.ifrs.meuifpoa.model.Documento.DocumentoResponse
import br.com.ifrs.meuifpoa.model.Perfil
import br.com.ifrs.meuifpoa.retrofit.DocumentoRetrofit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.awaitResponse
import java.io.IOException

data class HomeUiState(
    val perfil: Perfil? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showPasswordDialog: Boolean = false,
    val documentLoadingState: Map<String, Boolean> = emptyMap(),
    val documentResult: DocumentoResponse? = null
)

class HomeViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()
    private val documentoService = DocumentoRetrofit.documentoService // Use singleton

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var tipoDocumentoPendente: String? = null

    fun carregarPerfil() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            mAuth.currentUser?.uid?.let {
                try {
                    val document = db.collection("usuarios").document(it).get(Source.DEFAULT).await()
                    _uiState.update { state -> state.copy(perfil = document.toObject(Perfil::class.java), isLoading = false) }
                } catch (e: Exception) {
                    _uiState.update { state -> state.copy(error = "Erro ao carregar perfil", isLoading = false) }
                }
            } ?: _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onEmitirDocumentoClick(tipoDocumento: String) {
        tipoDocumentoPendente = tipoDocumento
        _uiState.update { it.copy(showPasswordDialog = true) }
    }

    fun onDialogDismiss() {
        _uiState.update { it.copy(showPasswordDialog = false) }
        tipoDocumentoPendente = null
    }

    fun onPasswordConfirm(senha: String, context: Context) {
        onDialogDismiss()
        tipoDocumentoPendente?.let {
            emitirDocumento(it, senha, context)
        }
    }

    private fun emitirDocumento(tipoDocumento: String, senha: String, context: Context) {
        viewModelScope.launch {
            setDocumentLoading(tipoDocumento, true)
            try {
                val token = mAuth.currentUser?.getIdToken(true)?.await()?.token ?: ""
                val request = DocumentoRequest(tipoDocumento, senha)
                val response = documentoService.obterDocumento("Bearer $token", request).awaitResponse()
                if (response.isSuccessful) {
                    _uiState.update { it.copy(documentResult = response.body()) }
                } else {
                    _uiState.update { it.copy(error = "Erro ao emitir documento") }
                }
            } catch (e: IOException) {
                _uiState.update { it.copy(error = "Falha na conexão") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Ocorreu um erro") }
            }
            setDocumentLoading(tipoDocumento, false)
        }
    }

    private fun setDocumentLoading(tipoDocumento: String, isLoading: Boolean) {
        val newMap = _uiState.value.documentLoadingState.toMutableMap()
        newMap[tipoDocumento] = isLoading
        _uiState.update { it.copy(documentLoadingState = newMap) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearDocumentResult() {
        _uiState.update { it.copy(documentResult = null) }
    }
}
