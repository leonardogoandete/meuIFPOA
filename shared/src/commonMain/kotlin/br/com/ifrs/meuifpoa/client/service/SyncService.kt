package br.com.ifrs.meuifpoa.client.service

import br.com.ifrs.meuifpoa.model.SyncResponse
import br.com.ifrs.meuifpoa.client.ktorClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class SyncService {
    suspend fun sincronizar(senha: String): SyncResponse {
        return ktorClient.post("sync") {
            contentType(ContentType.Application.Json)
            setBody(senha)
        }.body()
    }
}