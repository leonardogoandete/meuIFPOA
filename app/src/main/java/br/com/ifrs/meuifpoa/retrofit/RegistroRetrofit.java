package br.com.ifrs.meuifpoa.retrofit;

import static br.com.ifrs.meuifpoa.Constants.BASE_URL;

import java.util.concurrent.TimeUnit;

import br.com.ifrs.meuifpoa.retrofit.service.RegistroService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistroRetrofit {

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
