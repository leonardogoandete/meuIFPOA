package br.com.ifrs.meuifpoa.retrofit;


import static br.com.ifrs.meuifpoa.utils.Constants.BASE_URL;

import java.util.concurrent.TimeUnit;

import br.com.ifrs.meuifpoa.retrofit.service.NoticiasService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * The type Noticias retrofit.
 */
public class NoticiasRetrofit {

    private final NoticiasService noticiasService;

    /**
     * Instantiates a new Noticias retrofit.
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
     * Gets noticias service.
     *
     * @return the noticias service
     */
    public NoticiasService getNoticiasService() {
        return noticiasService;
    }
}

