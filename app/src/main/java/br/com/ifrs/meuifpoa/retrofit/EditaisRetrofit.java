package br.com.ifrs.meuifpoa.retrofit;


import static br.com.ifrs.meuifpoa.utils.Constants.BASE_URL;

import java.util.concurrent.TimeUnit;

import br.com.ifrs.meuifpoa.retrofit.service.EditaisService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Classe `EditaisRetrofit` configura e fornece uma instância do serviço `EditaisService`
 * para interagir com a API de editais.
 */
public class EditaisRetrofit {

    private final EditaisService editaisService;

    /**
     * Construtor da classe `NoticiasRetrofit`.
     * Configura o cliente HTTP e o Retrofit para o serviço de notícias.
     */
    public EditaisRetrofit() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                //.addInterceptor(interceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        editaisService = retrofit.create(EditaisService.class);
    }

    /**
     * Retorna a instância do serviço `NoticiasService`.
     *
     * @return Instância de `NoticiasService`.
     */
    public EditaisService getEditaisService() {
        return editaisService;
    }
}

