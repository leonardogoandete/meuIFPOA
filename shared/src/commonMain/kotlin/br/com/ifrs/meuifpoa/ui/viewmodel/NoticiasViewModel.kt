package br.com.ifrs.meuifpoa.ui.viewmodel

import androidx.lifecycle.ViewModel
import br.com.ifrs.meuifpoa.client.service.EditaisService
import br.com.ifrs.meuifpoa.client.service.NoticiasService
import br.com.ifrs.meuifpoa.model.Edital
import br.com.ifrs.meuifpoa.model.Noticia
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NoticiasUiState(
    val noticias: List<Noticia> = emptyList(),
    val editais: List<Edital> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategory: String = "noticia",
    val searchQuery: String = "",
    val limit: Int = 10
)

class NoticiasViewModel(
    private val noticiasService: NoticiasService,
    private val editaisService: EditaisService
) : ViewModel() {

    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _uiState = MutableStateFlow(NoticiasUiState())
    val uiState: StateFlow<NoticiasUiState> = _uiState.asStateFlow()

    init {
        loadDataForCategory()
    }

    fun loadDataForCategory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                println("NoticiasViewModel: Carregando categoria ${_uiState.value.selectedCategory}")
                when (_uiState.value.selectedCategory) {
                    "noticia" -> {
                        println("NoticiasViewModel: Buscando notícias (limite: ${_uiState.value.limit})...")
                        val noticias = noticiasService.listarNoticias(
                            filtro = _uiState.value.searchQuery.takeIf { it.isNotBlank() },
                            limite = _uiState.value.limit
                        )
                        println("NoticiasViewModel: Recebidas ${noticias.size} notícias")
                        _uiState.update { it.copy(noticias = noticias, isLoading = false) }
                    }
                    "edital" -> {
                        println("NoticiasViewModel: Buscando editais (limite: ${_uiState.value.limit})...")
                        val editais = editaisService.listarEditais(
                            filtro = _uiState.value.searchQuery.takeIf { it.isNotBlank() },
                            limite = _uiState.value.limit
                        )
                        println("NoticiasViewModel: Recebidos ${editais.size} editais")
                        _uiState.update { it.copy(editais = editais, isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                println("NoticiasViewModel: Erro ao carregar - ${e.message}")
                e.printStackTrace()
                _uiState.update { it.copy(error = e.message ?: "Erro ao carregar dados", isLoading = false) }
            }
        }
    }

    fun onCategoryChange(category: String) {
        _uiState.update { it.copy(selectedCategory = category, searchQuery = "") }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onLimitChange(limit: Int) {
        _uiState.update { it.copy(limit = limit) }
        loadDataForCategory()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

