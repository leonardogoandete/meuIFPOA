package br.com.ifrs.meuifpoa.model.login
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    var token: String? = null
)
