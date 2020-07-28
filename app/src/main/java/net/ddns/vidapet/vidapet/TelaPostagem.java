package net.ddns.vidapet.vidapet;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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

public class TelaPostagem extends AppCompatActivity {

    private String [] permissoesNecessarias = new String[]{
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private final int PERMISSAO_REQUEST = 2;
    private android.support.v7.widget.Toolbar toolbar;
    private ImageButton imagemSelecionada;
    private EditText txtTitulo;
    private EditText txtDescricao;
    private Button btnPostar;

    private Uri imagemUri = null;

    private static final int GALLERY_REQUEST = 1;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgress;
    private String imgUserTexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_postagem);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        mStorage = FirebaseStorage.getInstance().getReference();

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Postagens");
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        imagemSelecionada = (ImageButton) findViewById(R.id.imagemSelecionada);
        txtDescricao = (EditText) findViewById(R.id.txtDescricao);
        txtTitulo = (EditText) findViewById(R.id.txtTitulo);
        btnPostar = (Button) findViewById(R.id.btnPostar);


        //Checando permissão para acessar a galeria do dispositivo
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.INTERNET,},
                        PERMISSAO_REQUEST);
            }
        }


        mProgress = new ProgressDialog(this);
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Postagens");
        imagemSelecionada = (ImageButton)findViewById(R.id.imagemSelecionada);

        imagemSelecionada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galeriaIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galeriaIntent.setType("image/*");
                startActivityForResult(galeriaIntent, GALLERY_REQUEST);

            }
        });

        btnPostar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startPosting();

            }
        });

        Preferences preferencesImg = new Preferences(TelaPostagem.this);

        DatabaseReference databaseImg = FirebaseDatabase.getInstance().getReference();
        databaseImg = databaseImg.child("FotoPerfil").child(preferencesImg.getIdentificador()).child("imagem");

        databaseImg.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    String url = dataSnapshot.getValue(String.class);
                    imgUserTexto = url;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void startPosting() {

        mProgress.setMessage("Postando imagem...");


        final String titulo_val = txtTitulo.getText().toString().trim();
        final String descricao_val = txtDescricao.getText().toString().trim();

        if(!TextUtils.isEmpty(titulo_val) && !TextUtils.isEmpty(descricao_val) && imagemUri != null ){

            mProgress.show();

            StorageReference filepath = mStorage.child("imagens").child(imagemUri.getLastPathSegment());

            filepath.putFile(imagemUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();


                    Preferences preferences = new Preferences(TelaPostagem.this);
                    String nomeUsuario = preferences.getNome();
                    String emailUsuario = Base64Custom.decodificadorBase64(preferences.getIdentificador());

                    DatabaseReference newPost = mDatabase.push();

                    newPost.child("titulo").setValue(titulo_val);
                    newPost.child(("descricao")).setValue(descricao_val);
                    newPost.child("imagem").setValue(downloadUrl.toString());
                    newPost.child("nome").setValue(nomeUsuario);
                    newPost.child("email").setValue(emailUsuario);
                    newPost.child("imagemUser").setValue(imgUserTexto);

                    mProgress.dismiss();
                    Toast.makeText(getApplicationContext(),"Postagem realizada com sucesso!", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(TelaPostagem.this, HomePage.class));

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.d("testingImageInMain", "Falha na comunicação com o Storage");
                }

            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            imagemUri = data.getData();

            imagemSelecionada.setImageURI(imagemUri);

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

