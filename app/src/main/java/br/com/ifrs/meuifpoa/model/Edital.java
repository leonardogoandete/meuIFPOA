package br.com.ifrs.meuifpoa.model;


/**
 * A classe Edital representa uma edital com suas informações básicas.
 */
public class Edital {

    private int id;

    private String link;
    private String titulo;
    private String dataPublicacao;

    /**
     * Instancia uma nova Noticia.
     *
     * @param link           o link da notícia
     * @param titulo         o título da notícia
     * @param dataPublicacao a data de publicação da notícia
     */
    public Edital(String link, String titulo, String dataPublicacao) {
        this.link = link;
        this.titulo = titulo;
        this.dataPublicacao = dataPublicacao;
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
     * Retorna a data de publicação da notícia.
     *
     * @return a data de publicação da notícia
     */
    public String getDataPublicacao() {
        return dataPublicacao;
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
     * Define a data de publicação da notícia.
     *
     * @param dataPublicacao a nova data de publicação da notícia
     */
    public void setDataPublicacao(String dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }
}
