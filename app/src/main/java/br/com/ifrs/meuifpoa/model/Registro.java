package br.com.ifrs.meuifpoa.model;

public class Registro {
    private String uid;
    private String nome;
    private String cpf;
    private String email;

    public Registro(String uid, String nome, String cpf, String email) {
        this.uid = uid;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
    }

    public Registro() {

    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }


}
