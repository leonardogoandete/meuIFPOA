package br.com.ifrs.meuifpoa.retrofit.service;

import br.com.ifrs.meuifpoa.model.DocumentoResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface DocumentoService {
    @GET("documento")
    Call<DocumentoResponse> obterDocumento();
}
