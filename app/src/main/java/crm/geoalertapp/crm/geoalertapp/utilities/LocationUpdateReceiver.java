package crm.geoalertapp.crm.geoalertapp.utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;

import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.core.MultivaluedMap;

/**
 * Created by crm on 17/02/2016.
 */
public class LocationUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(BaseHelper.isInternetConnected(context)) {
            LocationHelper locationHelper = new LocationHelper(context);
            locationHelper.updateLocation();
            UpdateLocationTask UpdateLocationTask = new UpdateLocationTask();
            UpdateLocationTask.execute(SharedPreferencesHelper.getStringProperty(context, "username"), locationHelper.getLatitude(), locationHelper.getLongitude());
        }

    }

    public static void SetAlarm(Context context, Long time){
        Intent intentAlarm = new Intent(context, LocationUpdateReceiver.class);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), time, pi);
    }

    public static void CancelAlarm(Context context) {
        Intent intent = new Intent(context, LocationUpdateReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    private class UpdateLocationTask extends AsyncTask<String, Integer, Integer> {

        protected Integer doInBackground(String... params) {

            Integer responseCode = 0;
            try {
                MultivaluedMap map = new MultivaluedMapImpl();
                map.add("username", params[0]);
                map.add("latitude", params[1]);
                map.add("longitude", params[2]);

                RestClient tc = new RestClient(map);
                responseCode = tc.postForResponseCode("location/update");
            } catch (Exception e) {
                e.printStackTrace();
            }

            return responseCode;
        }
    }
}