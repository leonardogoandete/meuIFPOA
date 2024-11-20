package br.com.ifrs.meuifpoa.retrofit;

import static br.com.ifrs.meuifpoa.utils.Constants.BASE_URL;

import java.util.concurrent.TimeUnit;

import br.com.ifrs.meuifpoa.retrofit.service.SyncService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Classe `SyncRetrofit` configura e fornece uma instância do serviço `SyncService`
 * para interagir com a API de sincronização.
 */
public class SyncRetrofit {

    private final SyncService syncService;

    /**
     * Construtor da classe `SyncRetrofit`.
     * Configura o cliente HTTP e o Retrofit para o serviço de sincronização.
     */
    public SyncRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
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

    /**
     * Retorna a instância do serviço `SyncService`.
     *
     * @return Instância de `SyncService`.
     */
    public SyncService getSyncService() {
        return syncService;
    }
}
