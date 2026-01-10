package br.com.ifrs.meuifpoa.client.service

import br.com.ifrs.meuifpoa.model.Edital
import br.com.ifrs.meuifpoa.client.ktorClient
import io.ktor.client.call.*
import io.ktor.client.request.*

class EditaisService {
    suspend fun listarEditais(filtro: String? = null, limite: Int = 100): List<Edital> {
        // Se não tem filtro, usa GET (retorna todos)
        return if (filtro.isNullOrBlank() && limite >= 100) {
            ktorClient.get("editais").body()
        } else {
            // Se tem filtro ou limite, usa POST com parâmetros
            ktorClient.post("editais") {
                parameter("filter", filtro)
                parameter("limit", limite)
            }.body()
        }
    }
}
