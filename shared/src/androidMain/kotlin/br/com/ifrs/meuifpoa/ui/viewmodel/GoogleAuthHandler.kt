package br.com.ifrs.meuifpoa.ui.viewmodel

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import br.com.ifrs.meuifpoa.resources.SharedStrings
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

actual class GoogleAuthHandler(private val context: Context) {
    actual suspend fun signIn(): Pair<String, String>? {
        val credentialManager = CredentialManager.create(context)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(SharedStrings.GOOGLE_WEB_CLIENT_ID)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(context as Activity, request)
        val credential = result.credential

        if (credential is GoogleIdTokenCredential) {
            return Pair(credential.idToken, credential.id)
        } else if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            return Pair(googleIdTokenCredential.idToken, googleIdTokenCredential.id)
        }

        return null
    }
}