package com.alberovalley.novedadesumbria;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alberovalley.novedadesumbria.comm.LoginData;
import com.alberovalley.novedadesumbria.comm.UmbriaConnection;
import com.alberovalley.novedadesumbria.comm.UmbriaConnectionListener;
import com.alberovalley.novedadesumbria.comm.UmbriaData;

public class MainActivity extends Activity implements UmbriaConnectionListener {

    String user;
    String pass;

    TextView textview1;
    Button button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textview1 = (TextView) findViewById(R.id.textview1);
        button1 = (Button) findViewById(R.id.button1);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        user = sharedPrefs.getString("edittext_usuario", "");
        pass = sharedPrefs.getString("edittext_password", "");
        if (user.equalsIgnoreCase("") || pass.equalsIgnoreCase("")) {
            showSettings();
        } else {

            textview1.setText("usuario: " + user + " | pass: " + pass);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.action_settings:
            showSettings();
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void showSettings() {
        // getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void connectToUmbria(View v) {
        LoginData ld = new LoginData();
        ld.userName = user;
        ld.password = pass;

        UmbriaConnection uc = new UmbriaConnection();
        uc.setListener(this);
        uc.execute(ld);
    }

    @Override
    public void connectionReceived(UmbriaData data) {
        textview1.setText("connectionReceived");
        if (!data.isThereError()) {
            String text = "";
            if (data.getPlayerMessages() > 0)
                text += "Hay mensajes en partidas dónde eres Jugador.\n";
            if (data.getStorytellerMessages() > 0)
                text += "Hay mensajes en partidas dónde eres Narrador.\n";
            if (data.getVipMessages() > 0)
                text += "Hay mensajes en partidas dónde eres VIP.\n";

            if (text.equalsIgnoreCase(""))
                text = "No hay mensajes nuevos en ninguna de las partidas en las que estás registrado";
            textview1.setText(text);
        }
    }
}
