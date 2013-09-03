package com.alberovalley.novedadesumbria.service.task;

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

import android.util.Log;

import com.alberovalley.novedadesumbria.comm.LoginData;
import com.alberovalley.novedadesumbria.comm.NovedadesParser;
import com.alberovalley.novedadesumbria.data.UmbriaData;

public class TaskManager {
    public final static String URL_NOVEDADES = "http://www.comunidadumbria.com/usuario/novedades";
    private final static String URL_INICIAL = "http://www.comunidadumbria.com/front";
    public static final String RESULT = "result";
    public static final String UPDATE = "update";

    public static UmbriaData getNovedades(LoginData ld) {
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
            Log.v("novUmbria", "TaskManager.getNovedades código respuesta: " + statusCode);
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
                Log.d("novUmbria", "TaskManager.getNovedades respuesta recibida: " + html);
                
                // TODO parseo --> 1 step
                umbriadata = NovedadesParser.ParseData(html);
                //umbriadata.setPlayerMessages(NovedadesParser.findPlayerMessages(html));
                //umbriadata.setStorytellerMessages(NovedadesParser.findStorytellerMessages(html));
                //umbriadata.setVipMessages(NovedadesParser.findVIPMessages(html));
                //umbriadata.setPrivateMessages(NovedadesParser.findPrivateMessages(html));
            } else {
                umbriadata.flagError("Respuesta incorrecta ");
                Log.i("novUmbria", "TaskManager.getNovedades Respuesta incorrecta: " + statusCode);
            }
        } catch (UnsupportedEncodingException e) {
            umbriadata.flagError("Problema de codificación");
            Log.e("novUmbria", "TaskManager.getNovedades Problema de codificación " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalStateException e) {
            umbriadata.flagError("Problema de Estado Ilegal. Contacte con el desarrollador");
            Log.e("novUmbria", "TaskManager.getNovedades IllegalStateException Problema de Estado Ilegal " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            umbriadata.flagError("Problema de comunicación ¿Tienes conexión?");
            Log.e("novUmbria", "TaskManager.getNovedades IOException " + e.getMessage());
            e.printStackTrace();
        }
        return umbriadata;

    }

    public static boolean login(LoginData ld) {
        boolean ok = false;

        HttpClient httpClient = new DefaultHttpClient();
        StringBuilder builder = new StringBuilder();
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair(LoginData.userNameTAG, ld.getUserName()));
        nameValuePairs.add(new BasicNameValuePair(LoginData.passwordTAG, ld.getPassword()));

        HttpPost request = new HttpPost(URL_NOVEDADES);
        Log.v("novUmbria", "TaskManager.login llamando a: " + URL_INICIAL);
        try {
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            // request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(request);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            Log.v("novUmbria", "TaskManager.login código respuesta: " + statusCode);
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
                Log.v("novUmbria", "TaskManager.login html: " + html);
                if (html.lastIndexOf("Has perdido tu clave") < 0) {
                    Log.v("novUmbria", "TaskManager.login OK : ");
                    ok = true;
                } else {
                    Log.w("novUmbria", "TaskManager.login Falló el login : ");
                }
            }

        } catch (Exception e) {
            Log.e("novUmbria", "TaskManager.login Excepción : " + e.getMessage());
        }
        return ok;
    }

}
