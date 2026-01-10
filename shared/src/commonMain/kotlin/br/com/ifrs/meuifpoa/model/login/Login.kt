package br.com.ifrs.meuifpoa.model.login
import kotlinx.serialization.Serializable

@Serializable
data class Login(
    var login: String? = null,
    var senha: String? = null
)
