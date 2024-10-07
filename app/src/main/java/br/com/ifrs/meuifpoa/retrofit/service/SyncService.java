package br.com.ifrs.meuifpoa.retrofit.service;

import java.util.List;

import br.com.ifrs.meuifpoa.model.Nota;
import br.com.ifrs.meuifpoa.model.SyncResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * The interface Sync service.
 */
public interface SyncService {
        /**
         * Sincronizar call.
         *
         * @param token the token
         * @param senha the senha
         * @return the call
         */
        @POST("sync")
        Call<SyncResponse> sincronizar(
                @Header("Authorization") String token,
                @Body String senha);
}
