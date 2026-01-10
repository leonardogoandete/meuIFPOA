package br.com.ifrs.meuifpoa

import br.com.ifrs.meuifpoa.client.service.DocumentoService
import br.com.ifrs.meuifpoa.client.service.EditaisService
import br.com.ifrs.meuifpoa.client.service.NoticiasService
import br.com.ifrs.meuifpoa.client.service.SyncService

/**
 * Define a contract for a container of dependencies.
 * Each platform will provide its own actual implementation.
 */
expect class AppContainer {
    val noticiasService: NoticiasService
    val editaisService: EditaisService
    val documentoService: DocumentoService
    val syncService: SyncService
}
