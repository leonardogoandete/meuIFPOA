package br.com.ifrs.meuifpoa.utils

/**
 * O objeto `Constants` contém constantes que são utilizadas em todo o aplicativo.
 * Essas constantes são usadas para evitar duplicação de valores fixos e facilitar a manutenção do código.
 * <p>
 * Exemplos de constantes: URLs base, códigos de erro, e outros valores imutáveis utilizados no sistema.
 */
object Constants {
    /**
     * A constante BASE_URL, utilizada para definir a URL da api do backend.
     */
    const val BASE_URL = "https://app.poa.ifrs.edu.br/meuifpoa/"
     /**
      * A constante BASE_URL_NOTICIA, utilizada para definir a URL base das noticias.
     */
    const val BASE_URL_NOTICIA = "https://poa.ifrs.edu.br"
     /**
      * A constante DOC_HISTORICO, define o tipo de documento para o formulario.
     */
    const val DOC_HISTORICO = "historico"
     /**
      * A constante DOC_HISTORICO_EMENTAS, define o tipo de documento para o formulario.
     */
    const val DOC_HISTORICO_EMENTAS = "historicoEmentas"
     /**
      * A constante DOC_DECLARACAO_VINCULO, define o tipo de documento para o formulario.
     */
    const val DOC_DECLARACAO_VINCULO = "declaracaoVinculo"
     /**
      * A constante DOC_ATESTADO_MATRICULA, define o tipo de documento para o formulario.
     */
    const val DOC_ATESTADO_MATRICULA = "atestadoMatricula"
}
