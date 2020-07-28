package net.ddns.vidapet.vidapet;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class TelaAjuda extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;

    private ImageView botaoFaq;
    private ImageView botaoEmail;
    private ImageView botaoInfo;
    private LinearLayout llFaq, llPolitica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_ajuda);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Ajuda");
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        botaoFaq = (ImageView) findViewById(R.id.imgFaq);
        botaoEmail = (ImageView) findViewById(R.id.imgEmail);
        botaoInfo = (ImageView) findViewById(R.id.imgInfo);

        llFaq = (LinearLayout) findViewById(R.id.ll_FAQ);
        llPolitica = (LinearLayout) findViewById(R.id.ll_Politica);

        llFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TelaAjuda.this, TelaFAQ.class));
            }
        });

        botaoFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TelaAjuda.this, TelaFAQ.class));
            }
        });


        llPolitica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TelaAjuda.this, TelaPolitica.class));
            }
        });

        botaoInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TelaAjuda.this, TelaPolitica.class));
            }
        });




    }
}
