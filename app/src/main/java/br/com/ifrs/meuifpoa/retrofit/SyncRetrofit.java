package br.com.ifrs.meuifpoa.retrofit;

import java.util.concurrent.TimeUnit;

import br.com.ifrs.meuifpoa.retrofit.service.SyncService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SyncRetrofit {
    //private static final String BASE_URL = "http://192.168.0.15:8080/";
    private static final String BASE_URL = "https://2d15-200-180-191-23.ngrok-free.app/";

    private final SyncService syncService;

    public SyncRetrofit() {
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

        syncService = retrofit.create(SyncService.class);
    }

    public SyncService getSyncService() {
        return syncService;
    }
}
