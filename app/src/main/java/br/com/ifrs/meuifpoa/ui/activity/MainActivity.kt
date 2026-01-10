package br.com.ifrs.meuifpoa.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import br.com.ifrs.meuifpoa.App
import br.com.ifrs.meuifpoa.AppContainer
import br.com.ifrs.meuifpoa.ui.viewmodel.GoogleAuthHandler
import br.com.ifrs.meuifpoa.ui.viewmodel.HomeViewModel
import br.com.ifrs.meuifpoa.ui.viewmodel.LoginViewModel
import br.com.ifrs.meuifpoa.ui.viewmodel.NotasViewModel
import br.com.ifrs.meuifpoa.ui.viewmodel.NoticiasViewModel
import br.com.ifrs.meuifpoa.ui.viewmodel.PerfilViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create AppContainer
        val appContainer = AppContainer(this)

        // Create GoogleAuthHandler
        val googleAuthHandler = GoogleAuthHandler(this)

        // Create ViewModels manually
        val loginViewModel = LoginViewModel(googleAuthHandler)
        val homeViewModel = HomeViewModel(appContainer.documentoService)
        val notasViewModel = NotasViewModel(appContainer.syncService)
        val noticiasViewModel = NoticiasViewModel(
            appContainer.noticiasService,
            appContainer.editaisService
        )
        val perfilViewModel = PerfilViewModel(appContainer.syncService)

        setContent {
            App(
                loginViewModel = loginViewModel,
                homeViewModel = homeViewModel,
                notasViewModel = notasViewModel,
                noticiasViewModel = noticiasViewModel,
                perfilViewModel = perfilViewModel,
                startDestination = "login"
            )
        }
    }
}

