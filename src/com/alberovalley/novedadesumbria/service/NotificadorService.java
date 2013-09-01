package com.alberovalley.novedadesumbria.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.alberovalley.novedadesumbria.SettingsActivity;
import com.alberovalley.novedadesumbria.comm.LoginData;
import com.alberovalley.novedadesumbria.comm.UmbriaData;
import com.alberovalley.novedadesumbria.service.task.TaskManager;

public class NotificadorService extends IntentService {

    public static final String RESULT = "result";
    public static final String UPDATE = "update";
    public static final String NOTIFICATION = "com.alberovalley.novedadesumbria.service";

    private static String user;
    private static String pass;
    private static boolean director;
    private static boolean jugador;
    private static boolean vip;
    private static boolean privados;

    public NotificadorService() {
        super("NotificadorService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();
        Log.v("novUmbria", "NotificadorService constructor: ¿context? " + String.valueOf(context == null));
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        user = sharedPrefs.getString("edittext_usuario", "");
        pass = sharedPrefs.getString("edittext_password", "");
        jugador = sharedPrefs.getBoolean("cb_msg_Jugador", false);
        director = sharedPrefs.getBoolean("cb_msg_Narrador", false);
        vip = sharedPrefs.getBoolean("cb_msg_VIP", false);
        privados = sharedPrefs.getBoolean("cb_msg_Privado", false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // se pueden extraer extras del intent

        if (user.equalsIgnoreCase("") || pass.equalsIgnoreCase("")) {
            // Si faltan datos de login, mostrar la configuración
            showSettings(getApplicationContext());
        } else {
            connectToUmbria(user, pass);
        }
    }

    private void showSettings(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        // flag necesario cuando se abre una activity desde algo que NO sea una activity
        // por ejemplo, este service
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void connectToUmbria(String user, String pass) {

        LoginData ld = new LoginData(user, pass);
        UmbriaData umbriadata = new UmbriaData();
        Log.v("novUmbria", "UmbriaConnection.doInBackground llamando a: TaskManager.login");
        if (!TaskManager.login(ld)) {
            // falla el login
            umbriadata.flagError("No se puede conectar a Umbría");
            Log.w("novUmbria", "UmbriaConnection.doInBackground Login falla: ");
        } else {
            Log.v("novUmbria", "UmbriaConnection.doInBackground Login hecho: ");
            umbriadata = TaskManager.getNovedades(ld);
        }
        umbriadata.setNotifyPlayerMessages(jugador);
        umbriadata.setNotifyPrivateMessages(privados);
        umbriadata.setNotifyStorytellerMessages(director);
        umbriadata.setNotifyVipMessages(vip);
        publishResults(umbriadata);
    }

    private void publishResults(UmbriaData umbriadata) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, umbriadata);
        sendBroadcast(intent);

    }

}
