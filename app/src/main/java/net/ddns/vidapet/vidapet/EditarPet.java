package net.ddns.vidapet.vidapet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.ddns.vidapet.helper.Base64Custom;
import net.ddns.vidapet.helper.Preferences;
import net.ddns.vidapet.model.Pet;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPet extends AppCompatActivity {

    private String [] permissoesNecessarias = new String[]{
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private final int PERMISSAO_REQUEST = 2;
    private final int GALERIA_IMAGENS = 1;
    private CircleImageView img1;
    private Bitmap bitmap;
    private Uri imagemUri = null;
    private Button btnSalvar;
    private EditText nome, idade, raca, descricao;
    private RadioButton macho, femea;
    private RadioGroup radioGroup;
    private String imagemPet;
    private android.support.v7.widget.Toolbar toolbar;
    private Pet pet;
    private Preferences preferences;
    private ProgressDialog mProgress;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private CheckBox castrado, amigavel, raiva, v8;
    private Spinner spinner;
    private ArrayAdapter<CharSequence> adapter;
    private String textoSpinner;
    private LinearLayout llMaisSobrePet;
    private TextView maisSobrePet;
    private ImageView imgPet;

    private String idPet, emailDono, idUsuarioLogado, petNome;

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListenerPets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_pet);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //INSTANCIAS ===============================================================================
        pet = new Pet();
        preferences = new Preferences(EditarPet.this);

        idUsuarioLogado = preferences.getIdentificador();
        //==========================================================================================


        imgPet = (ImageView) findViewById(R.id.imgPetEdit);
        btnSalvar = (Button) findViewById(R.id.btnSalvarPetEdit);
        nome = (EditText) findViewById(R.id.txtNomePetEdit);
        idade = (EditText) findViewById(R.id.txtIdadePetEdit);
        raca = (EditText) findViewById(R.id.txtRacaPetEdit);
        descricao = (EditText) findViewById(R.id.txtDescPetEdit);
        radioGroup = (RadioGroup) findViewById(R.id.rg_machofemeaEdit);
        macho = (RadioButton) findViewById(R.id.rb_machoEdit);
        femea = (RadioButton) findViewById(R.id.rb_femeaEdit);
        castrado = (CheckBox) findViewById(R.id.cb_castradoEdit);
        amigavel = (CheckBox) findViewById(R.id.cb_amigavelEdit);
        raiva = (CheckBox) findViewById(R.id.cb_raivaEdit);
        v8 = (CheckBox) findViewById(R.id.cb_v8v10Edit);
        spinner = (Spinner) findViewById(R.id.spinnerPetEdit);
        maisSobrePet = (TextView) findViewById(R.id.txtDescPetEdit);


        mStorage = FirebaseStorage.getInstance().getReference();
        mProgress = new ProgressDialog(this);


        //Checando permissão para acessar a galeria do dispositivo
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSAO_REQUEST);
            }
        }

        //RECUPERAR DADOS ENVIADOS NA INTENT +++++++++++++++++++++++++++++++++++++++++++++++++++++++
        Bundle extra = getIntent().getExtras(); //OBJETO UTILIZADO PARA PEGAR DADOS ENTRE INTENTS
        if(extra != null){
            //RECUPERAR DADOS (DESTINATARIO)
            idPet = extra.getString("idPet");
            petNome = extra.getString("nomePet");
        }

        //Chamando toolbar padrão
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(petNome);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        img1 = (CircleImageView) findViewById(R.id.imgPetEdit);

        //Criando ação para chamar galeria ao clicar sob a imagem
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galeriaIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galeriaIntent.setType("image/*");
                startActivityForResult(galeriaIntent, GALERIA_IMAGENS);
            }
        });


        /******************************************************************************************
         * SPINNER
         ******************************************************************************************/
        adapter = ArrayAdapter.createFromResource(this, R.array.tamanho_pet, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);



        //RECUPERAR DADOS DO FIREBASE ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference = databaseReference.child("PETs")
                .child(idUsuarioLogado)
                .child(idPet);
        databaseReference.keepSynced(true);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Pet petClass = new Pet();
                petClass = dataSnapshot.getValue(Pet.class);

                //COLOCANDO VALORES

                if(!petClass.getFoto().equals(null) && !petClass.getFoto().equals("")){
                    Glide.with(getApplicationContext()).load(petClass.getFoto()).into(imgPet);
                    imagemPet = petClass.getFoto();
                } else {
                    imgPet.setImageResource(R.drawable.add_user);
                }

                nome.setText(petClass.getNome());
                idade.setText(petClass.getIdade());
                raca.setText(petClass.getRaca());

                String compareValue = petClass.getTamanho();
                if (!compareValue.equals(null)) {
                    int spinnerPosition = adapter.getPosition(compareValue);
                    spinner.setSelection(spinnerPosition);
                }


                if(petClass.getSexo().equals("Macho")){
                    macho.setChecked(true);
                    femea.setChecked(false);
                } else {
                    macho.setChecked(false);
                    femea.setChecked(true);
                }



                //VERIFICAÇÃO 1
                if(petClass.getCastrado().equals("true") ){
                    castrado.setChecked(true);
                } else{
                    castrado.setChecked(false);
                }
                if(petClass.getAmigavel().equals("true")){
                    amigavel.setChecked(true);
                } else{
                    amigavel.setChecked(false);
                }
                if(petClass.getVacinado_raiva().equals("true")){
                    raiva.setChecked(true);
                } else{
                    raiva.setChecked(false);
                }
                if(petClass.getVacinado_v8_v10().equals("true")){
                    v8.setChecked(true);
                } else{
                    v8.setChecked(false);
                }


                if(petClass.getDescricao() != null && petClass.getDescricao() != ""){
                    maisSobrePet.setText(petClass.getDescricao());
                } else {
                    maisSobrePet.setText("");
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                textoSpinner = (String) parent.getItemAtPosition(position);
                pet.setTamanho(textoSpinner);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /******************************************************************************************
         * Radio Group
         ******************************************************************************************/
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                if(femea.isChecked() == true){
                    pet.setSexo(femea.getText().toString());
                } else if (macho.isChecked() == true){
                    pet.setSexo(macho.getText().toString());
                }
            }
        });


        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgress.setMessage("Salvando dados...");
                mProgress.show();
                mProgress.setCancelable(false);

                /**********************************************************************************
                 * CHECKBOX
                 **********************************************************************************/
                if(castrado.isChecked() == true){
                    pet.setCastrado("true");
                } else if (castrado.isChecked() == false) {
                    pet.setCastrado("false");
                }

                if(amigavel.isChecked() == true){
                    pet.setAmigavel("true");
                } else if(amigavel.isChecked() == false){
                    pet.setAmigavel("false");
                }

                if(raiva.isChecked() == true){
                    pet.setVacinado_raiva("true");
                } else if(raiva.isChecked() == false){
                    pet.setVacinado_raiva("false");
                }

                if(v8.isChecked() == true){
                    pet.setVacinado_v8_v10("true");
                } else if(v8.isChecked() == false){
                    pet.setVacinado_v8_v10("false");
                }


                /***********************************************************************************************
                 * SALVANDO IMAGEM DO PET
                 **********************************************************************************************/

        final String idUsuarioLogado = preferences.getIdentificador();


        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("PETs")
                .child(idUsuarioLogado)
                .child(idPet);


        if(imagemUri != null){
            //Salvando foto no STORAGE
            StorageReference filepath = mStorage
                    .child("FotoPet")
                    .child(idUsuarioLogado)
                    .child(nome.getText().toString())
                    .child(imagemUri.getLastPathSegment());

            filepath.putFile(imagemUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    //preferences.salvarFotoPerfil(idUsuarioLogado, downloadUrl.toString());

                    DatabaseReference newPost = mDatabase;


                    newPost.child("id").setValue(idPet);
                    newPost.child("nome").setValue(nome.getText().toString());
                    newPost.child("idade").setValue(idade.getText().toString());
                    newPost.child("raca").setValue(raca.getText().toString());
                    newPost.child("descricao").setValue(descricao.getText().toString());
                    newPost.child("foto").setValue(downloadUrl.toString());
                    newPost.child("sexo").setValue(pet.getSexo());
                    newPost.child("tamanho").setValue(pet.getTamanho());
                    newPost.child("vacinado_raiva").setValue(pet.getVacinado_raiva());
                    newPost.child("vacinado_v8_v10").setValue(pet.getVacinado_v8_v10());
                    newPost.child("amigavel").setValue(pet.getAmigavel());
                    newPost.child("castrado").setValue(pet.getCastrado());
                    newPost.child("emailDono").setValue(Base64Custom.decodificadorBase64(idUsuarioLogado));

                    mProgress.dismiss();
                    Toast.makeText(getApplicationContext(),"Dados salvo com sucesso!", Toast.LENGTH_SHORT).show();


                    Intent intent = new Intent(EditarPet.this, ListarPets.class);
                    startActivity(intent);
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.d("testingImageInMain", "Falha na comunicação com o Storage");
                }

            });

        } else {

            DatabaseReference newPost = mDatabase;


            newPost.child("id").setValue(idPet);
            newPost.child("nome").setValue(nome.getText().toString());
            newPost.child("idade").setValue(idade.getText().toString());
            newPost.child("raca").setValue(raca.getText().toString());
            newPost.child("descricao").setValue(descricao.getText().toString());
            newPost.child("foto").setValue(imagemPet);
            newPost.child("sexo").setValue(pet.getSexo());
            newPost.child("tamanho").setValue(pet.getTamanho());
            newPost.child("vacinado_raiva").setValue(pet.getVacinado_raiva());
            newPost.child("vacinado_v8_v10").setValue(pet.getVacinado_v8_v10());
            newPost.child("amigavel").setValue(pet.getAmigavel());
            newPost.child("castrado").setValue(pet.getCastrado());
            newPost.child("emailDono").setValue(Base64Custom.decodificadorBase64(idUsuarioLogado));

            mProgress.dismiss();
            Toast.makeText(getApplicationContext(),"Dados salvo com sucesso!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditarPet.this, ListarPets.class);
            startActivity(intent);
            finish();

        }
            }

        });
    }

    //Fazendo verificação e setando imagem na ImageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALERIA_IMAGENS && resultCode == RESULT_OK) {

            imagemUri = data.getData();

            img1.setImageURI(imagemUri);
        }

    }

    //Tratamento da verificação
    public void onRequestPermissionResult(int requestCode,
                                          String permission[], int[] granResults) {
        if (requestCode == PERMISSAO_REQUEST) {

            if (granResults.length > 0
                    && granResults[0] == PackageManager.PERMISSION_GRANTED){
            }else{

            }
            return;
        }
    }



}
