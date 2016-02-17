package crm.geoalertapp.crm.geoalertapp.utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by crm on 17/02/2016.
 */
public class AutoStartLocationUpdateReceiver extends BroadcastReceiver
{
    LocationUpdateReceiver alarm = new LocationUpdateReceiver();
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            //alarm.SetAlarm(context);
        }
    }
}