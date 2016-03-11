package crm.geoalertapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import org.json.JSONObject;

import javax.ws.rs.core.MultivaluedMap;

import crm.geoalertapp.R;
import crm.geoalertapp.crm.geoalertapp.utilities.BaseHelper;
import crm.geoalertapp.crm.geoalertapp.utilities.LocationHelper;
import crm.geoalertapp.crm.geoalertapp.utilities.RestClient;
import crm.geoalertapp.crm.geoalertapp.utilities.SharedPreferencesHelper;
import crm.geoalertapp.crm.geoalertapp.utilities.ValidationHelper;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Intent intent;
    private String username;
    private ProgressDialog progress;
    private Toast toast;
    private int zoom;
    private Marker marker;
    private String latitude;
    private String longitude;
    private String lastUpdated;
    private String address;
    private Long reloadInterval;
    private ReloadMap reloadMap;
    boolean firstLoad;
    private String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));

        intent = getIntent();
        number = intent.getStringExtra("number");
        zoom = 14;
        reloadInterval = BaseHelper.INTERVAL_FIFTEEN_MINUTES;
        firstLoad = true;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        load();
    }

    public void locationCallNextOfKin(View view) {
        if(number.trim().length() > 0){
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + number));
            startActivity(callIntent);
        }else{
            Toast.makeText(getApplicationContext(), "No number provided", Toast.LENGTH_SHORT).show();
        }
    }

    public void load() {
        if(ValidationHelper.isInternetConnected(getApplicationContext())) {
            username = intent.getStringExtra("username");
            LocationTask locationTask = new LocationTask();
            locationTask.execute(username);

        }else{
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT);
        }
    }

    private class LocationTask extends AsyncTask<String, Integer, String> {

        protected String doInBackground(String... params) {

            String jsonString = null;
            String username = params[0];
            try {
                MultivaluedMap map = new MultivaluedMapImpl();
                map.add("username", username);

                RestClient tc = new RestClient(map);
                jsonString = tc.postForString("location/retreive");
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

        protected void onPostExecute(String result) {
            if(result != null) {
                SharedPreferencesHelper.setStringProperty(getApplication(), "location", result);
                try {
                    JSONObject profile = new JSONObject(result);
                    if(profile.length() > 0){
                        latitude = profile.getString("latitude");
                        longitude = profile.getString("longitude");
                        lastUpdated = BaseHelper.formatDateString("yyyy-MM-dd hh:mm:ss", "h:mm a, d MMM, yyyy", profile.getString("lastUpdated"));
                        updateLastUpdated();
                        updateAddress();
                        updateMarker();

                        if(reloadMap == null){
                            reloadMap = new ReloadMap();
                        }
                        if(!firstLoad) {
                            reloadMap.run();
                        }
                        firstLoad = false;
                    }
                } catch(Exception e) {
                    Log.e("", e.getMessage());
                }

            }else{
                if(toast == null) {
                    toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                }
                toast.setText("Could not retrieve users location.");
                toast.show();
            }

        }
    }

    private void updateMarker() {
        if(marker != null){
            marker.remove();
        }
        LatLng latlng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        marker = mMap.addMarker(new MarkerOptions().position(latlng).title(username));
        marker.setIcon((BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
       // mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom), 2000, null);
    }

    private void updateLastUpdated() {
        TextView t = (TextView) findViewById(R.id.locationLastUpdated);
        t.setText(lastUpdated);


    }

    private void updateAddress() {
        String address = LocationHelper.getAddress(getApplicationContext(), latitude, longitude);
        TextView t = (TextView) findViewById(R.id.locationAddress);
        if(address != null) {
            t.setText(address);
        }else{
            t.setText("No address found for this location");
        }
    }

    private class ReloadMap extends Thread {
        public void run() {
            try {
                Thread.sleep(reloadInterval);
                reloadMap();
            } catch (InterruptedException e) {
                Log.d("", e.getMessage());
            }
        }

        private void reloadMap() {
            load();
        }
    }
}
