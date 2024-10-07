package br.com.ifrs.meuifpoa.retrofit;

import static br.com.ifrs.meuifpoa.utils.Constants.BASE_URL;

import java.util.concurrent.TimeUnit;

import br.com.ifrs.meuifpoa.retrofit.service.DocumentoService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Classe `DocumentoRetrofit` configura e fornece uma instância do serviço `DocumentoService`
 * para interagir com a API de documentos.
 */
public class DocumentoRetrofit {
    private final DocumentoService documentoService;

    /**
     * Construtor da classe `DocumentoRetrofit`.
     * Configura o cliente HTTP e o Retrofit para o serviço de documentos.
     */
    public DocumentoRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        documentoService = retrofit.create(DocumentoService.class);
    }

    /**
     * Retorna a instância do serviço `DocumentoService`.
     *
     * @return Instância de `DocumentoService`.
     */
    public DocumentoService getDocumentoService(){
        return documentoService;
    }
}
