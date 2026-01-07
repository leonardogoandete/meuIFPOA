package br.com.ifrs.meuifpoa.model

data class Noticia(
    var id: Int = 0,
    var link: String? = null,
    var titulo: String? = null,
    var resumo: String? = null,
    var dataPublicacao: String? = null,
    var horaPublicacao: String? = null
) {
    val dataHoraPublicacao: String
        get() = "$dataPublicacao - $horaPublicacao"
}
