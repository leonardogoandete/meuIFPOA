package br.com.ifrs.meuifpoa.model.Documento;

/**
 * A classe `DocumentoRequest` encapsula os dados necessários para fazer uma solicitação de documento ao servidor.
 * Essa classe é usada para enviar os parâmetros que o backend precisa para gerar e retornar o documento solicitado.
 * <p>
 * Parâmetros incluem:
 * - Tipo de documento (Histórico, Declaração, etc.)
 * - Senha ou token do usuário, se necessário para autorização.
 */
public class DocumentoRequest {
    private String tipo;
    private String senha;

    /**
     * Construtor da classe DocumentoRequest.
     *
     * @param tipo  the tipo
     * @param senha Senha do usuário para autenticação (se aplicável).
     */
    public DocumentoRequest(String tipo, String senha) {
        this.senha = senha;
        this.tipo = tipo;
    }

    /**
     * Gets senha.
     *
     * @return the senha
     */
    public String getSenha() {
        return senha;
    }

    /**
     * Sets senha.
     *
     * @param senha the senha
     */
    public void setSenha(String senha) {
        this.senha = senha;
    }

    /**
     * Gets tipo documento.
     *
     * @return the tipo documento
     */
    public String getTipoDocumento() {
        return tipo;
    }

    /**
     * Sets tipo documento.
     *
     * @param tipoDocumento the tipo documento
     */
    public void setTipoDocumento(String tipoDocumento) {
        this.tipo = tipo;
    }
}
