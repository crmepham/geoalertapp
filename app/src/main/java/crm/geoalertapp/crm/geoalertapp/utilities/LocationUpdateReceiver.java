package crm.geoalertapp.crm.geoalertapp.utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.Toast;

/**
 * Created by crm on 17/02/2016.
 */
public class LocationUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "I'm running not BOOT", Toast.LENGTH_SHORT).show();
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
}