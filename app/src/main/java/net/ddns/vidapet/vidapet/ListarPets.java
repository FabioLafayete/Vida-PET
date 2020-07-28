package net.ddns.vidapet.vidapet;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.ddns.vidapet.adapter.PetsAdapterLista;
import net.ddns.vidapet.helper.Preferences;
import net.ddns.vidapet.helper.SlidingTabLayout;

public class ListarPets extends AppCompatActivity {

    private String[] permissoesNecessarias = new String[]{
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private SlidingTabLayout slidingTabLayout;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_pets);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        Preferences preferences = new Preferences(ListarPets.this);

        toolbar = (Toolbar) findViewById(R.id.tb_pets);
        toolbar.setTitle("PET");
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);


        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_listaPets);
        viewPager = (ViewPager) findViewById(R.id.vp_listaPets);


        //CONFIGURANDO TABS
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        //CONFIGURAR ADAPTER
        PetsAdapterLista petsAdapterLista = new PetsAdapterLista(getSupportFragmentManager());
        viewPager.setAdapter(petsAdapterLista);
        slidingTabLayout.setViewPager(viewPager);


}




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_listar_pet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.item_add_pet: abrirAddPet();
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    private void abrirAddPet(){
        Intent intent = new Intent(ListarPets.this, AddPet.class);
        startActivity(intent);
    }



}
