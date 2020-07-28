package net.ddns.vidapet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.ddns.vidapet.model.Pet;
import net.ddns.vidapet.vidapet.R;

import java.util.ArrayList;


public class PetsAdapterAnfitriao extends ArrayAdapter<Pet> {

    private Context context;
    private ArrayList<Pet> lista;
    private Pet pet;


    public PetsAdapterAnfitriao(Context c, ArrayList<Pet> objects) {
        super(c, 0, objects);

        this.context = c;
        this.lista = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        pet = lista.get(position);

        //convertView = LayoutInflater.from(this.context).inflate(R.layout.lista_pets, null);

        //INICIALIZA OBJETO PARA MONTAGEM DO LAYOUT
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        //MONTAR A VIEW A PARTIR DO XML
        view = inflater.inflate(R.layout.lista_pets, parent, false);

        //RECUPERAR ELEMENTOS DA TELA
        TextView nome = (TextView) view.findViewById(R.id.txtNomePetListaPets);
        TextView idade = (TextView) view.findViewById(R.id.txtIdadePetListaPets);
        TextView sexo = (TextView) view.findViewById(R.id.txtSexoPetListaPets);
        ImageView imgPet = (ImageView) view.findViewById(R.id.imgPetListaPets);


        //SETAR VALORES NOS COMPONENTES DE TELA
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
