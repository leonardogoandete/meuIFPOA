package br.com.ifrs.meuifpoa.model;

/**
 * Classe que representa a resposta de uma sincronização.
 */
public class SyncResponse {
    /**
     * A mensagem de erro retornada pela sincronização.
     */
    private String erro;

    /**
     * Obtém a mensagem de erro.
     *
     * @return A mensagem de erro.
     */
    public String getErro() {
        return erro;
    }

    /**
     * Define a mensagem de erro.
     *
     * @param erro A mensagem de erro a ser definida.
     */
    public void setErro(String erro) {
        this.erro = erro;
    }
}
