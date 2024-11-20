package br.com.ifrs.meuifpoa.model;


/**
 * A classe Noticia representa uma notícia com suas informações básicas.
 */
public class Noticia {

    /**
     * O ID da notícia.
     */
    private int id;

    /**
     * O link da notícia.
     */
    private String link;

    /**
     * O título da notícia.
     */
    private String titulo;

    /**
     * O resumo da notícia.
     */
    private String resumo;

    /**
     * A data de publicação da notícia.
     */
    private String dataPublicacao;

    /**
     * A hora de publicação da notícia.
     */
    private String horaPublicacao;

    /**
     * Instancia uma nova Noticia.
     *
     * @param link           o link da notícia
     * @param titulo         o título da notícia
     * @param resumo         o resumo da notícia
     * @param dataPublicacao a data de publicação da notícia
     * @param horaPublicacao a hora de publicação da notícia
     */
    public Noticia(String link, String titulo, String resumo, String dataPublicacao, String horaPublicacao) {
        this.link = link;
        this.titulo = titulo;
        this.resumo = resumo;
        this.dataPublicacao = dataPublicacao;
        this.horaPublicacao = horaPublicacao;
    }

    /**
     * Retorna o ID da notícia.
     *
     * @return o ID da notícia
     */
    public int getId() {
        return id;
    }

    /**
     * Retorna o link da notícia.
     *
     * @return o link da notícia
     */
    public String getLink() {
        return link;
    }

    /**
     * Retorna o título da notícia.
     *
     * @return o título da notícia
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Retorna o resumo da notícia.
     *
     * @return o resumo da notícia
     */
    public String getResumo() {
        return resumo;
    }

    /**
     * Retorna a data de publicação da notícia.
     *
     * @return a data de publicação da notícia
     */
    public String getDataPublicacao() {
        return dataPublicacao;
    }

    /**
     * Retorna a hora de publicação da notícia.
     *
     * @return a hora de publicação da notícia
     */
    public String getHoraPublicacao() {
        return horaPublicacao;
    }

    /**
     * Retorna a data e hora de publicação da notícia.
     *
     * @return a data e hora de publicação da notícia
     */
    public String getDataHoraPublicacao() {
        return dataPublicacao + " - " + horaPublicacao;
    }

    /**
     * Define o ID da notícia.
     *
     * @param id o novo ID da notícia
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Define o link da notícia.
     *
     * @param link o novo link da notícia
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Define o título da notícia.
     *
     * @param titulo o novo título da notícia
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Define o resumo da notícia.
     *
     * @param resumo o novo resumo da notícia
     */
    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    /**
     * Define a data de publicação da notícia.
     *
     * @param dataPublicacao a nova data de publicação da notícia
     */
    public void setDataPublicacao(String dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    /**
     * Define a hora de publicação da notícia.
     *
     * @param horaPublicacao a nova hora de publicação da notícia
     */
    public void setHoraPublicacao(String horaPublicacao) {
        this.horaPublicacao = horaPublicacao;
    }
}
