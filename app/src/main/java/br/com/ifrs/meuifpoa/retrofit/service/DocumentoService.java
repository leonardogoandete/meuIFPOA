package br.com.ifrs.meuifpoa.retrofit.service;

import br.com.ifrs.meuifpoa.model.DocumentoResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface DocumentoService {
    @POST("documento")
    Call<DocumentoResponse> obterDocumento(
            @Header("Authorization") String token,
            @Body String senha);
}
