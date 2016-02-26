package crm.geoalertapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.core.MultivaluedMap;

import crm.geoalertapp.R;
import crm.geoalertapp.crm.geoalertapp.utilities.BaseHelper;
import crm.geoalertapp.crm.geoalertapp.utilities.LocationUpdateReceiver;
import crm.geoalertapp.crm.geoalertapp.utilities.RestClient;
import crm.geoalertapp.crm.geoalertapp.utilities.SharedPreferencesHelper;
import crm.geoalertapp.crm.geoalertapp.utilities.StringEncrypter;
import crm.geoalertapp.crm.geoalertapp.utilities.ValidationHelper;

public class LoginActivity extends AppCompatActivity {

    Toast toast;
    ProgressDialog progress;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        boolean loggedIn = SharedPreferencesHelper.getBooleanProperty(getApplicationContext(), "loggedIn");
        // check loggedIn here
        if(loggedIn){
            Intent intent = new Intent(LoginActivity.this, ContactsActivity.class);
            startActivity(intent);
        }else{
            setContentView(R.layout.activity_login);
        }

    }

    public void navigateToRegisterActivity(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void navigateToForgotPasswordActivity(View view) {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivityStep1.class);
        startActivity(intent);
    }

    public void loginButton(View view) {
        EditText tv = (EditText) findViewById(R.id.inputUsername);
        username = tv.getText().toString();
        tv = (EditText) findViewById(R.id.inputPassword);
        String password = tv.getText().toString();

        String[] validationErrors = ValidationHelper.validateLoginCredentials(username, password);
        if(validationErrors.length > 0){
            progress.dismiss();
            String errors = "";
            for(String error : validationErrors){
                errors += error + "\n";
            }
            toast = Toast.makeText(getApplicationContext(), errors, Toast.LENGTH_LONG);
            toast.show();
        }else if(ValidationHelper.isInternetConnected(getApplicationContext())){

            LoginTask loginTask = new LoginTask();
            loginTask.execute(username, password);
        }else{
            progress.dismiss();
            toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
            toast.setText("No internet access.");
            toast.show();
        }

    }


    private class LoginTask extends AsyncTask<String, Integer, Integer>{

        protected Integer doInBackground(String... params) {
            String encryptedPassword = StringEncrypter.encrypt(params[1]);
            Integer responseCode = 0;
            // attempt login via post request
            try {
                MultivaluedMap map = new MultivaluedMapImpl();
                map.add("username", params[0]);
                map.add("password", encryptedPassword);

                RestClient tc = new RestClient(map);
                responseCode = tc.postForResponseCode("user/authenticate");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseCode;
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(LoginActivity.this, "", "Logging in. Please wait...", true);
            progress.show();
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        protected void onPostExecute(Integer result) {
            progress.dismiss();
            if(result == 200) {
                SharedPreferencesHelper.setBooleanProperty(getApplicationContext(), "loggedIn", true);
                SharedPreferencesHelper.setStringProperty(getApplication(), "username", username);

                if(SharedPreferencesHelper.getStringProperty(getApplicationContext(), "sensitivity").equals("")) {
                    SharedPreferencesHelper.setStringProperty(getApplication(), "sensitivity", "16");
                }

                if(SharedPreferencesHelper.getStringProperty(getApplicationContext(), "displayProfileMap").equals("Enabled")) {
                    LocationUpdateReceiver.SetAlarm(getApplicationContext(), BaseHelper.INTERVAL_FIFTEEN_MINUTES); // 30 mins approx.
                }

                Intent intent = new Intent(LoginActivity.this, ContactsActivity.class);
                startActivity(intent);
            }else if(result == 401){
                if(toast == null) {
                    toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                }
                toast.setText("Incorrect login details.");
                toast.show();
            }else{
                if(toast == null) {
                    toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                }
                toast.setText("Could not login at this time.");
                toast.show();
            }

        }
    }
}
