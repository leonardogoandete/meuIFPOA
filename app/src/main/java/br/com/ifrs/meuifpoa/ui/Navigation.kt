package br.com.ifrs.meuifpoa.ui

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Main : Screen("main")
}

sealed class MainScreen(val route: String) {
    object Home : MainScreen("home")
    object Notas : MainScreen("notas")
    object Noticias : MainScreen("noticias")
    object Perfil : MainScreen("perfil")
}
