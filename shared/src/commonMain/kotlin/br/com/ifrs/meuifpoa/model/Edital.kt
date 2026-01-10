package br.com.ifrs.meuifpoa.model

import kotlinx.serialization.Serializable

@Serializable
data class Edital(
    val id: Int = 0,
    val link: String? = null,
    val titulo: String? = null,
    val dataPublicacaoEdital: String? = null
)
