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
import android.widget.RemoteViews;

import com.alberovalley.novedadesumbria.R;
import com.alberovalley.novedadesumbria.comm.UmbriaConnectionException;
import com.alberovalley.novedadesumbria.comm.UmbriaMessenger;
import com.alberovalley.novedadesumbria.comm.data.UmbriaData;
import com.alberovalley.novedadesumbria.service.NewsCheckingService;
import com.alberovalley.novedadesumbria.utils.AppConstants;
import com.alberovalley.utils.AlberoLog;

public class UmbriaWidgetProvider extends AppWidgetProvider {
    // ////////////////////////////////////////////////////////////
    // Attributes
    // ////////////////////////////////////////////////////////////
    RemoteViews views;

    // ////////////////////////////////////////////////////////////
    // Lifecycle
    // ////////////////////////////////////////////////////////////

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        final int N = appWidgetIds.length;
        // loop for every App Widget belonging to this provide

        for (int i = 0; i < N; i++) {

            AlberoLog.v(this, ".onUpdate registra receiver");
            context.getApplicationContext()
                    .registerReceiver(receiver, new IntentFilter(NewsCheckingService.NOTIFICATION));

            AlberoLog.v(this, ".onUpdate widget nº: " + i);

            int appWidgetId = appWidgetIds[i];
            // check once on demand
            Intent serviceIntent = new Intent(context, NewsCheckingService.class);
            context.startService(serviceIntent);
            AlberoLog.d(this, ".onUpdate: inicia el SERVICIO ");
            // Create an Intent to open the browser
            Intent navegaIntent = new Intent(Intent.ACTION_VIEW);
            navegaIntent.setData(Uri.parse(AppConstants.URL_NOVEDADES));
            PendingIntent pendingIntentNavega = PendingIntent.getActivity(context, 0, navegaIntent, 0);

            // obtain the layout for the App Widget and assign an onClickListener to the button inside
            views = new RemoteViews(context.getPackageName(), R.layout.widget1);
            // views.setTextViewText(R.id.tvRespuestaUmbria, context.getResources().getString(R.string.widget_text_searching));
            views.setOnClickPendingIntent(R.id.btNavegaNovedades,
                    pendingIntentNavega);

            // update onDemand (onClick)
            views.setOnClickPendingIntent(R.id.btCompruebaNovedades,
                    updateWidgetIntent(context));

            // tells AppWidgetManager to update the current widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    /**
     * Create a PendingIntent to force an update of the widget
     * 
     * @param context
     * @return PendingIntent
     */
    private PendingIntent updateWidgetIntent(Context context) {
        AlberoLog.v(this, ".updateWidgetIntent ");

        Intent intent = new Intent(context, UmbriaWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
        ComponentName thisWidget = new ComponentName(context.getApplicationContext(), UmbriaWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    // ////////////////////////////////////////////////////////////
    // Inner Classes
    // ////////////////////////////////////////////////////////////
    /**
     * this receiver listens to the NewsCheckingService and updates the widget
     * accordingly
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            AlberoLog.v(this, ".BroadcastReceiver ");
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                AlberoLog.v(this, ".BroadcastReceiver recibidos datos");
                UmbriaData data = bundle.getParcelable(NewsCheckingService.RESULT);

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
                ComponentName thisWidget = new ComponentName(context.getApplicationContext(), UmbriaWidgetProvider.class);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

                if (appWidgetIds == null) {
                    AlberoLog.w(this, ".BroadcastReceiver appWidgetIds null");
                }

                String notificationText = "";
                try {
                    if (UmbriaMessenger.isThereAnythingNew(data)) {
                        notificationText = UmbriaMessenger.makeNotificationText(data, context.getApplicationContext());
                        AlberoLog.v(this, ".BroadcastReceiver hay Novedades");
                    } else {
                        notificationText = context.getResources().getString(R.string.widget_text_empty);
                        AlberoLog.v(this, ".BroadcastReceiver NO hay Novedades");
                    }
                } catch (UmbriaConnectionException e) {
                    notificationText = context.getResources().getString(R.string.widget_text_error);
                    AlberoLog.e(this, ".BroadcastReceiver error ");
                    // BugSenseHandler.sendExceptionMessage("log", "UmbriaConnectionException " + e.getMessage(), e);
                }

                if (appWidgetIds != null && appWidgetIds.length > 0) {
                    for (int widgetId : appWidgetIds) {
                        views = new RemoteViews(context.getPackageName(), R.layout.widget1);
                        // notificationText = UmbriaMessenger.makeNotificationText(data, context.getApplicationContext());
                        AlberoLog.v(this, ".BroadcastReceiver actualiza vista remota: " + notificationText);

                        views.setTextViewText(
                                R.id.tvRespuestaUmbria,
                                notificationText
                                );
                        appWidgetManager.updateAppWidget(widgetId, views);

                    }
                }

            } else {
                // algo ha fallado
                AlberoLog.v(this, ".BroadcastReceiver problemas recibiendo datos");
                views.setTextViewText(R.id.tvRespuestaUmbria, "Algo falló");
            }
        }
    };

}
