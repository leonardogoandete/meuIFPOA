package br.com.ifrs.meuifpoa.ui.viewmodel

actual class GoogleAuthHandler {
    actual suspend fun signIn(): Pair<String, String>? {
        // TODO: Implementar o login com o Google para iOS usando as APIs da Apple.
        // Esta implementação é um placeholder.
        throw UnsupportedOperationException("Login com Google não implementado para iOS")
    }
}