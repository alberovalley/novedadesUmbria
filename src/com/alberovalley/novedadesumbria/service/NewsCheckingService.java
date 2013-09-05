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

import com.alberovalley.novedadesumbria.R;
import com.alberovalley.novedadesumbria.SettingsActivity;
import com.alberovalley.novedadesumbria.comm.UmbriaConnectionException;
import com.alberovalley.novedadesumbria.comm.UmbriaData;
import com.alberovalley.novedadesumbria.comm.UmbriaLoginData;
import com.alberovalley.novedadesumbria.comm.UmbriaMessenger;
import com.alberovalley.novedadesumbria.service.task.TaskManager;
import com.alberovalley.novedadesumbria.utils.AppConstants;
import com.alberovalley.utils.AlberoLog;

public class NewsCheckingService extends IntentService {

    /**
     * This service deals with the http communication with the website in order to check for news
     */

    // ////////////////////////////////////////////////////////////
    // CONSTANTS
    // ////////////////////////////////////////////////////////////
    public static final String RESULT = "result";
    public static final String UPDATE = "update";
    public static final String NOTIFICATION = "com.alberovalley.novedadesumbria.service";

    // ////////////////////////////////////////////////////////////
    // attributes
    // ////////////////////////////////////////////////////////////

    private static String user;
    private static String pass;
    private static boolean storyteller;
    private static boolean player;
    private static boolean vip;
    private static boolean privateMessages;

    private static boolean notificacion;

    // ////////////////////////////////////////////////////////////
    // Lifecycle
    // ////////////////////////////////////////////////////////////

    public NewsCheckingService() {
        super("NewsCheckingService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        user = sharedPrefs.getString("edittext_usuario", "");
        AlberoLog.v(this, " sharedPrefs: user >> " + user);
        pass = sharedPrefs.getString("edittext_password", "");
        AlberoLog.v(this, " sharedPrefs: pass >> " + pass);
        player = sharedPrefs.getBoolean("cb_msg_Jugador", false);
        storyteller = sharedPrefs.getBoolean("cb_msg_Narrador", false);
        vip = sharedPrefs.getBoolean("cb_msg_VIP", false);
        privateMessages = sharedPrefs.getBoolean("cb_msg_Privado", false);
        notificacion = sharedPrefs.getBoolean("cb_notificacion", false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // se pueden extraer extras del intent
        AlberoLog.v(this, ".onHandleIntent");
        if (user.equalsIgnoreCase("") || pass.equalsIgnoreCase("")) {
            // Si faltan datos de login, mostrar la configuración
            AlberoLog.v(getClass().getSimpleName() + ".onHandleIntent faltan datos config, llamando a Settings");
            showSettings(getApplicationContext());

        } else {
            AlberoLog.v(this, ".onHandleIntent todo correcto, llamando a CU");
            connectToUmbria(user, pass);
        }
    }

    private void showSettings(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        // flag required to open an Activity from a
        // NON activity context, like this Service
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void connectToUmbria(String user, String pass) {

        UmbriaLoginData ld = new UmbriaLoginData(user, pass);
        UmbriaData umbriadata = new UmbriaData();
        AlberoLog.v(this, ".connectToUmbria llamando a: TaskManager.login");
        if (!TaskManager.login(ld)) {
            // login fails
            umbriadata.flagError(getResources().getString(R.string.error_cannot_connect));
        } else {
            AlberoLog.v(this, ".connectToUmbria Login hecho: ");
            umbriadata = TaskManager.getNovedades(ld, getApplicationContext());
        }
        umbriadata.setNotifyPlayerMessages(player);
        umbriadata.setNotifyPrivateMessages(privateMessages);
        umbriadata.setNotifyStorytellerMessages(storyteller);
        umbriadata.setNotifyVipMessages(vip);
        publishResults(umbriadata);
    }

    @SuppressWarnings("deprecation")
    private void publishResults(UmbriaData umbriadata) {
        AlberoLog.v(this, ".publishResults publicando ");
        if (notificacion) {

            AlberoLog.v(this, ".publishResults notificacion: ");
            Notification noti = null;
            try {
                if (UmbriaMessenger.isThereAnythingNew(umbriadata)) {
                    // avoid unnecessary notificaations when there's no news
                    AlberoLog.v(this, ".publishResults hay algo que notificar: ");
                    noti = createNotificationForNews(umbriadata);
                    // Hide the notification after its selected
                    noti.flags |= Notification.FLAG_AUTO_CANCEL;
                }

            } catch (UmbriaConnectionException e) {
                // there was some problem trying to connect to the website, notify on it
                noti = createNotificationForError(umbriadata);
                AlberoLog.v(this, ".publishResults UmbriaConnectionException = " + e.getMessage());
            } finally {

                if (noti != null) {
                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    AlberoLog.v(this, ".publishResults lanzar notificación ");
                    notificationManager.notify(0, noti);
                } else {
                    AlberoLog.v(this, ".publishResults ¿por qué llega aquí un noti null");
                }
            }

        }
        AlberoLog.v(this, ".publishResults publicando ");
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, umbriadata);
        sendBroadcast(intent);
    }

    // ////////////////////////////////////////////////////////////
    // Methods
    // ////////////////////////////////////////////////////////////

    @SuppressWarnings("deprecation")
    private Notification createNotificationForError(UmbriaData umbriadata) {
        Notification notification;
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        if (currentVersion < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            AlberoLog.v(this, ".createNotificationForError UmbriaConnectionException creamos notificación \"antigua\"");
            notification = new Notification(
                    R.drawable.ic_mini_widget_error,
                    getResources().getString(R.string.notification_error_title_short),
                    System.currentTimeMillis());
            // Hide the notification after its selected
            notification.setLatestEventInfo(this,
                    getResources().getString(R.string.notification_error_title_short),
                    getResources().getString(R.string.notification_error_message_short),
                    PendingIntent.getActivity(this, 0,
                            new Intent(this, SettingsActivity.class), 0)
                    );
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            // TODO check if user wants the notification to vibrate
            notification.defaults |= Notification.DEFAULT_VIBRATE;

        } else {
            AlberoLog.v(this, ".publishResults UmbriaConnectionException creamos notificación \"moderna\"");
            notification = new Notification.Builder(this)
                    .setContentTitle(getResources().getString(R.string.notification_error_title_short))
                    .setSmallIcon(R.drawable.ic_mini_widget_error)
                    // no intent needed
                    .setStyle(
                            new Notification.BigTextStyle()
                                    .bigText(getResources().getString(R.string.notification_error_message_long)))
                    .build();

            // After a 100ms delay, vibrate for 200ms then pause for another 100ms and then vibrate for 500ms
            // TODO check if user wants the notification to vibrate
            notification.vibrate = new long[] { 100, 200, 100, 500 };
            // Hide the notification after its selected
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
        }
        return notification;
    }

    @SuppressWarnings("deprecation")
    private Notification createNotificationForNews(UmbriaData umbriadata) {
        Notification notification;
        int currentVersion = android.os.Build.VERSION.SDK_INT;

        // action will open default browser on Constants.URL_NOVEDADES
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(AppConstants.URL_NOVEDADES));
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        String text = UmbriaMessenger.makeNotificationText(umbriadata, getApplicationContext());
        AlberoLog.v(this, ".publishResults texto a notificar: " + text);
        if (currentVersion < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            AlberoLog.v(this, ".publishResults creamos notificación \"antigua\"");
            notification = new Notification(
                    R.drawable.ic_mini_widget_on,
                    getResources().getString(R.string.notification_news_title),
                    System.currentTimeMillis());

            notification.setLatestEventInfo(this,
                    getResources().getString(R.string.notification_news_title),
                    getResources().getString(R.string.notification_news_action),
                    pIntent);

            // TODO check if user wants the notification to vibrate
            notification.defaults |= Notification.DEFAULT_VIBRATE;

            // Hide the notification after its selected
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
        } else {
            // currentVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN

            AlberoLog.v(this, ".publishResults creamos notificación \"moderna\"");
            notification = new Notification.Builder(this)
                    .setContentTitle(getResources().getString(R.string.notification_news_title))
                    // .setContentText("Subject")
                    .setSmallIcon(R.drawable.ic_mini_widget_on)
                    .setContentIntent(pIntent)
                    .addAction(R.drawable.ic_stat_notif_icon, getResources().getString(R.string.notification_news_action), pIntent)
                    .setStyle(new Notification.BigTextStyle().bigText(text))
                    .build();

            // TODO check if user wants the notification to vibrate
            notification.vibrate = new long[] { 100, 200, 100, 500 };
        }
        return notification;
    }

}