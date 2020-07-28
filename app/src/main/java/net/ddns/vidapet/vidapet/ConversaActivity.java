package net.ddns.vidapet.vidapet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.ddns.vidapet.adapter.MensagemAdapter;
import net.ddns.vidapet.helper.Base64Custom;
import net.ddns.vidapet.helper.Preferences;
import net.ddns.vidapet.model.Conversa;
import net.ddns.vidapet.model.Mensagem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editMensagem;
    private ImageButton btEnviar;
    private DatabaseReference databaseReference, databaseReference2, databaseReference3, databaseIMG;
    private ListView lvMensagens;
    private ArrayAdapter<Mensagem> arrayAdapter;
    private ArrayList<Mensagem> arrayListmensagens;
    private ValueEventListener valueEventListenerMensagens;
    private Conversa conversa;
    private TextView nomeContato;
    private CircleImageView imgContato;
    private ImageView btnVoltar;

    //DESTINATARIO
    private String nomeUsuarioDestinatario;
    private String idUsuarioDestinatario;

    //REMETENTE
    private String idUsuarioLogado;
    private String nomeUsuarioLogado;

    private String mydate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


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


        toolbar = (Toolbar) findViewById(R.id.tb_Conversa);
        editMensagem = (EditText) findViewById(R.id.edit_mensagem);
        btEnviar = (ImageButton) findViewById(R.id.bt_enviar);
        lvMensagens = (ListView) findViewById(R.id.lv_mensagens);
        nomeContato = (TextView) findViewById(R.id.txt_toolbar);
        btnVoltar = (ImageView) findViewById(R.id.img_voltar_toolbar);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        //RECUPERAR DADOS DO USUARIO LOGADO
        Preferences preferences = new Preferences(ConversaActivity.this);
        idUsuarioLogado = preferences.getIdentificador();
        nomeUsuarioLogado = preferences.getNome();


        //RECUPERAR DADOS ENVIADOS NA INTENT
        Bundle extra = getIntent().getExtras(); //OBJETO UTILIZADO PARA ENVIAR DADOS ENTRE INTENTS
        if(extra != null){
            //RECUPERAR DADOS (DESTINATARIO)
            nomeUsuarioDestinatario = extra.getString("nome");
            idUsuarioDestinatario = Base64Custom.converterBase64(extra.getString("email"));
        }

        //CONFIGURANDO A TOOLBAR
        //toolbar.setTitle(nomeUsuarioDestinatario);
        //toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /***********************************************************
         * IMAGEM USUARIO CONTATO
         ***********************************************************/
        nomeContato.setText(nomeUsuarioDestinatario);
        nomeContato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirPerfil();
            }
        });

        databaseIMG = FirebaseDatabase.getInstance().getReference()
                .child("FotoPerfil")
                .child(idUsuarioDestinatario)
                .child("imagem");
        databaseIMG.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imgContato = (CircleImageView) findViewById(R.id.img_toolbar);
                if(dataSnapshot.getValue() != null){
                    Object url = dataSnapshot.getValue(Object.class);
                    Glide.with(getApplicationContext()).load(url).into(imgContato);
                }else  {
                    imgContato.setImageResource(R.drawable.img_usuario);
                }
                imgContato.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        abrirPerfil();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                imgContato = (CircleImageView) findViewById(R.id.imgTelaUsuarioHomePage);
                imgContato.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(ConversaActivity.this, TelaUsuario.class));
                    }
                });
            }
        });

        /***********************************************************
         * MONTAGEM LISTVIEW E ADAPTER
         ***********************************************************/
        arrayListmensagens = new ArrayList<>();

        /*arrayAdapter = new ArrayAdapter<String>(
                ConversaActivity.this,
                android.R.layout.simple_list_item_1,
                arrayListmensagens
        );*/

        arrayAdapter = new MensagemAdapter(ConversaActivity.this, arrayListmensagens);
        lvMensagens.setAdapter(arrayAdapter);



        /***********************************************************
         * RECUPERAR MENSAGENS
         ***********************************************************/
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference = databaseReference.child("Mensagens")
                .child(idUsuarioLogado)
                .child(idUsuarioDestinatario);
        databaseReference.keepSynced(true);
        //CRIAR LISTENER PARA MENSAGENS
        valueEventListenerMensagens = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //LIMPAR ARRAY DE MENSAGENS
                arrayListmensagens.clear();

                //RECUPERAR MENSAGENS
                for(DataSnapshot dados: dataSnapshot.getChildren()){
                    //RECUPERAR MENSAGEM INDIVIDUAL
                    Mensagem mensagem = dados.getValue(Mensagem.class);
                    //ADICIONAR AO LIST
                    arrayListmensagens.add(mensagem);
                }

                arrayAdapter.notifyDataSetChanged(); //Notificando que dados mudaram

                lvMensagens.setSelection(lvMensagens.getAdapter().getCount() - 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addValueEventListener(valueEventListenerMensagens);


        //ENVIAR MENSAGEM
        btEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoMensagem = editMensagem.getText().toString();

                //VERFICAR SE FOI PREECHIDA A MENSAGEM
                if(textoMensagem.isEmpty()){
                    Toast.makeText(ConversaActivity.this, "Por favor, preencha o campo de mensagem antes de enviar :)",
                            Toast.LENGTH_SHORT).show();

                } else{

                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    mydate = sdf.format(new Date());

                    /**************************************************************************
                     * SALVAR MENSAGENS NO FIREBASE
                     **************************************************************************/
                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario(idUsuarioLogado);
                    mensagem.setMensagem(textoMensagem);
                    mensagem.setData(mydate);

                    //SALVAR MENSAGEM PARA O REMETENTE
                    Boolean retornoRemetente = salvarMensagemFirebase(idUsuarioLogado, idUsuarioDestinatario, mensagem);
                    if(!retornoRemetente){
                        Toast.makeText(ConversaActivity.this, "Erro ao enviar mensagens", Toast.LENGTH_SHORT).show();
                    }

                    //SALVAR MENSAGEM PARA O DESTINATARIO
                    Boolean retornoDestinatario = salvarMensagemFirebase(idUsuarioDestinatario, idUsuarioLogado, mensagem);
                    if(!retornoDestinatario){
                        Toast.makeText(ConversaActivity.this, "Erro ao enviar mensagens", Toast.LENGTH_SHORT).show();
                    }


                    /**************************************************************************
                     * SALVAR CONVERSAS
                     **************************************************************************/
                    //SALVAR CONVERSA REMETENTE
                    conversa = new Conversa();
                    conversa.setIdUsuario(idUsuarioDestinatario);
                    conversa.setNome(nomeUsuarioDestinatario);
                    conversa.setMensagem(textoMensagem);
                    conversa.setData(mydate);
                    conversa.setEmail(Base64Custom.decodificadorBase64(idUsuarioDestinatario));
                    Boolean retornoConvRem = salvarConversaFirebase(idUsuarioLogado, idUsuarioDestinatario, conversa);
                    if(!retornoConvRem){
                        Toast.makeText(ConversaActivity.this, "Erro ao salvar mensagens", Toast.LENGTH_SHORT).show();
                    }


                    //SALVAR CONVERSA DESTINATARIO
                    conversa = new Conversa();
                    conversa.setIdUsuario(idUsuarioLogado);
                    conversa.setNome(nomeUsuarioLogado);
                    conversa.setMensagem(textoMensagem);
                    conversa.setData(mydate);
                    conversa.setEmail(Base64Custom.decodificadorBase64(idUsuarioLogado));
                    Boolean retornoConvDest = salvarConversaFirebase(idUsuarioDestinatario, idUsuarioLogado, conversa);
                    if(!retornoConvDest){
                        Toast.makeText(ConversaActivity.this, "Erro ao salvar mensagens", Toast.LENGTH_SHORT).show();
                    }

                    //APAGAR TEXTO DIGITADO APÓS ENVIAR
                    editMensagem.setText("");
                }
            }
        });


    }


    public static void showToolBar(Toolbar toolbar,
                                   final AppCompatActivity activity) {
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        @SuppressWarnings("deprecation")
        Drawable drawable = activity.getResources().getDrawable(
                R.drawable.img_usuario);
        drawable.setColorFilter(
                activity.getResources().getColor(R.color.colorPrimaryDark),
                android.graphics.PorterDuff.Mode.SRC_ATOP);
        activity.getSupportActionBar().setHomeAsUpIndicator(drawable);
        toolbar.setBackgroundColor(activity.getResources().getColor(
                R.color.colorPrimary));
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }


    private Boolean salvarMensagemFirebase(String idRemetente, String idDestinatario, Mensagem mensagem ){
        try{
            databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference = databaseReference.child("Mensagens");
            databaseReference.child(idRemetente)
                    .child(idDestinatario)
                    .push()
                    .setValue(mensagem);


            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private Boolean salvarConversaFirebase(String idRemetente, String idDestinatario, Conversa conversa ){
        try{
            databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference = databaseReference.child("Conversas");
            databaseReference.child(idRemetente)
                    .child(idDestinatario)
                    .setValue(conversa);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_conversas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.item_ver_perfil: abrirPerfil();
                return true;
            case R.id.item_limpar_conversa: limparConversa();
                return true;
            case R.id.item_apagar_conversa: apagarConversa();

            default: return super.onOptionsItemSelected(item);
        }
    }

    private void abrirPerfil(){
        Preferences preferences = new Preferences(ConversaActivity.this);

        Intent intent = new Intent(ConversaActivity.this, TelaPerfilAnfitriao.class);
        intent.putExtra("emailAnfitriao", Base64Custom.decodificadorBase64(idUsuarioDestinatario)); //data is a string variable holding some value.
        intent.putExtra("itemMensagem", "false");
        startActivity(intent);

    }

    public void limparConversa(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConversaActivity.this);

        //Configurando a AlertDialog
        alertDialog.setTitle("Limpar conversa");
        alertDialog.setMessage("Deseja realmente limpar esta conversa?");
        alertDialog.setCancelable(false);

        //BOTAO SIM
        alertDialog.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseReference2 = FirebaseDatabase.getInstance().getReference()
                                .child("Mensagens")
                                .child(idUsuarioLogado)
                                .child(idUsuarioDestinatario);
                        databaseReference2.removeValue();

                        //SALVAR CONVERSA REMETENTE
                        conversa = new Conversa();
                        conversa.setIdUsuario(idUsuarioDestinatario);
                        conversa.setNome(nomeUsuarioDestinatario);
                        conversa.setMensagem("");
                        conversa.setEmail(Base64Custom.decodificadorBase64(idUsuarioDestinatario));
                        Boolean retornoConvRem = salvarConversaFirebase(idUsuarioLogado, idUsuarioDestinatario, conversa);
                        if(!retornoConvRem){
                            Toast.makeText(ConversaActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }

                        Toast.makeText(getApplicationContext(), "Conversa limpa!", Toast.LENGTH_SHORT).show();
                    }
                });
        //BOTAO CANCELAR
        alertDialog.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.create();
        alertDialog.show();


    }

    private void apagarConversa(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConversaActivity.this);

        //Configurando a AlertDialog
        alertDialog.setTitle("Apagar conversa");
        alertDialog.setMessage("Deseja realmente apagar esta conversa?");
        alertDialog.setCancelable(false);

        //BOTAO SIM
        alertDialog.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseReference2 = FirebaseDatabase.getInstance().getReference()
                                .child("Mensagens")
                                .child(idUsuarioLogado)
                                .child(idUsuarioDestinatario);
                        databaseReference2.removeValue();

                        databaseReference3 = FirebaseDatabase.getInstance().getReference()
                                .child("Conversas")
                                .child(idUsuarioLogado)
                                .child(idUsuarioDestinatario);
                        databaseReference3.removeValue();

                        startActivity(new Intent(ConversaActivity.this, TelaMensagem.class));
                        finish();
                        Toast.makeText(getApplicationContext(), "Conversa apagada!", Toast.LENGTH_SHORT).show();
                    }
                });
        //BOTAO CANCELAR
        alertDialog.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.create();
        alertDialog.show();

    }



    @Override
    protected void onStop() {
        super.onStop();
        databaseReference.removeEventListener(valueEventListenerMensagens);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        databaseReference.addValueEventListener(valueEventListenerMensagens);
    }

}
