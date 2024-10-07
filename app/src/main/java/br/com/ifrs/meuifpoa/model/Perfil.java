package br.com.ifrs.meuifpoa.model;

/**
 * The type Perfil.
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
    private String imgSrc;

    /**
     * Instantiates a new Perfil.
     */
    public Perfil(){}

    /**
     * Instantiates a new Perfil.
     *
     * @param nomeDocente the nome docente
     * @param matricula   the matricula
     * @param curso       the curso
     * @param nivel       the nivel
     * @param status      the status
     * @param anoIngresso the ano ingresso
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
     * Instantiates a new Perfil.
     *
     * @param nomeDocente            the nome docente
     * @param matricula              the matricula
     * @param cpf                    the cpf
     * @param curso                  the curso
     * @param nivel                  the nivel
     * @param status                 the status
     * @param anoIngresso            the ano ingresso
     * @param email                  the email
     * @param imgSrc                 the img src
     * @param chObrigatoriaPendente  the ch obrigatoria pendente
     * @param chOptativaPendente     the ch optativa pendente
     * @param chTotalCurriculo       the ch total curriculo
     * @param chComplementarPendente the ch complementar pendente
     * @param integralizado          the integralizado
     */
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

    /**
     * Gets nome docente.
     *
     * @return the nome docente
     */
    public String getNomeDocente() {
        return nomeDocente;
    }

    /**
     * Sets nome docente.
     *
     * @param nomeDocente the nome docente
     */
    public void setNomeDocente(String nomeDocente) {
        this.nomeDocente = nomeDocente;
    }

    /**
     * Gets matricula.
     *
     * @return the matricula
     */
    public String getMatricula() {
        return matricula;
    }

    /**
     * Sets matricula.
     *
     * @param matricula the matricula
     */
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    /**
     * Gets curso.
     *
     * @return the curso
     */
    public String getCurso() {
        return curso;
    }

    /**
     * Sets curso.
     *
     * @param curso the curso
     */
    public void setCurso(String curso) {
        this.curso = curso;
    }

    /**
     * Gets nivel.
     *
     * @return the nivel
     */
    public String getNivel() {
        return nivel;
    }

    /**
     * Sets nivel.
     *
     * @param nivel the nivel
     */
    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets ano ingresso.
     *
     * @return the ano ingresso
     */
    public String getAnoIngresso() {
        return anoIngresso;
    }

    /**
     * Sets ano ingresso.
     *
     * @param anoIngresso the ano ingresso
     */
    public void setAnoIngresso(String anoIngresso) {
        this.anoIngresso = anoIngresso;
    }

    /**
     * Gets ch obrigatoria pendente.
     *
     * @return the ch obrigatoria pendente
     */
    public String getChObrigatoriaPendente() {
        return chObrigatoriaPendente;
    }

    /**
     * Sets ch obrigatoria pendente.
     *
     * @param chObrigatoriaPendente the ch obrigatoria pendente
     */
    public void setChObrigatoriaPendente(String chObrigatoriaPendente) {
        this.chObrigatoriaPendente = chObrigatoriaPendente;
    }

    /**
     * Gets ch optativa pendente.
     *
     * @return the ch optativa pendente
     */
    public String getChOptativaPendente() {
        return chOptativaPendente;
    }

    /**
     * Sets ch optativa pendente.
     *
     * @param chOptativaPendente the ch optativa pendente
     */
    public void setChOptativaPendente(String chOptativaPendente) {
        this.chOptativaPendente = chOptativaPendente;
    }

    /**
     * Gets ch total curriculo.
     *
     * @return the ch total curriculo
     */
    public String getChTotalCurriculo() {
        return chTotalCurriculo;
    }

    /**
     * Sets ch total curriculo.
     *
     * @param chTotalCurriculo the ch total curriculo
     */
    public void setChTotalCurriculo(String chTotalCurriculo) {
        this.chTotalCurriculo = chTotalCurriculo;
    }

    /**
     * Gets ch complementar pendente.
     *
     * @return the ch complementar pendente
     */
    public String getChComplementarPendente() {
        return chComplementarPendente;
    }

    /**
     * Sets ch complementar pendente.
     *
     * @param chComplementarPendente the ch complementar pendente
     */
    public void setChComplementarPendente(String chComplementarPendente) {
        this.chComplementarPendente = chComplementarPendente;
    }

    /**
     * Gets integralizado.
     *
     * @return the integralizado
     */
    public String getIntegralizado() {
        return integralizado;
    }

    /**
     * Sets integralizado.
     *
     * @param integralizado the integralizado
     */
    public void setIntegralizado(String integralizado) {
        this.integralizado = integralizado;
    }

    /**
     * Gets img src.
     *
     * @return the img src
     */
    public String getImgSrc() {
        return imgSrc;
    }

    /**
     * Sets img src.
     *
     * @param imgSrc the img src
     */
    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }
}
