package br.com.ifrs.meuifpoa.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ifrs.meuifpoa.model.Edital
import br.com.ifrs.meuifpoa.model.Noticia
import br.com.ifrs.meuifpoa.retrofit.EditaisRetrofit
import br.com.ifrs.meuifpoa.retrofit.NoticiasRetrofit
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

data class NoticiasUiState(
    val noticias: List<Noticia> = emptyList(),
    val editais: List<Edital> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val limit: Int = 50,
    val selectedCategory: String = "noticia" // "noticia" or "edital"
)

class NoticiasViewModel : ViewModel() {

    private val noticiasService = NoticiasRetrofit.noticiasService
    private val editaisService = EditaisRetrofit.editaisService

    private val _uiState = MutableStateFlow(NoticiasUiState())
    val uiState: StateFlow<NoticiasUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadDataForCategory()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400L)
            loadDataForCategory(filtro = query)
        }
    }

    fun onLimitChange(newLimit: Int) {
        _uiState.update { it.copy(limit = newLimit) }
        loadDataForCategory(limite = newLimit)
    }

    fun onCategoryChange(newCategory: String) {
        _uiState.update { it.copy(selectedCategory = newCategory, searchQuery = "") } // Reset search on category change
        loadDataForCategory(categoria = newCategory, filtro = null)
    }

    fun loadDataForCategory(
        filtro: String? = _uiState.value.searchQuery,
        limite: Int = _uiState.value.limit,
        categoria: String? = _uiState.value.selectedCategory
    ) {
        val query = filtro?.ifEmpty { null }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                if (categoria == "noticia") {
                    val noticias = noticiasService.listarNoticias(query, limite, categoria)
                    _uiState.update { it.copy(noticias = noticias, editais = emptyList(), isLoading = false) }
                } else {
                    val editais = editaisService.listarEditais(query, limite)
                    _uiState.update { it.copy(editais = editais, noticias = emptyList(), isLoading = false) }
                }
            } catch (e: CancellationException) {
                // This is an expected exception when a job is cancelled.
                // We re-throw it to let the coroutine system handle it. Do not treat as an error.
                throw e
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
