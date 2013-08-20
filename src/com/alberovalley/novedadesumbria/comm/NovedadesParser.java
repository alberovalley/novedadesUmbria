package com.alberovalley.novedadesumbria.comm;

import android.util.Log;

public class NovedadesParser {

    public static final String STORYTELLER_TAG = "<h3>Nuevos mensajes en partidas como director</h3>";
    public static final String PLAYER_TAG = "<h3>Nuevos mensajes en las partidas como jugador</h3>";
    public static final String VIP_TAG = "<h3>Nuevos mensajes en partidas VIP</h3>";

    public static final String NO_MSG_TAG = "<p>No hay novedades</p>";

    public static int findStorytellerMessages(String html) {
        Log.d("novUmbria", "findStorytellerMessages ");
        return findMessageByTag(html, STORYTELLER_TAG);

    }

    public static int findPlayerMessages(String html) {
        Log.d("novUmbria", "findPlayerMessages");
        return findMessageByTag(html, PLAYER_TAG);

    }

    public static int findVIPMessages(String html) {
        Log.d("novUmbria", "findVIPMessages");
        return findMessageByTag(html, VIP_TAG);

    }

    public static String findNumericalId(String html, String username) {
        String numericalId = "";
        String substring = html.substring(html.lastIndexOf(username));
        return numericalId;
    }

    public static int findMessageByTag(String html, String tag) {
        Log.d("novUmbria", "findMessageByTag tag: " + tag);
        int msg = 0;
        int indexOfTag = html.lastIndexOf(tag);
        if (indexOfTag > 0) {
            String substring = html.substring(indexOfTag);
            Log.d("novUmbria", "findMessageByTag substring: " + substring);
            if (substring.lastIndexOf(NO_MSG_TAG) == -1) {
                // no aparece <p>No hay novedades</p>, señal de que sí hay mensajes
                msg = 1;
            }
            Log.d("novUmbria", "findMessageByTag resultado: " + msg);
        } else {

        }
        return msg;

    }

}
