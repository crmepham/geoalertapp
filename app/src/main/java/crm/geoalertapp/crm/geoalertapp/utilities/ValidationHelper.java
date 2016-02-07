package crm.geoalertapp.crm.geoalertapp.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by crm on 22/12/2015.
 */
public class ValidationHelper extends BaseHelper {
    private static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    public static String[] validateLoginCredentials(String username, String password) {
        List<String> errors = new ArrayList<>();
        if(username.isEmpty()){errors.add("Must provide username.\n");}
        if(password.isEmpty()){errors.add("Must provide password.\n");}
        if(!(username.length() > 2)){errors.add("Username must be more than 3 characters.");}
        if(errors.size() > 0){
            return errors.toArray(new String[errors.size()]);
        }
        return new String[0];
    }
    public static String[] validateRegistrationCredentials(String username, String password,
                                                           String confirmPassword, String email, String confirmEmail, String securityAnswer) {
        List<String> errors = new ArrayList<>();
        if(username.isEmpty()){errors.add("Must provide username.\n");}
        if(password.isEmpty()){errors.add("Must provide password.\n");}
        if(confirmPassword.isEmpty()){errors.add("Must provide confirmation password.\n");}
        if(email.isEmpty()){errors.add("Must provide email.\n");}
        if(confirmEmail.isEmpty()){errors.add("Must provide confirmation email.\n");}
        if(securityAnswer.isEmpty()){errors.add("Must provide security answer.\n");}
        if(!password.equals(confirmPassword)){errors.add("Passwords do not match");}
        if(!email.equals(confirmEmail)){errors.add("Emails do not match");}
        if(!validateEmail(email)){errors.add("Invalid email address");}
        if(errors.size() > 0){
            return errors.toArray(new String[errors.size()]);
        }
        return new String[0];
    }

    public static boolean validateEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    public static boolean matchStringValues(String string1, String string2) {
        boolean match = false;
        if(string1.equals(string2)){
            match = true;
        }
        return match;
    }

    public static String[] validateProfileInformation(List<String> info) {
        List<String> errors = new ArrayList<>();
        for(int i = 0, size = info.size(); i < size; i++) {
            if(info.get(i).length() > 50){
                errors.add("Invalid length (must be no more than 50 characters): "+info.get(i));
            }
            if(info.get(i).contains(",")){
                errors.add("Invalid character (,): "+info.get(i)+")");
            }
            /*char[] array = string.toCharArray();
            for(char c : array){
                if(c == ','){
                    errors.add("Invalid characters (,): " + string);
                }
            }*/
        }
        return errors.toArray(new String[errors.size()]);
    }
}
