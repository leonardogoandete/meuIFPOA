package br.com.ifrs.meuifpoa.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.ifrs.meuifpoa.AppContainer

/**
 * Factory para criar instâncias de ViewModels com suas dependências.
 */
class MeuIfpoaViewModelFactory(private val appContainer: AppContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(
                db = appContainer.firestore,
                mAuth = appContainer.firebaseAuth,
                documentoService = appContainer.documentoService
            ) as T
        }
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(
                mAuth = appContainer.firebaseAuth,
                db = appContainer.firestore
            ) as T
        }
        if (modelClass.isAssignableFrom(PerfilViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PerfilViewModel(
                db = appContainer.firestore,
                mAuth = appContainer.firebaseAuth,
                syncManager = appContainer.syncManager,
                syncService = appContainer.syncService
            ) as T
        }
        if (modelClass.isAssignableFrom(NotasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotasViewModel(
                db = appContainer.firestore,
                mAuth = appContainer.firebaseAuth,
                syncManager = appContainer.syncManager,
                syncService = appContainer.syncService
            ) as T
        }
        throw IllegalArgumentException("Classe de ViewModel desconhecida: ${modelClass.name}")
    }
}