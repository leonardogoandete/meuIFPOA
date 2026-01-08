package br.com.ifrs.meuifpoa

import android.content.Context
import br.com.ifrs.meuifpoa.retrofit.DocumentoRetrofit
import br.com.ifrs.meuifpoa.retrofit.EditaisRetrofit
import br.com.ifrs.meuifpoa.retrofit.NoticiasRetrofit
import br.com.ifrs.meuifpoa.retrofit.SyncRetrofit
import br.com.ifrs.meuifpoa.retrofit.service.DocumentoService
import br.com.ifrs.meuifpoa.retrofit.service.EditaisService
import br.com.ifrs.meuifpoa.retrofit.service.NoticiasService
import br.com.ifrs.meuifpoa.retrofit.service.SyncService
import br.com.ifrs.meuifpoa.ui.viewmodel.MeuIfpoaViewModelFactory
import br.com.ifrs.meuifpoa.utils.SyncManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * A container for dependencies that are shared across the app.
 */
class AppContainer(context: Context) {

    // Firebase instances
    val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    // SyncManager for handling sync logic
    val syncManager: SyncManager by lazy {
        SyncManager(context)
    }

    // Retrofit services
    val syncService: SyncService by lazy {
        SyncRetrofit(context).syncService
    }
    val documentoService: DocumentoService by lazy {
        DocumentoRetrofit(context).documentoService
    }
    val noticiasService: NoticiasService by lazy {
        NoticiasRetrofit(context).noticiasService
    }
    val editaisService: EditaisService by lazy {
        EditaisRetrofit(context).editaisService
    }

    // ViewModel Factory
    val viewModelFactory: MeuIfpoaViewModelFactory by lazy {
        MeuIfpoaViewModelFactory(this)
    }
}