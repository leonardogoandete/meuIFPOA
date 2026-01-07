package br.com.ifrs.meuifpoa.retrofit.service

import br.com.ifrs.meuifpoa.model.Noticia
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Interface `NoticiasService` define os endpoints da API para interagir com notícias no servidor.
 * Utiliza as anotações do Retrofit para mapear as chamadas HTTP.
 */
interface NoticiasService {
    /**
     * Lista as notícias do servidor com base nos filtros fornecidos.
     *
     * @param filter O filtro de texto a ser aplicado.
     * @param limit O número máximo de notícias a serem retornadas.
     * @param category A categoria da notícia (ex: "noticia" ou "edital").
     * @return Lista de notícias.
     */
    @POST("noticias")
    suspend fun listarNoticias(
        @Query("filter") filter: String?,
        @Query("limit") limit: Int,
        @Query("category") category: String?
    ): List<Noticia>
}
