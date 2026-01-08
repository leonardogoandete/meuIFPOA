package br.com.ifrs.meuifpoa

import android.app.Application

/**
 * A classe Application para o app, usada para inicializar e manter a AppContainer.
 */
class MeuIfpoaApp : Application() {

    // A instância da AppContainer será criada no onCreate
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        // Cria a instância do container, que viverá enquanto o app estiver rodando
        container = AppContainer(this)
    }
}