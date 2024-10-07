package br.com.ifrs.meuifpoa.model.login;

/**
 * A classe `Login` representa o modelo de dados para uma requisição de login.
 * Armazena o nome de usuário, senha e qualquer outro campo necessário para autenticação.
 */
public class Login {
    private String login;
    private String senha;

    /**
     * Construtor da classe `Login`.
     *
     * @param login the login
     * @param senha the senha
     */
    public Login(String login, String senha) {
        this.login = login;
        this.senha = senha;
    }


}
