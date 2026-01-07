package br.com.ifrs.meuifpoa.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ifrs.meuifpoa.model.Noticia
import br.com.ifrs.meuifpoa.retrofit.NoticiasRetrofit
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

data class NoticiasUiState(
    val noticias: List<Noticia> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

class NoticiasViewModel : ViewModel() {

    // Use the singleton object directly
    private val noticiasService = NoticiasRetrofit.noticiasService

    private val _uiState = MutableStateFlow(NoticiasUiState())
    val uiState: StateFlow<NoticiasUiState> = _uiState.asStateFlow()

    private var fetchJob: Job? = null

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            delay(400L) // Debounce
            carregarNoticias(query)
        }
    }

    fun carregarNoticias(filtro: String? = _uiState.value.searchQuery, limite: Int = 50) {
        val query = filtro?.ifEmpty { null }
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val noticias = noticiasService.listarNoticias(query, limite)
                _uiState.update {
                    it.copy(
                        noticias = noticias,
                        isLoading = false
                    )
                }
            } catch (e: IOException) {
                _uiState.update { it.copy(error = "Falha na conexão", isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Ocorreu um erro: ${e.message}", isLoading = false) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
