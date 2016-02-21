package crm.geoalertapp.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.core.MultivaluedMap;

import crm.geoalertapp.R;
import crm.geoalertapp.crm.geoalertapp.utilities.BaseHelper;
import crm.geoalertapp.crm.geoalertapp.utilities.RestClient;
import crm.geoalertapp.crm.geoalertapp.utilities.ShakeSensorService;
import crm.geoalertapp.crm.geoalertapp.utilities.SharedPreferencesService;

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.getHeaderView(0);
        TextView tv = (TextView) headerLayout.findViewById(R.id.nav_header_username);
        tv.setText(SharedPreferencesService.getStringProperty(getApplicationContext(), "username"));

        shakeSensorService = new ShakeSensorService();
        updateStatus = true;
        setUpButton();
    }

    private void setUpButton() {

        sensor = SharedPreferencesService.getStringProperty(getApplication(), "sensor");
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
                // update remote status
                // and local ??
                SharedPreferencesService.setStringProperty(getApplicationContext(), "status", "Active");
                updateRemoteStatus();

                Toast.makeText(getApplicationContext(), "Sensor activated. Status changed to Active.", Toast.LENGTH_SHORT).show();
                SharedPreferencesService.setStringProperty(getApplicationContext(), "sensor", "DEACTIVATE");
                //ShakeSensorService.SetAlarm(getApplicationContext());

                break;
            case "DEACTIVATE":
                shakeSensorService.deactivateSensor();
                t.setBackgroundResource(R.drawable.activatebutton);
                t.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, getResources().getDisplayMetrics()), 0, 0);
                t.setText("ACTIVATE");
                t = (TextView)findViewById(R.id.sensorNotification);
                t.setVisibility(View.INVISIBLE);
                SharedPreferencesService.setStringProperty(getApplicationContext(), "sensor", "ACTIVATE");

                // update remote status
                // and local ??
                SharedPreferencesService.setStringProperty(getApplicationContext(), "status", "Inactive");
                updateRemoteStatus();

                Toast.makeText(getApplicationContext(), "Sensor deactivated. Status changed to Inactive.", Toast.LENGTH_SHORT).show();
                break;
            case "RESET":
                SharedPreferencesService.setStringProperty(getApplicationContext(), "status", "Inactive");
                updateRemoteStatus();
                if(BaseHelper.isInternetConnected(getApplicationContext())) {
                    t.setBackgroundResource(R.drawable.activatebutton);
                    t.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, getResources().getDisplayMetrics()), 0, 0);
                    t.setText("ACTIVATE");
                    t = (TextView) findViewById(R.id.sensorNotification);
                    t.setVisibility(View.INVISIBLE);
                    SharedPreferencesService.setStringProperty(getApplicationContext(), "sensor", "ACTIVATE");
                    Toast.makeText(getApplicationContext(), "Status changed to Inactive.", Toast.LENGTH_SHORT).show();
                    // send notification to contacts???
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
            intent.putExtra("username", SharedPreferencesService.getStringProperty(getApplicationContext(), "username"));
        } else if (id == R.id.nav_contacts) {
            intent = new Intent(ActivationActivity.this, ContactsActivity.class);
        } else if (id == R.id.nav_settings) {
            intent = new Intent(ActivationActivity.this, SettingsActivity.class);
        } else if (id == R.id.nav_logout) {
            SharedPreferencesService.removeKey(getApplicationContext(), "username");
            SharedPreferencesService.removeKey(getApplicationContext(), "loggedIn");
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
                map.add("username", SharedPreferencesService.getStringProperty(getApplicationContext(), "username"));
                map.add("status", SharedPreferencesService.getStringProperty(getApplicationContext(), "status"));

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
}
