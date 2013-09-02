package com.alberovalley.novedadesumbria;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.alberovalley.novedadesumbria.service.NotificadorService;
import com.alberovalley.novedadesumbria.service.scheduler.Scheduler;

public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        Log.v("novUmbria", "SettingsActivity.onCreate currentVersion = " + currentVersion);
        if (currentVersion < android.os.Build.VERSION_CODES.HONEYCOMB) {
            addPreferencesFromResource(R.xml.preferences);
        }
    }

    @Override
    protected void onStop() {
        Log.v("novUmbria", "SettingsActivity.onStop");
        Intent serviceIntent = new Intent(getApplicationContext(), NotificadorService.class);
        Scheduler.cancelScheduledService(getApplicationContext(), serviceIntent);

        Context context = getApplicationContext();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String frecuencia = sharedPrefs.getString("lpFrecuencia", "60");
        Log.v("novUmbria", "SettingsActivity.onStop sharedPrefs: frecuencia >> " + frecuencia);
        long frequency = 1000 * 60 * Long.valueOf(frecuencia);
        Scheduler.scheduleService(getApplicationContext(),
                serviceIntent,
                frequency
                );
        Log.v("novUmbria", "SettingsActivity.onStop scheduleService");
        super.onStop();
    }
}
