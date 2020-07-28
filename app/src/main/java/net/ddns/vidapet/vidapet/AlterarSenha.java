package net.ddns.vidapet.vidapet;

import android.app.ProgressDialog;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import net.ddns.vidapet.helper.Preferences;

public class AlterarSenha extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText senhaAtual, senhaNova, senhaNova2;
    private Button btnSalvar;
    private FirebaseAuth firebaseAuth;
    private Preferences preferences;
    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_senha);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Alterar Senha");
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        mProgress = new ProgressDialog(this);

        senhaAtual = (EditText) findViewById(R.id.editSenhaAtual);
        senhaNova  = (EditText) findViewById(R.id.editSenhaNova);
        senhaNova2 = (EditText) findViewById(R.id.editSenhaNova2);
        btnSalvar  = (Button) findViewById(R.id.btnAlterarSenha);

        preferences = new Preferences(AlterarSenha.this);
        firebaseAuth = FirebaseAuth.getInstance();

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgress.setTitle("Carregando...");
                mProgress.setCancelable(false);
                mProgress.show();

                if(senhaAtual.getText().toString().equals("") ||
                        senhaNova.getText().toString().equals("") ||
                        senhaNova2.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),
                            "Precisa preencher todos os campos primeiro.",
                            Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();
                }
                else if(!senhaAtual.getText().toString().equals(preferences.getSenha())){
                    Toast.makeText(getApplicationContext(),
                            "Senha atual incorreta.",
                            Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();
                }
                else if(!senhaNova.getText().toString().equals(senhaNova2.getText().toString())){
                    Toast.makeText(getApplicationContext(),
                            "Senhas incompatíveis.",
                            Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();
                }
                else if(senhaNova.getText().toString().equals(senhaNova2.getText().toString()) &&
                        senhaAtual.getText().toString().equals(preferences.getSenha())){

                    firebaseAuth.getCurrentUser().updatePassword(senhaNova2.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),
                                        "Senha aletrada com sucesso!!",
                                        Toast.LENGTH_SHORT).show();
                                preferences.salvarSenha(senhaNova2.getText().toString());
                                mProgress.dismiss();
                                startActivity(new Intent(AlterarSenha.this, TelaUsuario.class));
                                finish();
                            }
                            else {
                                Toast.makeText(getApplicationContext(),
                                        "Ocorreu algum erro ao tentar trocar a senha.",
                                        Toast.LENGTH_SHORT).show();
                                mProgress.dismiss();
                                startActivity(new Intent(AlterarSenha.this, TelaUsuario.class));
                                finish();
                            }
                        }
                    });
                }
            }
        });



    }
}
