package com.alberovalley.novedadesumbria.service.scheduler;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.alberovalley.novedadesumbria.service.NewsCheckingService;
import com.alberovalley.utils.AlberoLog;

/**
 * Wrapper for cancelling and starting alarms methods
 * 
 * @author frank
 * 
 */
public class Scheduler {
    // ////////////////////////////////////////////////////////////
    // Methods
    // ////////////////////////////////////////////////////////////
    public static boolean cancelScheduledService(Context ctx, Intent serviceIntent) {
        boolean success = true;
        AlberoLog.v("Scheduler.cancelScheduledService");
        try {
            AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
            am.cancel(
                    PendingIntent.getBroadcast(
                            ctx, 0,
                            new Intent(ctx, NewsCheckingService.class),
                            // avoid creating a second service if there's already one running
                            PendingIntent.FLAG_CANCEL_CURRENT
                            )

                    );
            AlberoLog.v("Scheduler.cancelScheduledService Servicio cancelado");
        } catch (Exception e) {
            AlberoLog.e("Scheduler.cancelScheduledService Excepción: " + e.getMessage());
            success = false;
        }
        return success;
    }

    public static boolean scheduleService(Context ctx, Intent serviceIntent, long interval) {
        boolean success = true;
        AlberoLog.v("Scheduler.scheduleService Servicio ");
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");

            AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
            am.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    cal.getTimeInMillis(),
                    interval,
                    PendingIntent.getService(
                            ctx, 0,
                            new Intent(ctx, NewsCheckingService.class),
                            // avoid creating a second service if there's already one running
                            PendingIntent.FLAG_CANCEL_CURRENT
                            )

                    );
            AlberoLog.v("Scheduler.scheduleService Servicio programado a las "
                    + timeformat.format(cal.getTime())
                    + " cada " + (interval / 60000) + " minutos"
                    );

            AlberoLog.v("Scheduler.scheduleService Servicio iniciado"
                    );

        } catch (Exception e) {
            AlberoLog.e("Scheduler.scheduleService Excepción: " + e.getMessage());
            success = false;
        }
        return success;
    }

    /*
     * public static boolean startService(Context ctx, Intent serviceIntent) {
     * boolean success = true;
     * AlberoLog.v("Scheduler.startService");
     * try {
     * ctx.startService(serviceIntent);
     * AlberoLog.v("Scheduler.startService Servicio iniciado");
     * } catch (Exception e) {
     * AlberoLog.e("Scheduler.startService Excepción: " + e.getMessage());
     * success = false;
     * }
     * return success;
     * }
     */
}
