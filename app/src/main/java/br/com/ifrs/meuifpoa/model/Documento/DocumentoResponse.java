package br.com.ifrs.meuifpoa.model.Documento;

/**
 * Classe que representa a resposta de um documento.
 */
public class DocumentoResponse {
    private String pdfbase64;

    /**
     * Retorna o PDF codificado em base64.
     *
     * @return o PDF codificado em base64
     */
    public String getPdfbase64() {
        return pdfbase64;
    }

    /**
     * Define o PDF codificado em base64.
     *
     * @param pdfbase64 o PDF codificado em base64
     */
    public void setPdfbase64(String pdfbase64) {
        this.pdfbase64 = pdfbase64;
    }
}
