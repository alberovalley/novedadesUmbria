package com.alberovalley.novedadesumbria.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.alberovalley.novedadesumbria.SettingsActivity;
import com.alberovalley.novedadesumbria.comm.LoginData;
import com.alberovalley.novedadesumbria.comm.NovedadesParser;
import com.alberovalley.novedadesumbria.comm.UmbriaData;

public class NotificadorService extends IntentService {
    public final static String URL_NOVEDADES = "http://www.comunidadumbria.com/usuario/novedades";
    private final static String URL_INICIAL = "http://www.comunidadumbria.com/front";
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
        Log.v("novUmbria", "UmbriaConnection.doInBackground llamando a: " + URL_INICIAL);
        if (!login(ld)) {
            // falla el login
            umbriadata.flagError("No se puede conectar a Umbría");
            Log.w("novUmbria", "UmbriaConnection.doInBackground Login falla: ");
        } else {
            Log.v("novUmbria", "UmbriaConnection.doInBackground Login hecho: ");
            umbriadata = getNovedades(ld);
        }

        publishResults(umbriadata);
    }

    private void publishResults(UmbriaData umbriadata) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, umbriadata);
        sendBroadcast(intent);

    }

    private UmbriaData getNovedades(LoginData ld) {
        UmbriaData umbriadata = new UmbriaData();

        String html = "";
        HttpClient httpClient = new DefaultHttpClient();
        StringBuilder builder = new StringBuilder();
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair(LoginData.userNameTAG, ld.getUserName()));
        nameValuePairs.add(new BasicNameValuePair(LoginData.passwordTAG, ld.getPassword()));

        HttpPost request = new HttpPost(URL_NOVEDADES);

        try {
            // request.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(request);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            Log.v("novUmbria", "UmbriaConnection.doInBackground código respuesta: " + statusCode);
            if (statusCode == 200) {
                /*
                 * Si todo fue ok, montamos la String con los datos en formato JSON
                 */
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                html = builder.toString();
                Log.d("novUmbria", "UmbriaConnection.doInBackground respuesta recibida: " + html);
                // TODO parseo
                if (jugador)
                    umbriadata.setPlayerMessages(NovedadesParser.findPlayerMessages(html));
                if (director)
                    umbriadata.setStorytellerMessages(NovedadesParser.findStorytellerMessages(html));
                if (vip)
                    umbriadata.setVipMessages(NovedadesParser.findVIPMessages(html));
                if (privados)
                    umbriadata.setPrivateMessages(NovedadesParser.findPrivateMessages(html));
            } else {
                umbriadata.flagError("Respuesta incorrecta ");
                Log.i("novUmbria", "Respuesta incorrecta: " + statusCode);
            }
        } catch (UnsupportedEncodingException e) {
            umbriadata.flagError("Problema de codificación");
            Log.e("novUmbria", "Problema de codificación " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalStateException e) {
            umbriadata.flagError("Problema de Estado Ilegal");
            Log.e("novUmbria", "Problema de Estado Ilegal " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            umbriadata.flagError("Problema de comunicación ¿Tienes conexión?");
            Log.e("novUmbria", "" + e.getMessage());
            e.printStackTrace();
        }
        return umbriadata;

    }

    private boolean login(LoginData ld) {
        boolean ok = false;

        HttpClient httpClient = new DefaultHttpClient();
        StringBuilder builder = new StringBuilder();
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair(LoginData.userNameTAG, ld.getUserName()));
        nameValuePairs.add(new BasicNameValuePair(LoginData.passwordTAG, ld.getPassword()));

        HttpPost request = new HttpPost(URL_NOVEDADES);
        Log.v("novUmbria", "UmbriaConnection.doInBackground llamando a: " + URL_INICIAL);
        try {
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            // request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(request);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            Log.v("novUmbria", "UmbriaConnection.login código respuesta: " + statusCode);
            if (statusCode == 200) {
                /*
                 * Si todo fue ok, montamos la String con los datos en formato JSON
                 */
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                String html = builder.toString();
                Log.v("novUmbria", "UmbriaConnection.login html: " + html);
                if (html.lastIndexOf("Has perdido tu clave") < 0) {
                    Log.v("novUmbria", "UmbriaConnection.login OK : ");
                    ok = true;
                } else {
                    Log.w("novUmbria", "UmbriaConnection.login Falló el login : ");
                }
            }

        } catch (Exception e) {
            Log.e("novUmbria", "UmbriaConnection.login Excepción : " + e.getMessage());
        }
        return ok;
    }
}
