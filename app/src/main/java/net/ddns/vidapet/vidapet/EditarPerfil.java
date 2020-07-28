package net.ddns.vidapet.vidapet;

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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
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
import net.ddns.vidapet.model.Endereco;
import net.ddns.vidapet.model.Telefone;
import net.ddns.vidapet.model.Usuario;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfil extends AppCompatActivity {

    private String [] permissoesNecessarias = new String[]{
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private final int PERMISSAO_REQUEST = 2;
    private Uri imagemUri = null;
    private final int GALERIA_IMAGENS = 1;
    private CircleImageView imagemSelecionada;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgress;
    private android.support.v7.widget.Toolbar toolbar;

    private EditText nome, sobrenome, idade, telefone, codPais, codArea, rua, numero, bairro, cep, cidade, cpf;
    private Button btnSalvar, btnCancelar;

    private Usuario usuario;
    private Telefone telefoneClasse;
    private Endereco enderecoClasse;
    private Preferences preferences;
    private String imgSelect;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mProgress = new ProgressDialog(this);


        //INSTANCIAS ****************************************************************************
        preferences = new Preferences(EditarPerfil.this);

        mStorage = FirebaseStorage.getInstance().getReference();

        /******************************************************************************************
         *  COLOCANDO DEPENDENCIAS
         ******************************************************************************************/

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

        toolbar = (Toolbar) findViewById(R.id.tb_userPerfil);
        toolbar.setTitle("Editar Perfil");
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        imagemSelecionada = (CircleImageView) findViewById(R.id.imgUserEdit);

        String idUsuarioLogado = preferences.getIdentificador();


        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("FotoPerfil")
                .child(idUsuarioLogado);


        imagemSelecionada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALERIA_IMAGENS);

            }
        });






        nome = (EditText) findViewById(R.id.editNomePerfil);
        sobrenome = (EditText) findViewById(R.id.editSobrenomePerfil);
        idade = (EditText) findViewById(R.id.editIdadePerfil);
        telefone = (EditText) findViewById(R.id.editTelefonePerfilID);
        codPais = (EditText) findViewById(R.id.edit55PerfilID);
        codArea = (EditText) findViewById(R.id.edit11PerfilID);
        rua = (EditText) findViewById(R.id.editRuaPerfil);
        numero = (EditText) findViewById(R.id.editNumRuaPerfil);
        bairro = (EditText) findViewById(R.id.editBairroRuaPerfil);
        cep = (EditText) findViewById(R.id.editCepPerfil);
        cidade = (EditText) findViewById(R.id.editCidadeRuaPerfil);
        cpf = (EditText) findViewById(R.id.editCpfPerfil);

        btnSalvar = (Button) findViewById(R.id.btnSalvarPerfil);
        btnCancelar = (Button) findViewById(R.id.btnCancelarPerfil);


        /*****************************************************************************************
         * COLOCANDO DADOS AO INICIAR TELA
         *****************************************************************************************/

        nome.setText(preferences.getNome());
        sobrenome.setText(preferences.getSobrenome());
        idade.setText(preferences.getIdade());
        cpf.setText(preferences.getCPF());

        codPais.setText(preferences.getCodPais());
        codArea.setText(preferences.getCodArea());
        telefone.setText(preferences.getTelefone());

        rua.setText(preferences.getRua());
        numero.setText(preferences.getNumero());
        bairro.setText(preferences.getBairro());
        cep.setText(preferences.getCEP());
        cidade.setText(preferences.getCidade());


        /*****************************************************************************************
         * EDIT'S.GETTEXT
         *****************************************************************************************/

        final String nomeEdit = nome.getText().toString();
        final String sobrenomeEdit = sobrenome.getText().toString();
        final String idadeEdit = idade.getText().toString();
        final String telefoneEdit = telefone.getText().toString();
        final String codPaisEdit = codPais.getText().toString();
        final String codAreaEdit = codArea.getText().toString();
        final String ruaEdit = rua.getText().toString().trim();
        final String numeroEdit = numero.getText().toString().trim();
        final String bairroEdit = bairro.getText().toString().trim();
        final String cepEdit = cep.getText().toString().trim();
        final String cidadeEdit = cidade.getText().toString().trim();
        final String cpfEdit = cpf.getText().toString();

        /*****************************************************************************************
         * MASCARAS
         *****************************************************************************************/

        //MASCARA TELEFONE #########################################################################
        SimpleMaskFormatter simpleMaskTelefone = new SimpleMaskFormatter("NNNNN - NNNN");
        MaskTextWatcher maskTelefone = new MaskTextWatcher(telefone, simpleMaskTelefone);
        telefone.addTextChangedListener(maskTelefone);

        //MASCARA COD_PAIS #########################################################################
        SimpleMaskFormatter simpleMaskCodPais = new SimpleMaskFormatter("+NN");
        MaskTextWatcher maskCodPais = new MaskTextWatcher(codPais, simpleMaskCodPais);
        codPais.addTextChangedListener(maskCodPais);

        //MASCARA COD_AREA #########################################################################
        SimpleMaskFormatter simpleMaskCodArea = new SimpleMaskFormatter("NN");
        MaskTextWatcher maskCodArea = new MaskTextWatcher(codArea, simpleMaskCodArea);
        codArea.addTextChangedListener(maskCodArea);

        //MASCARA CEP #########################################################################
        SimpleMaskFormatter simpleMaskCEP = new SimpleMaskFormatter("NNNNN - NNN");
        MaskTextWatcher maskCEP = new MaskTextWatcher(cep, simpleMaskCEP);
        cep.addTextChangedListener(maskCEP);

        //MASCARA CPF ##############################################################################
        SimpleMaskFormatter simpleMaskCPF = new SimpleMaskFormatter("NNN.NNN.NNN / NN");
        MaskTextWatcher maskTextCPF = new MaskTextWatcher(cpf, simpleMaskCPF);
        cpf.addTextChangedListener(maskTextCPF);

        //+++++++++++++++++++++++++++++++++++++++++ATUALIZAR O SALVAR DADOS PREFERENCES POR ULTIMO PEGANDO VALORES DO USUARIO +++++++++++++++++++++++++++++++++++++++++++++++//

        /****************************************************************************************
         * BOTAO SALVAR
         ****************************************************************************************/
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Preferences preferences = new Preferences(EditarPerfil.this);
                String idUsuarioLogado = preferences.getIdentificador();


                /***********************************************************************************
                 * PEGANDO DADOS PESSOAIS
                 **********************************************************************************/
                //EXTRAINDO CELULAR DA FORMATACAO
                String telefoneCompleto =
                        codPaisEdit + codAreaEdit + telefoneEdit;

                String telefoneSemFormatacao = telefoneCompleto.replace("+", "");
                telefoneSemFormatacao = telefoneSemFormatacao.replace(" - ", "");

                //EXTRAINDO CPF DA FORMATACAO
                //String cpfCompleto = cpf;
                //String cpfSemFormatacao = cpfCompleto.replace(".", "");
                //cpfSemFormatacao = cpfSemFormatacao.replace(" / ", ""); // SALVAR NO USUARIO.SALVARCPF


                //ENDEREÇO COMPLETO
                String enderecoCompleto = ruaEdit + ", " + numeroEdit + ", " + bairroEdit + ", " + cidadeEdit + "\nCEP: " + cepEdit;
                //SALVAR NO USUARIO.SALVARENDERECO

                /***********************************************************************************
                 * SALVANDO DADOS PESSOAIS
                 **********************************************************************************/

                usuario = new Usuario();
                telefoneClasse = new Telefone();
                enderecoClasse = new Endereco();


                //nome, sobrenome, idade, cpf

                usuario.setNome(nome.getText().toString());
                usuario.setSobrenome(sobrenome.getText().toString());
                usuario.setIdade(idade.getText().toString());
                usuario.setCpf(cpf.getText().toString());
                usuario.setEmail(Base64Custom.decodificadorBase64(preferences.getIdentificador()));
                usuario.setId(preferences.getIdentificador());
                usuario.setSenha(preferences.getSenha());
                usuario.setTelefone(telefoneSemFormatacao);
                usuario.setEndereco(rua.getText().toString()
                        + ", " + numero.getText().toString()
                        + ", " + bairro.getText().toString()
                        + ", " + cidade.getText().toString()
                        + ", CEP: " + cep.getText().toString());
                usuario.salvar();

                preferences.salvarDados(idUsuarioLogado,
                        usuario.getNome(),
                        usuario.getSobrenome(),
                        usuario.getIdade(),
                        usuario.getTelefone(),
                        usuario.getCpf(),
                        usuario.getSenha(),
                        usuario.getEndereco());




                //telefone

                telefoneClasse.setCodPais(codPais.getText().toString());
                telefoneClasse.setCodArea(codArea.getText().toString());
                telefoneClasse.setCelular(telefone.getText().toString());   //CLASSE TELEFONE
                telefoneClasse.salvarTelefone(usuario.getId());

                preferences.salvarTelefone(usuario.getId(),
                        telefoneClasse.getCodPais(),
                        telefoneClasse.getCodArea(),
                        telefoneClasse.getCelular()); // CLASSE PREFERENCES



                //endereço

                enderecoClasse.setRua(rua.getText().toString());
                enderecoClasse.setNumero(numero.getText().toString());
                enderecoClasse.setBairro(bairro.getText().toString());  //CLASSE ENDERECO
                enderecoClasse.setCidade(cidade.getText().toString());
                enderecoClasse.setCep(cep.getText().toString());
                enderecoClasse.salvarEndereco(usuario.getId());


                preferences.salvarEndereco(usuario.getId(),
                        enderecoClasse.getRua(),
                        enderecoClasse.getNumero(),
                        enderecoClasse.getBairro(),                 //CLASSE PREFERENCES
                        enderecoClasse.getCep(),
                        enderecoClasse.getCidade());


                //MENSAGEM
                //Toast.makeText(getApplicationContext(), "Dados salvo com sucesso!!" , Toast.LENGTH_SHORT).show();

                startPosting();

            }
        });



        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditarPerfil.this, TelaUsuario.class);
                startActivity(intent);
                finish();
            }
        });



    }

    private void startPosting() {

        final Preferences preferences = new Preferences(EditarPerfil.this);
        final String idUsuarioLogado = preferences.getIdentificador();
        final String nomeUsuario = preferences.getNome();

        mProgress.setMessage("Salvando dados...");
        mProgress.show();


        if(imagemUri != null){
            //Salvando foto no STORAGE
            StorageReference filepath = mStorage
                    .child("FotoPerfil")
                    .child(idUsuarioLogado)
                    .child(imagemUri.getLastPathSegment());


            filepath.putFile(imagemUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    //preferences.salvarFotoPerfil(idUsuarioLogado, downloadUrl.toString());

                    DatabaseReference newPost = mDatabase;

                    newPost.child("imagem").setValue(downloadUrl.toString());
                    newPost.child("usuario").setValue(idUsuarioLogado);
                    newPost.child("nome").setValue(nomeUsuario);

                    mProgress.dismiss();
                    Toast.makeText(getApplicationContext(),"Dados salvo com sucesso!", Toast.LENGTH_SHORT).show();


                    Intent intent = new Intent(EditarPerfil.this, TelaUsuario.class);
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
            mProgress.dismiss();
            Toast.makeText(getApplicationContext(),"Dados salvo com sucesso!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditarPerfil.this, TelaUsuario.class);
            startActivity(intent);
            finish();
        }



    }


    //Fazendo verificação e setando imagem na ImageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALERIA_IMAGENS && resultCode == RESULT_OK) {

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

    @Override
    protected void onStart() {
        super.onStart();
        mProgress.setMessage("Carregando...");
        mProgress.setCancelable(false);
        mProgress.show();

        /************************************************************************************
         * Setando imagem do firebase
         */
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference = databaseReference.child("FotoPerfil").child(preferences.getIdentificador()).child("imagem");

        if(imagemUri == null){
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.getValue() != null){
                        String url = dataSnapshot.getValue(String.class);
                        Glide.with(getApplicationContext()).load(url).into(imagemSelecionada);
                        mProgress.dismiss();
                    } else {
                        mProgress.dismiss();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mProgress.dismiss();
                }
            });
        } else {
            mProgress.dismiss();
        }

    }


    /***********************************************************************************
     * GETTERS AND SETTERS
     ***********************************************************************************/

    public EditText getNome() {
        return nome;
    }

    public void setNome(EditText nome) {
        this.nome = nome;
    }

    public EditText getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(EditText sobrenome) {
        this.sobrenome = sobrenome;
    }

    public EditText getIdade() {
        return idade;
    }

    public void setIdade(EditText idade) {
        this.idade = idade;
    }

    public EditText getTelefone() {
        return telefone;
    }

    public void setTelefone(EditText telefone) {
        this.telefone = telefone;
    }

    public EditText getCodPais() {
        return codPais;
    }

    public void setCodPais(EditText codPais) {
        this.codPais = codPais;
    }

    public EditText getCodArea() {
        return codArea;
    }

    public void setCodArea(EditText codArea) {
        this.codArea = codArea;
    }

    public EditText getRua() {
        return rua;
    }

    public void setRua(EditText rua) {
        this.rua = rua;
    }

    public EditText getNumero() {
        return numero;
    }

    public void setNumero(EditText numero) {
        this.numero = numero;
    }

    public EditText getBairro() {
        return bairro;
    }

    public void setBairro(EditText bairro) {
        this.bairro = bairro;
    }

    public EditText getCep() {
        return cep;
    }

    public void setCep(EditText cep) {
        this.cep = cep;
    }

    public EditText getCidade() {
        return cidade;
    }

    public void setCidade(EditText cidade) {
        this.cidade = cidade;
    }

    public EditText getCpf() {
        return cpf;
    }

    public void setCpf(EditText cpf) {
        this.cpf = cpf;
    }
}
