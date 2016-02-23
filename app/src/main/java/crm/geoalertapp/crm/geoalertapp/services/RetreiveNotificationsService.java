package crm.geoalertapp.crm.geoalertapp.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import crm.geoalertapp.R;
import crm.geoalertapp.activities.ContactsActivity;
import crm.geoalertapp.activities.LocationActivity;
import crm.geoalertapp.activities.ProfileActivity;
import crm.geoalertapp.crm.geoalertapp.utilities.AlarmActivator;
import crm.geoalertapp.crm.geoalertapp.utilities.BaseHelper;
import crm.geoalertapp.crm.geoalertapp.utilities.SharedPreferencesHelper;

/**
 * Created by crm on 21/02/2016.
 */
public class RetreiveNotificationsService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, filter);
        getNotifications(getApplicationContext());
    }

    private void getNotifications(Context applicationContext) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        JSONArray notifications = AlarmActivator.getNotifications(SharedPreferencesHelper.getStringProperty(getApplicationContext(), "username"));
                        if (notifications != null | notifications.length() > 0) {
                            if (notifications.length() > 3) {
                                createNotification("Alert: Multiple", null, null, true);
                            } else {
                                for (int i = 0, size = notifications.length(); i < size; i++) {
                                    try {
                                        JSONObject notification = notifications.getJSONObject(i);

                                        String name;
                                        try {
                                            name = notification.getString("senderFullName");
                                        } catch(JSONException e) {
                                            name = notification.getString("senderUsername");
                                        }
                                        createNotification("Alert: " + name, name, null, false);
                                        name = notification.getString("senderUsername");
                                        AlarmActivator.deleteNotification(getApplicationContext(), name);
                                    } catch (Exception e) {
                                        Log.d("", e.getMessage());
                                    }
                                }
                            }
                        }
                        Thread.sleep(BaseHelper.INTERVAL_ONE_MINUTE);
                    } catch (InterruptedException e) {

                    } catch (NullPointerException npe) {
                        Log.d("", npe.getMessage());
                    }
                }
        }
        }).start();
    }

    public void createNotification(String title, String name, String number, boolean multiple) {

        Intent intent = null;
        if(multiple) {
            intent = new Intent(getApplicationContext(), ContactsActivity.class);
        }
        intent = new Intent(getApplicationContext(), ContactsActivity.class);


        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification n  = new Notification.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText("Touch to view location details")
                .setSmallIcon(R.drawable.icon_only_light)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, n);

    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent myIntent = new Intent(context, RetreiveNotificationsService.class);
            context.startService(myIntent);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
