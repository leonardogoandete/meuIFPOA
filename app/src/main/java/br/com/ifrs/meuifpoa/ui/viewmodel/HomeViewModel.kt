package br.com.ifrs.meuifpoa.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ifrs.meuifpoa.AppContainer
import br.com.ifrs.meuifpoa.model.Documento.DocumentoRequest
import br.com.ifrs.meuifpoa.model.Documento.DocumentoResponse
import br.com.ifrs.meuifpoa.model.Perfil
import br.com.ifrs.meuifpoa.retrofit.DocumentoRetrofit
import com.google.firebase.firestore.Source
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.awaitResponse
import java.io.IOException

// Simplified state
data class HomeUiState(
    val perfil: Perfil? = null,
    val isProfileLoading: Boolean = false,
    val isDocumentLoading: Boolean = false, // Global flag for any document
    val loadingDocumentType: String? = null, // To know which button shows the spinner
    val error: String? = null,
    val showPasswordDialog: Boolean = false,
    val documentResult: DocumentoResponse? = null
)

class HomeViewModel : ViewModel() {

    private val db = AppContainer.firestore
    private val mAuth = AppContainer.firebaseAuth
    private val documentoService = DocumentoRetrofit.documentoService

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var tipoDocumentoPendente: String? = null

    fun carregarPerfil() {
        viewModelScope.launch {
            _uiState.update { it.copy(isProfileLoading = true) }
            mAuth.currentUser?.uid?.let {
                try {
                    val document = db.collection("usuarios").document(it).get(Source.DEFAULT).await()
                    _uiState.update { state -> state.copy(perfil = document.toObject(Perfil::class.java), isProfileLoading = false) }
                } catch (e: Exception) {
                    _uiState.update { state -> state.copy(error = "Erro ao carregar perfil", isProfileLoading = false) }
                }
            } ?: _uiState.update { it.copy(isProfileLoading = false) }
        }
    }

    fun onEmitirDocumentoClick(tipoDocumento: String) {
        tipoDocumentoPendente = tipoDocumento
        _uiState.update { it.copy(showPasswordDialog = true) } // Just show the dialog
    }

    fun onDialogDismiss() {
        _uiState.update { it.copy(showPasswordDialog = false) }
        tipoDocumentoPendente = null
    }

    fun onPasswordConfirm(senha: String, context: Context) {
        _uiState.update { it.copy(showPasswordDialog = false) }
        tipoDocumentoPendente?.let {
            emitirDocumento(it, senha, context)
        }
    }

    private fun emitirDocumento(tipoDocumento: String, senha: String, context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDocumentLoading = true, loadingDocumentType = tipoDocumento) }
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
            } finally {
                _uiState.update { it.copy(isDocumentLoading = false, loadingDocumentType = null) } 
                tipoDocumentoPendente = null
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearDocumentResult() {
        _uiState.update { it.copy(documentResult = null) }
    }
}
