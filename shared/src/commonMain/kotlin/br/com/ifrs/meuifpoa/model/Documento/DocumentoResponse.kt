package br.com.ifrs.meuifpoa.model.Documento

import kotlinx.serialization.Serializable

@Serializable
data class DocumentoResponse(
    var pdfbase64: String? = null
)
