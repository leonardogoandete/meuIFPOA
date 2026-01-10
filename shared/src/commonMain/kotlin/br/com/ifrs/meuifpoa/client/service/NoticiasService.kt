package br.com.ifrs.meuifpoa.client.service

import br.com.ifrs.meuifpoa.model.Noticia
import br.com.ifrs.meuifpoa.client.ktorClient
import io.ktor.client.call.*
import io.ktor.client.request.*

class NoticiasService {
    suspend fun listarNoticias(filtro: String? = null, limite: Int = 100, categoria: String? = null): List<Noticia> {
        // Se não tem filtro, usa GET (retorna todas)
        return if (filtro.isNullOrBlank() && limite >= 100) {
            ktorClient.get("noticias").body()
        } else {
            // Se tem filtro ou limite, usa POST com parâmetros
            ktorClient.post("noticias") {
                parameter("filter", filtro)
                parameter("limit", limite)
            }.body()
        }
    }
}
