package net.ddns.vidapet.vidapet;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import net.ddns.vidapet.helper.Base64Custom;
import net.ddns.vidapet.helper.Preferences;
import net.ddns.vidapet.model.Postagem;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth firebaseAuth;
    private RecyclerView publica_lista;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseCurtir;
    private ProgressDialog mProgress;
    private DatabaseReference databaseReference;

    private TextView nomeUsuario, emailUsuario;
    private CircleImageView imgTelaUsuario;
    private Preferences preferences;
    private static String imgUsuPostagem;
    private static DatabaseReference databaseImgAtu = FirebaseDatabase.getInstance().getReference();

    private android.support.v7.widget.Toolbar toolbar;

    private boolean mProcessoCurtir = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        mProgress = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Publicações");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Postagens");
        mDatabase.keepSynced(true);
        mDatabaseCurtir = FirebaseDatabase.getInstance().getReference().child("Curtidas");

        mProgress.dismiss();
        mDatabaseCurtir.keepSynced(true);

        //Organizando layout do que for recuperado da outra tela
        publica_lista = (RecyclerView) findViewById(R.id.publicacoes_lista);
        publica_lista.setHasFixedSize(true);
        publica_lista.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_publica) {

        } else if (id == R.id.nav_mensagens) {
            Intent intent = new Intent(HomePage.this, TelaMensagem.class);
            startActivity(intent);
        } else if (id == R.id.nav_pets) {
            Intent intent = new Intent(HomePage.this, ListarPets.class);
            startActivity(intent);
        } else if (id == R.id.nav_user) {
            Intent intent = new Intent(HomePage.this, TelaUsuario.class);
            startActivity(intent);
        } else if (id == R.id.nav_ajuda) {
            Intent intent = new Intent(HomePage.this, TelaAjuda.class);
            startActivity(intent);
        } else if (id == R.id.nav_sair) {
            eventoSair();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public void deslogarUsuario(){
        firebaseAuth.signOut();
        Intent intent = new Intent(HomePage.this, TelaLogin.class);
        startActivity(intent);
        finish();
    }

    public void abrirTelaUsuario(View view){
        Intent intent = new Intent(HomePage.this, TelaUsuario.class);
        startActivity(intent);
    }

    public interface SimpleCallback {
        void callback(Object data);
    }




    @Override
    protected void onStart() {
        super.onStart();

        mProgress.setMessage("Carregando publicações...");
        mProgress.setCancelable(false);
        mProgress.show();

        databaseImgAtu.keepSynced(true);

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

        /*****************************************************************************************
         * Pegando imagem usuario Logado
         *****************************************************************************************/



        Preferences preferencesImg = new Preferences(HomePage.this);

        DatabaseReference databaseImg = FirebaseDatabase.getInstance().getReference();
        databaseImg = databaseImg.child("FotoPerfil").child(preferencesImg.getIdentificador()).child("imagem");
        databaseImg.keepSynced(true);

        databaseImg.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imgTelaUsuario = (CircleImageView) findViewById(R.id.imgTelaUsuarioHomePage);
                if(dataSnapshot.getValue() != null){
                    Object url = dataSnapshot.getValue(Object.class);
                    Glide.with(getApplicationContext()).load(url).into(imgTelaUsuario);
                }else  {
                    imgTelaUsuario.setImageResource(R.drawable.img_usuario);
                }
                imgTelaUsuario.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(HomePage.this, TelaUsuario.class));
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                imgTelaUsuario = (CircleImageView) findViewById(R.id.imgTelaUsuarioHomePage);
                imgTelaUsuario.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(HomePage.this, TelaUsuario.class));
                    }
                });
            }
        });



        //Recuperar instacia do firebase
        //firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference = databaseReference.child("Postagens");
        databaseReference.keepSynced(true);


        //Verificar consulta uma vez somente
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                //Verificar se foi retornado algum valor
                if (dataSnapshot.getValue() != null) {
                    FirebaseRecyclerAdapter<Postagem, HomePage.PostagemViewHolder>
                            firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Postagem,
                            HomePage.PostagemViewHolder>(
                            Postagem.class,
                            R.layout.postagem_row,
                            HomePage.PostagemViewHolder.class,
                            mDatabase) {

                        @Override
                        protected void populateViewHolder(final HomePage.PostagemViewHolder viewHolder, final Postagem model, int position) {
                            final String post_key = getRef(position).getKey();

                            //Recuperando Nome e email
                            nomeUsuario = (TextView) findViewById(R.id.txtNomeUsuarioHomePage);
                            emailUsuario = (TextView) findViewById(R.id.txtEmailUsuarioHomePage);
                            preferences = new Preferences(HomePage.this);
                            nomeUsuario.setText("Olá, " + preferences.getNome().toString());
                            emailUsuario.setText(Base64Custom.decodificadorBase64(preferences.getIdentificador()).toString());




                            viewHolder.setTitulo(model.getTitulo());
                            viewHolder.setDescricao(model.getDescricao());
                            viewHolder.setimagem(getApplicationContext(), model.getImagem());
                            viewHolder.setNome(model.getNome());
                            viewHolder.setEmail(model.getEmail());



                            final SimpleCallback simple = new SimpleCallback() {
                                @Override
                                public void callback(Object data) {

                                    if(imgUsuPostagem != null){
                                        viewHolder.setImagemUser(getApplicationContext(), imgUsuPostagem);
                                    } else{
                                        viewHolder.setImagemUser2();
                                    }
                                }
                            };




                            databaseImgAtu = FirebaseDatabase.getInstance().getReference();
                            databaseImgAtu = databaseImgAtu.child("FotoPerfil")
                                    .child(Base64Custom.converterBase64(model.getEmail()))
                                    .child("imagem");







                            viewHolder.imgUsu.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if(model.getEmail().toString().equals(Base64Custom.decodificadorBase64(preferences.getIdentificador()))){
                                        startActivity(new Intent(HomePage.this, TelaUsuario.class));
                                    } else {
                                        Intent intent = new Intent(HomePage.this, TelaPerfilAnfitriao.class);
                                        intent.putExtra("emailAnfitriao",model.getEmail().toString()); //data is a string variable holding some value.
                                        intent.putExtra("aplication", getApplicationContext().toString());
                                        intent.putExtra("itemMensagem", "true");
                                        startActivity(intent);
                                    }
                                }
                            });



                            databaseImgAtu.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot2) {
                                    if(dataSnapshot.getValue() != null){
                                        String url = dataSnapshot2.getValue(String.class);
                                        simple.callback(imgUsuPostagem = url);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Problemas ao carregar", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });




                            viewHolder.setmCurtirbtn(post_key);
                            mProgress.dismiss();
                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    //Toast.makeText(HomePage.this, post_key, Toast.LENGTH_LONG).show();

                                }
                            });

                            viewHolder.mCurtirbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mProcessoCurtir = true;

                                    if(mProcessoCurtir){

                                        mDatabaseCurtir.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                if (mProcessoCurtir) {

                                                    if (dataSnapshot.child(post_key).hasChild(firebaseAuth.getCurrentUser().getUid())) {

                                                        mDatabaseCurtir.child(post_key).child(firebaseAuth.getCurrentUser().getUid()).removeValue();

                                                        mProcessoCurtir = false;

                                                    } else {

                                                        mDatabaseCurtir.child(post_key).child(firebaseAuth.getCurrentUser().getUid()).setValue("RandowValue");

                                                        mProcessoCurtir = false;
                                                    }
                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }

                                        });

                                    }

                                }

                            });


                        }
                    };
                    publica_lista.setAdapter(firebaseRecyclerAdapter);

                } else {

                    //Recuperando Nome e email
                    nomeUsuario = (TextView) findViewById(R.id.txtNomeUsuarioHomePage);
                    emailUsuario = (TextView) findViewById(R.id.txtEmailUsuarioHomePage);
                    preferences = new Preferences(HomePage.this);
                    nomeUsuario.setText("Olá, " + preferences.getNome().toString());
                    emailUsuario.setText(Base64Custom.decodificadorBase64(preferences.getIdentificador()).toString());

                    Toast.makeText(getApplicationContext(), "Nenhuma publicação", Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    public static class PostagemViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageButton mCurtirbtn;
        DatabaseReference mDatabaseCurtir;
        FirebaseAuth firebaseAuth;

        public CircleImageView imgUsu;



        public PostagemViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mCurtirbtn = (ImageButton) mView.findViewById(R.id.btnCurtir);

            imgUsu = (CircleImageView) mView.findViewById(R.id.imgUsuarioPostagem);

            mDatabaseCurtir = FirebaseDatabase.getInstance().getReference().child("Curtidas");
            firebaseAuth = FirebaseAuth.getInstance();

            mDatabaseCurtir.keepSynced(true);




        }


        public void setmCurtirbtn(final String post_key){

            mDatabaseCurtir.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child(post_key).hasChild(firebaseAuth.getCurrentUser().getUid())){

                        mCurtirbtn.setImageResource(R.drawable.curtirlaranja);

                    }else{

                        mCurtirbtn.setImageResource(R.drawable.curtir);

                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        public void setTitulo(String titulo) {

            TextView titulo_postagem = (TextView) mView.findViewById(R.id.titulo_postagem);
            titulo_postagem.setText(titulo);
        }

        public void setDescricao(String descricao) {

            TextView descricao_postagem = (TextView) mView.findViewById(R.id.descricao_postagem);
            descricao_postagem.setText(descricao);
        }

        public void setimagem(final Context ctx, final String imagem) {

            final ImageView imagem_postagem = (ImageView) mView.findViewById(R.id.imagem_postagem);
            Picasso.with(ctx).load(imagem).networkPolicy(NetworkPolicy.OFFLINE).into(imagem_postagem, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(imagem).into(imagem_postagem);
                }
            });
        }

        public void setNome(String nome){
            TextView nome_usuario = (TextView) mView.findViewById(R.id.txtNomeUsuarioPostagem);
            nome_usuario.setText(nome);
        }
        public void setEmail(String nome){
            TextView email_usuario = (TextView) mView.findViewById(R.id.txtEmailUsuarioPostagem);
            email_usuario.setText(nome);
        }
        public void setImagemUser(final Context ctx, final String imagem){
            Glide.with(ctx).load(imagem).into(imgUsu);
        }
        public void setImagemUser2(){
            imgUsu.setImageResource(R.drawable.img_usuario);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.adicionar_postagem){

            startActivity(new Intent(HomePage.this, TelaPostagem.class));
        }

        return super.onOptionsItemSelected(item);

    }

    //ALERT DIALOG
    public void eventoSair() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomePage.this);

        //Configurando a AlertDialog
        alertDialog.setTitle("SAIR");
        alertDialog.setMessage(preferences.getNome().toString() + ", deseja realmente sair?");
        alertDialog.setCancelable(false);

        //BOTAO SIM
        alertDialog.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deslogarUsuario();
            }
        });

        //BOTAO NÃO
        alertDialog.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.create();
        alertDialog.show();
    }

}
