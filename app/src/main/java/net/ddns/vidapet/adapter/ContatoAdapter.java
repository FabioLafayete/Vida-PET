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
import net.ddns.vidapet.model.Contato;
import net.ddns.vidapet.vidapet.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContatoAdapter extends ArrayAdapter<Contato> {

    private Context context;
    private ArrayList<Contato> contatos;

    public ContatoAdapter(Context c, ArrayList<Contato> objects) {
        super(c, 0, objects);
        this.context = c;
        this.contatos = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        //INICIALIZA LAYOUT PARA MONTAGEM DO LAYOUT
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE); //Esse metodo é uma interface global que permite a utilização de recursos e classes.

        //MONTAR A VIEW ATRAVES DO XML
        view = inflater.inflate(R.layout.lista_contatos, parent, false);

        //RECUPERAR ELEMENTOS PARA EXIBIÇÃO
        TextView tvNome = (TextView) view.findViewById(R.id.tv_nome);
        final CircleImageView imgUser = (CircleImageView) view.findViewById(R.id.imgUserContato);

        Contato contato = contatos.get(position);
        tvNome.setText(contato.getNome() + " " + contato.getSobrenome());

        final DatabaseReference databaseImg = FirebaseDatabase.getInstance().getReference()
                .child("FotoPerfil")
                .child(Base64Custom.converterBase64(contato.getEmail()))
                .child("imagem");
        databaseImg.keepSynced(true);
        databaseImg.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null && dataSnapshot.getValue() != ""){
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


        return view;
    }
}
