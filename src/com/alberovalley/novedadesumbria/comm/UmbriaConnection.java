package com.alberovalley.novedadesumbria.comm;

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

import android.os.AsyncTask;
import android.util.Log;

public class UmbriaConnection extends AsyncTask<LoginData, Void, UmbriaData> {

    private final static String URL_NOVEDADES = "http://www.comunidadumbria.com/usuario/novedades";
    private final static String URL_INICIAL = "http://www.comunidadumbria.com/front";
    private UmbriaConnectionListener listener;

    public UmbriaConnectionListener getListener() {
        return listener;
    }

    public void setListener(UmbriaConnectionListener listener) {
        this.listener = listener;
    }

    @Override
    protected UmbriaData doInBackground(LoginData... params) {
        String html = "";

        LoginData data = params[0];
        UmbriaData umbriadata = new UmbriaData();

        HttpClient httpClient = new DefaultHttpClient();
        StringBuilder builder = new StringBuilder();
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair(LoginData.userNameTAG, data.userName));
        nameValuePairs.add(new BasicNameValuePair(LoginData.passwordTAG, data.password));

        HttpPost request = new HttpPost(URL_NOVEDADES);
        Log.v("novUmbria", "UmbriaConnection.doInBackground llamando a: " + URL_INICIAL);
        if (login(data.userName, data.password)) {
            Log.v("novUmbria", "UmbriaConnection.doInBackground Login hecho: ");
        } else {
            umbriadata.flagError("No se puede conectar a Umbría");
            Log.w("novUmbria", "UmbriaConnection.doInBackground No se puede conectar a Umbría: ");
        }
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
                umbriadata.setPlayerMessages(NovedadesParser.findPlayerMessages(html));
                umbriadata.setStorytellerMessages(NovedadesParser.findStorytellerMessages(html));
                umbriadata.setVipMessages(NovedadesParser.findVIPMessages(html));
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

    @Override
    protected void onPostExecute(UmbriaData result) {
        super.onPostExecute(result);
        listener.connectionReceived(result);
    }

    private boolean login(String user, String pass) {
        boolean ok = false;

        HttpClient httpClient = new DefaultHttpClient();
        StringBuilder builder = new StringBuilder();
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair(LoginData.userNameTAG, user));
        nameValuePairs.add(new BasicNameValuePair(LoginData.passwordTAG, pass));

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
                // Log.v("novUmbria", "UmbriaConnection.login html: " + html);
                if (html.lastIndexOf("'value=\"Entrar\"'") < 0)
                    ok = true;
            }

        } catch (Exception e) {
            Log.e("novUmbria", "UmbriaConnection.login Excepción : " + e.getMessage());
        }
        return ok;
    }
}
