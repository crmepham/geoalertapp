package crm.geoalertapp.crm.geoalertapp.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by crm on 19/12/2015.
 */
public class SharedPreferencesHelper {

    public static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setStringProperty(Context ctx, String key, String value){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getStringProperty(Context ctx, String key) {
        return getSharedPreferences(ctx).getString(key, "");
    }

    public static void setBooleanProperty(Context ctx, String key, Boolean value){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static Boolean getBooleanProperty(Context ctx, String key) {
        return getSharedPreferences(ctx).getBoolean(key, false);
    }

    public static void removeKey(Context ctx, String key)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(key);
        editor.commit();
    }

    public static void clearAllProperties(Context ctx)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear();
        editor.commit();
    }
}
