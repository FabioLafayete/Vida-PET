package net.ddns.vidapet.vidapet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.ddns.vidapet.helper.Base64Custom;
import net.ddns.vidapet.helper.Preferences;
import net.ddns.vidapet.model.Pet;

import de.hdodenhof.circleimageview.CircleImageView;

public class TelaPet extends AppCompatActivity {

    private String [] permissoesNecessarias = new String[]{
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private Toolbar toolbar;
    private CircleImageView imgPet;
    private TextView nomePet, idadePet, sexoPet, racaPet, tamanhoPet, titleDescricao;
    private CheckBox castradoPet, amigavelPet, raivaPet, v8v10Pet;
    private LinearLayout llMaisSobrePet;
    private TextView maisSobrePet;

    private String idPet, emailDono, idUsuarioLogado, petNome;


    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListenerPets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_pet);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //INSTANCIAS ===============================================================================
        Preferences preferences = new Preferences(TelaPet.this);

        idUsuarioLogado = preferences.getIdentificador();
        //==========================================================================================


        //FindViewById's ===========================================================================
        imgPet = (CircleImageView) findViewById(R.id.petImg);
        nomePet = (TextView) findViewById(R.id.txtNomePetTelaPet);
        idadePet = (TextView) findViewById(R.id.txtIdadePetTelaPet);
        sexoPet = (TextView) findViewById(R.id.txtSexoPetTelaPet);
        racaPet = (TextView) findViewById(R.id.txtRacaPetTelaPet);
        tamanhoPet = (TextView) findViewById(R.id.txtTamanhoPetTelaPet);

        castradoPet = (CheckBox) findViewById(R.id.cb_castrado);
        amigavelPet = (CheckBox) findViewById(R.id.cb_amigavel);
        raivaPet = (CheckBox) findViewById(R.id.cb_raiva);
        v8v10Pet = (CheckBox) findViewById(R.id.cb_v8);

        llMaisSobrePet = (LinearLayout) findViewById(R.id.llInfoPet);

        titleDescricao = (TextView) findViewById(R.id.txtTitleDescricao);
        maisSobrePet = (TextView) findViewById(R.id.txtDescricaoPet);
        //==========================================================================================


        //RECUPERAR DADOS ENVIADOS NA INTENT +++++++++++++++++++++++++++++++++++++++++++++++++++++++
        Bundle extra = getIntent().getExtras(); //OBJETO UTILIZADO PARA PEGAR DADOS ENTRE INTENTS
        if(extra != null){
            //RECUPERAR DADOS (DESTINATARIO)
            idPet = extra.getString("idPet");
            emailDono = Base64Custom.converterBase64(extra.getString("emailDono"));
            petNome = extra.getString("nomePet");
        }
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


        //Toolbar ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        toolbar = (Toolbar) findViewById(R.id.tb_pet);
        toolbar.setTitle(petNome);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


        //RECUPERAR DADOS DO PET ===================================================================

        castradoPet.setClickable(false);
        raivaPet.setClickable(false);
        amigavelPet.setClickable(false);
        v8v10Pet.setClickable(false);

        titleDescricao.setText("");

        llMaisSobrePet.setVisibility(View.INVISIBLE);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference = databaseReference.child("PETs")
                .child(idUsuarioLogado)
                .child(idPet);


        //CRIAR LISTENER PARA MENSAGENS
        valueEventListenerPets = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue() != null){
                    Pet petClass = new Pet();
                    petClass = dataSnapshot.getValue(Pet.class);

                    //COLOCANDO VALORES
                    if(!petClass.getFoto().equals(null) && !petClass.getFoto().equals("")){
                        Glide.with(getApplicationContext()).load(petClass.getFoto()).into(imgPet);
                    } else if (petClass.getFoto() == null || petClass.getFoto() == "") {
                        imgPet.setImageResource(R.drawable.add_btn2);
                    }
                    nomePet.setText(petClass.getNome());
                    idadePet.setText("Idade: " + petClass.getIdade());
                    sexoPet.setText("Sexo: " + petClass.getSexo());
                    racaPet.setText("Raça: " + petClass.getRaca());
                    tamanhoPet.setText("Tamanho: " + petClass.getTamanho());

                    if(petClass.getCastrado().equals("true")){
                        castradoPet.setChecked(true);
                    } else{
                        castradoPet.setChecked(false);
                    }
                    if(petClass.getAmigavel().equals("true")){
                        amigavelPet.setChecked(true);
                    } else{
                        amigavelPet.setChecked(false);
                    }
                    if(petClass.getVacinado_raiva().equals("true")){
                        raivaPet.setChecked(true);
                    } else{
                        raivaPet.setChecked(false);
                    }
                    if(petClass.getVacinado_v8_v10().equals("true")){
                        v8v10Pet.setChecked(true);
                    } else{
                        v8v10Pet.setChecked(false);
                    }


                    if(!petClass.getDescricao().equals(null) && !petClass.getDescricao().equals("")){
                        llMaisSobrePet.setVisibility(View.VISIBLE);
                        maisSobrePet.setText(petClass.getDescricao());
                        titleDescricao.setText("Mais sobre o PET:");
                    } else {
                        llMaisSobrePet.setVisibility(View.INVISIBLE);
                    }
                } else {
                    startActivity(new Intent(TelaPet.this, ListarPets.class));
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addValueEventListener(valueEventListenerPets);


        databaseReference.keepSynced(true);
        //==========================================================================================
    }


    @Override
    protected void onStop() {
        super.onStop();
        databaseReference.removeEventListener(valueEventListenerPets);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        databaseReference.addValueEventListener(valueEventListenerPets);
    }

    @Override
    protected void onStart() {
        super.onStart();

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if((connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
                || (connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState() == NetworkInfo.State.CONNECTED)) {
            //we are connected to a network
            connected = true;
        } else {
            connected = false;
            Toast.makeText(getApplicationContext(), "Sem conexão com a Internet", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tela_pet, menu);

        Drawable drawable = menu.findItem(R.id.item_trash_pet).getIcon();

        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this,R.color.whiteColor));
        menu.findItem(R.id.item_trash_pet).setIcon(drawable);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.item_trash_pet:
                removerPet();
                return true;

            case R.id.item_editar_pet:
                //CRIAR INTENT PARA TELAPET
                Intent intent = new Intent(TelaPet.this, EditarPet.class);

                intent.putExtra("idPet", idPet);
                intent.putExtra("nomePet", petNome);
                startActivity(intent);
                return true;


            default: return super.onOptionsItemSelected(item);
        }
    }

    public void removerPet(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TelaPet.this);

        //Configurando a AlertDialog
        alertDialog.setTitle("Excluir Pet");
        alertDialog.setMessage("Deseja realmente excluir este Pet?");
        alertDialog.setCancelable(false);

        //BOTAO SIM
        alertDialog.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //INSTACIAR PREFERENCES PARA PEGAR VALOR
                Preferences preferences = new Preferences(TelaPet.this);
                String identificadorUsuarioLogado =  preferences.getIdentificador();

                //EXCLUIR CONVERSAS
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference = databaseReference.child("PETs")
                        .child(identificadorUsuarioLogado)
                        .child(idPet); //Pegando id do pet

                startActivity(new Intent(TelaPet.this, ListarPets.class));
                Toast.makeText(getApplicationContext(), "Pet excluido com sucesso!!", Toast.LENGTH_SHORT).show();
                databaseReference.removeValue();
                finish();


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


}
