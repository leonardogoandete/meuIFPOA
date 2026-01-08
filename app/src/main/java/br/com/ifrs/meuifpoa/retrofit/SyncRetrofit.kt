package br.com.ifrs.meuifpoa.retrofit

import android.content.Context
import br.com.ifrs.meuifpoa.retrofit.service.SyncService
import br.com.ifrs.meuifpoa.utils.Constants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Classe `SyncRetrofit` configura e fornece uma instância do serviço `SyncService`
 * para interagir com a API de sincronização.
 */
class SyncRetrofit(context: Context) {

    private val httpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(AuthInterceptor(context)) // Reutilizando o interceptor
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val syncService: SyncService by lazy {
        retrofit.create(SyncService::class.java)
    }
}