package br.com.ifrs.meuifpoa.model;

import java.util.ArrayList;

/**
 * A classe Perfil representa o perfil de um estudante ou docente.
 */
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
    private String imgPerfil;
    private ArrayList<Nota> notas;

    /**
     * Instancia um novo Perfil.
     */
    public Perfil(){}

    /**
     * Instancia um novo Perfil.
     *
     * @param nomeDocente o nome do docente
     * @param matricula   a matrícula
     * @param curso       o curso
     * @param nivel       o nível
     * @param status      o status
     * @param anoIngresso o ano de ingresso
     */
    public Perfil(String nomeDocente, String matricula, String curso, String nivel, String status, String anoIngresso) {
        this.nomeDocente = nomeDocente;
        this.matricula = matricula;
        this.curso = curso;
        this.nivel = nivel;
        this.status = status;
        this.anoIngresso = anoIngresso;
    }

    /**
     * Instancia um novo Perfil.
     *
     * @param nomeDocente            o nome do docente
     * @param matricula              a matrícula
     * @param cpf                    o CPF
     * @param curso                  o curso
     * @param nivel                  o nível
     * @param status                 o status
     * @param anoIngresso            o ano de ingresso
     * @param email                  o email
     * @param imgPerfil                 a URL da imagem
     * @param chObrigatoriaPendente  a carga horária obrigatória pendente
     * @param chOptativaPendente     a carga horária optativa pendente
     * @param chTotalCurriculo       a carga horária total do currículo
     * @param chComplementarPendente a carga horária complementar pendente
     * @param integralizado          o status de integralização
     * @param notas                  a lista de notas
     */
    public Perfil(String nomeDocente, String matricula, String cpf, String curso, String nivel, String status, String anoIngresso, String email, String imgPerfil, String chObrigatoriaPendente, String chOptativaPendente, String chTotalCurriculo, String chComplementarPendente, String integralizado, ArrayList<Nota> notas){
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
        this.imgPerfil = imgPerfil;
        this.notas = notas;
    }

    /**
     * Retorna o nome do docente.
     *
     * @return o nome do docente
     */
    public String getNomeDocente() {
        return nomeDocente;
    }
    /**
 * Define o nome do docente.
     *
 * @param nomeDocente o novo nome do docente
     */
    public void setNomeDocente(String nomeDocente) {
        this.nomeDocente = nomeDocente;
    }

    /**
     * Retorna a matrícula.
     *
     * @return a matrícula
     */
    public String getMatricula() {
        return matricula;
    }

    /**
     * Define a matrícula.
     *
     * @param matricula a nova matrícula
     */
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    /**
     * Retorna o curso.
     *
     * @return o curso
     */
    public String getCurso() {
        return curso;
    }

    /**
     * Define o curso.
     *
     * @param curso o novo curso
     */
    public void setCurso(String curso) {
        this.curso = curso;
    }

    /**
     * Retorna o nível.
     *
     * @return o nível
     */
    public String getNivel() {
        return nivel;
    }

    /**
     * Define o nível.
     *
     * @param nivel o novo nível
     */
    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    /**
     * Retorna o status.
     *
     * @return o status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Define o status.
     *
     * @param status o novo status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     /**
      * Retorna o ano de ingresso.
     *
      * @return o ano de ingresso
     */
    public String getAnoIngresso() {
        return anoIngresso;
    }

    /**
     * Define o ano de ingresso.
     *
     * @param anoIngresso o novo ano de ingresso
     */
    public void setAnoIngresso(String anoIngresso) {
        this.anoIngresso = anoIngresso;
    }

    /**
     * Retorna a carga horária obrigatória pendente.
     *
     * @return a carga horária obrigatória pendente
     */
    public String getChObrigatoriaPendente() {
        return chObrigatoriaPendente;
    }

    /**
     * Define a carga horária obrigatória pendente.
     *
     * @param chObrigatoriaPendente a nova carga horária obrigatória pendente
     */
    public void setChObrigatoriaPendente(String chObrigatoriaPendente) {
        this.chObrigatoriaPendente = chObrigatoriaPendente;
    }

    /**
     /**
      * Retorna a carga horária optativa pendente.
     *
      * @return a carga horária optativa pendente
     */
    public String getChOptativaPendente() {
        return chOptativaPendente;
    }

    /**
     /**
      * Define a carga horária optativa pendente.
     *
      * @param chOptativaPendente a nova carga horária optativa pendente
     */
    public void setChOptativaPendente(String chOptativaPendente) {
        this.chOptativaPendente = chOptativaPendente;
    }

    /**
     /**
      * Retorna a carga horária total do currículo.
     *
      * @return a carga horária total do currículo
     */
    public String getChTotalCurriculo() {
        return chTotalCurriculo;
    }

    /**
     /**
      * Define a carga horária total do currículo.
     *
      * @param chTotalCurriculo a nova carga horária total do currículo
     */
    public void setChTotalCurriculo(String chTotalCurriculo) {
        this.chTotalCurriculo = chTotalCurriculo;
    }

    /**
     /**
      * Retorna a carga horária complementar pendente.
     *
      * @return a carga horária complementar pendente
     */
    public String getChComplementarPendente() {
        return chComplementarPendente;
    }

    /**
     /**
      * Define a carga horária complementar pendente.
     *
      * @param chComplementarPendente a nova carga horária complementar pendente
     */
    public void setChComplementarPendente(String chComplementarPendente) {
        this.chComplementarPendente = chComplementarPendente;
    }

    /**
     /**
      * Retorna o status de integralização.
     *
      * @return o status de integralização
     */
    public String getIntegralizado() {
        return integralizado;
    }

    /**
     /**
      * Define o status de integralização.
     *
      * @param integralizado o novo status de integralização
     */
    public void setIntegralizado(String integralizado) {
        this.integralizado = integralizado;
    }

    /**
     /**
      * Retorna a URL da imagem.
     *
      * @return a URL da imagem
     */
    public String getImgPerfil() {
        return imgPerfil;
    }

    /**
     /**
      * Define a URL da imagem.
     *
      * @param imgPerfil a nova URL da imagem
     */
    public void setImgPerfil(String imgPerfil) {
        this.imgPerfil = imgPerfil;
    }

    /**
     * Retorna a lista de notas.
     *
     * @return a lista de notas
     */
    public ArrayList<Nota> getNotas() {
        return notas;
    }

    /**
     * Define a lista de notas.
     *
     * @param notas a nova lista de notas
     */
    public void setNotas(ArrayList<Nota> notas) {
        this.notas = notas;
    }
}
