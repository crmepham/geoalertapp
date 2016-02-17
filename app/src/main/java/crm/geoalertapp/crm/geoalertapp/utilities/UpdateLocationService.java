package crm.geoalertapp.crm.geoalertapp.utilities;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by crm on 17/02/2016.
 */
public class UpdateLocationService extends Service
{
    LocationUpdateReceiver alarm = new LocationUpdateReceiver();
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        //alarm.SetAlarm(this);
        return START_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
       // alarm.SetAlarm(this);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
