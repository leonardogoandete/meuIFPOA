package br.com.ifrs.meuifpoa.model;

/**
 * The type Registro.
 */
public class Registro {
    private String uid;
    private String nome;
    private String cpf;
    private String email;

    /**
     * Instantiates a new Registro.
     *
     * @param uid   the uid
     * @param nome  the nome
     * @param cpf   the cpf
     * @param email the email
     */
    public Registro(String uid, String nome, String cpf, String email) {
        this.uid = uid;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
    }

    /**
     * Instantiates a new Registro.
     */
    public Registro() {

    }

    /**
     * Sets uid.
     *
     * @param uid the uid
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Sets nome.
     *
     * @param nome the nome
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Sets cpf.
     *
     * @param cpf the cpf
     */
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets uid.
     *
     * @return the uid
     */
    public String getUid() {
        return uid;
    }

    /**
     * Gets nome.
     *
     * @return the nome
     */
    public String getNome() {
        return nome;
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
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }


}
