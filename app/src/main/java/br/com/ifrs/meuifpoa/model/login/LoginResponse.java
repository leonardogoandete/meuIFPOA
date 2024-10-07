package br.com.ifrs.meuifpoa.model.login;

/**
 * The type Login response.
 */
public class LoginResponse {

    private String token;

    /**
     * Instantiates a new Login response.
     *
     * @param token the token
     */
    public LoginResponse(String token) {
        this.token = token;
    }

    /**
     * Gets token.
     *
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets token.
     *
     * @param token the token
     */
    public void setToken(String token) {
        this.token = token;
    }
}
