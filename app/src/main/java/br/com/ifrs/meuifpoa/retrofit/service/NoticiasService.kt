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
     * Lista as notícias do servidor com base no filtro e no limite fornecidos.
     *
     * @param filter O filtro a ser aplicado na listagem de notícias.
     * @param limit O número máximo de notícias a serem retornadas.
     * @return Call com a lista de notícias (List<Noticia>).
     */
    @POST("noticias")
    suspend fun listarNoticias(
        @Query("filter") filter: String?,
        @Query("limit") limit: Int
    ): List<Noticia> // Changed to suspend and return the list directly
}

