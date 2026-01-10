package br.com.ifrs.meuifpoa.model.login
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val cpf: String,
    val senha: String
)
