package net.ddns.vidapet.vidapet;

import android.Manifest;
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

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import net.ddns.vidapet.helper.Base64Custom;
import net.ddns.vidapet.model.Telefone;
import net.ddns.vidapet.model.Usuario;

public class TelaCadastro extends AppCompatActivity {

    private Button btnCadastrar, btnCancelar;
    private EditText nomeCadastro, sobrenome, idade, cpf, emailCadastro, senhaCadastro, confirmarSenha;
    private FirebaseAuth firebaseAuth;
    private Usuario usuario;
    private Telefone telefoneClass;
    private EditText telefone, codPais, codArea;

    private String [] permissoesNecessarias = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        firebaseAuth = FirebaseAuth.getInstance();

        btnCadastrar = (Button) findViewById(R.id.btnCadastrarID);
        btnCancelar = (Button) findViewById(R.id.btnCancelarID);
        nomeCadastro = (EditText) findViewById(R.id.editNomeCadastroID);
        sobrenome = (EditText) findViewById(R.id.editSobrenomeCadasrtroID);
        idade = (EditText) findViewById(R.id.editIdadeCadastroID);
        cpf = (EditText) findViewById(R.id.editCpfCadastroID);
        emailCadastro = (EditText) findViewById(R.id.editEmailCadastroID);
        senhaCadastro = (EditText) findViewById(R.id.editSenhaCadastroID);
        confirmarSenha = (EditText) findViewById(R.id.editConfirmarSenhaCadastroID);

        //MASCARA TELEFONE #########################################################################
        telefone = (EditText) findViewById(R.id.editTelefoneCadastroID);
        SimpleMaskFormatter simpleMaskTelefone = new SimpleMaskFormatter("NNNNN - NNNN");
        MaskTextWatcher maskTelefone = new MaskTextWatcher(telefone, simpleMaskTelefone);
        telefone.addTextChangedListener(maskTelefone);

        //MASCARA COD_PAIS #########################################################################
        codPais = (EditText) findViewById(R.id.edit55CadastroID);
        SimpleMaskFormatter simpleMaskCodPais = new SimpleMaskFormatter("+NN");
        MaskTextWatcher maskCodPais = new MaskTextWatcher(codPais, simpleMaskCodPais);
        codPais.addTextChangedListener(maskCodPais);

        //MASCARA COD_AREA #########################################################################
        codArea = (EditText) findViewById(R.id.edit11CadastroID);
        SimpleMaskFormatter simpleMaskCodArea = new SimpleMaskFormatter("NN");
        MaskTextWatcher maskCodArea = new MaskTextWatcher(codArea, simpleMaskCodArea);
        codArea.addTextChangedListener(maskCodArea);

        //MASCARA CPF ##############################################################################
        SimpleMaskFormatter simpleMaskCPF = new SimpleMaskFormatter("NNN.NNN.NNN / NN");
        MaskTextWatcher maskTextCPF = new MaskTextWatcher(cpf, simpleMaskCPF);
        cpf.addTextChangedListener(maskTextCPF);


        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaCadastro.this, TelaLogin.class);
                startActivity(intent);
                finish();
            }
        });

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(nomeCadastro.getText().toString().isEmpty() ||
                        emailCadastro.getText().toString().isEmpty() ||
                        senhaCadastro.getText().toString().isEmpty() ||
                        nomeCadastro.getText().toString().isEmpty() ||
                        sobrenome.getText().toString().isEmpty() ||
                        idade.getText().toString().isEmpty() ||
                        codPais.getText().toString().isEmpty() ||
                        codArea.getText().toString().isEmpty() ||
                        telefone.getText().toString().isEmpty() ||
                        confirmarSenha.getText().toString().isEmpty()){

                    Toast.makeText(getApplicationContext(),"É necessário preencher todos os campos obrigatórios!",
                            Toast.LENGTH_SHORT).show();
                } else if(senhaCadastro.getText().toString().equals(confirmarSenha.getText().toString())){
                    //EXTRAINDO CELULAR DA FORMATACAO
                    String telefoneCompleto =
                            codPais.getText().toString() +
                                    codArea.getText().toString() +
                                    telefone.getText().toString();

                    String telefoneSemFormatacao = telefoneCompleto.replace("+", "");
                    telefoneSemFormatacao = telefoneSemFormatacao.replace(" - ", "");

                    //EXTRAINDO CPF DA FORMATACAO
                    String cpfCompleto = cpf.getText().toString();
                    String cpfSemFormatacao = cpfCompleto.replace(".", "");
                    cpfSemFormatacao = cpfSemFormatacao.replace(" / ", "");

                    telefoneClass = new Telefone();
                    telefoneClass.setCodPais(codPais.getText().toString());
                    telefoneClass.setCodArea(codArea.getText().toString());
                    telefoneClass.setCelular(telefone.getText().toString());


                    //Preferences preferences = new Preferences(TelaCadastro.this);
                    //preferences.salvarTelefone(preferences.getIdentificador(),
                    //        codPais.getText().toString(), codArea.getText().toString(), telefone.getText().toString());


                    usuario = new Usuario();
                    usuario.setNome(nomeCadastro.getText().toString());
                    usuario.setSobrenome(sobrenome.getText().toString());
                    usuario.setEmail(emailCadastro.getText().toString().toLowerCase());
                    usuario.setSenha(senhaCadastro.getText().toString());
                    usuario.setCpf(cpfSemFormatacao);
                    usuario.setIdade(idade.getText().toString());
                    usuario.setTelefone(telefoneSemFormatacao);

                    cadastrarUsuario();
                } else {

                    Toast.makeText(getApplicationContext(), "Eita, as senhas não estão iguais :(", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void cadastrarUsuario(){

        firebaseAuth.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(TelaCadastro.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Cadastro efetuado", Toast.LENGTH_SHORT).show();

                            String identificador = Base64Custom.converterBase64(usuario.getEmail());
                            usuario.setId(identificador);
                            usuario.salvar();
                            telefoneClass.salvarTelefone(identificador);
                            firebaseAuth.signOut();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
