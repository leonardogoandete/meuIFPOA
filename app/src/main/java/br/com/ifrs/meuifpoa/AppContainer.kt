package br.com.ifrs.meuifpoa

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * A simple singleton that provides dependencies to the rest of the app.
 */
object AppContainer {

    // Firebase instances
    val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    // You can add other global dependencies here, like Retrofit services
    // val noticiasService = NoticiasRetrofit.noticiasService
}
