package br.com.ifrs.meuifpoa.model.login;

/**
 * Classe que representa um login.
 */
public class Login {
    private String login;
    private String senha;

    /**
     * Construtor da classe Login.
     *
     * @param login o nome de usuário
     * @param senha a senha do usuário
     */
    public Login(String login, String senha) {
        this.login = login;
        this.senha = senha;
    }

    /**
     * Retorna o nome de usuário.
     *
     * @return o nome de usuário
     */
    public String getLogin() {
        return login;
    }

    /**
     * Define o nome de usuário.
     *
     * @param login o novo nome de usuário
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Retorna a senha do usuário.
     *
     * @return a senha do usuário
     */
    public String getSenha() {
        return senha;
    }

    /**
     * Define a senha do usuário.
     *
     * @param senha a nova senha do usuário
     */
    public void setSenha(String senha) {
        this.senha = senha;
    }
}
