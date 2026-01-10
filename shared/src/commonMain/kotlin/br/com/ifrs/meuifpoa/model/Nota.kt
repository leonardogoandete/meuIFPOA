package br.com.ifrs.meuifpoa.model

import kotlinx.serialization.Serializable

/**
 * Classe que representa uma Nota.
 */
@Serializable
data class Nota(
    var codigoDisciplina: String? = null,
    var nomeDisciplina: String? = null,
    var primeiraUnidade: String? = null,
    var segundaUnidade: String? = null,
    var notaRecuperacao: String? = null,
    var notaFinal: String? = null,
    var numeroFaltas: String? = null,
    var situacao: String? = null
)
