package net.ddns.vidapet.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.ddns.vidapet.adapter.PetsAdapter;
import net.ddns.vidapet.helper.Preferences;
import net.ddns.vidapet.model.Pet;
import net.ddns.vidapet.vidapet.R;
import net.ddns.vidapet.vidapet.TelaPet;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PetFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter arrayAdapter;
    private ArrayList<Pet> listPet;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListenerPet;


    public PetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /***************************************************************************
         * MONTAR LISTVIEW E ADAPTER
         ***************************************************************************/
        //Instanciar objetos
        listPet = new ArrayList<>();


        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_pet, container, false);

        //MONTAR LIST VIEW
        listView = (ListView) view.findViewById(R.id.lv_listaPets);

        /*
        adapter = new ArrayAdapter(getActivity(), R.layout.lista_contatos, contatos);
        */
        arrayAdapter = new PetsAdapter(getActivity(), listPet);
        listView.setAdapter(arrayAdapter);



        /***************************************************************************
         * RECUPERAR PETS NO FIREBASE
         ***************************************************************************/
        Preferences preferences = new Preferences(getActivity());
        String idUsuarioLogado = preferences.getIdentificador();


        //INSTACIA DO FIREBASE
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference = databaseReference
                .child("PETs")
                .child(idUsuarioLogado);
        databaseReference.keepSynced(true);

        valueEventListenerPet = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue() == null){
                    view.setBackgroundResource(R.drawable.pet_sem_cadastro);
                } else {

                    listPet.clear();

                    for (DataSnapshot dados : dataSnapshot.getChildren()) {
                        Pet pet = dados.getValue(Pet.class);

                        if(pet.getFoto() == null || pet.getFoto() == ""){
                            pet.setFoto("");
                        }
                        listPet.add(pet);
                    }

                    arrayAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        databaseReference.addValueEventListener(valueEventListenerPet);



        //ADICIONAR EVENTO DE CLIQUE
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //RECUPERAR PET PARA A POSIÇÃO
                Pet pet = listPet.get(position);

                //CRIAR INTENT PARA TELAPET
                Intent intent = new Intent(getActivity(), TelaPet.class);
                String petId = (pet.getId());
                intent.putExtra("idPet", petId);
                intent.putExtra("emailDono", pet.getEmailDono());
                intent.putExtra("nomePet", pet.getNome());
                startActivity(intent);
            }
        });



        return view;



    }

    @Override
    public void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(valueEventListenerPet);
    }

    @Override
    public void onStop() {
        super.onStop();
        databaseReference.removeEventListener(valueEventListenerPet);

    }

}
