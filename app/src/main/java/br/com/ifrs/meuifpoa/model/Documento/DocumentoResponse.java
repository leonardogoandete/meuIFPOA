package br.com.ifrs.meuifpoa.model.Documento;

/**
 * A classe `DocumentoResponse` encapsula a resposta recebida do servidor após a solicitação de um documento.
 * Contém os dados do documento em formato base64, além de outras informações relevantes.
 * <p>
 * Atributos:
 * - `pdfbase64`: Conteúdo do documento codificado em base64.
 * - Outros metadados opcionais, dependendo da resposta do servidor.
 */
public class DocumentoResponse {
    private String pdfbase64;

    /**
     * Retorna o conteúdo do PDF codificado em base64.
     *
     * @return String contendo os dados em base64.
     */
    public String getPdfbase64() {
        return pdfbase64;
    }

    /**
     * Sets pdfbase 64.
     *
     * @param pdfbase64 the pdfbase 64
     */
    public void setPdfbase64(String pdfbase64) {
        this.pdfbase64 = pdfbase64;
    }
}
