package crm.geoalertapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sun.jersey.core.util.MultivaluedMapImpl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.core.MultivaluedMap;

import crm.geoalertapp.R;
import crm.geoalertapp.crm.geoalertapp.utilities.AlarmActivator;
import crm.geoalertapp.crm.geoalertapp.utilities.BaseHelper;
import crm.geoalertapp.crm.geoalertapp.utilities.RestClient;
import crm.geoalertapp.crm.geoalertapp.services.ShakeSensorService;
import crm.geoalertapp.crm.geoalertapp.utilities.SharedPreferencesHelper;

public class ActivationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String sensor;
    private ShakeSensorService shakeSensorService;
    private boolean updateStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);
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



        shakeSensorService = new ShakeSensorService();
        updateStatus = true;

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateRemoteStatus();
        setUpButton();
    }

    @Override
    protected void onPause() {
        super.onPause();
        TextView t = (TextView)findViewById(R.id.sensorButton);
        t.setVisibility(View.INVISIBLE);
    }

    private void setUpButton() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.getHeaderView(0);
        TextView tv = (TextView) headerLayout.findViewById(R.id.nav_header_username);
        tv.setText(SharedPreferencesHelper.getStringProperty(getApplicationContext(), "username"));

        // get from DB
        boolean goReset = getIntent().getBooleanExtra("goReset", false);
        if(!goReset) {
            if (BaseHelper.isInternetConnected(getApplicationContext())) {
                ActivationTask activationTask = new ActivationTask();
                activationTask.execute();
            } else {
                sensor = SharedPreferencesHelper.getStringProperty(getApplication(), "sensor");
                addButton();
            }
        }else{
            sensor = "RESET";
            addButton();
        }
    }

    private void addButton() {
        TextView t = (TextView)findViewById(R.id.sensorButton);
        if(sensor == null || sensor.equals("ACTIVATE")) {
            t.setBackgroundResource(R.drawable.activatebutton);
            t.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, getResources().getDisplayMetrics()), 0, 0);
            t.setText("ACTIVATE");
        }else if(sensor.equals("DEACTIVATE")){
            t.setBackgroundResource(R.drawable.deactivatebutton);
            t.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, getResources().getDisplayMetrics()), 0, 0);
            t.setText("DEACTIVATE");
        }else if(sensor.equals("RESET")) {
            t.setBackgroundResource(R.drawable.resetbutton);
            t.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, getResources().getDisplayMetrics()), 0, 0);
            t.setText("RESET");
            t = (TextView)findViewById(R.id.sensorNotification);
            t.setVisibility(View.VISIBLE);
        }
        t = (TextView)findViewById(R.id.sensorButton);
        t.setVisibility(View.VISIBLE);
    }

    public void activateSensor(View view) throws Exception {
        TextView t = (TextView)view;
        switch(t.getText().toString()){
            case "ACTIVATE":
                shakeSensorService.activateSensor(getApplicationContext());
                t.setBackgroundResource(R.drawable.deactivatebutton);
                t.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, getResources().getDisplayMetrics()), 0, 0);
                t.setText("DEACTIVATE");
                t = (TextView)findViewById(R.id.sensorNotification);
                t.setVisibility(View.INVISIBLE);
                SharedPreferencesHelper.setStringProperty(getApplicationContext(), "status", "Active");
                updateRemoteStatus();

                Toast.makeText(getApplicationContext(), "Sensor activated. Status changed to Active.", Toast.LENGTH_SHORT).show();
                SharedPreferencesHelper.setStringProperty(getApplicationContext(), "sensor", "DEACTIVATE");

                break;
            case "DEACTIVATE":
                shakeSensorService.deactivateSensor();
                t.setBackgroundResource(R.drawable.activatebutton);
                t.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, getResources().getDisplayMetrics()), 0, 0);
                t.setText("ACTIVATE");
                t = (TextView)findViewById(R.id.sensorNotification);
                t.setVisibility(View.INVISIBLE);
                SharedPreferencesHelper.setStringProperty(getApplicationContext(), "sensor", "ACTIVATE");
                SharedPreferencesHelper.setStringProperty(getApplicationContext(), "status", "Inactive");
                updateRemoteStatus();

                Toast.makeText(getApplicationContext(), "Sensor deactivated. Status changed to Inactive.", Toast.LENGTH_SHORT).show();
                break;
            case "RESET":
                SharedPreferencesHelper.setStringProperty(getApplicationContext(), "status", "Inactive");
                updateRemoteStatus();
                if(BaseHelper.isInternetConnected(getApplicationContext())) {
                    t.setBackgroundResource(R.drawable.activatebutton);
                    t.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, getResources().getDisplayMetrics()), 0, 0);
                    t.setText("ACTIVATE");
                    t = (TextView) findViewById(R.id.sensorNotification);
                    t.setVisibility(View.INVISIBLE);
                    SharedPreferencesHelper.setStringProperty(getApplicationContext(), "sensor", "ACTIVATE");
                    Toast.makeText(getApplicationContext(), "Status changed to Inactive.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Could not reset status. No internet connection.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private void updateRemoteStatus() {
        if(updateStatus){
            UpdateStatusTask updateStatusTask = new UpdateStatusTask();
            updateStatusTask.execute();
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
        getMenuInflater().inflate(R.menu.activation, menu);
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
            Intent intent = new Intent(ActivationActivity.this, SettingsActivity.class);
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
            intent = new Intent(ActivationActivity.this, ActivationActivity.class);
        } else if (id == R.id.nav_profile) {
            intent = new Intent(ActivationActivity.this, ProfileActivity.class);
            intent.putExtra("username", SharedPreferencesHelper.getStringProperty(getApplicationContext(), "username"));
        } else if (id == R.id.nav_contacts) {
            intent = new Intent(ActivationActivity.this, ContactsActivity.class);
        } else if (id == R.id.nav_settings) {
            intent = new Intent(ActivationActivity.this, SettingsActivity.class);
        } else if (id == R.id.nav_logout) {
            SharedPreferencesHelper.removeKey(getApplicationContext(), "username");
            SharedPreferencesHelper.removeKey(getApplicationContext(), "loggedIn");
            intent = new Intent(ActivationActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        startActivity(intent);
        return true;
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
            updateStatus = false;
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        protected void onPostExecute(Integer result) {
            updateStatus = true;
        }
    }

    private class ActivationTask extends AsyncTask<String, Integer, String> {

        protected String doInBackground(String... params) {

            String jsonString = null;
            try {
                MultivaluedMap map = new MultivaluedMapImpl();
                map.add("username", SharedPreferencesHelper.getStringProperty(getApplicationContext(), "username"));

                RestClient tc = new RestClient(map);
                jsonString = tc.postForString("user/retreive/status");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        protected void onPostExecute(String status) {
            if(status != null) {
                SharedPreferencesHelper.setStringProperty(getApplication(), "status", status);
                switch(status) {
                    case "Inactive":
                        sensor = "ACTIVATE";
                        break;
                    case "Active":
                        sensor = "DEACTIVATE";
                        break;
                    case "Alert":
                        sensor = "RESET";
                        break;
                }

            }else{
                sensor = SharedPreferencesHelper.getStringProperty(getApplication(), "sensor");
            }
            addButton();
        }
    }
}
