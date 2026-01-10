package br.com.ifrs.meuifpoa.model.Documento

import kotlinx.serialization.Serializable

@Serializable
data class Documento(
    val id: String = "",
    val nome: String = "",
    val tipo: String = "",
    val descricao: String = "",
    val url: String = ""
)

