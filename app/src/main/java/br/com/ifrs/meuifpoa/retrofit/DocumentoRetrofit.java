package br.com.ifrs.meuifpoa.retrofit;

import static br.com.ifrs.meuifpoa.utils.Constants.BASE_URL;

import java.util.concurrent.TimeUnit;

import br.com.ifrs.meuifpoa.retrofit.service.DocumentoService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A classe `DocumentoRetrofit` configura o cliente Retrofit para realizar chamadas à API relacionadas aos documentos.
 * Esta classe é responsável por inicializar o Retrofit com a URL base e outros parâmetros de configuração.
 */
public class DocumentoRetrofit {
    private final DocumentoService documentoService;

    /**
     * Construtor que inicializa o cliente Retrofit com a URL base.
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
     * Retorna o serviço `DocumentoService` para fazer as requisições relacionadas aos documentos.
     *
     * @return DocumentoService documento service
     */
    public DocumentoService getDocumentoService(){
        return documentoService;
    }
}
