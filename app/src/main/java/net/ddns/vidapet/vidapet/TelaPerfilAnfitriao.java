package net.ddns.vidapet.vidapet;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.ddns.vidapet.adapter.PetsAdapterAnfitriao;
import net.ddns.vidapet.helper.Base64Custom;
import net.ddns.vidapet.helper.Preferences;
import net.ddns.vidapet.model.Contato;
import net.ddns.vidapet.model.Endereco;
import net.ddns.vidapet.model.Pet;
import net.ddns.vidapet.model.Telefone;
import net.ddns.vidapet.model.Usuario;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TelaPerfilAnfitriao extends AppCompatActivity {

    private Toolbar toolbar;
    private ArrayAdapter<Pet> arrayAdapter;
    private ArrayList<Pet> lista;
    private ListView listView;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListenerPet;
    private String idUsuario;
    private LinearLayout linearLayout;
    private ScrollView scrollView;

    private TextView nomeAnfitriao, telefoneAnfitriao,
            emailAnfitriao, bairroAnfitriao,
            cidadeAnfitriao, cepAnfitriao ,semPet;

    private CircleImageView fotoAnfitriao;
    private DatabaseReference databaseAnfitriao,
            databaseEndereco,
            databaseFoto,
            databaseTelefone, databaseAddContato;

    private int countItem;

    private String nomeToolbar, navigation, verificarMsg;

    private static Object url;

    private static Boolean verificarContato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_perfil_anfitriao);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Toast.makeText(getApplicationContext(), this.getClass().getName() , Toast.LENGTH_SHORT).show();

        //FindViewById==============================================================================

        nomeAnfitriao = (TextView) findViewById(R.id.nomeTelaAnfitriao);
        telefoneAnfitriao = (TextView) findViewById(R.id.telefoneTelaAnfitriao);
        emailAnfitriao = (TextView) findViewById(R.id.emailTelaAnfitriao);
        bairroAnfitriao = (TextView) findViewById(R.id.bairroTelaAnfitriao);
        cidadeAnfitriao = (TextView) findViewById(R.id.cidadeTelaAnfitriao);
        cepAnfitriao = (TextView) findViewById(R.id.cepTelaAnfitriao);
        fotoAnfitriao = (CircleImageView) findViewById(R.id.imgUserAnfitriao);
        semPet = (TextView) findViewById(R.id.tv_semPet);
        listView = (ListView) findViewById(R.id.lv_listaPetsAnfitriao);
        linearLayout = (LinearLayout) findViewById(R.id.ll_listaPetAnfitriao);
        scrollView = (ScrollView) findViewById(R.id.sv_tela_anfitriao);
        //SCROLLVIEW OBTENDO FOCO E PERMANCENDO NO TOPO
        scrollView.setFocusableInTouchMode(true);
        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);


        //Toolbar ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        toolbar = (Toolbar) findViewById(R.id.tb_anfitriao2);

        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        /******************************************************************************************
         * AJEITANDO A LIST VIEW
         ******************************************************************************************/
        lista = new ArrayList<>();
        arrayAdapter = new PetsAdapterAnfitriao(TelaPerfilAnfitriao.this, lista);
        //PetsAdapterAnfitriao petsAdapter = new PetsAdapterAnfitriao(this, lista);
        listView.setAdapter(arrayAdapter);


        listView.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });


        //RECUPERAR DADOS ENVIADOS NA INTENT +++++++++++++++++++++++++++++++++++++++++++++++++++++++
        Bundle extra = getIntent().getExtras(); //OBJETO UTILIZADO PARA PEGAR DADOS ENTRE INTENTS
        if(extra != null){
            //RECUPERAR DADOS (DESTINATARIO)
            idUsuario = Base64Custom.converterBase64(extra.getString("emailAnfitriao"));
            navigation = extra.getString("aplication");
        }
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        /***************************************************************************
         * RECUPERAR PETS NO FIREBASE
         ***************************************************************************/
        //INSTACIA DO FIREBASE
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference = databaseReference
                .child("PETs")
                .child(idUsuario);
        databaseReference.keepSynced(true);

        valueEventListenerPet = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    semPet.setText("Não possui PETs");
                }
                else {
                    semPet.setVisibility(View.INVISIBLE);
                    lista.clear();
                    for (DataSnapshot dados: dataSnapshot.getChildren()) {
                        Pet pet = dados.getValue(Pet.class);
                        if(pet.getFoto() == null || pet.getFoto() == ""){
                            pet.setFoto("");
                        }
                        lista.add(pet);
                    }

                    arrayAdapter.notifyDataSetChanged();
                    countItem = lista.size();
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();


                    if(countItem == 2){
                        lp.height = 400;
                    } else if(countItem >= 3){
                        lp.height = 500;
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        databaseReference.addValueEventListener(valueEventListenerPet);
        //******************************************************************************************

        /***************************************************************************
         * RECUPERAR Dados NO FIREBASE
         ***************************************************************************/
        //DADOS PESSOAIS
        databaseAnfitriao = FirebaseDatabase.getInstance().getReference()
                .child("Usuarios")
                .child(idUsuario);
        databaseAnfitriao.keepSynced(true);
        databaseAnfitriao.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuarioAnfitriao = dataSnapshot.getValue(Usuario.class);

                nomeAnfitriao.setText(usuarioAnfitriao.getNome() + " " + usuarioAnfitriao.getSobrenome());
                //telefoneAnfitriao.setText(usuarioAnfitriao.getTelefone());
                emailAnfitriao.setText(usuarioAnfitriao.getEmail());
                nomeToolbar = usuarioAnfitriao.getNome();
                toolbar.setTitle(nomeToolbar);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Erro ao retornar dados", Toast.LENGTH_SHORT).show();
            }
        });

        databaseTelefone = FirebaseDatabase.getInstance().getReference()
                .child("Usuarios")
                .child(idUsuario)
                .child("Telefone");
        databaseTelefone.keepSynced(true);
        databaseTelefone.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.getValue().equals(null) && !dataSnapshot.getValue().equals("")){
                    Telefone telAnfitriao = dataSnapshot.getValue(Telefone.class);
                    telefoneAnfitriao.setText(telAnfitriao.getCodPais()
                            + " "
                            + telAnfitriao.getCodArea()
                            + " "
                            + telAnfitriao.getCelular());
                } else {
                    telefoneAnfitriao.setText("");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //ENDEREÇO
        databaseEndereco = FirebaseDatabase.getInstance().getReference()
                .child("Usuarios")
                .child(idUsuario)
                .child("Endereco");
        databaseEndereco.keepSynced(true);
        databaseEndereco.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.getValue().equals(null) && !dataSnapshot.getValue().equals("")){
                    Endereco enderecoAnfitriao = dataSnapshot.getValue(Endereco.class);
                    bairroAnfitriao.setText(enderecoAnfitriao.getBairro());
                    cidadeAnfitriao.setText(enderecoAnfitriao.getCidade());
                    cepAnfitriao.setText(enderecoAnfitriao.getCep());
                } else {
                    bairroAnfitriao.setText("");
                    cidadeAnfitriao.setText("");
                    cepAnfitriao.setText("");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Erro ao retornar dados", Toast.LENGTH_SHORT).show();
            }
        });

        //FOTO
        databaseFoto = FirebaseDatabase.getInstance().getReference()
                .child("FotoPerfil")
                .child(idUsuario)
                .child("imagem");
        databaseFoto.keepSynced(true);
        databaseFoto.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    url = dataSnapshot.getValue(Object.class);
                    Glide.with(getApplicationContext()).load(url).into(fotoAnfitriao);
                }else  {
                    fotoAnfitriao.setImageResource(R.drawable.img_usuario);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Erro ao retornar dados", Toast.LENGTH_SHORT).show();
            }
        });



    } //FIM DO MÉTODO OnCreate =====================================================================


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        //RECUPERAR DADOS ENVIADOS NA INTENT +++++++++++++++++++++++++++++++++++++++++++++++++++++++
        Bundle extra = getIntent().getExtras(); //OBJETO UTILIZADO PARA PEGAR DADOS ENTRE INTENTS
        if(extra != null){
            //RECUPERAR DADOS (DESTINATARIO);
            verificarMsg = extra.getString("itemMensagem");
        }
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


        getMenuInflater().inflate(R.menu.menu_contato, menu);

        Drawable drawable = menu.findItem(R.id.item_mensagem_contato).getIcon();

        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this,R.color.whiteColor));
        menu.findItem(R.id.item_mensagem_contato).setIcon(drawable);

        MenuItem msgItem = menu.findItem(R.id.item_mensagem_contato);
        final MenuItem addItem = menu.findItem(R.id.item_adicionar_contato);

        if(verificarMsg.equals("true")){
            msgItem.setVisible(true);
        }

        //VERIFICANDO====================================
        Preferences preferences = new Preferences(TelaPerfilAnfitriao.this);
        final String idUsuarioLogado = preferences.getIdentificador();

        databaseAddContato = FirebaseDatabase.getInstance().getReference()
                .child("Contatos")
                .child(idUsuarioLogado)
                .child(idUsuario);

        databaseAddContato.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    addItem.setVisible(false);
                } else {
                    addItem.setVisible(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                addItem.setVisible(false);
            }
        });

            return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.item_adicionar_contato: adicionarContato();
                return true;
            case R.id.item_mensagem_contato:

                //CRIAR INTENT PARA CONVERSAACTIVITY
                Intent intent = new Intent(TelaPerfilAnfitriao.this, ConversaActivity.class);
                String emailDecod = Base64Custom.decodificadorBase64(idUsuario);
                intent.putExtra("email", emailDecod);
                intent.putExtra("nome", nomeAnfitriao.getText().toString());
                startActivity(intent);

                finish();
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }


    private void adicionarContato(){

        Preferences preferences = new Preferences(TelaPerfilAnfitriao.this);
        final String idUsuarioLogado = preferences.getIdentificador();

        databaseAddContato = FirebaseDatabase.getInstance().getReference()
                .child("Contatos")
                .child(idUsuarioLogado)
                .child(idUsuario);

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth);
            //Configurando a AlertDialog
            alertDialog.setTitle("Novo Contato");
            alertDialog.setMessage("Adicionar este usuário aos seus contatos?");
            alertDialog.setCancelable(false);

            alertDialog.setPositiveButton("CADASTRAR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    databaseAddContato = FirebaseDatabase.getInstance().getReference()
                            .child("Contatos")
                            .child(idUsuarioLogado)
                            .child(idUsuario);

                    databaseAnfitriao.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.getValue().equals(null)){
                                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                                //contato
                                Contato contato = new Contato();
                                contato.setIdentificadorUsuario(idUsuario);
                                contato.setEmail(usuario.getEmail());
                                contato.setNome(usuario.getNome());
                                contato.setFoto((String) url);
                                contato.setSobrenome(usuario.getSobrenome());

                                databaseAddContato.setValue(contato);

                                Toast.makeText(getApplicationContext(), "Contato adiciondo com sucesso!!", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), "Erro inesperado.", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });

            alertDialog.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            alertDialog.create();
            alertDialog.show();

    }


    @Override
    public void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(valueEventListenerPet);
    }

    @Override
    public void onStop() {
        super.onStop();
        databaseReference.removeEventListener(valueEventListenerPet);

    }

} //FIM DA CLASSE =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
