package crm.geoalertapp.crm.geoalertapp.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

    public static boolean isInternetConnected (Context ctx) {
        ConnectivityManager connectivityMgr = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        // Check if wifi or mobile network is available or not. If any of them is
        // available or connected then it will return true, otherwise false;
        if (wifi != null) {
            if (wifi.isConnected()) {
                return true;
            }
        }
        if (mobile != null) {
            if (mobile.isConnected()) {
                return true;
            }
        }
        return false;
    }

    public static String formatDateString(String in, String out, String value){
        String result = "";
        try{
            SimpleDateFormat inFormat = new SimpleDateFormat(in);
            SimpleDateFormat outFormat = new SimpleDateFormat(out);
            Date date = inFormat.parse(value);
            result = outFormat.format(date);
        }catch(ParseException e) {
            Log.e("", e.getMessage());
        }
        return result;
    }


    public static void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public static java.util.Date getDateFromDatePicker (DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }

    public static String createStringFromList(List<String> list) {
        String result = "";
        for(int i = 0, size = list.size(); i < size; i++) {
            if(i == size-1) {
                result += list.get(i);
            }else{
                result += list.get(i) + ",";
            }
        }
        return result;
    }
}
