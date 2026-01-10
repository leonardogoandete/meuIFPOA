package br.com.ifrs.meuifpoa

import androidx.compose.runtime.*
import br.com.ifrs.meuifpoa.ui.screen.LoginScreen
import br.com.ifrs.meuifpoa.ui.screen.MainScreen
import br.com.ifrs.meuifpoa.ui.theme.MeuIFPOATheme
import br.com.ifrs.meuifpoa.ui.viewmodel.HomeViewModel
import br.com.ifrs.meuifpoa.ui.viewmodel.LoginViewModel
import br.com.ifrs.meuifpoa.ui.viewmodel.NotasViewModel
import br.com.ifrs.meuifpoa.ui.viewmodel.NoticiasViewModel
import br.com.ifrs.meuifpoa.ui.viewmodel.PerfilViewModel

@Composable
fun App(
    loginViewModel: LoginViewModel,
    homeViewModel: HomeViewModel,
    notasViewModel: NotasViewModel,
    noticiasViewModel: NoticiasViewModel,
    perfilViewModel: PerfilViewModel,
    startDestination: String = "login"
) {
    var currentScreen by remember { mutableStateOf(startDestination) }

    MeuIFPOATheme {
        when (currentScreen) {
            "login" -> {
                LoginScreen(
                    loginViewModel = loginViewModel,
                    onLoginSuccess = {
                        currentScreen = "main"
                    }
                )
            }
            "main" -> {
                MainScreen(
                    homeViewModel = homeViewModel,
                    notasViewModel = notasViewModel,
                    noticiasViewModel = noticiasViewModel,
                    perfilViewModel = perfilViewModel,
                    onLogout = {
                        currentScreen = "login"
                    }
                )
            }
        }
    }
}

