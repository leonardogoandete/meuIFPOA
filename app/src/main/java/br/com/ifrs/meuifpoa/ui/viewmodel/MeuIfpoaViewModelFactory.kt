package br.com.ifrs.meuifpoa.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.ifrs.meuifpoa.AppContainer

class MeuIfpoaViewModelFactory(
    private val appContainer: AppContainer,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(appContainer.documentoService) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(GoogleAuthHandler(context)) as T
            }
            modelClass.isAssignableFrom(NoticiasViewModel::class.java) -> {
                NoticiasViewModel(appContainer.noticiasService, appContainer.editaisService) as T
            }
            modelClass.isAssignableFrom(PerfilViewModel::class.java) -> {
                PerfilViewModel(appContainer.syncService) as T
            }
            modelClass.isAssignableFrom(NotasViewModel::class.java) -> {
                NotasViewModel(appContainer.syncService) as T
            }
            else -> throw IllegalArgumentException("Classe de ViewModel desconhecida: ${modelClass.name}")
        }
    }
}

