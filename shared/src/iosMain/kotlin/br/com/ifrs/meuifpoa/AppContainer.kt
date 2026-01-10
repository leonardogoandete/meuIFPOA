package br.com.ifrs.meuifpoa

import br.com.ifrs.meuifpoa.client.service.DocumentoService
import br.com.ifrs.meuifpoa.client.service.EditaisService
import br.com.ifrs.meuifpoa.client.service.NoticiasService
import br.com.ifrs.meuifpoa.client.service.SyncService
import br.com.ifrs.meuifpoa.ui.viewmodel.MeuIfpoaViewModelFactory
import br.com.ifrs.meuifpoa.utils.SyncManager

/**
 * Actual implementation of AppContainer for the iOS platform.
 * This is a placeholder and will need to be implemented with native iOS APIs.
 */
actual class AppContainer {
    actual val noticiasService: NoticiasService by lazy {
        NoticiasService()
    }
    actual val editaisService: EditaisService by lazy {
        EditaisService()
    }
    actual val documentoService: DocumentoService by lazy {
        DocumentoService()
    }
    actual val syncService: SyncService by lazy {
        SyncService()
    }
    actual val syncManager: SyncManager by lazy {
        // TODO: Implement SyncManager for iOS using NSUserDefaults
        throw UnsupportedOperationException("SyncManager não implementado para iOS")
    }
    actual val viewModelFactory: MeuIfpoaViewModelFactory by lazy {
        MeuIfpoaViewModelFactory(this)
    }
}