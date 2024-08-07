package br.com.ifrs.meuifpoa.retrofit.service;

import java.util.List;

import br.com.ifrs.meuifpoa.model.Nota;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface SyncService {
        @POST("sync")
        Call<Void> sincronizar(
                @Header("Authorization") String token,
                @Body String senha);
}
