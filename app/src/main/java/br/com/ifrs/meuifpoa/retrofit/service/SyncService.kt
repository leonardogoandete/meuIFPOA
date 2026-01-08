package br.com.ifrs.meuifpoa.retrofit.service

import br.com.ifrs.meuifpoa.model.SyncResponse
import retrofit2.http.POST

/**
 * Interface `SyncService` define o endpoint da API para sincronização.
 */
interface SyncService {
    /**
     * Sincroniza os dados com o servidor.
     * O token e a senha são injetados automaticamente pelo AuthInterceptor.
     *
     * @return SyncResponse A resposta da sincronização.
     */
    @POST("sync")
    suspend fun sincronizar(): SyncResponse
}
