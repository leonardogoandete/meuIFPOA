package br.com.ifrs.meuifpoa.retrofit.service;

import br.com.ifrs.meuifpoa.model.Perfil;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface PerfilService {
    @POST("perfil")
    Call<Perfil> listarPerfil(
            @Header("Authorization") String token,
            @Body String senha);

}
