package br.com.ifrs.meuifpoa.retrofit;


import br.com.ifrs.meuifpoa.retrofit.service.NoticiasService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NoticiasRetrofit {
    //private static final String BASE_URL = "https://66aaa2fe636a4840d7c83bae.mockapi.io/";
    //private static final String BASE_URL = "http://192.168.0.15:8080/";
    private static final String BASE_URL = "https://2d15-200-180-191-23.ngrok-free.app/";

    private final NoticiasService noticiasService;
    public NoticiasRetrofit() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                //.addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        noticiasService = retrofit.create(NoticiasService.class);
    }

    public NoticiasService getNoticiasService() {
        return noticiasService;
    }
}

