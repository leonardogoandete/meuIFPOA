package br.com.ifrs.meuifpoa.model;

import java.io.Serializable;

/**
 * The type Nota.
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
     * Instantiates a new Nota.
     */
    public Nota() {
    }

    /**
     * Instantiates a new Nota.
     *
     * @param codigoDisciplina the codigo disciplina
     * @param nomeDisciplina   the nome disciplina
     * @param primeiraUnidade  the primeira unidade
     * @param segundaUnidade   the segunda unidade
     * @param notaRecuperacao  the nota recuperacao
     * @param notaFinal        the nota final
     * @param numeroFaltas     the numero faltas
     * @param situacao         the situacao
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
     * Gets codigo disciplina.
     *
     * @return the codigo disciplina
     */
    public String getCodigoDisciplina() {
        return codigoDisciplina;
    }

    /**
     * Gets nome disciplina.
     *
     * @return the nome disciplina
     */
    public String getNomeDisciplina() {
        return nomeDisciplina;
    }

    /**
     * Gets primeira unidade.
     *
     * @return the primeira unidade
     */
    public String getPrimeiraUnidade() {
        return primeiraUnidade;
    }

    /**
     * Gets segunda unidade.
     *
     * @return the segunda unidade
     */
    public String getSegundaUnidade() {
        return segundaUnidade;
    }

    /**
     * Gets nota recuperacao.
     *
     * @return the nota recuperacao
     */
    public String getNotaRecuperacao() {
        return notaRecuperacao;
    }

    /**
     * Gets nota final.
     *
     * @return the nota final
     */
    public String getNotaFinal() {
        return notaFinal;
    }

    /**
     * Gets numero faltas.
     *
     * @return the numero faltas
     */
    public String getNumeroFaltas() {
        return numeroFaltas;
    }

    /**
     * Gets situacao.
     *
     * @return the situacao
     */
    public String getSituacao() {
        return situacao;
    }

    /**
     * Sets codigo disciplina.
     *
     * @param codigoDisciplina the codigo disciplina
     */
    public void setCodigoDisciplina(String codigoDisciplina) {
        this.codigoDisciplina = codigoDisciplina;
    }

    /**
     * Sets nome disciplina.
     *
     * @param nomeDisciplina the nome disciplina
     */
    public void setNomeDisciplina(String nomeDisciplina) {
        this.nomeDisciplina = nomeDisciplina;
    }

    /**
     * Sets primeira unidade.
     *
     * @param primeiraUnidade the primeira unidade
     */
    public void setPrimeiraUnidade(String primeiraUnidade) {
        this.primeiraUnidade = primeiraUnidade;
    }

    /**
     * Sets segunda unidade.
     *
     * @param segundaUnidade the segunda unidade
     */
    public void setSegundaUnidade(String segundaUnidade) {
        this.segundaUnidade = segundaUnidade;
    }

    /**
     * Sets nota recuperacao.
     *
     * @param notaRecuperacao the nota recuperacao
     */
    public void setNotaRecuperacao(String notaRecuperacao) {
        this.notaRecuperacao = notaRecuperacao;
    }

    /**
     * Sets nota final.
     *
     * @param notaFinal the nota final
     */
    public void setNotaFinal(String notaFinal) {
        this.notaFinal = notaFinal;
    }

    /**
     * Sets numero faltas.
     *
     * @param numeroFaltas the numero faltas
     */
    public void setNumeroFaltas(String numeroFaltas) {
        this.numeroFaltas = numeroFaltas;
    }

    /**
     * Sets situacao.
     *
     * @param situacao the situacao
     */
    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }
}
