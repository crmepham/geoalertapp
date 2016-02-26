package crm.geoalertapp.crm.geoalertapp.utilities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationHelper extends BaseHelper {
    private String latitude;
    private String longitude;
    private Location location;
    private LocationManager lm;
    private Context context;
    private PackageManager pm;

    public LocationHelper(Context context) {
        this.context = context;
        this.pm = context.getPackageManager();
        this.lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        updateLocation();
    }

    public boolean isLocationAvailable() {
        return (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) ? true : lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public boolean updateLocation() {
        if(isLocationAvailable()) {
            location = getLocation();
            latitude = (location != null) ? String.valueOf(location.getLatitude()) : "0";
            longitude = (location != null) ? String.valueOf(location.getLongitude()) : "0";
            if(location != null) {
                return true;
            }
        }
        return false;
    }

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

    private Location getLocation() {
        try {
            location = lm.getLastKnownLocation(lm.GPS_PROVIDER);
            if (location == null) {
                location = lm.getLastKnownLocation(lm.NETWORK_PROVIDER);
            }
            //location = new Location("Defaut address");
            //location.setLatitude(0L);
            //location.setLongitude(0L);
        } catch (SecurityException e) {
            Log.d("", e.getMessage());
        }
        return location;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
