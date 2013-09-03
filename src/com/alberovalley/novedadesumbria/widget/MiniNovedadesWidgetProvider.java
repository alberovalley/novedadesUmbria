package com.alberovalley.novedadesumbria.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.alberovalley.novedadesumbria.R;
import com.alberovalley.novedadesumbria.comm.LoginData;
import com.alberovalley.novedadesumbria.comm.UmbriaConnection;
import com.alberovalley.novedadesumbria.comm.UmbriaConnectionException;
import com.alberovalley.novedadesumbria.comm.UmbriaMensajes;
import com.alberovalley.novedadesumbria.data.UmbriaData;
import com.alberovalley.novedadesumbria.service.NotificadorService;
import com.alberovalley.novedadesumbria.service.task.TaskManager;

public class MiniNovedadesWidgetProvider extends AppWidgetProvider {

    public static String WIDGET_UPDATE = "com.alberovalley.novedadesumbria.widget";
    RemoteViews views;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        final int N = appWidgetIds.length;
        // Este bucle se ejecuta para cada App Widget perteneciente a este provider

        for (int i = 0; i < N; i++) {

            Log.v("novUmbria", "MiniNovedadesWidgetProvider.onUpdate registra receiver");
            context.getApplicationContext()
                    .registerReceiver(receiver, new IntentFilter(NotificadorService.NOTIFICATION));

            Log.v("novUmbria", "MiniNovedadesWidgetProvider.onUpdate widget nº: " + i);
            // inicio servicio
            Intent serviceIntent = new Intent(context, NotificadorService.class);
            // se pueden añadir extras al intent
            int appWidgetId = appWidgetIds[i];

            // Obtiene el layout para el App Widget y le asigna un on-click listener al botón
            views = new RemoteViews(context.getPackageName(), R.layout.miniwidget);

            // Crea un Intent para lanzar el navegador

            Intent navegaIntent = new Intent(Intent.ACTION_VIEW);
            navegaIntent.setData(Uri.parse(TaskManager.URL_NOVEDADES));

            PendingIntent pendingIntentNavega = PendingIntent.getActivity(context, 0, navegaIntent, 0);
            views.setOnClickPendingIntent(R.id.mini_image,
                    pendingIntentNavega);

            // Le dice al AppWidgetManager que realice una actualización al widget actual
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    /*
     * private PendingIntent updateWidgetIntent(Context context) {
     * Log.v("novUmbria", "MiniNovedadesWidgetProvider.updateWidgetIntent ");
     * 
     * Intent intent = new Intent(context, MiniNovedadesWidgetProvider.class);
     * intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
     * 
     * AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
     * ComponentName thisWidget = new ComponentName(context.getApplicationContext(), MiniNovedadesWidgetProvider.class);
     * int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
     * 
     * intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
     * 
     * PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
     * 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
     * return pendingIntent;
     * }
     */

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    public void connectToUmbria(String user, String pass) {

        LoginData ld = new LoginData(user, pass);

        UmbriaConnection uc = new UmbriaConnection();

        uc.execute(ld);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("novUmbria", "MiniNovedadesWidgetProvider.BroadcastReceiver ");
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Log.v("novUmbria", "MiniNovedadesWidgetProvider.BroadcastReceiver recibidos datos");
                UmbriaData data = bundle.getParcelable(NotificadorService.RESULT);

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
                ComponentName thisWidget = new ComponentName(context.getApplicationContext(), MiniNovedadesWidgetProvider.class);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

                if (appWidgetIds == null) {
                    Log.e("novUmbria", "MiniNovedadesWidgetProvider.BroadcastReceiver appWidgetIds null");
                }

                int miniIcon = 0;
                try {
                    if (UmbriaMensajes.isThereAnythingNew(data)) {
                        miniIcon = R.drawable.ic_mini_widget_on;
                        Log.v("novUmbria", "MiniNovedadesWidgetProvider.BroadcastReceiver hay Novedades");
                    } else {
                        miniIcon = R.drawable.ic_mini_widget_off;
                        Log.v("novUmbria", "MiniNovedadesWidgetProvider.BroadcastReceiver NO hay Novedades");
                    }
                } catch (UmbriaConnectionException e) {
                    miniIcon = R.drawable.ic_mini_widget_error;
                }
                if (appWidgetIds != null && appWidgetIds.length > 0) {
                    for (int widgetId : appWidgetIds) {
                        views = new RemoteViews(context.getPackageName(), R.layout.miniwidget);
                        Log.v("novUmbria", "MiniNovedadesWidgetProvider.BroadcastReceiver actualiza vista remota ");
                        views.setImageViewResource(R.id.mini_image, miniIcon);
                        appWidgetManager.updateAppWidget(widgetId, views);

                    }
                }

            } else {
                // algo ha fallado
                Log.v("novUmbria", "MiniNovedadesWidgetProvider.BroadcastReceiver problemas recibiendo datos");
                views.setTextViewText(R.id.tvRespuestaUmbria, "Algo falló");
            }
        }
    };

}
