package br.com.ifrs.meuifpoa.retrofit.service;

import java.util.List;

import br.com.ifrs.meuifpoa.model.Edital;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Interface `EditaisService` define os endpoints da API para interagir com editais no servidor.
 * Utiliza as anotações do Retrofit para mapear as chamadas HTTP.
 */
public interface EditaisService {
    /**
     * Lista os editais do servidor com base no filtro e no limite fornecidos.
     *
     * @param filter O filtro a ser aplicado na listagem de editais.
     * @param limit  O número máximo de editais a serem retornadas.
     * @return Call com a lista de notícias (List<Edital>).
     */
    @POST("editais")
    Call<List<Edital>> listarEditais(
            @Query("filter") String filter,
            @Query("limit") int limit);
}
