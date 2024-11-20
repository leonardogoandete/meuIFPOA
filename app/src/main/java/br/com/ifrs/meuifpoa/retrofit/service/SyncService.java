package br.com.ifrs.meuifpoa.retrofit.service;

import java.util.List;

import br.com.ifrs.meuifpoa.model.Nota;
import br.com.ifrs.meuifpoa.model.SyncResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Interface `SyncService` define os endpoints da API para sincronização.
 * Utiliza as anotações do Retrofit para mapear as chamadas HTTP.
 */
public interface SyncService {
        /**
         * Sincroniza os dados com o servidor.
         *
         * @param token Token de autenticação do usuário.
         * @param senha A senha a ser utilizada na sincronização.
         * @return Call com a resposta da sincronização (SyncResponse).
         */
        @POST("sync")
        Call<SyncResponse> sincronizar(
                @Header("Authorization") String token,
                @Body String senha);
}
