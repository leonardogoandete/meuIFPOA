package br.com.ifrs.meuifpoa.model;


/**
 * A classe Edital representa uma edital com suas informações básicas.
 */
public class Edital {

    /**
     * O link do edital.
     */
    private String link;
    /**
     * O título do edital.
     */
    private String titulo;
    /**
     * A data de publicação do edital.
     */
    private String dataPublicacaoEdital;

    /**
     * Instancia um novo Edital.
     *
     * @param link           o link do edital.
     * @param titulo         o título do edital.
     * @param dataPublicacaoEdital a data de publicação do edital.
     */
    public Edital(String link, String titulo, String dataPublicacaoEdital) {
        this.link = link;
        this.titulo = titulo;
        this.dataPublicacaoEdital = dataPublicacaoEdital;
    }

    /**
     * Retorna o link do edital.
     *
     * @return o link do edital.
     */
    public String getLink() {
        return link;
    }

    /**
     * Retorna o título do edital.
     *
     * @return o título do edital.
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Retorna a data de publicação do edital.
     *
     * @return a data de publicação do edital.
     */
    public String getDataPublicacao() {
        return dataPublicacaoEdital;
    }

    /**
     * Define o link do edital.
     *
     * @param link o novo link do edital.
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Define o título do edital.
     *
     * @param titulo o novo título do edital.
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Define a data de publicação do edital.
     *
     * @param dataPublicacaoEdital a nova data de publicação do edital.
     */
    public void setDataPublicacao(String dataPublicacaoEdital) {
        this.dataPublicacaoEdital = dataPublicacaoEdital;
    }
}
