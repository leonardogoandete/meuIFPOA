package br.com.ifrs.meuifpoa.retrofit.service

import br.com.ifrs.meuifpoa.model.Documento.DocumentoRequest
import br.com.ifrs.meuifpoa.model.Documento.DocumentoResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Interface `DocumentoService` define os endpoints da API para interagir com documentos no servidor.
 * Utiliza as anotações do Retrofit para mapear as chamadas HTTP.
 *
 * Exemplos de endpoints:
 * - Obter documento por tipo
 * - Enviar requisições de documentos com parâmetros específicos
 */
interface DocumentoService {
    /**
     * Solicita um documento do servidor com base no tipo de documento e nas credenciais fornecidas.
     *
     * @param token Token de autenticação do usuário.
     * @param documentoRequest O objeto de requisição do documento.
     * @return Call com a resposta do documento (DocumentoResponse).
     */
    @POST("documento")
    fun obterDocumento(
        @Header("Authorization") token: String,
        @Body documentoRequest: DocumentoRequest
    ): Call<DocumentoResponse>
}

