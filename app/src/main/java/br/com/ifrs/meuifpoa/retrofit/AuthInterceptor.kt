package br.com.ifrs.meuifpoa.retrofit

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor para adicionar o token de autenticação do Firebase a cada requisição.
 */
class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        val originalRequest = chain.request()

        // Se não houver usuário logado, prossegue com a requisição original
        if (currentUser == null) {
            return chain.proceed(originalRequest)
        }

        // Obtém o token de autenticação de forma síncrona (adequado para interceptors)
        val token = runBlocking {
            try {
                currentUser.getIdToken(true).await()?.token
            } catch (e: Exception) {
                null // Em caso de erro, prossegue sem token
            }
        }

        if (token == null) {
            return chain.proceed(originalRequest)
        }

        // Adiciona o cabeçalho de autorização com o token Bearer
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(newRequest)
    }
}