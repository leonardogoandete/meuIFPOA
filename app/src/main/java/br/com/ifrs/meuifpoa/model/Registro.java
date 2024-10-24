package br.com.ifrs.meuifpoa.model;

/**
 * Classe que representa um registro.
 */
public class Registro {
    /**
     * O identificador único.
     */
    private String uid;

    /**
     * O nome do registro.
     */
    private String nome;

    /**
     * O CPF do registro.
     */
    private String cpf;

    /**
     * O email do registro.
     */
    private String email;

    /**
     * Construtor que inicializa um registro com os parâmetros fornecidos.
     *
     * @param uid   o identificador único
     * @param nome  o nome do registro
     * @param cpf   o CPF do registro
     * @param email o email do registro
     */
    public Registro(String uid, String nome, String cpf, String email) {
        this.uid = uid;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
    }

    /**
     * Construtor padrão.
     */
    public Registro() {
    }

    /**
     * Define o identificador único.
     *
     * @param uid o novo identificador único
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Define o nome.
     *
     * @param nome o novo nome
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Define o CPF.
     *
     * @param cpf o novo CPF
     */
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    /**
     * Define o email.
     *
     * @param email o novo email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retorna o identificador único.
     *
     * @return o identificador único
     */
    public String getUid() {
        return uid;
    }

    /**
     * Retorna o nome.
     *
     * @return o nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * Retorna o CPF.
     *
     * @return o CPF
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * Retorna o email.
     *
     * @return o email
     */
    public String getEmail() {
        return email;
    }
}
