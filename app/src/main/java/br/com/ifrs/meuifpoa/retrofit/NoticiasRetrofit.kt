package br.com.ifrs.meuifpoa.retrofit

import br.com.ifrs.meuifpoa.retrofit.service.NoticiasService
import br.com.ifrs.meuifpoa.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton que configura e fornece uma instância do serviço `NoticiasService`
 * para interagir com a API de notícias.
 */
object NoticiasRetrofit {

    private val httpClient: OkHttpClient by lazy {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL_NOTICIA) // Corrected to use noticia base url
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val noticiasService: NoticiasService by lazy {
        retrofit.create(NoticiasService::class.java)
    }
}
