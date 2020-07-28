package net.ddns.vidapet.vidapet;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.ddns.vidapet.helper.Preferences;

public class ExcluirConta extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText senha;
    private Button btnExcluir;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference database, dataFoto, dataPet;
    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excluir_conta);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Excluir conta");
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        senha = (EditText) findViewById(R.id.editExcluirConta);
        btnExcluir = (Button) findViewById(R.id.btnExcluirConta);


        preferences = new Preferences(ExcluirConta.this);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference().child("Usuarios")
                .child(preferences.getIdentificador());

        dataFoto = FirebaseDatabase.getInstance().getReference()
                .child("FotoPerfil")
                .child(preferences.getIdentificador());

        dataPet = FirebaseDatabase.getInstance().getReference()
                .child("PETs")
                .child(preferences.getIdentificador());



        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!senha.getText().toString().equals("")){

                    if(senha.getText().toString().equals(preferences.getSenha().toString())){

                        firebaseAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(),
                                        "Conta excluida com sucesso!!",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ExcluirConta.this, TelaLogin.class));
                                database.removeValue();
                                dataFoto.removeValue();
                                dataPet.removeValue();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),
                                        "Ocorreu um erro ao tentar excluir a conta.",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ExcluirConta.this, TelaUsuario.class));
                                finish();
                            }
                        });


                    } else{
                        Toast.makeText(getApplicationContext(),
                                "Senha errada!!",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Precisa colocar sua senha para excluir a conta.",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
