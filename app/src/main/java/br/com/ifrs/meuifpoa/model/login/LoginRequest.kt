package br.com.ifrs.meuifpoa.model.login

data class LoginRequest(
    val cpf: String,
    val senha: String
)
