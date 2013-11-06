package com.alberovalley.novedadesumbria.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.alberovalley.novedadesumbria.R;
import com.alberovalley.novedadesumbria.comm.data.UmbriaSimpleData;
import com.alberovalley.novedadesumbria.service.NewsCheckingService;
import com.alberovalley.novedadesumbria.utils.AppConstants;
import com.alberovalley.utils.AlberoLog;

public class UmbriaMiniWidgetProvider extends AppWidgetProvider {
    // ////////////////////////////////////////////////////////////
    // attributes
    // ////////////////////////////////////////////////////////////

    private RemoteViews views;

    // ////////////////////////////////////////////////////////////
    // Lifecycle
    // ////////////////////////////////////////////////////////////
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        final int numberOfWidgets = appWidgetIds.length;
        // loop for every App Widget belonging to this provider

        for (int i = 0; i < numberOfWidgets; i++) {

            AlberoLog.v(this, ".onUpdate registra receiver");
            context.getApplicationContext()
                    .registerReceiver(receiver, new IntentFilter(NewsCheckingService.NOTIFICATION));

            AlberoLog.v(this, ".onUpdate widget nº: " + i);
            int appWidgetId = appWidgetIds[i];

            // Create anun Intent to open the browser
            Intent navegaIntent = new Intent(Intent.ACTION_VIEW);
            navegaIntent.setData(Uri.parse(AppConstants.URL_NOVEDADES));
            PendingIntent pendingIntentNavega = PendingIntent.getActivity(context, 0, navegaIntent, 0);

            // obtain the layout for the App Widget and assign an onClickListener to the widget image
            views = new RemoteViews(context.getPackageName(), R.layout.miniwidget);
            views.setOnClickPendingIntent(R.id.mini_image,
                    pendingIntentNavega);

            // tells AppWidgetManager to update current widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    // ////////////////////////////////////////////////////////////
    // Inner classes
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
                UmbriaSimpleData data = bundle.getParcelable(NewsCheckingService.RESULT);

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
                ComponentName thisWidget = new ComponentName(context.getApplicationContext(), UmbriaMiniWidgetProvider.class);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

                if (appWidgetIds == null) {
                    AlberoLog.v(this, ".BroadcastReceiver appWidgetIds null");
                }

                int miniIcon = 0;
                
                boolean storyteller;
            	boolean player;
            	boolean vip;
            	boolean privateMessages;
            	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            	storyteller = sharedPrefs.getBoolean("cb_msg_Narrador", false);
        		vip = sharedPrefs.getBoolean("cb_msg_VIP", false);
        		privateMessages = sharedPrefs.getBoolean("cb_msg_Privado", false);
        		player = sharedPrefs.getBoolean("cb_msg_Jugador", false);
                
                
                if (data.isThereAnythingNew(storyteller, player, vip, privateMessages)) {
				    miniIcon = R.drawable.ic_mini_widget_on;
				    AlberoLog.v(this, ".BroadcastReceiver hay Novedades");
				} else {
				    miniIcon = R.drawable.ic_mini_widget_off;
				    AlberoLog.v(this, ".BroadcastReceiver NO hay Novedades");
				}
                if (appWidgetIds != null && appWidgetIds.length > 0) {
                    for (int widgetId : appWidgetIds) {
                        views = new RemoteViews(context.getPackageName(), R.layout.miniwidget);
                        AlberoLog.v(this, ".BroadcastReceiver actualiza vista remota ");
                        views.setImageViewResource(R.id.mini_image, miniIcon);
                        appWidgetManager.updateAppWidget(widgetId, views);

                    }
                }

            } else {
                // bundle is null
                AlberoLog.v(this, ".BroadcastReceiver problemas recibiendo datos");
            }
        }
    };

}
