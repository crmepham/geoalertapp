package crm.geoalertapp.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.core.MultivaluedMap;

import crm.geoalertapp.R;
import crm.geoalertapp.crm.geoalertapp.utilities.BaseHelper;
import crm.geoalertapp.crm.geoalertapp.utilities.LocationUpdateReceiver;
import crm.geoalertapp.crm.geoalertapp.utilities.RestClient;
import crm.geoalertapp.crm.geoalertapp.utilities.SharedPreferencesHelper;

public class SettingsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private SeekBar seekBar;
    private Integer sensitivity;
    private String displayProfileMap;
    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity

    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter

            if(sensitivity == null){
                sensitivity = Integer.parseInt(SharedPreferencesHelper.getStringProperty(getApplicationContext(), "sensitivity"));
            }

            if (mAccel > sensitivity) {
                mSensorManager.unregisterListener(mSensorListener);
                Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
                Toast toast = Toast.makeText(getApplicationContext(), "Device shake registered.", Toast.LENGTH_SHORT);
                toast.show();
                Button btn = (Button) findViewById(R.id.settingsTestSensitivityButton);
                btn.setBackgroundColor(Color.BLACK);
                btn.setText("Test");
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private void load() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;


        loadSensitivity();



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int _progress, boolean fromUser) {
                progress = _progress;
                sensitivity = _progress;
                final TextView t = (TextView) findViewById(R.id.settingsSeekBarValue);
                t.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        displayProfileMap = SharedPreferencesHelper.getStringProperty(getApplicationContext(), "displayProfileMap");
        if(displayProfileMap.equals("")) {
            displayProfileMap = "Enabled";
        }
        Button btn = (Button) findViewById(R.id.settingsProfileLocationButton);
        btn.setText(displayProfileMap);
    }

    private void loadSensitivity() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.getHeaderView(0);
        TextView tv = (TextView) headerLayout.findViewById(R.id.nav_header_username);
        tv.setText(SharedPreferencesHelper.getStringProperty(getApplicationContext(), "username"));

        final TextView t = (TextView) findViewById(R.id.settingsSeekBarValue);
        seekBar = (SeekBar) findViewById(R.id.settingsSeekBar);
        try {
            sensitivity = Integer.parseInt(SharedPreferencesHelper.getStringProperty(getApplicationContext(), "sensitivity"));
        }catch(Exception e){
            sensitivity = 15;
        }
        seekBar.setProgress(sensitivity);
        t.setText(sensitivity.toString());
    }

    public void testSensitivity(View view) {
        Button btn = (Button) findViewById(R.id.settingsTestSensitivityButton);
        String text = btn.getText().toString();
        if(text.equals("Test")){
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
            btn.setText("Listening");
            btn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }else if(text.equals("Listening")){
            mSensorManager.unregisterListener(mSensorListener);
            Toast.makeText(getApplicationContext(), "Shake test cancelled", Toast.LENGTH_SHORT).show();
            btn.setBackgroundColor(Color.BLACK);
            btn.setText("Test");
        }
    }

    public void settingsProfileLocation(View view) {
        Button btn = (Button) findViewById(R.id.settingsProfileLocationButton);
        String action = btn.getText().toString();
        if(action.equals("Enabled")){
            btn.setText("Disabled");
            displayProfileMap = "Disabled";
        }else{
            btn.setText("Enabled");
            displayProfileMap = "Enabled";
        }
    }

    public void saveSettings(View view) {
        if(displayProfileMap.equals("Enabled")){
            LocationUpdateReceiver.SetAlarm(this, BaseHelper.INTERVAL_THIRTY_MINUTES);
        }else{
            LocationUpdateReceiver.CancelAlarm(this);
        }
        UpdateShowMapTask UpdateLocationTask = new UpdateShowMapTask();
        UpdateLocationTask.execute();

        SharedPreferencesHelper.setStringProperty(getApplicationContext(), "displayProfileMap", displayProfileMap);
        SharedPreferencesHelper.setStringProperty(getApplicationContext(), "sensitivity", sensitivity.toString());
        Toast.makeText(getApplicationContext(), "Settings saved", Toast.LENGTH_SHORT).show();
    }

    private class UpdateShowMapTask extends AsyncTask<String, Integer, Integer> {

        protected Integer doInBackground(String... params) {

            Integer responseCode = 0;
            try {
                MultivaluedMap map = new MultivaluedMapImpl();
                map.add("username", SharedPreferencesHelper.getStringProperty(getApplicationContext(), "username"));
                map.add("showMap", (displayProfileMap.equals("Enabled") ? "true" : "false"));

                RestClient tc = new RestClient(map);
                responseCode = tc.postForResponseCode("user/update/map/view");
            } catch (Exception e) {
                e.printStackTrace();
            }

            return responseCode;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(integer == 201){

            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent intent = new Intent();
        int id = item.getItemId();

        if (id == R.id.nav_activation) {
            intent = new Intent(SettingsActivity.this, ActivationActivity.class);
        } else if (id == R.id.nav_profile) {
            intent = new Intent(SettingsActivity.this, ProfileActivity.class);
            intent.putExtra("username", SharedPreferencesHelper.getStringProperty(getApplicationContext(), "username"));
        } else if (id == R.id.nav_contacts) {
            intent = new Intent(SettingsActivity.this, ContactsActivity.class);
        } else if (id == R.id.nav_settings) {
            intent = new Intent(SettingsActivity.this, SettingsActivity.class);
        } else if (id == R.id.nav_logout) {
            LocationUpdateReceiver.CancelAlarm(this);
            SharedPreferencesHelper.removeKey(getApplicationContext(), "username");
            SharedPreferencesHelper.removeKey(getApplicationContext(), "loggedIn");
            intent = new Intent(SettingsActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        startActivity(intent);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSensitivity();
    }

    @Override
    protected void onPause() {
        if(mSensorManager != null){
            mSensorManager.unregisterListener(mSensorListener);
        }
        super.onPause();
    }
}
