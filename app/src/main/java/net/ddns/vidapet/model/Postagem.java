package net.ddns.vidapet.model;

/**
 * Created by Jose on 15/06/2017.
 */

public class Postagem {


    private String descricao;
    private String titulo;
    private String imagem;
    private String nome, email, imagemUser;

    public Postagem(){

    }

    public Postagem(String descricao, String titulo, String imagem, String nome, String imagemUser) {
        this.descricao = descricao;
        this.titulo = titulo;
        this.imagem = imagem;
        this.nome = nome;
        this.email = email;
        this.imagemUser = imagemUser;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImagemUser() {return imagemUser;}

    public void setImagemUser(String imagemUser) {this.imagemUser = imagemUser;}
}
