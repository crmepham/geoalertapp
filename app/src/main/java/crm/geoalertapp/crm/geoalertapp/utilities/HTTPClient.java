package crm.geoalertapp.crm.geoalertapp.utilities;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Base64;

import com.squareup.okhttp.Response;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import se.simbio.encryption.Encryption;

/**
 * Created by crm on 18/12/2015.
 */
public class HTTPClient {

    private URL url;
    private int responseCode;
    private static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    public HTTPClient(String url) throws MalformedURLException {
        this.url = new URL(url);
    }


    public int Login(String username, String password, String method){
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setReadTimeout(10000);
            con.setConnectTimeout(15000);
            con.setRequestMethod(method);
            con.setDoInput(true);
            con.setDoOutput(true);

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", encryptString(password)));

            OutputStream os = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();

            con.connect();
            responseCode = con.getResponseCode();

        } catch (IOException e) {
            // Catch IOException
            e.printStackTrace();
        }
        return responseCode;
    }

    public int Register(Context context, String method, String... regParams){
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setReadTimeout(10000);
            con.setConnectTimeout(15000);
            con.setRequestMethod(method);
            con.setDoInput(true);
            con.setDoOutput(true);

            String contactNumber = getContactNumber(context);
            String lang = getLanguage();
            String password = encryptString(regParams[1]);

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("username", regParams[0]));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("email", regParams[2]));
            params.add(new BasicNameValuePair("contactNumber", contactNumber));
            params.add(new BasicNameValuePair("lang", lang));

            OutputStream os = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();

            con.connect();
            responseCode = con.getResponseCode();

        } catch (IOException e) {
            // Catch IOException
            e.printStackTrace();
        }
        return responseCode;
    }

    public int RecoverAccount(String email, String method){
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setReadTimeout(10000);
            con.setConnectTimeout(15000);
            con.setRequestMethod(method);
            con.setDoInput(true);
            con.setDoOutput(true);

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("email", email));

            OutputStream os = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();

            con.connect();
            responseCode = con.getResponseCode();

        } catch (IOException e) {
            // Catch IOException
            e.printStackTrace();
        }
        return responseCode;
    }

    private String encryptString(String regParam) {
        String encrypted = "";
        String de = "";
        try{
            Encryption encryption = Encryption.getDefault("030686808", "030686808", new byte[16]);
            encrypted = encryption.encryptOrNull(regParam);
            String decrypted = encryption.decryptOrNull(encrypted);
            de = decrypted;

        } catch(Exception e){
            e.printStackTrace();
        }
        return encrypted;
    }

    private String decryptString(String encryptedString) {
        String decrypted = "";
        try{
            Encryption encryption = Encryption.getDefault("030686808", "030686808", new byte[16]);
            decrypted = encryption.decryptOrNull(encryptedString);

        } catch(Exception e){
            e.printStackTrace();
        }
        return decrypted;
    }

    private String getContactNumber(Context context) {
        String number = "";
        TelephonyManager mngr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        number = mngr.getLine1Number();
        return number;
    }

    private String getLanguage() {
        String language = Locale.getDefault().getLanguage();
        if(language == null) {
            language = "en";
        }
        return language;
    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public static String[] validateRegistrationCredentials(String username, String password, String confirmPassword, String email, String confirmEmail) {
        List<String> errors = new ArrayList<>();
        if(username.isEmpty()){errors.add("Must provide username.\n");}
        if(password.isEmpty()){errors.add("Must provide password.\n");}
        if(confirmPassword.isEmpty()){errors.add("Must provide confirmation password.\n");}
        if(email.isEmpty()){errors.add("Must provide email.\n");}
        if(confirmEmail.isEmpty()){errors.add("Must provide confirmation email.\n");}
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
}
