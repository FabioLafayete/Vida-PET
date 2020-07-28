package net.ddns.vidapet.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.ddns.vidapet.adapter.ContatoAdapter;
import net.ddns.vidapet.helper.Preferences;
import net.ddns.vidapet.model.Contato;
import net.ddns.vidapet.vidapet.ConversaActivity;
import net.ddns.vidapet.vidapet.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<Contato> contatos;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListenerContato;

    public ContatosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Instanciar objetos
        contatos = new ArrayList<>();


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        //MONTAR LIST VIEW
        listView = (ListView) view.findViewById(R.id.lv_contatos);

        /*
        adapter = new ArrayAdapter(getActivity(), R.layout.lista_contatos, contatos);
        */
        adapter = new ContatoAdapter(getActivity(), contatos);
        listView.setAdapter(adapter);


        /*******************************************************
         RECUPERAR CONTATOS DO FIREBASE
         ******************************************************/
        Preferences preferences = new Preferences(getActivity());
        String identificadorUsuarioLogado =  preferences.getIdentificador();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference = databaseReference
                .child("Contatos")
                .child(identificadorUsuarioLogado);

        //LISTENER PARA RECUPERAROS CONTATOS
        valueEventListenerContato = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //limpar dados
                contatos.clear();

                //LISTAR CONTATOS PRA O USUARIO
                for(DataSnapshot dados: dataSnapshot.getChildren()){
                    Contato contato = dados.getValue(Contato.class);
                    contatos.add(contato);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //Adicionar evento clicar na lista
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ConversaActivity.class);

                //RECUPERAR DADOS A SEREM PASSADOS
                Contato contato = contatos.get(position);

                //ENVIAR DADOS PARA ConversaActivity
                intent.putExtra("nome",contato.getNome());
                intent.putExtra("email",contato.getEmail());


                startActivity(intent);
            }
        });

        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Contato contato = contatos.get(position);
                removerContato(contato);
                return true;
            }
        });

        return view;
    }



    //ALERT DIALOG
    public void removerContato(final Contato contato){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth);

        //Configurando a AlertDialog
        alertDialog.setTitle("Excluir Contato");
        alertDialog.setMessage("Deseja realmente excluir este contato? Isso removerá também as mensagens e conversas");
        alertDialog.setCancelable(false);

        //BOTAO SIM
        alertDialog.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //IDENTIFICADOR DO CONTATO
                String idContato = contato.getIdentificadorUsuario();

                //INSTACIAR PREFERENCES PARA PEGAR VALOR
                Preferences preferences = new Preferences(getActivity());
                String identificadorUsuarioLogado =  preferences.getIdentificador();

                //EXCLUIR CONTATO
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference = databaseReference.child("Contatos")
                        .child(identificadorUsuarioLogado)
                        .child(idContato); //Pegando id do usuario pressionado
                databaseReference.removeValue();

                //EXCLUIR MENSAGENS
                DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference();
                databaseReference2 = databaseReference2.child("Mensagens")
                        .child(identificadorUsuarioLogado)
                        .child(idContato);
                databaseReference2.removeValue();

                //EXCLUIR CONVERSAS
                DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference();
                databaseReference3 = databaseReference3.child("Conversas")
                        .child(identificadorUsuarioLogado)
                        .child(idContato); //Pegando id do usuario pressionado
                databaseReference3.removeValue();

                Toast.makeText(getContext(), "Contato apagado!!", Toast.LENGTH_SHORT).show();

            }
        });


        //BOTAO CANCELAR
        alertDialog.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.create();
        alertDialog.show();

    }


    @Override
    public void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(valueEventListenerContato);
    }

    @Override
    public void onStop() {
        super.onStop();
        databaseReference.removeEventListener(valueEventListenerContato);
    }

}
