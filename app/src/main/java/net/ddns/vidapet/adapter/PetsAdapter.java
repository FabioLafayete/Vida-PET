package net.ddns.vidapet.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.ddns.vidapet.model.Pet;
import net.ddns.vidapet.vidapet.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PetsAdapter extends ArrayAdapter<Pet> {

    private ArrayList<Pet> listPets;
    private Context context;
    private Pet pet;

    public PetsAdapter(Context c, ArrayList<Pet> objects) {
        super(c, 0, objects);
        this.context = c;
        this.listPets = objects;
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;


        //INICIALIZA OBJETO PARA MONTAGEM DO LAYOUT
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //MONTAR A VIEW A PARTIR DO XML
        view = inflater.inflate(R.layout.lista_pets, parent, false);

        //RECUPERAR ELEMENTOS DA TELA
        TextView nome = (TextView) view.findViewById(R.id.txtNomePetListaPets);
        TextView idade = (TextView) view.findViewById(R.id.txtIdadePetListaPets);
        TextView sexo = (TextView) view.findViewById(R.id.txtSexoPetListaPets);
        CircleImageView imgPet = (CircleImageView) view.findViewById(R.id.imgPetListaPets);


        //SETAR VALORES NOS COMPONENTES DE TELA
        pet = listPets.get(position);
        nome.setText(pet.getNome());
        idade.setText(pet.getIdade() + " anos");
        sexo.setText(pet.getSexo());

        imgPet.setImageResource(R.drawable.petimg7);

        if(!pet.getFoto().equals(null) && !pet.getFoto().equals("")){
            Glide.with(getContext()).load(pet.getFoto()).into(imgPet);
        } else if (pet.getFoto() == null || pet.getFoto() == "") {
            imgPet.setImageResource(R.drawable.petimg7);
        }

        return view;
    }
}
