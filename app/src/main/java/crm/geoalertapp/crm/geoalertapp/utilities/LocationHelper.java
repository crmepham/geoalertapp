package crm.geoalertapp.crm.geoalertapp.utilities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.sun.jersey.core.util.MultivaluedMapImpl;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.core.MultivaluedMap;

public class LocationHelper extends BaseHelper {
    private LocationManager lm;
    private Context context;

    public LocationHelper(Context context) {
        this.context = context;
        this.lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        try {
            this.lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
            this.lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
        } catch(Exception e) {
            Log.d("", e.getMessage());
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {

            if(BaseHelper.isInternetConnected(context) && location != null) {
                UpdateLocationTask UpdateLocationTask = new UpdateLocationTask();
                UpdateLocationTask.execute(SharedPreferencesHelper.getStringProperty(context, "username"), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                lm.removeUpdates(mLocationListener);
            }
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}
    };

    public static String getAddress(Context context, String latitude, String longitude) {
        Geocoder geo = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geo.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);
            if(!addresses.isEmpty()) {
                return String.format("%s, %s, %s", addresses.get(0).getAddressLine(0), addresses.get(0).getAddressLine(1), addresses.get(0).getAddressLine(2));
            }
        } catch (IOException e) {
            Log.d("", e.getMessage());
        }

        return null;
    }

    private class UpdateLocationTask extends AsyncTask<String, Integer, Integer> {

        protected Integer doInBackground(String... params) {
            try {
                MultivaluedMap map = new MultivaluedMapImpl();
                map.add("username", params[0]);
                map.add("latitude", params[1]);
                map.add("longitude", params[2]);

                RestClient tc = new RestClient(map);
                tc.postForResponseCode("location/update");
            } catch (Exception e) {
                e.printStackTrace();
            }

            return 0;
        }
    }
}
