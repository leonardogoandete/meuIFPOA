package br.com.ifrs.meuifpoa.retrofit;


import static br.com.ifrs.meuifpoa.utils.Constants.BASE_URL;

import java.util.concurrent.TimeUnit;

import br.com.ifrs.meuifpoa.retrofit.service.NoticiasService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Classe `NoticiasRetrofit` configura e fornece uma instância do serviço `NoticiasService`
 * para interagir com a API de notícias.
 */
public class NoticiasRetrofit {

    private final NoticiasService noticiasService;

    /**
     * Construtor da classe `NoticiasRetrofit`.
     * Configura o cliente HTTP e o Retrofit para o serviço de notícias.
     */
    public NoticiasRetrofit() {

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
        noticiasService = retrofit.create(NoticiasService.class);
    }

    /**
     * Retorna a instância do serviço `NoticiasService`.
     *
     * @return Instância de `NoticiasService`.
     */
    public NoticiasService getNoticiasService() {
        return noticiasService;
    }
}

