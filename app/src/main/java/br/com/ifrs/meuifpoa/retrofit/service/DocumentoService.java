package br.com.ifrs.meuifpoa.retrofit.service;

import java.util.Map;

import br.com.ifrs.meuifpoa.model.Documento.DocumentoRequest;
import br.com.ifrs.meuifpoa.model.Documento.DocumentoResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface DocumentoService {
    @POST("documento")
    Call<DocumentoResponse> obterDocumento(
            @Header("Authorization") String token,
            @Body DocumentoRequest documentoRequest);
}
