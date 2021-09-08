package com.example.whatsapp.Helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissao {

    public static boolean validarPermissoes (String[] permissoes , Activity activity, int requestcode) {

        //verificando verssão
        if (Build.VERSION.SDK_INT >= 23 ){

            List<String> listaPermissoes = new ArrayList<>();

            /*percorrer as permissões passadas,
            * verificando uma a uma
            * se já tem permissão liberada*/
            for (String permissao : permissoes) {
             Boolean tempermissao =   ContextCompat.checkSelfPermission(activity,permissao) == PackageManager.PERMISSION_GRANTED;

             if (!tempermissao){
                 //adiciona permissão a ser solicitada , que ainda n foi permitida pelo usuario
                 listaPermissoes.add(permissao);
             }
            }

            //caso a lista de permissões a ser solicitada esteja vaiza
            if (listaPermissoes.isEmpty()){ return true;}

            //tranformando a "listaPermissoes" em uma array de String
            String[] novaspermissoes = new String[listaPermissoes.size()];
            listaPermissoes.toArray(novaspermissoes);

            //solicita permissão
            ActivityCompat.requestPermissions(activity,novaspermissoes,requestcode);

        }

        return true;
    }

}
