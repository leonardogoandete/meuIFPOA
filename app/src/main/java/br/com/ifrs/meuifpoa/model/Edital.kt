package br.com.ifrs.meuifpoa.model

import java.io.Serializable

data class Edital(
    val id: Int = 0,
    val link: String? = null,
    val titulo: String? = null,
    val dataPublicacaoEdital: String? = null

) : Serializable {
}
