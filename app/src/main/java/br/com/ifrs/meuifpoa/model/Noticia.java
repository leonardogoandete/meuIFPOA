package br.com.ifrs.meuifpoa.model;


/**
 * The type Noticia.
 */
public class Noticia {

    private int id;

    private String link;
    private String titulo;
    private String resumo;
    private String dataPublicacao;
    private String horaPublicacao;

    /**
     * Instantiates a new Noticia.
     *
     * @param link           the link
     * @param titulo         the titulo
     * @param resumo         the resumo
     * @param dataPublicacao the data publicacao
     * @param horaPublicacao the hora publicacao
     */
    public Noticia(String link, String titulo, String resumo, String dataPublicacao, String horaPublicacao) {
        this.link = link;
        this.titulo = titulo;
        this.resumo = resumo;
        this.dataPublicacao = dataPublicacao;
        this.horaPublicacao = horaPublicacao;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets link.
     *
     * @return the link
     */
    public String getLink() {
        return link;
    }

    /**
     * Gets titulo.
     *
     * @return the titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Gets resumo.
     *
     * @return the resumo
     */
    public String getResumo() {
        return resumo;
    }

    /**
     * Gets data publicacao.
     *
     * @return the data publicacao
     */
    public String getDataPublicacao() {
        return dataPublicacao;
    }

    /**
     * Gets hora publicacao.
     *
     * @return the hora publicacao
     */
    public String getHoraPublicacao() {
        return horaPublicacao;
    }

    /**
     * Gets data hora publicacao.
     *
     * @return the data hora publicacao
     */
    public String getDataHoraPublicacao() {
        return dataPublicacao + " - " + horaPublicacao;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets link.
     *
     * @param link the link
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Sets titulo.
     *
     * @param titulo the titulo
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Sets resumo.
     *
     * @param resumo the resumo
     */
    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    /**
     * Sets data publicacao.
     *
     * @param dataPublicacao the data publicacao
     */
    public void setDataPublicacao(String dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    /**
     * Sets hora publicacao.
     *
     * @param horaPublicacao the hora publicacao
     */
    public void setHoraPublicacao(String horaPublicacao) {
        this.horaPublicacao = horaPublicacao;
    }
}
