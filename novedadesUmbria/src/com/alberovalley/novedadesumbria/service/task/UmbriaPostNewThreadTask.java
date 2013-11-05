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

import com.alberovalley.novedadesumbria.comm.UmbriaLoginData;
import com.alberovalley.novedadesumbria.comm.data.UmbriaThreadData;
import com.alberovalley.novedadesumbria.utils.AppConstants;
import com.alberovalley.utils.AlberoLog;
import com.bugsense.trace.BugSenseHandler;

public class UmbriaPostNewThreadTask implements UmbriaTask {
	
	public boolean postNewThread(UmbriaLoginData ld, UmbriaThreadData utd) {
		boolean result = false;
		
		String html = "";
        HttpClient httpClient = new DefaultHttpClient();
        StringBuilder builder = new StringBuilder();
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
               
        nameValuePairs.add(new BasicNameValuePair(UmbriaThreadData.TITLE_TAG, utd.getTitle()));
        nameValuePairs.add(new BasicNameValuePair(UmbriaThreadData.TEXT_TAG, utd.getText()));

        HttpPost request = new HttpPost(AppConstants.URL_ABSENCE_THREADS);
		
        try {
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(request);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            AlberoLog.v("TaskManager.getNovedades código respuesta: " + statusCode);
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
                AlberoLog.d("TaskManager.getNovedades respuesta recibida: " + html);
                result = true;
            } else {
                result = false;
                AlberoLog.i("TaskManager.getNovedades Respuesta incorrecta: " + statusCode);
            }
        } catch (UnsupportedEncodingException e) {
        	result = false;
            AlberoLog.e("TaskManager.getNovedades Problema de codificación " + e.getMessage());

            BugSenseHandler.sendExceptionMessage("log", "UnsupportedEncodingException " + e.getMessage(), e);
        } catch (IllegalStateException e) {
        	result = false;
            AlberoLog.e("TaskManager.getNovedades IllegalStateException Problema de Estado Ilegal " + e.getMessage());

            BugSenseHandler.sendExceptionMessage("log", "IllegalStateException " + e.getMessage(), e);
        } catch (IOException e) {
        	result = false;
            AlberoLog.e("TaskManager.getNovedades IOException " + e.getMessage());

        }
		return result;
	}

}
