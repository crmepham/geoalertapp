package crm.geoalertapp.crm.geoalertapp.utilities;

import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Window;
import android.view.WindowManager;

import java.util.Locale;

/**
 * Created by crm on 22/12/2015.
 */
public class BaseHelper {
    public static String getContactNumber(Context context) {
        String number = "";
        TelephonyManager mngr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        number = mngr.getLine1Number();
        return number;
    }

    public static String getLanguage() {
        String language = Locale.getDefault().getLanguage();
        if(language == null) {
            language = "en";
        }
        return language;
    }
}
