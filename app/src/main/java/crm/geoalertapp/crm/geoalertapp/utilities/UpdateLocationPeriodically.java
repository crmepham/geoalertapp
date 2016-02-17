package crm.geoalertapp.crm.geoalertapp.utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.Toast;

/**
 * Created by crm on 17/02/2016.
 */
public class UpdateLocationPeriodically{

    public static void registerAlarm(Context context) {
        Intent i = new Intent(context, LocationUpdateReceiver.class);

        PendingIntent sender = PendingIntent.getBroadcast(context, 9, i, 0);

        // We want the alarm to go off 3 seconds from now.
        long firstTime = SystemClock.elapsedRealtime();
        firstTime += 3 * 1000;//start 3 seconds after first register.

        // Schedule the alarm!
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME, firstTime, 3000, sender);//10min interval

        Toast.makeText(context, "Alarm Scheduled", Toast.LENGTH_SHORT).show();
    }
}
