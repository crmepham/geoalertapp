package crm.geoalertapp.crm.geoalertapp.utilities;

import android.content.Context;
import android.util.Log;

import com.sun.jersey.core.util.MultivaluedMapImpl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.core.MultivaluedMap;

public class AlarmActivator extends Thread implements Runnable{
    String username;
    String status;
    MultivaluedMap map;
    RestClient tc;
    Context context;

    public AlarmActivator(Context context){
        this.context = context;
        username = SharedPreferencesHelper.getStringProperty(context, "username");
        status = SharedPreferencesHelper.getStringProperty(context, "status");
        map = new MultivaluedMapImpl();
        tc = new RestClient(map);
    }

    @Override
    public void run() {
        while(true) {
            try {
                if(BaseHelper.isInternetConnected(context)) {
                    LocationHelper l = new LocationHelper(context);

                    updateLocation(l.getLatitude(), l.getLongitude());
                    updateRemoteStatus();
                    notifyContacts();
                    break;
                }
                sleep(60000);
            } catch (InterruptedException e) {
                Log.d("", e.getMessage());
            }

        }
    }

    private void updateLocation(String latitude, String longitude) {
        try {

            map.clear();
            map.add("username", username);
            map.add("latitude", latitude);
            map.add("longitude", longitude);

            tc.updateMap(map);
            tc.postForResponseCode("location/update");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateRemoteStatus() {
        try {
            map.clear();
            map.add("username", username);
            map.add("status", status);

            tc.updateMap(map);
            tc.postForResponseCode("user/update/status");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notifyContacts(){

        // get list of contacts
        JSONArray contacts = retreiveContacts();

        if(contacts != null && contacts.length() > 0) {
            for (int i = 0, size = contacts.length(); i < size; i++) {
                try {
                    JSONObject contact = contacts.getJSONObject(i);
                    sendNotification(contact);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // notify each contact
            try {
                map.clear();
                map.add("username", username);

                tc.updateMap(map);
                tc.postForResponseCode("user/retreive/notifications");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private JSONArray retreiveContacts() {
        String jsonString = null;
        try {
            MultivaluedMap map = new MultivaluedMapImpl();
            map.add("username", username);

            RestClient tc = new RestClient(map);
            jsonString = tc.postForString("user/retrieve/user/contacts");
        } catch (Exception e) {
            Log.d("", e.getMessage());
        }

        if(!jsonString.contains("This user has no contacts")) {
            try {
                return new JSONArray(jsonString);
            } catch (JSONException je) {
                Log.d("", je.getMessage());
                return new JSONArray();
            }
        }

        return null;
    }

    private boolean sendNotification(JSONObject contact) {
        try {
            MultivaluedMap map = new MultivaluedMapImpl();
            map.add("username", username);
            map.add("contactUsername", contact.getString("username"));

            RestClient tc = new RestClient(map);
            tc.postForString("user/add/notification");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static JSONArray getNotifications(String username) {
        String jsonString = null;
        try {
            MultivaluedMap map = new MultivaluedMapImpl();
            map.add("username", username);

            RestClient tc = new RestClient(map);
            jsonString = tc.postForString("user/retreive/notifications");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(!jsonString.contains("This user has no notifications")) {
            try {
                return new JSONArray(jsonString);
            } catch (JSONException je) {
                Log.d("", je.getMessage());
                return new JSONArray();
            }
        }

        return null;
    }

    public static void deleteNotification(Context context, String username) {
        try {
            MultivaluedMap map = new MultivaluedMapImpl();
            map.add("username", username);
            map.add("contactUsername", SharedPreferencesHelper.getStringProperty(context, "username"));

            RestClient tc = new RestClient(map);
            tc.postForString("user/delete/notification");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cancelNotification(Context context, String username) {
        try {
            MultivaluedMap map = new MultivaluedMapImpl();
            map.add("username", username);
            map.add("contactUsername", SharedPreferencesHelper.getStringProperty(context, "username"));

            RestClient tc = new RestClient(map);
            tc.postForString("user/delete/notification");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}