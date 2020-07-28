package net.ddns.vidapet.helper;

import android.util.Base64;

public class Base64Custom {

    public static String converterBase64(String texto){
        String textoConvertido = Base64.encodeToString(texto.getBytes(), Base64.DEFAULT);
        return textoConvertido.trim(); //trim() = remove qualquer espaço gerado
    }

    public static String decodificadorBase64(String textoDecodificado){
        byte[] byteDecodificado = Base64.decode(textoDecodificado, Base64.DEFAULT);
        return new String(byteDecodificado);
    }
}
