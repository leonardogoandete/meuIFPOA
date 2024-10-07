package br.com.ifrs.meuifpoa.model.login;

/**
 * Classe que representa a resposta de um login.
 */
public class LoginResponse {

    private String token;

    /**
     * Construtor da classe LoginResponse.
     *
     * @param token o token de autenticação
     */
    public LoginResponse(String token) {
        this.token = token;
    }

    /**
     /**
      * Retorna o token de autenticação.
     *
      * @return o token de autenticação
     */
    public String getToken() {
        return token;
    }

    /**
     * Define o token de autenticação.
     *
     * @param token o novo token de autenticação
     */
    public void setToken(String token) {
        this.token = token;
    }
}
