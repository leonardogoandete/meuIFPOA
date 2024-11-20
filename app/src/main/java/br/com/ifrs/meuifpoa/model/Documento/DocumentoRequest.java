package br.com.ifrs.meuifpoa.model.Documento;

/**
 * Classe que representa uma requisição de documento.
 */
public class DocumentoRequest {
    /**
     * O tipo do documento.
     * Tipos possíveis: "historico", "historicoEmentas", "atestadoMatricula", "declaracaoVinculo"
     */
    private String tipo;
    /**
     * A senha do usuario no SIGAA.
     */
    private String senha;

    /**
     * Construtor da classe DocumentoRequest.
     *
     * @param tipo o tipo do documento
     * @param senha a senha do documento
     */
    public DocumentoRequest(String tipo, String senha) {
        this.senha = senha;
        this.tipo = tipo;
    }

    /**
     * Retorna a senha do documento.
     *
     * @return a senha do documento
     */
    public String getSenha() {
        return senha;
    }

    /**
     * Define a senha do documento.
     *
     * @param senha a nova senha do documento
     */
    public void setSenha(String senha) {
        this.senha = senha;
    }

    /**
     * Retorna o tipo do documento.
     *
     * @return o tipo do documento
     */
    public String getTipoDocumento() {
        return tipo;
    }

    /**
     * Define o tipo do documento.
     *
     * @param tipoDocumento o novo tipo do documento
     */
    public void setTipoDocumento(String tipoDocumento) {
        this.tipo = tipoDocumento;
    }
}
