package br.com.ifrs.meuifpoa.model

import kotlinx.serialization.Serializable

@Serializable
data class Registro(
    var uid: String? = null,
    var nome: String? = null,
    var cpf: String? = null,
    var email: String? = null
)
