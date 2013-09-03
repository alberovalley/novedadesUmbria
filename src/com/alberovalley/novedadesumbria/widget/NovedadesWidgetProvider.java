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
import com.alberovalley.novedadesumbria.comm.UmbriaMensajes;
import com.alberovalley.novedadesumbria.data.UmbriaData;
import com.alberovalley.novedadesumbria.service.NotificadorService;
import com.alberovalley.novedadesumbria.service.task.TaskManager;

public class NovedadesWidgetProvider extends AppWidgetProvider {

    public static String WIDGET_UPDATE = "com.alberovalley.novedadesumbria.widget";
    // private static final String ACTION_CLICK = "ACTION_CLICK";
    RemoteViews views;

    private int SECONDS = 15 * 60;// 15 minutos

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        final int N = appWidgetIds.length;
        // Este bucle se ejecuta para cada App Widget perteneciente a este provider

        for (int i = 0; i < N; i++) {

            Log.v("novUmbria", "NovedadesWidgetProvider.onUpdate registra receiver");
            context.getApplicationContext()
                    .registerReceiver(receiver, new IntentFilter(NotificadorService.NOTIFICATION));

            Log.v("novUmbria", "NovedadesWidgetProvider.onUpdate widget nº: " + i);
            /*
             * // inicio servicio
             * Intent serviceIntent = new Intent(context, NotificadorService.class);
             * // se pueden añadir extras al intent
             * context.startService(serviceIntent);
             * Log.d("novUmbria", "NovedadesWidgetProvider.onUpdate: inicia el SERVICIO ");
             */
            int appWidgetId = appWidgetIds[i];

            // Obtiene el layout para el App Widget y le asigna un on-click listener al botón
            views = new RemoteViews(context.getPackageName(), R.layout.widget1);
            views.setTextViewText(R.id.tvRespuestaUmbria, "Buscando... ");

            // Crea un Intent para lanzar el navegador

            Intent navegaIntent = new Intent(Intent.ACTION_VIEW);
            navegaIntent.setData(Uri.parse(TaskManager.URL_NOVEDADES));
            PendingIntent pendingIntentNavega = PendingIntent.getActivity(context, 0, navegaIntent, 0);
            views.setOnClickPendingIntent(R.id.btNavegaNovedades,
                    pendingIntentNavega);

            // actualizar onDemand (onClick)
            views.setOnClickPendingIntent(R.id.btCompruebaNovedades,
                    updateWidgetIntent(context));

            // Le dice al AppWidgetManager que realice una actualización al widget actual
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private PendingIntent updateWidgetIntent(Context context) {
        Log.v("novUmbria", "NovedadesWidgetProvider.updateWidgetIntent ");

        Intent intent = new Intent(context, NovedadesWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
        ComponentName thisWidget = new ComponentName(context.getApplicationContext(), NovedadesWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        // El Widget Provider está habilitado, iniciamos el temporizador para
        // actualizar el widget cada 15 minutos
        /*
         * AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
         * Calendar calendar = Calendar.getInstance();
         * calendar.setTimeInMillis(System.currentTimeMillis());
         * calendar.add(Calendar.MINUTE, 1);
         * Log.d("novUmbria", "NovedadesWidgetProvider.onEnabled alarmManager hora: " + calendar.getTime().getHours() + ":"
         * + calendar.getTime().getMinutes());
         * alarmManager.setRepeating(
         * AlarmManager.RTC,
         * calendar.getTimeInMillis(),
         * SECONDS * 1000 // tiempo para que se repita
         * , updateWidgetIntent(context));
         */

    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        // El Widget Provider está deshabilitado, apagamos el temporizador
        /*
         * AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
         * alarmManager.cancel(updateWidgetIntent(context));
         */
    }

    /*
     * private void showSettings(Context context) {
     * // getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
     * Intent intent = new Intent(context, SettingsActivity.class);
     * context.startActivity(intent);
     * }
     */
    /*
     * public void connectToUmbria(String user, String pass) {
     * // textview1.setText("Conectando...");
     * LoginData ld = new LoginData(user, pass);
     * 
     * UmbriaConnection uc = new UmbriaConnection();
     * // uc.setListener(this);
     * uc.execute(ld);
     * }
     */

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("novUmbria", "NovedadesWidgetProvider.BroadcastReceiver ");
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Log.v("novUmbria", "NovedadesWidgetProvider.BroadcastReceiver recibidos datos");
                UmbriaData data = bundle.getParcelable(NotificadorService.RESULT);

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
                ComponentName thisWidget = new ComponentName(context.getApplicationContext(), NovedadesWidgetProvider.class);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

                if (appWidgetIds == null) {
                    Log.e("novUmbria", "NovedadesWidgetProvider.BroadcastReceiver appWidgetIds null");
                }

                if (appWidgetIds != null && appWidgetIds.length > 0) {
                    for (int widgetId : appWidgetIds) {
                        views = new RemoteViews(context.getPackageName(), R.layout.widget1);
                        Log.v("novUmbria", "NovedadesWidgetProvider.BroadcastReceiver actualiza vista remota ");
                        views.setTextViewText(R.id.tvRespuestaUmbria, UmbriaMensajes.makeNotificationText(data));
                        appWidgetManager.updateAppWidget(widgetId, views);

                    }
                }

            } else {
                // algo ha fallado
                Log.v("novUmbria", "NovedadesWidgetProvider.BroadcastReceiver problemas recibiendo datos");
                views.setTextViewText(R.id.tvRespuestaUmbria, "Algo falló");
            }
        }
    };

}
