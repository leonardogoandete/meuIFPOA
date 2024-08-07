package br.com.ifrs.meuifpoa.retrofit.service;

import br.com.ifrs.meuifpoa.model.login.LoginRequest;
import br.com.ifrs.meuifpoa.model.login.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginService {

    @POST("auth")
    Call<LoginResponse> login(@Body LoginRequest login);
}
