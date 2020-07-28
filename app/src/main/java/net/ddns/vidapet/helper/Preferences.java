package net.ddns.vidapet.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    private Context contexto;
    private SharedPreferences preferences;
    private final String NOME_ARQUIVO = "petPreferences";
    private int MODE = 0;
    private SharedPreferences.Editor editor;
    private final String CHAVE_IDENTIFICADOR = "identificadorUsuario";
    //USUARIO
    private final String CHAVE_NOME = "nome";
    private final String CHAVE_SOBRENOME = "sobrenome";
    private final String CHAVE_IDADE = "idade";
    private final String CHAVE_CPF = "cpf";

    private final String CHAVE_ENDERECO = "endereco";
    private final String CHAVE_RUA = "rua";
    private final String CHAVE_NUMERO = "numero";
    private final String CHAVE_CEP = "cep";
    private final String CHAVE_BAIRRO = "bairro";
    private final String CHAVE_CIDADE = "cidade";

    private final String CHAVE_CELULAR = "celular";
    private final String CHAVE_TELEFONE = "telefone";
    private final String CHAVE_CODPAIS = "codpais";
    private final String CHAVE_CODAREA = "codarea";
    private final String CHAVE_SENHA = "senha";

    private final String CHAVE_FOTO = "foto";

    //PET
    private final String CHAVE_IDPET = "idpet";
    private final String CHAVE_NOMEPET = "nomepet";
    private final String CHAVE_IDADEPET = "idadepet";
    private final String CHAVE_SEXOPET = "sexopet";
    private final String CHAVE_DESCRICAOPET = "descricaopet";
    private final String CHAVE_FOTOPET = "fotopet";


    public Preferences(Context contextoParametro){
        contexto = contextoParametro;
        preferences = contexto.getSharedPreferences(NOME_ARQUIVO, MODE);
        editor = preferences.edit();
    }


    public void salvarDados(String identificador, String nome, String sobrenome,
                            String idade, String celular, String cpf, String senha, String endereco) {
        editor.putString(CHAVE_IDENTIFICADOR, identificador);
        editor.putString(CHAVE_NOME, nome);
        editor.putString(CHAVE_SOBRENOME, sobrenome);
        editor.putString(CHAVE_IDADE, idade);
        editor.putString(CHAVE_CELULAR, celular);
        editor.putString(CHAVE_CPF, cpf);
        editor.putString(CHAVE_SENHA, senha);
        editor.putString(CHAVE_ENDERECO, endereco);
        editor.commit();
    }

    public void salvarSenha(String senha){
        editor.putString(CHAVE_SENHA, senha);
        editor.commit();
    }

    public void salvarFotoPerfil(String identificador, String foto){
        editor.putString(CHAVE_FOTO, foto);
        editor.commit();
    }

    public void salvarEndereco(String identificador, String rua, String numero, String bairro, String cep, String cidade){
        editor.putString(CHAVE_IDENTIFICADOR, identificador);
        editor.putString(CHAVE_RUA, rua);
        editor.putString(CHAVE_NUMERO, numero);
        editor.putString(CHAVE_BAIRRO, bairro);
        editor.putString(CHAVE_CEP, cep);
        editor.putString(CHAVE_CIDADE, cidade);
        editor.commit();
    }

    public void salvarEnderecoCompleto(String endereco){
        editor.putString(CHAVE_ENDERECO, endereco);
        editor.commit();
    }

    public void salvarTelefone(String identificador, String codPais, String codArea, String telefone){
        editor.putString(CHAVE_IDENTIFICADOR, identificador);
        editor.putString(CHAVE_CODPAIS, codPais);
        editor.putString(CHAVE_CODAREA, codArea);
        editor.putString(CHAVE_TELEFONE, telefone);
        editor.commit();
    }

    public void salvarPet(String idpet, String nome, String idade, String sexo, String descricao){
        editor.putString(CHAVE_IDPET, idpet);
        editor.putString(CHAVE_NOMEPET, nome);
        editor.putString(CHAVE_IDADEPET, idade);
        editor.putString(CHAVE_SEXOPET, sexo);
        editor.putString(CHAVE_DESCRICAOPET, descricao);
        editor.commit();
    }


    public String getIdentificador(){
        return preferences.getString(CHAVE_IDENTIFICADOR, null); //null é um valor default, caso não ache o primeiro parametro
    }

    public  String getNome(){
        return preferences.getString(CHAVE_NOME, null); //null é um valor default, caso não ache o primeiro parametro
    }

    public  String getSobrenome(){
        return preferences.getString(CHAVE_SOBRENOME, null); //null é um valor default, caso não ache o primeiro parametro
    }

    public  String getIdade(){
        return preferences.getString(CHAVE_IDADE, null); //null é um valor default, caso não ache o primeiro parametro
    }



    public  String getCelular(){
        return preferences.getString(CHAVE_CELULAR, null); //null é um valor default, caso não ache o primeiro parametro
    }



    public  String getCPF(){
        return preferences.getString(CHAVE_CPF, null); //null é um valor default, caso não ache o primeiro parametro
    }


    public  String getEndereco(){
        return preferences.getString(CHAVE_ENDERECO, null); //null é um valor default, caso não ache o primeiro parametro
    }

    public  String getRua(){
        return preferences.getString(CHAVE_RUA, null); //null é um valor default, caso não ache o primeiro parametro
    }

    public  String getNumero(){
        return preferences.getString(CHAVE_NUMERO, null); //null é um valor default, caso não ache o primeiro parametro
    }

    public  String getBairro(){
        return preferences.getString(CHAVE_BAIRRO, null); //null é um valor default, caso não ache o primeiro parametro
    }

    public  String getCEP(){
        return preferences.getString(CHAVE_CEP, null); //null é um valor default, caso não ache o primeiro parametro
    }

    public  String getCidade(){
        return preferences.getString(CHAVE_CIDADE, null); //null é um valor default, caso não ache o primeiro parametro
    }


    public  String getCodPais(){
        return preferences.getString(CHAVE_CODPAIS, null); //null é um valor default, caso não ache o primeiro parametro
    }

    public  String getCodArea(){
        return preferences.getString(CHAVE_CODAREA, null); //null é um valor default, caso não ache o primeiro parametro
    }

    public  String getTelefone(){
        return preferences.getString(CHAVE_TELEFONE, null); //null é um valor default, caso não ache o primeiro parametro
    }

    public  String getSenha(){
        return preferences.getString(CHAVE_SENHA, null); //null é um valor default, caso não ache o primeiro parametro
    }

    //PET
    public  String getIdPet(){
        return preferences.getString(CHAVE_IDPET, null); //null é um valor default, caso não ache o primeiro parametro
    }
    public  String getNomePet(){
        return preferences.getString(CHAVE_NOMEPET, null); //null é um valor default, caso não ache o primeiro parametro
    }
    public  String getIdadePet(){
        return preferences.getString(CHAVE_IDADEPET, null); //null é um valor default, caso não ache o primeiro parametro
    }
    public  String getSexoPet(){
        return preferences.getString(CHAVE_SEXOPET, null); //null é um valor default, caso não ache o primeiro parametro
    }
    public  String getDescrPet(){
        return preferences.getString(CHAVE_DESCRICAOPET, null); //null é um valor default, caso não ache o primeiro parametro
    }

}
