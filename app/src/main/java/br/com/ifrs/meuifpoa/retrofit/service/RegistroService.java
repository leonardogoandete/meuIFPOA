package br.com.ifrs.meuifpoa.retrofit.service;

import br.com.ifrs.meuifpoa.model.Registro;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegistroService {
    @POST("register")
    Call<Void> registro(@Body Registro registro);
}
