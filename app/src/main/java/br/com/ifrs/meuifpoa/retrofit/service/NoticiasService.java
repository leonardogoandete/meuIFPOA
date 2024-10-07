package br.com.ifrs.meuifpoa.retrofit.service;

import java.util.List;

import br.com.ifrs.meuifpoa.model.Noticia;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Interface `NoticiasService` define os endpoints da API para interagir com notícias no servidor.
 * Utiliza as anotações do Retrofit para mapear as chamadas HTTP.
 */
public interface NoticiasService {
    /**
     * Lista as notícias do servidor com base no filtro e no limite fornecidos.
     *
     * @param filter O filtro a ser aplicado na listagem de notícias.
     * @param limit  O número máximo de notícias a serem retornadas.
     * @return Call com a lista de notícias (List<Noticia>).
     */
    @POST("noticias")
    Call<List<Noticia>> listarNoticias(
            @Query("filter") String filter,
            @Query("limit") int limit);
}
