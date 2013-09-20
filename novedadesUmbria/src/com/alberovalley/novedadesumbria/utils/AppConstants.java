package com.alberovalley.novedadesumbria.utils;

public interface AppConstants {

    static final String LOGTAG = "novUmbria";

    static final String URL_NOVEDADES = "http://www.comunidadumbria.com/usuario/novedades";

    static final String DEFAULT_FREQ = "60";

    static final String BUGSENSE_API_KEY = "bb8a8dff";
    // to avoid repetition of the service
    static final int INTERVAL_NEVER = 0;
    
    // After a 100ms delay, vibrate for 200ms then pause for another 100ms and then vibrate for 500ms
    static final int VIBRATION_DELAY_100 = 100;
    static final int VIBRATION_DURATION_200 = 200;
    static final int VIBRATION_DURATION_500 = 500;
}
