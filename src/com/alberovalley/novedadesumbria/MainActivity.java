package com.alberovalley.novedadesumbria;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alberovalley.novedadesumbria.comm.UmbriaConnectionListener;
import com.alberovalley.novedadesumbria.comm.UmbriaData;
import com.alberovalley.novedadesumbria.service.NotificadorService;

public class MainActivity extends Activity implements UmbriaConnectionListener {

    String user;
    String pass;

    TextView textview1;
    Button button1;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                UmbriaData data = bundle.getParcelable(NotificadorService.RESULT);
                connectionReceived(data);
            } else {
                // algo ha fallado
                textview1.setText("Algo falló");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textview1 = (TextView) findViewById(R.id.textview1);
        button1 = (Button) findViewById(R.id.button1);
        /*
         * 
         * SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
         * user = sharedPrefs.getString("edittext_usuario", "");
         * pass = sharedPrefs.getString("edittext_password", "");
         * if (user.equalsIgnoreCase("") || pass.equalsIgnoreCase("")) {
         * showSettings();
         * } else {
         * 
         * textview1.setText("usuario: " + user + " | pass: " + pass);
         * }
         */

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

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(NotificadorService.NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public void connectToUmbria(View v) {
        Log.d("novUmbria", "connectToUmbria: ");

        Intent intent = new Intent(this, NotificadorService.class);
        // se pueden añadir extras al intent
        startService(intent);
        Log.d("novUmbria", "connectToUmbria: inicia el SERVICIO ");
        textview1.setText("Estableciendo conexión...");
    }

    @Override
    public String connectionReceived(UmbriaData data) {
        textview1.setText("connectionReceived");
        String message = "Conexión Recibida";
        if (!data.isThereError()) {
            message = "";
            if (data.getPlayerMessages() > 0)
                message += "Hay mensajes en partidas dónde eres Jugador.\n";
            if (data.getStorytellerMessages() > 0)
                message += "Hay mensajes en partidas dónde eres Narrador.\n";
            if (data.getVipMessages() > 0)
                message += "Hay mensajes en partidas dónde eres VIP.\n";
            if (data.getPrivateMessages() > 0)
                message += "Tienes mensajes privados pendientes de leer.\n";

            if (message.equalsIgnoreCase(""))
                message = "No hay mensajes nuevos en ninguna de las partidas en las que estás registrado";
            // textview1.setText(text);
        } else {
            message = "Se produjo algún error durante la comunicación con Comunidad Umbría\n Por favor, revise su conexión.";
            Log.e("novUmbria", "Error en comunicación: " + data.getErrorMessage());
        }
        textview1.setText(message);
        return message;
    }

}
