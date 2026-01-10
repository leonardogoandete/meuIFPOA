package br.com.ifrs.meuifpoa.model

import kotlinx.serialization.Serializable

@Serializable
data class Perfil(
    var nomeDocente: String? = null,
    var matricula: String? = null,
    var curso: String? = null,
    var nivel: String? = null,
    var status: String? = null,
    var anoIngresso: String? = null,
    var chObrigatoriaPendente: String? = null,
    var chOptativaPendente: String? = null,
    var chTotalCurriculo: String? = null,
    var chComplementarPendente: String? = null,
    var integralizado: String? = null,
    var imgPerfil: String? = null,
    var notas: ArrayList<Nota>? = null
)
