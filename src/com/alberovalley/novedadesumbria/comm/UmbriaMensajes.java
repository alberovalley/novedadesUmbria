package com.alberovalley.novedadesumbria.comm;

import android.util.Log;

public class UmbriaMensajes {

    public static String connectionReceived(UmbriaData data) {
        String message = "Conexión Recibida";
        Log.d("novUmbria", "NovedadesWidgetProvider.connectionReceived: ");
        if (!data.isThereError()) {
            message = "";
            if (data.getPlayerMessages() > 0)
                message += "Hay mensajes en Jugador.\n";
            if (data.getStorytellerMessages() > 0)
                message += "Hay mensajes en Narrador.\n";
            if (data.getVipMessages() > 0)
                message += "Hay mensajes en VIP.\n";
            if (data.getPrivateMessages() > 0)
                message += "Tienes mensajes privados.\n";

            if (message.equalsIgnoreCase(""))
                message = "No hay mensajes nuevos en ninguna de las partidas en las que estás registrado";
            // textview1.setText(text);
        } else {
            message = "Se produjo algún error durante la comunicación con Comunidad Umbría\n Por favor, revise su conexión.";
            Log.e("novUmbria", "Error en comunicación: " + data.getErrorMessage());
        }

        return message;
    }
}
