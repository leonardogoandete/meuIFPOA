package br.com.ifrs.meuifpoa.retrofit.service

import br.com.ifrs.meuifpoa.model.Edital
import retrofit2.http.POST
import retrofit2.http.Query

interface EditaisService {
    @POST("editais")
    suspend fun listarEditais(
        @Query("filter") filter: String?,
        @Query("limit") limit: Int
    ): List<Edital>
}
