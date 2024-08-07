package br.com.ifrs.meuifpoa.retrofit;

import java.util.concurrent.TimeUnit;

import br.com.ifrs.meuifpoa.retrofit.service.RegistroService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistroRetrofit {
    //private static final String BASE_URL = "http://192.168.0.15:8081/";
    private static final String BASE_URL = "https://2d15-200-180-191-23.ngrok-free.app/";

    private final RegistroService registroService;

    public RegistroRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        registroService = retrofit.create(RegistroService.class);
    }

    public RegistroService getRegistroService() {
        return registroService;
    }
}
