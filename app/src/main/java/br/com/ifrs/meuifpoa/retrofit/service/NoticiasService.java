package br.com.ifrs.meuifpoa.retrofit.service;

import java.util.List;

import br.com.ifrs.meuifpoa.model.Noticia;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NoticiasService {
    @POST("noticias")
    Call<List<Noticia>> listarNoticias(
            @Query("filter") String filter,
            @Query("limit") int limit);
    //@GET("noticias")
    //Call<List<Noticia>> listarNoticias();
}
