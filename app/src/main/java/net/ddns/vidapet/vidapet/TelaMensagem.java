package net.ddns.vidapet.vidapet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.ddns.vidapet.adapter.TabAdapter;
import net.ddns.vidapet.helper.Base64Custom;
import net.ddns.vidapet.helper.Preferences;
import net.ddns.vidapet.helper.SlidingTabLayout;
import net.ddns.vidapet.model.Contato;
import net.ddns.vidapet.model.Usuario;

public class TelaMensagem extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Toolbar toolbar;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private String identificadorContato;
    private static  String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_mensagem);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        toolbar = (Toolbar) findViewById(R.id.tb_mensagem);
        toolbar.setTitle("Mensagens");
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs);
        viewPager = (ViewPager) findViewById(R.id.vp_pagina);

        //CONFIGURANDO TABS
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.colorPrimaryDark));


        //CONFIGURAR ADAPTER
        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);
        slidingTabLayout.setViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.item_adicionar: abrirCadastroContato();
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }


    public void abrirCadastroContato(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth);


        //Configurando a AlertDialog
        alertDialog.setTitle("Novo Contato");
        alertDialog.setMessage("E-mail do contato:");
        alertDialog.setCancelable(false);

        //Campo de texto
        final EditText editText = new EditText(this);
        editText.setRawInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        alertDialog.setView(editText);

        //Botao OK
        alertDialog.setPositiveButton("CADASTRAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Preferences usuarioTeste = new Preferences(TelaMensagem.this);

                String emailDigitado = editText.getText().toString();

                if(emailDigitado.isEmpty()){
                    Toast.makeText(getApplicationContext(), "O campo está vazio", Toast.LENGTH_SHORT).show();
                } else if(emailDigitado.equals(Base64Custom.decodificadorBase64(usuarioTeste.getIdentificador()))){
                    Toast.makeText(getApplicationContext(), "Você não pode se auto adicionar! :/ ", Toast.LENGTH_SHORT).show();
                } else {
                    //Verificar se o usuario ja esta cadastrado
                    identificadorContato = Base64Custom.converterBase64(emailDigitado);

                    //Recuperar instacia do firebase
                    //firebaseAuth = FirebaseAuth.getInstance();
                    databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference = databaseReference.child("Usuarios").child(identificadorContato);

                    /*******************************************************************************
                     * PEGANDO FOTO DO CABOCLO
                     ******************************************************************************/
                    DatabaseReference databaseImg = FirebaseDatabase.getInstance().getReference()
                            .child("FotoPerfil")
                            .child(identificadorContato)
                            .child("imagem");
                    databaseImg.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue() != null){
                               url = dataSnapshot.getValue(String.class);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                    //Verificar consulta uma vez somente
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Verificar se foi retornado algum valor
                            if(dataSnapshot.getValue() != null){
                                //Recuperando dados do contato à ser adicionado
                                Usuario usuarioContato = new Usuario();
                                usuarioContato = dataSnapshot.getValue(Usuario.class);

                                //Recuperando dados do usuario logado
                                Preferences preferences = new Preferences(TelaMensagem.this);
                                String identificadorUsuarioLogado =  preferences.getIdentificador();


                                //contato
                                Contato contato = new Contato();
                                contato.setIdentificadorUsuario(identificadorContato);
                                contato.setEmail(usuarioContato.getEmail());
                                contato.setNome(usuarioContato.getNome());
                                contato.setFoto(url);
                                contato.setSobrenome(usuarioContato.getSobrenome());


                                //Recuperar dados FIREBASE
                                databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference = databaseReference
                                        .child("Contatos")
                                        .child(identificadorUsuarioLogado)
                                        .child(identificadorContato);
                                databaseReference.setValue(contato);



                            } else {
                                Toast.makeText(getApplicationContext(), "Ops! Usuário não encontrado", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        });

        //Botao cancelar
        alertDialog.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.create();
        alertDialog.show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if((connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
                || (connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState() == NetworkInfo.State.CONNECTED)) {
            //we are connected to a network
            connected = true;
        } else {
            connected = false;
            Toast.makeText(getApplicationContext(), "Sem conexão com a Internet", Toast.LENGTH_SHORT).show();
        }
    }
}
