package net.ddns.vidapet.vidapet;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.ddns.vidapet.helper.Base64Custom;
import net.ddns.vidapet.helper.Preferences;
import net.ddns.vidapet.model.Pet;

import de.hdodenhof.circleimageview.CircleImageView;


public class AddPet extends AppCompatActivity {

    private String [] permissoesNecessarias = new String[]{
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE
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
    private ImageView imagemPet;
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
    private static final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        pet = new Pet();
        preferences = new Preferences(AddPet.this);


        btnSalvar = (Button) findViewById(R.id.btnSalvarPetAdd);
        nome = (EditText) findViewById(R.id.txtNomePetAdd);
        idade = (EditText) findViewById(R.id.txtIdadePetAdd);
        raca = (EditText) findViewById(R.id.txtRacaPetAdd);
        descricao = (EditText) findViewById(R.id.txtDescPetAdd);
        radioGroup = (RadioGroup) findViewById(R.id.rg_machofemea);
        macho = (RadioButton) findViewById(R.id.rb_macho);
        femea = (RadioButton) findViewById(R.id.rb_femea);
        castrado = (CheckBox) findViewById(R.id.cb_castradoAdd);
        amigavel = (CheckBox) findViewById(R.id.cb_amigavelAdd);
        raiva = (CheckBox) findViewById(R.id.cb_raivaAdd);
        v8 = (CheckBox) findViewById(R.id.cb_v8v10Add);
        spinner = (Spinner) findViewById(R.id.spinnerPet);


        mStorage = FirebaseStorage.getInstance().getReference();
        mProgress = new ProgressDialog(this);


        //Checando permissão para acessar a galeria do dispositivo
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSAO_REQUEST);
            }
        }

        //Chamando toolbar padrão
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Meu Pet");
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        img1 = (CircleImageView) findViewById(R.id.imgPet);

        //Criando ação para chamar galeria ao clicar sob a imagem
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galeriaIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galeriaIntent.setType("image/*");
                startActivityForResult(galeriaIntent, GALLERY_REQUEST);
            }
        });


        /******************************************************************************************
         * Radio Group
         ******************************************************************************************/
        macho.setChecked(true);
        pet.setSexo(macho.getText().toString());

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

        /******************************************************************************************
         * SPINNER
         ******************************************************************************************/
        adapter = ArrayAdapter.createFromResource(this, R.array.tamanho_pet, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

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
         * BOTAO SALVAR
         ******************************************************************************************/
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


                boolean connected = false;
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if((connectivityManager
                        .getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager
                        .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
                        || (connectivityManager
                        .getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connectivityManager
                        .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                        .getState() == NetworkInfo.State.CONNECTED)) {

                    /***********************************************************************************************
                     * SALVANDO IMAGEM DO PET
                     **********************************************************************************************/

                    final String idUsuarioLogado = preferences.getIdentificador();


                    mDatabase = FirebaseDatabase.getInstance().getReference()
                            .child("PETs")
                            .child(idUsuarioLogado)
                            .push();

                    pet.setId(mDatabase.getKey());

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


                                newPost.child("id").setValue(pet.getId());
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


                                Intent intent = new Intent(AddPet.this, ListarPets.class);
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


                        newPost.child("id").setValue(pet.getId());
                        newPost.child("nome").setValue(nome.getText().toString());
                        newPost.child("idade").setValue(idade.getText().toString());
                        newPost.child("raca").setValue(raca.getText().toString());
                        newPost.child("descricao").setValue(descricao.getText().toString());
                        newPost.child("foto").setValue("");
                        newPost.child("sexo").setValue(pet.getSexo());
                        newPost.child("tamanho").setValue(pet.getTamanho());
                        newPost.child("vacinado_raiva").setValue(pet.getVacinado_raiva());
                        newPost.child("vacinado_v8_v10").setValue(pet.getVacinado_v8_v10());
                        newPost.child("amigavel").setValue(pet.getAmigavel());
                        newPost.child("castrado").setValue(pet.getCastrado());
                        newPost.child("emailDono").setValue(Base64Custom.decodificadorBase64(idUsuarioLogado));

                        mProgress.dismiss();
                        Toast.makeText(getApplicationContext(),"Dados salvo com sucesso!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddPet.this, ListarPets.class);
                        startActivity(intent);
                        finish();
                    }

                    connected = true;
                } else {
                    mProgress.dismiss();
                    connected = false;
                    Toast.makeText(getApplicationContext(), "Sem conexão com a Internet", Toast.LENGTH_SHORT).show();
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

