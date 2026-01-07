package br.com.ifrs.meuifpoa.model;

import java.io.Serializable;

/**
 * Classe que representa uma Nota.
 */
public class Nota implements Serializable{
    private String codigoDisciplina;
    private String nomeDisciplina;
    private String primeiraUnidade;
    private String segundaUnidade;
    private String notaRecuperacao;
    private String notaFinal;
    private String numeroFaltas;
    private String situacao;

    /**
     * Construtor padrão da classe Nota.
     */
    public Nota() {
    }

    /**
     * Construtor da classe Nota.
     *
     * @param codigoDisciplina o código da disciplina
     * @param nomeDisciplina o nome da disciplina
     * @param primeiraUnidade a nota da primeira unidade
     * @param segundaUnidade a nota da segunda unidade
     * @param notaRecuperacao a nota de recuperação
     * @param notaFinal a nota final
     * @param numeroFaltas o número de faltas
     * @param situacao a situação
     */
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

    /**
     * Retorna o código da disciplina.
     *
     * @return o código da disciplina
     */
    public String getCodigoDisciplina() {
        return codigoDisciplina;
    }

    /**
     * Retorna o nome da disciplina.
     *
     * @return o nome da disciplina
     */
    public String getNomeDisciplina() {
        return nomeDisciplina;
    }

    /**
     * Retorna a nota da primeira unidade.
     *
     * @return a nota da primeira unidade
     */
    public String getPrimeiraUnidade() {
        return primeiraUnidade;
    }

    /**
     * Retorna a nota da segunda unidade.
     *
     * @return a nota da segunda unidade
     */
    public String getSegundaUnidade() {
        return segundaUnidade;
    }

    /**
     * Retorna a nota de recuperação.
     *
     * @return a nota de recuperação
     */
    public String getNotaRecuperacao() {
        return notaRecuperacao;
    }

    /**
     * Retorna a nota final.
     *
     * @return a nota final
     */
    public String getNotaFinal() {
        return notaFinal;
    }

    /**
     * Retorna o número de faltas.
     *
     * @return o número de faltas
     */
    public String getNumeroFaltas() {
        return numeroFaltas;
    }

    /**
     * Retorna a situação.
     *
     * @return a situação
     */
    public String getSituacao() {
        return situacao;
    }

    /**
     * Define o código da disciplina.
     *
     * @param codigoDisciplina o novo código da disciplina
     */
    public void setCodigoDisciplina(String codigoDisciplina) {
        this.codigoDisciplina = codigoDisciplina;
    }

    /**
     * Define o nome da disciplina.
     *
     * @param nomeDisciplina o novo nome da disciplina
     */
    public void setNomeDisciplina(String nomeDisciplina) {
        this.nomeDisciplina = nomeDisciplina;
    }

    /**
     * Define a nota da primeira unidade.
     *
     * @param primeiraUnidade a nova nota da primeira unidade
     */
    public void setPrimeiraUnidade(String primeiraUnidade) {
        this.primeiraUnidade = primeiraUnidade;
    }

    /**
     * Define a nota da segunda unidade.
     *
     * @param segundaUnidade a nova nota da segunda unidade
     */
    public void setSegundaUnidade(String segundaUnidade) {
        this.segundaUnidade = segundaUnidade;
    }

    /**
     * Define a nota de recuperação.
     *
     * @param notaRecuperacao a nova nota de recuperação
     */
    public void setNotaRecuperacao(String notaRecuperacao) {
        this.notaRecuperacao = notaRecuperacao;
    }

    /**
     * Define a nota final.
     *
     * @param notaFinal a nova nota final
     */
    public void setNotaFinal(String notaFinal) {
        this.notaFinal = notaFinal;
    }

    /**
     * Define o número de faltas.
     *
     * @param numeroFaltas o novo número de faltas
     */
    public void setNumeroFaltas(String numeroFaltas) {
        this.numeroFaltas = numeroFaltas;
    }

    /**
     * Define a situação.
     *
     * @param situacao a nova situação
     */
    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }
}
