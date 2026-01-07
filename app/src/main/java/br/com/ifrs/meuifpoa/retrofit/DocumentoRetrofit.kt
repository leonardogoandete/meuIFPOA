package br.com.ifrs.meuifpoa.retrofit

import br.com.ifrs.meuifpoa.retrofit.service.DocumentoService
import br.com.ifrs.meuifpoa.utils.Constants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton que configura e fornece uma instância do serviço `DocumentoService`
 * para interagir com a API de documentos.
 */
object DocumentoRetrofit {

    private val httpClient: OkHttpClient by lazy {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val documentoService: DocumentoService by lazy {
        retrofit.create(DocumentoService::class.java)
    }
}
