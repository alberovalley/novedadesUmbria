package com.alberovalley.novedadesumbria.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.alberovalley.novedadesumbria.R;
import com.alberovalley.novedadesumbria.SettingsActivity;
import com.alberovalley.novedadesumbria.comm.LoginData;
import com.alberovalley.novedadesumbria.comm.UmbriaConnectionException;
import com.alberovalley.novedadesumbria.comm.UmbriaData;
import com.alberovalley.novedadesumbria.comm.UmbriaMensajes;
import com.alberovalley.novedadesumbria.service.task.TaskManager;

public class NotificadorService extends IntentService {

    public static final String RESULT = "result";
    public static final String UPDATE = "update";
    public static final String NOTIFICATION = "com.alberovalley.novedadesumbria.service";

    private static String user;
    private static String pass;
    private static boolean director;
    private static boolean jugador;
    private static boolean vip;
    private static boolean privados;

    private static boolean notificacion;

    public NotificadorService() {
        super("NotificadorService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();
        Log.v("novUmbria", "NotificadorService constructor: ¿context? " + String.valueOf(context == null));
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        user = sharedPrefs.getString("edittext_usuario", "");
        Log.v("novUmbria", "NotificadorService sharedPrefs: user >> " + user);
        pass = sharedPrefs.getString("edittext_password", "");
        Log.v("novUmbria", "NotificadorService sharedPrefs: pass >> " + pass);
        jugador = sharedPrefs.getBoolean("cb_msg_Jugador", false);
        director = sharedPrefs.getBoolean("cb_msg_Narrador", false);
        vip = sharedPrefs.getBoolean("cb_msg_VIP", false);
        privados = sharedPrefs.getBoolean("cb_msg_Privado", false);
        notificacion = sharedPrefs.getBoolean("cb_notificacion", false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // se pueden extraer extras del intent
        Log.v("novUmbria", "NotificadorService.onHandleIntent");
        if (user.equalsIgnoreCase("") || pass.equalsIgnoreCase("")) {
            // Si faltan datos de login, mostrar la configuración
            Log.v("novUmbria", "NotificadorService.onHandleIntent faltan datos config, llamando a Settings");
            showSettings(getApplicationContext());

        } else {
            Log.v("novUmbria", "NotificadorService.onHandleIntent todo correcto, llamando a CU");
            connectToUmbria(user, pass);
        }
    }

    private void showSettings(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        // flag necesario cuando se abre una activity desde algo que NO sea una activity
        // por ejemplo, este service
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void connectToUmbria(String user, String pass) {

        LoginData ld = new LoginData(user, pass);
        UmbriaData umbriadata = new UmbriaData();
        Log.v("novUmbria", "NotificadorService.connectToUmbria llamando a: TaskManager.login");
        if (!TaskManager.login(ld)) {
            // falla el login
            umbriadata.flagError("No se puede conectar a Umbría");
            Log.w("novUmbria", "NotificadorService.connectToUmbria Login falla: ");
        } else {
            Log.v("novUmbria", "NotificadorService.connectToUmbria Login hecho: ");
            umbriadata = TaskManager.getNovedades(ld);
        }
        umbriadata.setNotifyPlayerMessages(jugador);
        umbriadata.setNotifyPrivateMessages(privados);
        umbriadata.setNotifyStorytellerMessages(director);
        umbriadata.setNotifyVipMessages(vip);
        publishResults(umbriadata);
    }

    @SuppressWarnings("deprecation")
    private void publishResults(UmbriaData umbriadata) {

        if (notificacion) {
            int currentVersion = android.os.Build.VERSION.SDK_INT;
            Log.v("novUmbria", "NotificadorService.publishResults notificacion: ");
            Notification noti = null;
            try {
                if (UmbriaMensajes.isThereAnythingNew(umbriadata)) {// comprobamos para evitar notificaciones tontas
                    Log.v("novUmbria", "NotificadorService.publishResults hay algo que notificar: ");
                    // Prepara intent a lanzar cuando pique en la notificación
                    // irá a "novedades" de Comunidad Umbría

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(TaskManager.URL_NOVEDADES));
                    PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

                    Log.v("novUmbria", "NotificadorService.publishResults currentVersion = " + currentVersion);
                    // construir la notificación

                    String texto = UmbriaMensajes.makeNotificationText(umbriadata);

                    // if (currentVersion < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    if (currentVersion < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        Log.v("novUmbria", "NotificadorService.publishResults creamos notificación \"antigua\"");
                        noti = new Notification(
                                R.drawable.ic_mini_widget_on,
                                "Novedades en Comunidad Umbría", System.currentTimeMillis());
                        // Vibrate if vibrate is enabled
                        noti.defaults |= Notification.DEFAULT_VIBRATE;
                        // noti.setLatestEventInfo(this, "contentTitle", "contentText",pIntent);
                        noti.setLatestEventInfo(this, "Novedades en Comunidad Umbría", "Ir a Novedades", pIntent);

                        // Hide the notification after its selected
                        noti.flags |= Notification.FLAG_AUTO_CANCEL;
                    } else if (currentVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        Log.v("novUmbria", "NotificadorService.publishResults creamos notificación \"moderna\"");
                        noti = new Notification.Builder(this)
                                .setContentTitle("Novedades en Comunidad Umbría")
                                // .setContentText("Subject")
                                .setSmallIcon(R.drawable.ic_mini_widget_on)
                                .setContentIntent(pIntent)
                                .addAction(R.drawable.ic_stat_notif_icon, "Ir a Novedades", pIntent)
                                .setStyle(new Notification.BigTextStyle().bigText(texto))
                                .build();
                        noti.vibrate = new long[] { 100, 200, 100, 500 };
                    }

                    // Hide the notification after its selected
                    noti.flags |= Notification.FLAG_AUTO_CANCEL;
                }

            } catch (UmbriaConnectionException e) {
                // posiblemente: poner notificación roja
                if (currentVersion < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    Log.v("novUmbria", "NotificadorService.publishResults UmbriaConnectionException creamos notificación \"antigua\"");
                    noti = new Notification(
                            R.drawable.ic_mini_widget_error,
                            "No pude conectar con Umbría. Por favor, revisa tus datos de acceso y tu conexión.",
                            System.currentTimeMillis());
                    // Hide the notification after its selected
                    noti.setLatestEventInfo(this, "No pude conectar con Umbría.", "Por favor, revisa tus datos de acceso y tu conexión", null);
                    noti.flags |= Notification.FLAG_AUTO_CANCEL;
                    // Vibrate if vibrate is enabled
                    noti.defaults |= Notification.DEFAULT_VIBRATE;

                } else {
                    Log.v("novUmbria", "NotificadorService.publishResults UmbriaConnectionException creamos notificación \"moderna\"");
                    noti = new Notification.Builder(this)
                            .setContentTitle("Novedades en Comunidad Umbría")
                            // .setContentText("Subject")
                            .setSmallIcon(R.drawable.ic_mini_widget_error)
                            // .setContentIntent(pIntent)
                            // .addAction(R.drawable.ic_mini_widget_off, "Ir a Novedades", pIntent)
                            .setStyle(
                                    new Notification.BigTextStyle()
                                            .bigText("No pude conectar con Umbría. Por favor, revisa tus datos de acceso y tu conexión."))
                            .build();
                    Log.e("novUmbria", "NotificadorService.publishResults UmbriaConnectionException = " + e.getMessage());
                    // After a 100ms delay, vibrate for 200ms then pause for another
                    // 100ms and then vibrate for 500ms
                    noti.vibrate = new long[] { 100, 200, 100, 500 };
                    // Hide the notification after its selected
                    noti.flags |= Notification.FLAG_AUTO_CANCEL;
                }
            } finally {

                if (noti != null) {
                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    Log.v("novUmbria", "NotificadorService.publishResults lanzar notificación ");
                    notificationManager.notify(0, noti);
                } else {
                    Log.wtf("novUmbria", "NotificadorService.publishResults ¿por qué llega aquí un noti null");
                }
            }

            Intent intent = new Intent(NOTIFICATION);
            intent.putExtra(RESULT, umbriadata);
            sendBroadcast(intent);

        }
    }

}
