package br.com.ifrs.meuifpoa.retrofit

import android.content.Context
import br.com.ifrs.meuifpoa.retrofit.service.SyncService
import br.com.ifrs.meuifpoa.utils.Constants.BASE_URL
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class SyncRetrofit(private val context: Context) {

    private val httpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(AuthInterceptor(context))
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

    private class AuthInterceptor(private val context: Context) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val mAuth = FirebaseAuth.getInstance()
            val currentUser = mAuth.currentUser

            val originalRequest = chain.request()

            if (currentUser == null) {
                return chain.proceed(originalRequest)
            }

            val token = runBlocking {
                try {
                    currentUser.getIdToken(true).await()?.token
                } catch (e: Exception) {
                    null
                }
            }

            if (token == null) {
                return chain.proceed(originalRequest)
            }

            val sharedPrefs = context.getSharedPreferences("loginSigaa", Context.MODE_PRIVATE)
            val senha = sharedPrefs.getString("senha", "") ?: ""

            val requestBuilder = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")

            // Adiciona a senha ao corpo da requisição POST
            if (originalRequest.method == "POST") {
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = senha.toRequestBody(mediaType)
                requestBuilder.post(requestBody)
            }

            return chain.proceed(requestBuilder.build())
        }
    }
}