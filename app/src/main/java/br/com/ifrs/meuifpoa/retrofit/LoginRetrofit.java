package br.com.ifrs.meuifpoa.retrofit;

import br.com.ifrs.meuifpoa.retrofit.service.LoginService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginRetrofit {
    private static final String BASE_URL = "http://192.168.0.15:8081/";
    private final LoginService authService;

    public LoginRetrofit() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                //.addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
        authService = retrofit.create(LoginService.class);
    }

    public LoginService getAuthService() {
        return authService;
    }
}
