package br.com.ifrs.meuifpoa.model.login;

/**
 * Classe que representa uma requisição de login.
 */
public class LoginRequest {
    private String cpf;
    private String senha;

    /**
     * Construtor da classe LoginRequest.
     *
     * @param cpf o CPF do usuário
     * @param senha a senha do usuário
     */
    public LoginRequest(String cpf, String senha) {
        this.cpf = cpf;
        this.senha = senha;
    }

    /**
     * Retorna o CPF do usuário.
     *
     * @return o CPF do usuário
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * Retorna a senha do usuário.
     *
     * @return a senha do usuário
     */
    public String getSenha() {
        return senha;
    }
}
