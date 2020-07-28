package net.ddns.vidapet.model;

/**
 * Created by Lafayete on 25/06/2017.
 */

public class Pet {

    private String id, nome, idade, descricao, sexo, raca, foto, tamanho, vacinado_raiva, vacinado_v8_v10, amigavel, castrado, emailDono;



    //METODOS ESPECIAS

    public Pet() {
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getTamanho() {
        return tamanho;
    }

    public void setTamanho(String tamanho) {
        this.tamanho = tamanho;
    }

    public String getVacinado_raiva() {
        return vacinado_raiva;
    }

    public void setVacinado_raiva(String vacinado_raiva) {
        this.vacinado_raiva = vacinado_raiva;
    }

    public String getVacinado_v8_v10() {
        return vacinado_v8_v10;
    }

    public void setVacinado_v8_v10(String vacinado_v8_v10) {
        this.vacinado_v8_v10 = vacinado_v8_v10;
    }

    public String getAmigavel() {
        return amigavel;
    }

    public void setAmigavel(String amigavel) {
        this.amigavel = amigavel;
    }

    public String getCastrado() {
        return castrado;
    }

    public void setCastrado(String castrado) {
        this.castrado = castrado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmailDono() {
        return emailDono;
    }

    public void setEmailDono(String emailDono) {
        this.emailDono = emailDono;
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }
}
