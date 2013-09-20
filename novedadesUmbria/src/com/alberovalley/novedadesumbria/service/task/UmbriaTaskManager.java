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

import android.content.Context;

import com.alberovalley.novedadesumbria.R;
import com.alberovalley.novedadesumbria.comm.UmbriaLoginData;
import com.alberovalley.novedadesumbria.comm.data.UmbriaNaeinData;
import com.alberovalley.novedadesumbria.comm.data.UmbriaSimpleData;
import com.alberovalley.novedadesumbria.comm.data.parse.UmbriaParser;
import com.alberovalley.novedadesumbria.comm.data.parse.UmbriaParserException;
import com.alberovalley.novedadesumbria.utils.AppConstants;
import com.alberovalley.utils.AlberoLog;
import com.bugsense.trace.BugSenseHandler;

/**
 * Wrapper for different tasks (like log-in to the website and check for news)
 * to be carried out by Services
 * 
 * @author frank
 * 
 */
public class UmbriaTaskManager {
    // ////////////////////////////////////////////////////////////
    // Constants
    // ////////////////////////////////////////////////////////////
    protected static final String URL_INICIAL = "http://www.comunidadumbria.com/front";
    protected static final String LOGIN_CHECKER_USUARIO = "<strong>Usuario:</strong>";
    protected static final String LOGIN_CHECKER_CLAVE = "Has perdido tu clave";
    
    private UmbriaTask task;
    
    // ============================================================================================
    // CONSTRUCTOR
    // ============================================================================================
    public UmbriaTaskManager(){
    	
    	
    }

    // ////////////////////////////////////////////////////////////
    // Methods
    // ////////////////////////////////////////////////////////////
    public UmbriaSimpleData getUmbriaSimpleData(UmbriaLoginData ld, Context ctx) {
    	this.task = new UmbriaSimpleTask();
        return (UmbriaSimpleData) task.getNovedades(ld, ctx);

    }
    
    public UmbriaNaeinData getUmbriaNaeinData(UmbriaLoginData ld, Context ctx) {
    	this.task = new UmbriaNaeinTask();
        return (UmbriaNaeinData) task.getNovedades(ld, ctx);

    }

    public static boolean login(UmbriaLoginData ld) throws IOException {
        boolean ok = false;

        HttpClient httpClient = new DefaultHttpClient();
        StringBuilder builder = new StringBuilder();
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair(UmbriaLoginData.USER_NAME_TAG, ld.getUserName()));
        nameValuePairs.add(new BasicNameValuePair(UmbriaLoginData.PASSWORD_TAG, ld.getPassword()));

        HttpPost request = new HttpPost(AppConstants.URL_NOVEDADES);
        AlberoLog.v("TaskManager.login llamando a: " + URL_INICIAL);

        request.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

        HttpResponse response = httpClient.execute(request);
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        AlberoLog.v("TaskManager.login código respuesta: " + statusCode);
        if (statusCode == 200) {

            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            String html = builder.toString();
            AlberoLog.v("TaskManager.login html: " + html);
            if (html.lastIndexOf(LOGIN_CHECKER_CLAVE) < 0 && html.lastIndexOf(LOGIN_CHECKER_USUARIO) > 0) {
                AlberoLog.v("TaskManager.login OK : ");
                ok = true;
            } else {
                AlberoLog.w("TaskManager.login Falló el login : ");
            }
        }

        return ok;
    }

}
