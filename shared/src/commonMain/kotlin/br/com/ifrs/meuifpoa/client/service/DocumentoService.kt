package br.com.ifrs.meuifpoa.client.service

import br.com.ifrs.meuifpoa.model.Documento.DocumentoRequest
import br.com.ifrs.meuifpoa.model.Documento.DocumentoResponse
import br.com.ifrs.meuifpoa.client.ktorClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class DocumentoService {
    suspend fun obterDocumento(request: DocumentoRequest): DocumentoResponse {
        return ktorClient.post("documentos") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
