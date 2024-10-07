package br.com.ifrs.meuifpoa.model.login;

/**
 * A classe `LoginRequest` encapsula os dados de uma requisição de login para o backend.
 * Contém os dados de autenticação, como nome de usuário e senha.
 */
public class LoginRequest {
    private String cpf;
    private String senha;

    /**
     * Construtor da classe `LoginRequest`.
     *
     * @param cpf   the cpf
     * @param senha the senha
     */
    public LoginRequest(String cpf, String senha) {
        this.cpf = cpf;
        this.senha = senha;
    }

    /**
     * Gets cpf.
     *
     * @return the cpf
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * Gets senha.
     *
     * @return the senha
     */
    public String getSenha() {
        return senha;
    }
}
