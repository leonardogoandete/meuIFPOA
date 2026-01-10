package br.com.ifrs.meuifpoa.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ifrs.meuifpoa.model.Perfil
import br.com.ifrs.meuifpoa.client.service.DocumentoService
import br.com.ifrs.meuifpoa.model.Documento.Documento
import br.com.ifrs.meuifpoa.model.Documento.DocumentoRequest
import br.com.ifrs.meuifpoa.model.Documento.DocumentoResponse
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

data class HomeUiState(
    val perfil: Perfil? = null,
    val isProfileLoading: Boolean = false,
    val isDocumentLoading: Boolean = false,
    val loadingDocumentType: String? = null,
    val error: String? = null,
    val showPasswordDialog: Boolean = false,
    val documentResult: DocumentoResponse? = null
)

class HomeViewModel(
    private val documentoService: DocumentoService
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var tipoDocumentoPendente: String? = null

    fun carregarPerfil() {
        viewModelScope.launch {
            _uiState.update { it.copy(isProfileLoading = true) }
            try {
                val userId = Firebase.auth.currentUser?.uid
                if (userId != null) {
                    val document = withContext(Dispatchers.IO) {
                        Firebase.firestore.collection("usuarios").document(userId).get()
                    }
                    _uiState.update { state -> state.copy(perfil = document.data(), isProfileLoading = false) }
                } else {
                    _uiState.update { it.copy(isProfileLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { state -> state.copy(error = "Erro ao carregar perfil", isProfileLoading = false) }
            }
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

    fun onPasswordConfirm(senha: String) {
        _uiState.update { it.copy(showPasswordDialog = false) }
        tipoDocumentoPendente?.let {
            emitirDocumento(it, senha)
        }
    }

    private fun emitirDocumento(tipoDocumento: String, senha: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDocumentLoading = true, loadingDocumentType = tipoDocumento) }
            try {
                val request = DocumentoRequest(tipoDocumento, senha)
                val response = withContext(Dispatchers.IO) {
                    documentoService.obterDocumento(request)
                }
                _uiState.update { it.copy(documentResult = response) }
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