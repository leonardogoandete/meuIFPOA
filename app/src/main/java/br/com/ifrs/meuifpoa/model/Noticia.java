package br.com.ifrs.meuifpoa.model;



public class Noticia {

    private int id;

    private String link;
    private String titulo;
    private String resumo;
    private String dataPublicacao;
    private String horaPublicacao;

    public Noticia(String link, String titulo, String resumo, String dataPublicacao, String horaPublicacao) {
        this.link = link;
        this.titulo = titulo;
        this.resumo = resumo;
        this.dataPublicacao = dataPublicacao;
        this.horaPublicacao = horaPublicacao;
    }

    public int getId() {
        return id;
    }

    public String getLink() {
        return link;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getResumo() {
        return resumo;
    }

    public String getDataPublicacao() {
        return dataPublicacao;
    }

    public String getHoraPublicacao() {
        return horaPublicacao;
    }

    public String getDataHoraPublicacao() {
        return dataPublicacao + " - " + horaPublicacao;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public void setDataPublicacao(String dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public void setHoraPublicacao(String horaPublicacao) {
        this.horaPublicacao = horaPublicacao;
    }
}
