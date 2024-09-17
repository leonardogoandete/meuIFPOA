package br.com.ifrs.meuifpoa.model;

public class Perfil {
    private String nomeDocente;
    private String matricula;
    private String curso;
    private String nivel;
    private String status;
    private String anoIngresso;
    private String chObrigatoriaPendente;
    private String chOptativaPendente;
    private String chTotalCurriculo;
    private String chComplementarPendente;
    private String integralizado;

    public Perfil(){}

    public Perfil(String nomeDocente, String matricula, String curso, String nivel, String status, String anoIngresso) {
        this.nomeDocente = nomeDocente;
        this.matricula = matricula;
        this.curso = curso;
        this.nivel = nivel;
        this.status = status;
        this.anoIngresso = anoIngresso;
    }

    public Perfil(String nomeDocente, String matricula, String cpf, String curso, String nivel, String status, String anoIngresso, String email, String imgSrc, String chObrigatoriaPendente, String chOptativaPendente, String chTotalCurriculo, String chComplementarPendente, String integralizado){
        this.nomeDocente = nomeDocente;
        this.matricula = matricula;
        this.curso = curso;
        this.nivel = nivel;
        this.status = status;
        this.anoIngresso = anoIngresso;
        this.chObrigatoriaPendente = chObrigatoriaPendente;
        this.chOptativaPendente = chOptativaPendente;
        this.chTotalCurriculo = chTotalCurriculo;
        this.chComplementarPendente = chComplementarPendente;
        this.integralizado = integralizado;
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

    public String getChObrigatoriaPendente() {
        return chObrigatoriaPendente;
    }

    public void setChObrigatoriaPendente(String chObrigatoriaPendente) {
        this.chObrigatoriaPendente = chObrigatoriaPendente;
    }

    public String getChOptativaPendente() {
        return chOptativaPendente;
    }

    public void setChOptativaPendente(String chOptativaPendente) {
        this.chOptativaPendente = chOptativaPendente;
    }

    public String getChTotalCurriculo() {
        return chTotalCurriculo;
    }

    public void setChTotalCurriculo(String chTotalCurriculo) {
        this.chTotalCurriculo = chTotalCurriculo;
    }

    public String getChComplementarPendente() {
        return chComplementarPendente;
    }

    public void setChComplementarPendente(String chComplementarPendente) {
        this.chComplementarPendente = chComplementarPendente;
    }

    public String getIntegralizado() {
        return integralizado;
    }

    public void setIntegralizado(String integralizado) {
        this.integralizado = integralizado;
    }
}
