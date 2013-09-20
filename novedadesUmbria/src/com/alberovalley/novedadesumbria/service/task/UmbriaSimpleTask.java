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

import android.content.Context;

import com.alberovalley.novedadesumbria.R;
import com.alberovalley.novedadesumbria.comm.UmbriaLoginData;
import com.alberovalley.novedadesumbria.comm.data.UmbriaSimpleData;
import com.alberovalley.novedadesumbria.comm.data.parse.UmbriaParser;
import com.alberovalley.novedadesumbria.comm.data.parse.UmbriaParserException;
import com.alberovalley.novedadesumbria.utils.AppConstants;
import com.alberovalley.utils.AlberoLog;
import com.bugsense.trace.BugSenseHandler;

public class UmbriaSimpleTask extends UmbriaTask{

    // ============================================================================================
    // MANDATORY METHODS
    // ============================================================================================
	
	
	@Override
	public UmbriaSimpleData getNovedades(UmbriaLoginData ld, Context ctx) {
		UmbriaSimpleData umbriadata = new UmbriaSimpleData();

        String html = "";
        HttpClient httpClient = new DefaultHttpClient();
        StringBuilder builder = new StringBuilder();
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair(UmbriaLoginData.USER_NAME_TAG, ld.getUserName()));
        nameValuePairs.add(new BasicNameValuePair(UmbriaLoginData.PASSWORD_TAG, ld.getPassword()));

        HttpPost request = new HttpPost(AppConstants.URL_NOVEDADES);

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
                // parseo
                try {
                    umbriadata.setPlayerMessageCount(UmbriaParser.findPlayerMessages(html));
                    umbriadata.setStorytellerMessageCount(UmbriaParser.findStorytellerMessages(html));
                    umbriadata.setVIPMessageCount(UmbriaParser.findVIPMessages(html));
                    umbriadata.setPrivateMessageCount(UmbriaParser.findPrivateMessages(html));
                } catch (UmbriaParserException e) {

                    AlberoLog.e("UmbriaData.parseHtml UmbriaParserException " + e.getMessage());
                    umbriadata.flagError(
                            ctx.getApplicationContext().
                                    getResources().getString(R.string.error_parse_title),
                            ctx.getApplicationContext().
                                    getResources().getString(R.string.error_parse_body));
                    BugSenseHandler.sendExceptionMessage("log", "UmbriaParserException " + e.getMessage(), e);
                }
            } else {
                umbriadata.flagError(
                        ctx.getResources().getString(R.string.error_wrong_response_title),
                        ctx.getResources().getString(R.string.error_wrong_response_body)
                        );
                AlberoLog.i("TaskManager.getNovedades Respuesta incorrecta: " + statusCode);
            }
        } catch (UnsupportedEncodingException e) {
            umbriadata.flagError(
                    ctx.getResources().getString(R.string.error_encoding_title),
                    ctx.getResources().getString(R.string.error_encoding_body)
                    );
            AlberoLog.e("TaskManager.getNovedades Problema de codificación " + e.getMessage());

            BugSenseHandler.sendExceptionMessage("log", "UnsupportedEncodingException " + e.getMessage(), e);
        } catch (IllegalStateException e) {
            umbriadata.flagError(
                    ctx.getResources().getString(R.string.error_ilegal_state_title),
                    ctx.getResources().getString(R.string.error_ilegal_state_body)
                    );
            AlberoLog.e("TaskManager.getNovedades IllegalStateException Problema de Estado Ilegal " + e.getMessage());

            BugSenseHandler.sendExceptionMessage("log", "IllegalStateException " + e.getMessage(), e);
        } catch (IOException e) {
            umbriadata.flagError(
                    ctx.getResources().getString(R.string.error_ioexception_title),
                    ctx.getResources().getString(R.string.error_ioexception_body)
                    );
            AlberoLog.e("TaskManager.getNovedades IOException " + e.getMessage());

        }
        return umbriadata;
	}

}
