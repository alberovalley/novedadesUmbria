package com.alberovalley.novedadesumbria.service.scheduler;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alberovalley.novedadesumbria.service.NotificadorService;

public class Scheduler {

    public static boolean cancelScheduledService(Context ctx, Intent serviceIntent) {
        boolean success = true;
        Log.v("novUmbria", "Scheduler.cancelScheduledService");
        try {
            AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
            am.cancel(
                    PendingIntent.getBroadcast(
                            ctx, 0,
                            new Intent(ctx, NotificadorService.class),
                            // si ya existe, no genera un 2º
                            PendingIntent.FLAG_CANCEL_CURRENT
                            )

                    );
            Log.v("novUmbria", "Scheduler.cancelScheduledService Servicio cancelado");
        } catch (Exception e) {
            Log.e("novUmbria", "Scheduler.cancelScheduledService Excepción: " + e.getMessage());
            success = false;
        }
        return success;
    }

    public static boolean scheduleService(Context ctx, Intent serviceIntent, long interval) {
        boolean success = true;
        Log.v("novUmbria", "Scheduler.scheduleService Servicio ");
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
            // timeformat.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));

            AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
            am.setRepeating(
                    // am.setInexactRepeating(
                    // AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    AlarmManager.RTC_WAKEUP,
                    cal.getTimeInMillis(),
                    interval,
                    PendingIntent.getService(
                            ctx, 0,
                            new Intent(ctx, NotificadorService.class),
                            // si ya existe, no genera un 2º
                            PendingIntent.FLAG_CANCEL_CURRENT
                            )

                    );
            Log.v("novUmbria", "Scheduler.scheduleService Servicio programado a las "
                    + timeformat.format(cal.getTime())
                    + " cada " + (interval / 60000) + " minutos"
                    );

            startService(ctx, serviceIntent);
            Log.v("novUmbria", "Scheduler.scheduleService Servicio iniciado"
                    );

        } catch (Exception e) {
            Log.e("novUmbria", "Scheduler.scheduleService Excepción: " + e.getMessage());
            success = false;
        }
        return success;
    }

    public static boolean startService(Context ctx, Intent serviceIntent) {
        boolean success = true;
        Log.v("novUmbria", "Scheduler.startService");
        try {
            ctx.startService(serviceIntent);
            Log.v("novUmbria", "Scheduler.startService Servicio iniciado");
        } catch (Exception e) {
            Log.e("novUmbria", "Scheduler.startService Excepción: " + e.getMessage());
            success = false;
        }
        return success;
    }

}
