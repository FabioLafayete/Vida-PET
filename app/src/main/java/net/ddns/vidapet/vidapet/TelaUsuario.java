package net.ddns.vidapet.vidapet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.ddns.vidapet.helper.Base64Custom;
import net.ddns.vidapet.helper.Preferences;
import net.ddns.vidapet.model.Usuario;

import de.hdodenhof.circleimageview.CircleImageView;

public class TelaUsuario extends AppCompatActivity {

    private String [] permissoesNecessarias = new String[]{
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private final int PERMISSAO_REQUEST = 2;

    private Toolbar toolbar;
    private Usuario usuario;
    private CircleImageView imagem;
    private ProgressDialog progress;
    private TextView nome, idade, telefone, cpf, email, rua, numero, bairro, cidade, cep;
    private TextView excluirConta, trocarSenha;
    private CalendarView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_usuario);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        progress = new ProgressDialog(this);

        usuario = new Usuario();
        Preferences preferences = new Preferences(TelaUsuario.this);


        toolbar = (Toolbar) findViewById(R.id.tb_user);
        toolbar.setTitle(preferences.getNome());
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        imagem = (CircleImageView) findViewById(R.id.imgUser);

        nome = (TextView) findViewById(R.id.nomeTelaUsuario);
        idade = (TextView) findViewById(R.id.idadeTelaUsuario);
        telefone = (TextView) findViewById(R.id.telefoneTelaUsuario);
        cpf = (TextView) findViewById(R.id.cpfTelaUsuario);
        email = (TextView) findViewById(R.id.emailTelaUsuario);
        rua = (TextView) findViewById(R.id.ruaTelaUsuario);
        numero = (TextView) findViewById(R.id.numeroTelaUsuario);
        bairro = (TextView) findViewById(R.id.bairroTelaUsuario);
        cidade = (TextView) findViewById(R.id.cidadeTelaUsuario);
        cep = (TextView) findViewById(R.id.cepTelaUsuario);

        excluirConta = (TextView) findViewById(R.id.txtExcluirConta);
        trocarSenha = (TextView) findViewById(R.id.txtTrocarSenha);

        calendar = (CalendarView) findViewById(R.id.calendar_perfil);

        /*****************************************************
         * Colcoando Valores
         *****************************************************/

        String nomeS = preferences.getNome();
        String sobrenomeS = preferences.getSobrenome();
        String emailS = Base64Custom.decodificadorBase64(preferences.getIdentificador());
        String idadeS = preferences.getIdade();
        String cpfS = preferences.getCPF();
        //String celular = preferences.getCelular();
        String codPaisS = preferences.getCodPais();
        String codAreaS = preferences.getCodArea();
        String telefoneS = preferences.getTelefone();

        nome.setText(nomeS + " " + sobrenomeS);
        idade.setText(idadeS + " anos");
        telefone.setText(codPaisS + " " + codAreaS + " " + telefoneS);
        cpf.setText(cpfS);
        email.setText(emailS);

        rua.setText(preferences.getRua());
        numero.setText(preferences.getNumero());
        bairro.setText(preferences.getBairro());
        cidade.setText(preferences.getCidade());
        cep.setText(preferences.getCEP());


        excluirConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TelaUsuario.this, ExcluirConta.class));
            }
        });
        trocarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TelaUsuario.this, AlterarSenha.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        progress.setMessage("Carregando...");
        progress.setCancelable(false);
        progress.show();

        Preferences preferences = new Preferences(TelaUsuario.this);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference = databaseReference.child("FotoPerfil").child(preferences.getIdentificador()).child("imagem");
        databaseReference.keepSynced(true);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue() != null){
                    Object url = dataSnapshot.getValue(Object.class);
                    Glide.with(getApplicationContext()).load(url).into(imagem);
                    progress.dismiss();
                }else {
                    imagem.setImageResource(R.drawable.img_usuario);
                    progress.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_perfil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.item_editar_perfil: abrirEditarPerfil();
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    public void abrirEditarPerfil(){
        Intent intent = new Intent(TelaUsuario.this, EditarPerfil.class);
        startActivity(intent);
    }



}