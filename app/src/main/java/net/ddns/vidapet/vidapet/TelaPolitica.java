package net.ddns.vidapet.vidapet;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class TelaPolitica extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;

    private TextView texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_politica);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tb_politica);
        toolbar.setTitle("Políticas de privacidade");
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        texto = (TextView) findViewById(R.id.txtPolitica);

        texto.setText(Html.fromHtml(getString(R.string.textoPoliticaPrivacidade)));


    }
}
