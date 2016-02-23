package crm.geoalertapp.crm.geoalertapp.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.core.MultivaluedMap;

import crm.geoalertapp.activities.ActivationActivity;
import crm.geoalertapp.crm.geoalertapp.utilities.AlarmActivator;
import crm.geoalertapp.crm.geoalertapp.utilities.BaseHelper;
import crm.geoalertapp.crm.geoalertapp.utilities.RestClient;
import crm.geoalertapp.crm.geoalertapp.utilities.SharedPreferencesHelper;

public class ShakeSensorService extends Service implements SensorEventListener {

    private static Integer sensitivity;
    private SensorManager mSensorEventManager;
    private Sensor mSensor;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private boolean activated;
    private Context context;

    // BroadcastReceiver for handling ACTION_SCREEN_OFF.
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Check action just to be on the safe side.
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Log.v("", "trying re-registration");


                if(activated) {
                    // Unregisters the listener and registers it again.
                    mSensorEventManager.unregisterListener(ShakeSensorService.this);
                    mSensorEventManager.registerListener(ShakeSensorService.this, mSensor,
                            SensorManager.SENSOR_DELAY_NORMAL);
                }
            }
        }
    };

    @Override
    public void onCreate() {

    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta; // perform low-cut filter

        if(sensitivity == null){
            sensitivity = Integer.parseInt(SharedPreferencesHelper.getStringProperty(context, "sensitivity"));
        }

        if (mAccel > sensitivity) {
            mSensorEventManager.unregisterListener(ShakeSensorService.this);
            SharedPreferencesHelper.setStringProperty(context, "sensor", "RESET");
            SharedPreferencesHelper.setStringProperty(context, "status", "Alert");
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);
            Toast.makeText(context, "Device shake registered.", Toast.LENGTH_SHORT).show();
            AlarmActivator activator = new AlarmActivator(context);
            activator.start();
            Intent intent = new Intent(context, ActivationActivity.class);
            intent.putExtra("goReset", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        mSensorEventManager.unregisterListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public boolean activateSensor(Context context) throws Exception {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        context.registerReceiver(mReceiver, filter);
        sensitivity = Integer.parseInt(SharedPreferencesHelper.getStringProperty(context, "sensitivity"));
        this.context = context;
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        try {
            mSensorEventManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            mSensor = mSensorEventManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorEventManager.registerListener(ShakeSensorService.this, mSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
            activated = true;
            return true;
        }catch (Exception e) {
            activated = false;
            throw new Exception(e);
        }
    }

    public boolean deactivateSensor() {
        try {
            mSensorEventManager.unregisterListener(this);
            activated = false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    private class UpdateStatusTask extends AsyncTask<String, Integer, Integer> {

        protected Integer doInBackground(String... params) {
            try {
                MultivaluedMap map = new MultivaluedMapImpl();
                map.add("username", SharedPreferencesHelper.getStringProperty(getApplicationContext(), "username"));
                map.add("status", SharedPreferencesHelper.getStringProperty(getApplicationContext(), "status"));

                RestClient tc = new RestClient(map);
                while(true) {
                    if(BaseHelper.isInternetConnected(getApplicationContext())) {
                        tc.postForResponseCode("user/update/status");
                        break;
                    }
                }
            } catch (Exception e) {
                Log.e("", e.getMessage());
            }
            return 0;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        protected void onPostExecute(Integer result) {
        }
    }

}
