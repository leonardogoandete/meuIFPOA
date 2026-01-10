package br.com.ifrs.meuifpoa.model

import kotlinx.serialization.Serializable

@Serializable
data class SyncResponse(
    var erro: String? = null
)
