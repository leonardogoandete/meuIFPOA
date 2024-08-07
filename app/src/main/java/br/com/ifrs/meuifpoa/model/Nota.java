package br.com.ifrs.meuifpoa.model;

import java.io.Serializable;
public class Nota implements Serializable{
    private String codigoDisciplina;
    private String nomeDisciplina;
    private String primeiraUnidade;
    private String segundaUnidade;
    private String notaRecuperacao;
    private String notaFinal;
    private String numeroFaltas;
    private String situacao;

    public Nota() {
    }
    public Nota(String codigoDisciplina, String nomeDisciplina, String primeiraUnidade, String segundaUnidade, String notaRecuperacao, String notaFinal, String numeroFaltas, String situacao) {
        this.codigoDisciplina = codigoDisciplina;
        this.nomeDisciplina = nomeDisciplina;
        this.primeiraUnidade = primeiraUnidade;
        this.segundaUnidade = segundaUnidade;
        this.notaRecuperacao = notaRecuperacao;
        this.notaFinal = notaFinal;
        this.numeroFaltas = numeroFaltas;
        this.situacao = situacao;
    }

    public String getCodigoDisciplina() {
        return codigoDisciplina;
    }

    public String getNomeDisciplina() {
        return nomeDisciplina;
    }

    public String getPrimeiraUnidade() {
        return primeiraUnidade;
    }

    public String getSegundaUnidade() {
        return segundaUnidade;
    }

    public String getNotaRecuperacao() {
        return notaRecuperacao;
    }

    public String getNotaFinal() {
        return notaFinal;
    }

    public String getNumeroFaltas() {
        return numeroFaltas;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setCodigoDisciplina(String codigoDisciplina) {
        this.codigoDisciplina = codigoDisciplina;
    }

    public void setNomeDisciplina(String nomeDisciplina) {
        this.nomeDisciplina = nomeDisciplina;
    }

    public void setPrimeiraUnidade(String primeiraUnidade) {
        this.primeiraUnidade = primeiraUnidade;
    }

    public void setSegundaUnidade(String segundaUnidade) {
        this.segundaUnidade = segundaUnidade;
    }

    public void setNotaRecuperacao(String notaRecuperacao) {
        this.notaRecuperacao = notaRecuperacao;
    }

    public void setNotaFinal(String notaFinal) {
        this.notaFinal = notaFinal;
    }

    public void setNumeroFaltas(String numeroFaltas) {
        this.numeroFaltas = numeroFaltas;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }
}
