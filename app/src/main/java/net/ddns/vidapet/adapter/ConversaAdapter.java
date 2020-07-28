package net.ddns.vidapet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.ddns.vidapet.helper.Base64Custom;
import net.ddns.vidapet.model.Conversa;
import net.ddns.vidapet.vidapet.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversaAdapter extends ArrayAdapter<Conversa> {

    private ArrayList<Conversa> listConversas;
    private Context context;
    private Conversa conversa;

    public ConversaAdapter(Context c, ArrayList<Conversa> objects) {
        super(c, 0, objects);
        this.context = c;
        this.listConversas = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        //VERFICAR SE ESTA PREECHIDO
        if(listConversas != null){
            //INICIALIZA OBJETO PARA MONTAGEM DO LAYOUT
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //MONTAR A VIEW A PARTIR DO XML
            view = inflater.inflate(R.layout.lista_conversas, parent, false);

            //RECUPERAR ELEMENTOS DA TELA
            TextView nome = (TextView) view.findViewById(R.id.textNome);
            TextView ultimaConversa = (TextView) view.findViewById(R.id.textUltimaConversa);
            final CircleImageView imgUser = (CircleImageView) view.findViewById(R.id.imgUserListaConversa);

            //SETAR VALORES NOS COMPONENTES DE TELA
            conversa = listConversas.get(position);
            nome.setText(conversa.getNome());
            ultimaConversa.setText(conversa.getMensagem());
            DatabaseReference databaseImg = FirebaseDatabase.getInstance().getReference()
                    .child("FotoPerfil")
                    .child(Base64Custom.converterBase64(conversa.getEmail()))
                    .child("imagem");
            databaseImg.keepSynced(true);
            databaseImg.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null){
                        String url = dataSnapshot.getValue(String.class);
                        Glide.with(getContext()).load(url).into(imgUser);
                    } else {
                        imgUser.setImageResource(R.drawable.img_usuario);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        return view;
    }
}
