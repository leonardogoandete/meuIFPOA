package br.com.ifrs.meuifpoa.model

import java.io.Serializable

data class Edital(
    val id: Int = 0,
    val link: String? = null,
    val titulo: String? = null,
    val resumo: String? = null,
    val dataPublicacao: String? = null,
    val horaPublicacao: String? = null
) : Serializable {
    val dataHoraPublicacao: String
        get() = "$dataPublicacao - $horaPublicacao"
}
