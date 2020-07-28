package net.ddns.vidapet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.ddns.vidapet.helper.Preferences;
import net.ddns.vidapet.model.Mensagem;
import net.ddns.vidapet.vidapet.R;

import java.util.ArrayList;

public class MensagemAdapter extends ArrayAdapter<Mensagem>{

    private Context context;
    private ArrayList<Mensagem> arrayListmensagens;

    public MensagemAdapter(Context c, ArrayList<Mensagem> objects) {
        super(c, 0, objects);
        this.context = c;
        this.arrayListmensagens = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) { //Exibição Customizada
        View view = null;

        //VERIFICAR SE A LISTA ESTA VAZIA
        if(arrayListmensagens != null){
            //Recuperar mensagem
            Mensagem mensagem = arrayListmensagens.get(position);

            //RECUPERAR USUARIO LOGADO
            Preferences preferences = new Preferences(context);
            String idUsuarioLogado = preferences.getIdentificador();

            //INICIALIZA OBJETO PARA MONTAGEM DO LAYOUT
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);


            //MONTAR A VIEW A PARTIR DO XML
            if(idUsuarioLogado.equals(mensagem.getIdUsuario())){
                view = layoutInflater.inflate(R.layout.item_mensagem_direita, parent, false);
            } else {
                view = layoutInflater.inflate(R.layout.item_mensagem_esquerda, parent, false);
            }

            //RECUPERAR MENSAGENS DO FIREBASE
            TextView textView = (TextView) view.findViewById(R.id.tv_mensagem);
            TextView textData = (TextView) view.findViewById(R.id.tv_dataMensagem);

            textData.setText(mensagem.getData());
            textView.setText(mensagem.getMensagem());


        }

        return view;
    }
}
