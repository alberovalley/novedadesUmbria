package com.alberovalley.novedadesumbria.service.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.alberovalley.novedadesumbria.service.NotificadorService;
import com.alberovalley.novedadesumbria.service.scheduler.Scheduler;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("novUmbria", "BootReceiver.onReceive");
        Context ctx = context.getApplicationContext();
        Intent serviceIntent = new Intent(ctx, NotificadorService.class);
        // interval en milisegundos

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String frecuencia = sharedPrefs.getString("lpFrecuencia", "60");
        Log.v("novUmbria", "BootReceiver.onReceive sharedPrefs: frecuencia >> " + frecuencia);
        long interval = 1000 * 60 * Long.valueOf(frecuencia);

        if (Scheduler.cancelScheduledService(ctx, serviceIntent)) {
            Log.v("novUmbria", "BootReceiver.onReceive cancelado posible servicio anterior");
            if (Scheduler.scheduleService(ctx, serviceIntent, interval)) {
                Log.v("novUmbria", "BootReceiver.onReceive arrancado el servicio");
            } else {
                Log.wtf("novUmbria", "BootReceiver.onReceive ¿por qué falla el iniciar servicio? ");
            }
        } else {
            Log.wtf("novUmbria", "BootReceiver.onReceive ¿por qué falla el cancelar servicio? ");
        }

    }
}
