package br.com.ifrs.meuifpoa

import android.content.Context
import br.com.ifrs.meuifpoa.client.service.DocumentoService
import br.com.ifrs.meuifpoa.client.service.EditaisService
import br.com.ifrs.meuifpoa.client.service.NoticiasService
import br.com.ifrs.meuifpoa.client.service.SyncService

/**
 * Actual implementation of AppContainer for the Android platform.
 */
actual class AppContainer(private val context: Context) {
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
}

