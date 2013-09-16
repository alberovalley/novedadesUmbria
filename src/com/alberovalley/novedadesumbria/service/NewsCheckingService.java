package com.alberovalley.novedadesumbria.service;

import java.io.IOException;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.alberovalley.novedadesumbria.R;
import com.alberovalley.novedadesumbria.SettingsActivity;
import com.alberovalley.novedadesumbria.comm.UmbriaConnectionException;
import com.alberovalley.novedadesumbria.comm.UmbriaLoginData;
import com.alberovalley.novedadesumbria.comm.UmbriaMessenger;
import com.alberovalley.novedadesumbria.comm.data.UmbriaData;
import com.alberovalley.novedadesumbria.service.task.TaskManager;
import com.alberovalley.novedadesumbria.utils.AppConstants;
import com.alberovalley.utils.AlberoLog;
import com.bugsense.trace.BugSenseHandler;

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

    public static final int SERVICE_ID = 8008;
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
    private static boolean vibration;

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
        vibration = sharedPrefs.getBoolean("cb_notificacion_vibrante", false);
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

            // Notify the lack of login data as an error
            showNoLoginDataError(getApplicationContext());

        } else {
            AlberoLog.v(this, ".onHandleIntent todo correcto, llamando a CU");
            connectToUmbria(user, pass);
        }
    }

    private void showNoLoginDataError(Context context) {
        AlberoLog.v(this, ".showNoLoginDataError");
        UmbriaData data = new UmbriaData();
        data.flagError(context.
                getApplicationContext().
                getResources().
                getString(R.string.error_no_login_data_title),
                context.
                        getApplicationContext().
                        getResources().
                        getString(R.string.error_no_login_data_body)
                );
        AlberoLog.v(this, ".showNoLoginDataError creating notification");
        Notification noti = null;
        noti = createNotificationForError(data);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        AlberoLog.v(this, ".showNoLoginDataError lanzar notificación ");
        notificationManager.notify(0, noti);

        /*
         * Intent intent = new Intent(context, SettingsActivity.class);
         * // flag required to open an Activity from a
         * // NON activity context, like this Service
         * intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         * context.startActivity(intent);
         */
    }

    public void connectToUmbria(String user, String pass) {

        UmbriaLoginData ld = new UmbriaLoginData(user, pass);
        UmbriaData umbriadata = new UmbriaData();
        AlberoLog.v(this, ".connectToUmbria llamando a: TaskManager.login");
        try {
            if (!TaskManager.login(ld)) {
                // login fails. Since we know it's not an IOException, not a 404 error
                // it must be a wrong username or password
                umbriadata.flagError(
                        getResources().getString(R.string.error_wrong_login_data_title),
                        getResources().getString(R.string.error_wrong_login_data_body)
                        );
            } else {
                AlberoLog.v(this, ".connectToUmbria Login hecho: ");
                umbriadata = TaskManager.getNovedades(ld, getApplicationContext());
            }
        } catch (IllegalStateException e) {
            umbriadata.flagError(
                    getApplicationContext().getResources().getString(R.string.error_ilegal_state_title),
                    getApplicationContext().getResources().getString(R.string.error_ilegal_state_body)
                    );
            AlberoLog.e(this, ".connectToUmbria IllegalStateException Problema de Estado Ilegal " + e.getMessage());
            e.printStackTrace();
            BugSenseHandler.sendExceptionMessage("log", "IllegalStateException " + e.getMessage(), e);
        } catch (NotFoundException e) {
            umbriadata.flagError(
                    getApplicationContext().getResources().getString(R.string.error_notfoundexception_title),
                    getApplicationContext().getResources().getString(R.string.error_notfoundexception_body)
                    );
            AlberoLog.e(this, ".connectToUmbria NotFoundException " + e.getMessage());
            e.printStackTrace();
            // BugSenseHandler.sendExceptionMessage("log", "NotFoundException " + e.getMessage(), e);
        } catch (IOException e) {
            umbriadata.flagError(
                    getApplicationContext().getResources().getString(R.string.error_ioexception_title),
                    getApplicationContext().getResources().getString(R.string.error_ioexception_body)
                    );
            AlberoLog.e(this, ".connectToUmbria IOException " + e.getMessage());
            e.printStackTrace();
            // BugSenseHandler.sendExceptionMessage("log", "IOException " + e.getMessage(), e);
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
                AlberoLog.e(this, ".publishResults UmbriaConnectionException = " + e.getMessage());
                // BugSenseHandler.sendExceptionMessage("log", "UmbriaConnectionException " + e.getMessage(), e);
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
                    umbriadata.getErrorMessageTitle(),
                    System.currentTimeMillis());
            // Hide the notification after its selected
            notification.setLatestEventInfo(this,
                    umbriadata.getErrorMessageTitle(),
                    umbriadata.getErrorMessageBody(),
                    PendingIntent.getActivity(this, 0,
                            new Intent(this, SettingsActivity.class), 0)
                    );
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            // check if user wants the notification to vibrate
            if (vibration) {
                notification.defaults |= Notification.DEFAULT_VIBRATE;
            }

        } else {
            AlberoLog.v(this, ".createNotificationForError UmbriaConnectionException creamos notificación \"moderna\"");
            notification = new Notification.Builder(this)
                    .setContentTitle(umbriadata.getErrorMessageTitle())
                    .setSmallIcon(R.drawable.ic_mini_widget_error)
                    // no intent needed
                    .setStyle(
                            new Notification.BigTextStyle()
                                    .bigText(umbriadata.getErrorMessageBody()))
                    .build();

            // check if user wants the notification to vibrate
            if (vibration) {
                // After a 100ms delay, vibrate for 200ms then pause for another 100ms and then vibrate for 500ms
                notification.vibrate = new long[] { 100, 200, 100, 500 };
            }
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
        AlberoLog.v(this, ".createNotificationForNews texto a notificar: " + text);
        if (currentVersion < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            AlberoLog.v(this, ".createNotificationForNews creamos notificación \"antigua\"");
            notification = new Notification(
                    R.drawable.ic_mini_widget_on,
                    getResources().getString(R.string.notification_news_title),
                    System.currentTimeMillis());

            notification.setLatestEventInfo(this,
                    getResources().getString(R.string.notification_news_title),
                    getResources().getString(R.string.notification_news_action),
                    pIntent);

            // check if user wants the notification to vibrate
            if (vibration) {
                notification.defaults |= Notification.DEFAULT_VIBRATE;
            }

            // Hide the notification after its selected
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
        } else {

            AlberoLog.v(this, ".createNotificationForNews creamos notificación \"moderna\"");
            notification = new Notification.Builder(this)
                    .setContentTitle(getResources().getString(R.string.notification_news_title))
                    // .setContentText("Subject")
                    .setSmallIcon(R.drawable.ic_mini_widget_on)
                    .setContentIntent(pIntent)
                    .addAction(R.drawable.ic_stat_notif_icon, getResources().getString(R.string.notification_news_action), pIntent)
                    .setStyle(new Notification.BigTextStyle().bigText(text))
                    .build();

            // check if user wants the notification to vibrate
            if (vibration) {
                // After a 100ms delay, vibrate for 200ms then pause for another 100ms and then vibrate for 500ms
                notification.vibrate = new long[] { 100, 200, 100, 500 };
            }
        }
        return notification;
    }

}