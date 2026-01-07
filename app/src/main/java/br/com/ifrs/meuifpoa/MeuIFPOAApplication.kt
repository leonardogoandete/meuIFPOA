package br.com.ifrs.meuifpoa

import android.app.Application
import com.google.firebase.FirebaseApp

class MeuIFPOAApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
