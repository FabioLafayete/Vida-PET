package net.ddns.vidapet.vidapet;

import android.app.ProgressDialog;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.ddns.vidapet.helper.Base64Custom;
import net.ddns.vidapet.helper.Preferences;
import net.ddns.vidapet.model.Endereco;
import net.ddns.vidapet.model.Telefone;
import net.ddns.vidapet.model.Usuario;

public class TelaLogin extends AppCompatActivity {

    private EditText emailLogin, senhaLogin;
    private Button btnLogar;
    private Usuario usuario;
    private String idUsuarioLogado;
    private ProgressDialog mProgress;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseTel = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseEnd = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_login);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        verificarUsuarioLogado();

        mProgress = new ProgressDialog(this);
        emailLogin = (EditText) findViewById(R.id.editEmailLoginID);
        senhaLogin = (EditText) findViewById(R.id.editSenhaLoginID);
        btnLogar = (Button) findViewById(R.id.btnLogarID);

        //________________________________________________________________________________________



        //________________________________________________________________________________________

        btnLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(emailLogin.getText().toString().isEmpty() || senhaLogin.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Precisa preencher todos os campos!", Toast.LENGTH_SHORT).show();
                } else {
                    usuario = new Usuario();
                    usuario.setEmail(emailLogin.getText().toString().toLowerCase());
                    usuario.setSenha(senhaLogin.getText().toString());
                    validarLogin();
                }
            }
        });

    }


    private void validarLogin(){

        mProgress.setMessage("Entrando...");

        firebaseAuth.signInWithEmailAndPassword(usuario.getEmail().toLowerCase(), usuario.getSenha())
                .addOnCompleteListener(TelaLogin.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            mProgress.show();


                            //RECUPERAR DADOS USUARIO LOGADO
                            idUsuarioLogado = Base64Custom.converterBase64(usuario.getEmail());

                            //USUARIO
                            databaseReference = FirebaseDatabase.getInstance().getReference()
                                    .child("Usuarios")
                                    .child(idUsuarioLogado);
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() { //Consulta apenas uma vez
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    Usuario usuario1 = dataSnapshot.getValue(Usuario.class);

                                    //Salvar dados do usuario logado
                                    String identificadorUsuarioLogado =  Base64Custom.converterBase64(usuario1.getEmail());
                                    Preferences preferences = new Preferences(TelaLogin.this);

                                    preferences.salvarDados(identificadorUsuarioLogado,
                                            usuario1.getNome(),
                                            usuario1.getSobrenome(),
                                            usuario1.getIdade(),
                                            usuario1.getTelefone(),
                                            usuario1.getCpf(),
                                            usuario.getSenha(),
                                            usuario1.getEndereco());

                                    mProgress.dismiss();

                                    abrirTelaPrincipal();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                            /********************************************************************
                             * Telefone
                             */


                            //Recuperar instacia do firebase
                            //firebaseAuth = FirebaseAuth.getInstance();
                            databaseTel = FirebaseDatabase.getInstance().getReference();
                            databaseTel = databaseTel.child("Usuarios")
                                    .child(idUsuarioLogado)
                                    .child("Telefone");

                            //Verificar consulta uma vez somente
                            databaseTel.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //Verificar se foi retornado algum valor
                                    if (dataSnapshot.getValue() != null) {
                                        //Recuperando dados do endereco
                                        Telefone usuarioTel = new Telefone();
                                        usuarioTel = dataSnapshot.getValue(Telefone.class);

                                        //Recuperando dados do usuario logado
                                        Preferences preferences = new Preferences(TelaLogin.this);
                                        String identificadorUsuarioLogado =  Base64Custom.converterBase64(usuario.getEmail());


                                        //contato
                                        preferences.salvarTelefone(identificadorUsuarioLogado,
                                                usuarioTel.getCodPais(),
                                                usuarioTel.getCodArea(),
                                                usuarioTel.getCelular());


                                        //Recuperar dados FIREBASE
                                        databaseTel = FirebaseDatabase.getInstance().getReference();
                                        databaseTel = databaseTel
                                                .child("Usuarios")
                                                .child(identificadorUsuarioLogado)
                                                .child("Telefone");
                                        databaseTel.setValue(usuarioTel);


                                    } else {
                                        Preferences preferences = new Preferences(TelaLogin.this);
                                        String identificadorUsuarioLogado =  Base64Custom.converterBase64(usuario.getEmail());

                                        //contato
                                        preferences.salvarTelefone(identificadorUsuarioLogado,
                                                "",
                                                "",
                                                "");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            /***********************************************************************
                             * Endereco
                             */

                            //Verificar se o usuario ja esta cadastrado

                            //Recuperar instacia do firebase
                            //firebaseAuth = FirebaseAuth.getInstance();
                            databaseEnd = FirebaseDatabase.getInstance().getReference();
                            databaseEnd = databaseEnd.child("Usuarios")
                                    .child(idUsuarioLogado)
                                    .child("Endereco");

                            //Verificar consulta uma vez somente
                            databaseEnd.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //Verificar se foi retornado algum valor
                                    if (dataSnapshot.getValue() != null) {
                                        //Recuperando dados do endereco
                                        Endereco usuarioEnd = new Endereco();
                                        usuarioEnd = dataSnapshot.getValue(Endereco.class);

                                        //Recuperando dados do usuario logado
                                        Preferences preferences = new Preferences(TelaLogin.this);
                                        String identificadorUsuarioLogado =  Base64Custom.converterBase64(usuario.getEmail());


                                        //contato
                                        preferences.salvarEndereco(identificadorUsuarioLogado,
                                                usuarioEnd.getRua(),
                                                usuarioEnd.getNumero(),
                                                usuarioEnd.getBairro(),
                                                usuarioEnd.getCep(),
                                                usuarioEnd.getCidade());


                                        //Recuperar dados FIREBASE
                                        databaseEnd = FirebaseDatabase.getInstance().getReference();
                                        databaseEnd = databaseEnd
                                                .child("Usuarios")
                                                .child(identificadorUsuarioLogado)
                                                .child("Endereco");
                                        databaseEnd.setValue(usuarioEnd);


                                    } else {
                                        Preferences preferences = new Preferences(TelaLogin.this);
                                        String identificadorUsuarioLogado =  Base64Custom.converterBase64(usuario.getEmail());

                                        //contato
                                        preferences.salvarEndereco(identificadorUsuarioLogado,
                                                "",
                                                "",
                                                "",
                                                "",
                                                "");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        } else {
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }



    private void verificarUsuarioLogado(){
        if(firebaseAuth.getCurrentUser() != null){
            abrirTelaPrincipal();
        }
    }



    private void abrirTelaPrincipal(){
        Intent intent = new Intent(TelaLogin.this, HomePage.class);
        startActivity(intent);
        finish();
    }

    public void abrirCadastroUsuario(View view){
        Intent intent = new Intent(TelaLogin.this, TelaCadastro.class);
        startActivity(intent);
    }

    public void abrirEsqueceuSenha(View view){
        Intent intent = new Intent(TelaLogin.this, EsqueceuSenha.class);
        startActivity(intent);
    }

}