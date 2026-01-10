package br.com.ifrs.meuifpoa.model.Documento

import kotlinx.serialization.Serializable

@Serializable
data class DocumentoRequest(
    var tipo: String? = null,
    var senha: String? = null
)
