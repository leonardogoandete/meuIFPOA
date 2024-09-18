package br.com.ifrs.meuifpoa.model.Documento;

public class DocumentoRequest {
    private String tipo;
    private String senha;

    public DocumentoRequest(String tipo, String senha) {
        this.senha = senha;
        this.tipo = tipo;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTipoDocumento() {
        return tipo;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipo = tipo;
    }
}
