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
import com.alberovalley.novedadesumbria.comm.data.UmbriaConfig;
import com.alberovalley.novedadesumbria.comm.data.UmbriaSimpleData;
import com.alberovalley.novedadesumbria.service.NewsCheckingService;
import com.alberovalley.novedadesumbria.utils.AppConstants;
import com.alberovalley.utils.AlberoLog;

public class UmbriaWidgetProvider extends AppWidgetProvider {
    // ////////////////////////////////////////////////////////////
    // Constants
    // ////////////////////////////////////////////////////////////
	public static int ON_DEMAND = 1;
	public static int AUTO = 0;
	// ////////////////////////////////////////////////////////////
    // Attributes
    // ////////////////////////////////////////////////////////////
    private RemoteViews views;

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

        final int numberOfWidgets = appWidgetIds.length;
        // loop for every App Widget belonging to this provide

        for (int i = 0; i < numberOfWidgets; i++) {

            AlberoLog.v(this, ".onUpdate registra receiver");
            context.getApplicationContext()
                    .registerReceiver(receiver, new IntentFilter(NewsCheckingService.NOTIFICATION));

            AlberoLog.v(this, ".onUpdate widget nº: " + i);

            int appWidgetId = appWidgetIds[i];
            
            // Create an Intent to open the browser
            Intent navegaIntent = new Intent(Intent.ACTION_VIEW);
            navegaIntent.setData(Uri.parse(AppConstants.URL_NOVEDADES));
            PendingIntent pendingIntentNavega = PendingIntent.getActivity(context, 0, navegaIntent, 0);

            // obtain the layout for the App Widget and assign an onClickListener to the button inside
            views = new RemoteViews(context.getPackageName(), R.layout.widget1);

            views.setOnClickPendingIntent(R.id.btNavegaNovedades,
                    pendingIntentNavega);

            // update onDemand (onClick)
            views.setOnClickPendingIntent(R.id.btCompruebaNovedades,
                    updateWidgetIntent(context, ON_DEMAND));

            // tells AppWidgetManager to update the current widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private PendingIntent updateWidgetIntent(Context context, int mode) {
    	//String strFreq = "";
    	AlberoLog.d(this, ".updateWidgetIntent " + mode + " call");
    	if (mode == ON_DEMAND){
    		// check once on demand
            Intent serviceIntent = new Intent(context, NewsCheckingService.class);
            context.startService(serviceIntent);
            AlberoLog.d(this, ".updateWidgetIntent: inicia el SERVICIO ");
            //AlberoLog.d(this, ".onUpdate: inicia el SERVICIO ");
    	}
    	/*
    	if (mode != ON_DEMAND){
    		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    		strFreq = sharedPrefs.getString("lpFrecuencia", "60");
    		long frequency = 1000 * 60 * Long.valueOf(strFreq);
    		if (frequency == 0){
    			// if the widget updates automatically
    			// and frequency is set to NEVER
    			// there's no request to be sent
    			AlberoLog.d(this, ".updateWidgetIntent automatic call with frequency set to 'NEVER'");
    			return null;
    		}
    	}
    	// if the widget updates either 
        //  onDemand (via button) 
    	//  or automatically AND frequency is NOT set to NEVER
    	//  it returns the pending intent to send the request
    	AlberoLog.d(this, ".updateWidgetIntent " + mode + " call, frequency set to [" + strFreq + "]");*/
    	return updateWidgetIntent(context);
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
            boolean storyteller;
        	boolean player;
        	boolean vip;
        	boolean privateMessages;
        	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        	storyteller = sharedPrefs.getBoolean("cb_msg_Narrador", false);
    		vip = sharedPrefs.getBoolean("cb_msg_VIP", false);
    		privateMessages = sharedPrefs.getBoolean("cb_msg_Privado", false);
    		player = sharedPrefs.getBoolean("cb_msg_Jugador", false);
    		// instantiate an UmbriaConfig object to pass to UmbriaSimpleData
    		UmbriaConfig uc = new UmbriaConfig(privateMessages, storyteller, player, vip);
            
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                AlberoLog.v(this, ".BroadcastReceiver recibidos datos");
                UmbriaSimpleData data = bundle.getParcelable(NewsCheckingService.RESULT);

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
                ComponentName thisWidget = new ComponentName(context.getApplicationContext(), UmbriaWidgetProvider.class);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

                if (appWidgetIds == null) {
                    AlberoLog.w(this, ".BroadcastReceiver appWidgetIds null");
                }

                String notificationText = "";
                if (data.isThereAnythingNew()) {
				    notificationText = data.getLongNoticeText(uc, context.getApplicationContext());
				    AlberoLog.v(this, ".BroadcastReceiver hay Novedades");
				} else {
				    notificationText = context.getResources().getString(R.string.widget_text_empty);
				    AlberoLog.v(this, ".BroadcastReceiver NO hay Novedades");
				}

                if (appWidgetIds != null && appWidgetIds.length > 0) {
                    for (int widgetId : appWidgetIds) {
                        views = new RemoteViews(context.getPackageName(), R.layout.widget1);
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
