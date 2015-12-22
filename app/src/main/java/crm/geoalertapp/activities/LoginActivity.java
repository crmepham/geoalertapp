package crm.geoalertapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import crm.geoalertapp.crm.geoalertapp.utilities.RestClient;
import crm.geoalertapp.crm.geoalertapp.utilities.SharedPreferencesService;
import crm.geoalertapp.crm.geoalertapp.utilities.StringEncrypter;
import crm.geoalertapp.crm.geoalertapp.utilities.ValidationHelper;

public class LoginActivity extends AppCompatActivity {

    Toast toast;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        boolean loggedIn = SharedPreferencesService.getBooleanProperty(getApplicationContext(), "loggedIn");
        SharedPreferencesService.removeKey(getApplicationContext(), "loggedIn");
        // check loggedIn here
        if(loggedIn){
            setContentView(R.layout.activity_contacts);
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
        String username = tv.getText().toString();
        tv = (EditText) findViewById(R.id.inputPassword);
        String password = tv.getText().toString();

        String[] validationErrors = ValidationHelper.validateLoginCredentials(username, password);
        if(validationErrors.length > 0){
            String errors = "";
            for(String error : validationErrors){
                errors += error + "\n";
            }
            toast = Toast.makeText(getApplicationContext(), errors, Toast.LENGTH_LONG);
            toast.show();
        }else{
            String encryptedPassword = StringEncrypter.encrypt(password);
            LoginTask loginTask = new LoginTask();
            loginTask.execute(username, encryptedPassword);
        }
    }


    private class LoginTask extends AsyncTask<String, Integer, Integer>{

        protected Integer doInBackground(String... params) {

            Integer responseCode = 0;
            // attempt login via post request
            try {
                MultivaluedMap map = new MultivaluedMapImpl();
                map.add("username", params[0]);
                map.add("password", params[1]);

                RestClient tc = new RestClient(map);
                responseCode = tc.postForResponseCode("user/authenticate");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);;
            progress = ProgressDialog.show(LoginActivity.this, "", "Logging in. Please wait...", true);
            progress.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        protected void onPostExecute(Integer result) {
            progress.dismiss();
            if(result == 200) {
                SharedPreferencesService.setBooleanProperty(getApplicationContext(), "loggedIn", true);
                Intent intent = new Intent(LoginActivity.this, ContactsActivity.class);
                startActivity(intent);
            }else{
                if(toast == null) {
                    toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                }
                toast.setText("Incorrect login details.");
                toast.show();
            }

        }
    }
}
