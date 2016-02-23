package crm.geoalertapp.crm.geoalertapp.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import crm.geoalertapp.crm.geoalertapp.services.RetreiveNotificationsService;

/**
 * Created by crm on 21/02/2016.
 */
public class StartNotificationRetreiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(!BaseHelper.isServiceRunning(RetreiveNotificationsService.class, context)) {
            Intent myIntent = new Intent(context, RetreiveNotificationsService.class);
            context.startService(myIntent);
        }
    }
}