package br.com.ifrs.meuifpoa.model;

public class Perfil {
    private String nomeDocente;
    private String matricula;
    private String curso;
    private String nivel;
    private String status;
    private String anoIngresso;

    public Perfil(){}

    public Perfil(String nomeDocente, String matricula, String curso, String nivel, String status, String anoIngresso) {
        this.nomeDocente = nomeDocente;
        this.matricula = matricula;
        this.curso = curso;
        this.nivel = nivel;
        this.status = status;
        this.anoIngresso = anoIngresso;
    }

    public String getNomeDocente() {
        return nomeDocente;
    }

    public void setNomeDocente(String nomeDocente) {
        this.nomeDocente = nomeDocente;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAnoIngresso() {
        return anoIngresso;
    }

    public void setAnoIngresso(String anoIngresso) {
        this.anoIngresso = anoIngresso;
    }
}
