package net.ddns.vidapet.vidapet;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class EsqueceuSenha extends AppCompatActivity {

    private EditText email;
    private Button btnEnviar, btnCancelar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueceu_senha);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        firebaseAuth = FirebaseAuth.getInstance();

        email = (EditText) findViewById(R.id.editRecuperarSenha);
        btnEnviar = (Button) findViewById(R.id.btnRecuperarSenha);
        btnCancelar = (Button) findViewById(R.id.btnCancelarRecuperacaoID);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(email.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Preencha o E-mail primeiro.", Toast.LENGTH_SHORT).show();
                } else{
                    firebaseAuth.sendPasswordResetEmail(email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        email.setText("");
                                        Toast.makeText(EsqueceuSenha.this, "Recuperação de acesso iniciada. Email enviado.",
                                                Toast.LENGTH_SHORT).show();
                                        abrirTelaLogin();
                                    } else {
                                        Toast.makeText(EsqueceuSenha.this, "Falhou! Tente novamente", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaLogin();
            }
        });

    }

    private void abrirTelaLogin(){
        Intent intent = new Intent(EsqueceuSenha.this, TelaLogin.class);
        startActivity(intent);
        finish();
    }
}
