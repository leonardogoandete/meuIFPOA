package br.com.ifrs.meuifpoa.retrofit.service

import br.com.ifrs.meuifpoa.model.SyncResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Interface `SyncService` define os endpoints da API para sincronização.
 * Utiliza as anotações do Retrofit para mapear as chamadas HTTP.
 */
interface SyncService {
    /**
     * Sincroniza os dados com o servidor.
     *
     * @param token Token de autenticação do usuário.
     * @param senha A senha a ser utilizada na sincronização.
     * @return Call com a resposta da sincronização (SyncResponse).
     */
    @POST("sync")
    fun sincronizar(
        @Header("Authorization") token: String,
        @Body senha: String
    ): Call<SyncResponse>
}

