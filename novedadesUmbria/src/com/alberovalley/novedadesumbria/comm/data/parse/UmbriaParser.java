package com.alberovalley.novedadesumbria.comm.data.parse;

import android.util.Log;

import com.alberovalley.utils.AlberoLog;
import com.bugsense.trace.BugSenseHandler;

public final class UmbriaParser {
	
    // ////////////////////////////////////////////////////////////
    // Constants
    // ////////////////////////////////////////////////////////////
    private static final String STORYTELLER_TAG = "<h3>Nuevos mensajes en partidas como director</h3>";
    private static final String PLAYER_TAG = "<h3>Nuevos mensajes en las partidas como jugador</h3>";
    private static final String VIP_TAG = "<h3>Nuevos mensajes en partidas VIP</h3>";
    private static final String PRIVATE_MESSAGES_TAG = "title=\"Mensajes privados\"><span>";

    private static final String NO_MSG_TAG = "<p>No hay novedades</p>";

    // ============================================================================================
    // HIDE CONSTRUCTOR
    // ============================================================================================    
    private UmbriaParser() {	}
    // ////////////////////////////////////////////////////////////
    // Methods
    // ////////////////////////////////////////////////////////////
    public static int findStorytellerMessages(String html) throws UmbriaParserException {
        AlberoLog.d("NovedadesParser.findStorytellerMessages ");
        return findMessageByTag(html, STORYTELLER_TAG);

    }

    public static int findPlayerMessages(String html) throws UmbriaParserException {
        AlberoLog.d("NovedadesParser.findPlayerMessages");
        return findMessageByTag(html, PLAYER_TAG);

    }

    public static int findVIPMessages(String html) throws UmbriaParserException {
        AlberoLog.d("NovedadesParser.findVIPMessages");
        return findMessageByTag(html, VIP_TAG);

    }

    public static int findPrivateMessages(String html) throws UmbriaParserException {
        AlberoLog.d("NovedadesParser.findPrivateMessages ");
        int msg = 0;
        Log.w("novUmbria", "findPrivateMessages html" + html);
        final String tag = PRIVATE_MESSAGES_TAG;
        int indexOfTag = html.lastIndexOf(tag) + tag.length();
        if (indexOfTag > 0) {
            String substring = html.substring(
                    indexOfTag, indexOfTag + 1
                    );
            AlberoLog.v("findPrivateMessages substring  " + substring);
            try {
                msg = Integer.parseInt(substring);
            } catch (NumberFormatException e) {
                AlberoLog.e("findPrivateMessages NumberFormatException  " + e.getMessage());
                BugSenseHandler.addCrashExtraData("log", "NumberFormatException " + e.getMessage() + " parsing: " + substring);
                throw new UmbriaParserException("findPrivateMessages error (not a number) parsing: " + substring);
            }
        } else {
            AlberoLog.v("findPrivateMessages indexOfTag <=0 ");
            throw new UmbriaParserException("No encontrado el tag: [" + PRIVATE_MESSAGES_TAG + "]");
        }
        AlberoLog.v("findPrivateMessages returns: " + msg);
        return msg;
    }

    public static int findMessageByTag(String html, String tag) throws UmbriaParserException {
        AlberoLog.d("findMessageByTag tag: " + tag);
        int msg = 0;
        int indexOfTag = html.lastIndexOf(tag);
        if (indexOfTag < 0) {
            // tag wasn't found, html code must have changed
            throw new UmbriaParserException("No encontrado el tag: [" + tag + "]");
        }
        String substring = html.substring(indexOfTag + tag.length(), indexOfTag + (3 * tag.length()));
        AlberoLog.d("findMessageByTag substring: " + substring);
        indexOfTag = substring.lastIndexOf(NO_MSG_TAG);
        AlberoLog.d("findMessageByTag indexOfTag: " + indexOfTag);
        switch (indexOfTag) {
        case -1:
            // sí hay mensaje
            AlberoLog.d("findMessageByTag hay mensaje: ");
            msg = 1;
            break;
        default:
            // no hay mensaje
            msg = 0;
            AlberoLog.d("findMessageByTag no hay mensaje: ");

        }
        AlberoLog.v("findMessageByTag " + tag + "  returns: " + msg);
        return msg;

    }

}
