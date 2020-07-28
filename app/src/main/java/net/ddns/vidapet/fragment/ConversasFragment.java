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

import net.ddns.vidapet.adapter.ConversaAdapter;
import net.ddns.vidapet.helper.Base64Custom;
import net.ddns.vidapet.helper.Preferences;
import net.ddns.vidapet.model.Conversa;
import net.ddns.vidapet.vidapet.ConversaActivity;
import net.ddns.vidapet.vidapet.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversasFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter<Conversa> arrayAdapter;
    private ArrayList<Conversa> listConversa;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListenerConversas;

    private String mydate;


    public ConversasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mydate = sdf.format(c.getTime());
        /***************************************************************************
         * MONTAR LISTVIEW E ADAPTER
         ***************************************************************************/
        listView = (ListView) view.findViewById(R.id.lv_conversas);
        listConversa = new ArrayList<Conversa>();
        arrayAdapter = new ConversaAdapter(getActivity(), listConversa);
        listView.setAdapter(arrayAdapter);



        /***************************************************************************
         * RECUPERAR CONVERSAS NO FIREBASE
         ***************************************************************************/
        Preferences preferences = new Preferences(getActivity());
        String idUsuarioLogado = preferences.getIdentificador();


        //INSTACIA DO FIREBASE
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Conversas")
                .child(idUsuarioLogado);

        valueEventListenerConversas = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                listConversa.clear();

                for(DataSnapshot dados: dataSnapshot.getChildren()){
                    Conversa conversa = dados.getValue(Conversa.class);
                    listConversa.add(conversa);
                    //databaseReference.child(conversa.getIdUsuario()).orderByChild("data").startAt(mydate);
                }
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        databaseReference.addValueEventListener(valueEventListenerConversas);



        //ADICIONAR EVENTO DE CLIQUE
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //RECUPERAR CONVERSA PARA A POSIÇÃO
                Conversa conversa = listConversa.get(position);

                //CRIAR INTENT PARA CONVERSAACTIVITY
                Intent intent = new Intent(getActivity(), ConversaActivity.class);
                String emailDecod = Base64Custom.decodificadorBase64(conversa.getIdUsuario());
                intent.putExtra("email", emailDecod);
                intent.putExtra("nome", conversa.getNome());
                startActivity(intent);
            }
        });


        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Conversa conversa = listConversa.get(position);

                removerConversa(conversa);
                return true;
            }
        });


        return view;
    }


    //ALERT DIALOG
    public void removerConversa(final Conversa conversa){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth);

        //Configurando a AlertDialog
        alertDialog.setTitle("Excluir Conversa");
        alertDialog.setMessage("Deseja realmente excluir esta conversa?");
        alertDialog.setCancelable(false);

        //BOTAO SIM
        alertDialog.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //INSTACIAR PREFERENCES PARA PEGAR VALOR
                Preferences preferences = new Preferences(getActivity());
                String identificadorUsuarioLogado =  preferences.getIdentificador();

                //EXCLUIR CONVERSAS
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference = databaseReference.child("Conversas")
                        .child(identificadorUsuarioLogado)
                        .child(conversa.getIdUsuario()); //Pegando id do usuario pressionado
                databaseReference.removeValue();

                //EXCLUIR MENSAGENS
                DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference();
                databaseReference2 = databaseReference2.child("Mensagens")
                        .child(identificadorUsuarioLogado)
                        .child(conversa.getIdUsuario());
                databaseReference2.removeValue();
                Toast.makeText(getContext(), "Conversa apagada!!", Toast.LENGTH_SHORT).show();

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
        databaseReference.addValueEventListener(valueEventListenerConversas);
    }

    @Override
    public void onStop() {
        super.onStop();
        databaseReference.removeEventListener(valueEventListenerConversas);

    }

}
