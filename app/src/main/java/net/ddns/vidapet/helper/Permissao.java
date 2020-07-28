package net.ddns.vidapet.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;


public class Permissao {

    public static boolean validaPermissoes(int requestCode, Activity activity, String[] permissoes) {

        if (Build.VERSION.SDK_INT >= 22) {

            List<String> listaPermissoes = new ArrayList<String>();

             /*Percorrer as permissoes passadas, verificando um a um se ja tem a permissao liberada */
            for (String permissao : permissoes) {
                Boolean validaPermisssao = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED;
                if (!validaPermisssao) {
                    listaPermissoes.add(permissao);
                }

                // Caso a lista esteja vazia náo é necessário verificar
                if (listaPermissoes.isEmpty()) return true;

                String[] novaspermissoes = new String[listaPermissoes.size()];
                listaPermissoes.toArray(novaspermissoes);

                //Solicita Permissão
                ActivityCompat.requestPermissions(activity, novaspermissoes, requestCode);

            }


        }

        return true;

    }
}