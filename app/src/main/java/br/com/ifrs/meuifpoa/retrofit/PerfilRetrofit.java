package br.com.ifrs.meuifpoa.retrofit;

import java.util.concurrent.TimeUnit;

import br.com.ifrs.meuifpoa.retrofit.service.PerfilService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PerfilRetrofit {
    //public static final String BASE_URL = "http://192.168.0.15:8080/";
    public static final String BASE_URL = "https://2d15-200-180-191-23.ngrok-free.app/";
    private final PerfilService perfilService;

    public PerfilRetrofit() {

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
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build();
        perfilService = retrofit.create(PerfilService.class);
    }

    public PerfilService getPerfilService() {
        return perfilService;
    }
}
