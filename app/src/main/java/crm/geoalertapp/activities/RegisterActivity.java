package crm.geoalertapp.activities;

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

import java.net.MalformedURLException;

import crm.geoalertapp.R;
import crm.geoalertapp.crm.geoalertapp.utilities.HTTPClient;
import crm.geoalertapp.crm.geoalertapp.utilities.SharedPreferencesService;

public class RegisterActivity extends AppCompatActivity {

    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
    }

    public void navigateToLoginActivity(View view) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void logout(View view) {
        SharedPreferencesService.removeKey(getApplicationContext(), "loggedIn");
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void registerAccount(View view) {
        EditText tv = (EditText) findViewById(R.id.registerUsername);
        String username = tv.getText().toString();
        tv = (EditText) findViewById(R.id.registerPassword);
        String password = tv.getText().toString();
        tv = (EditText) findViewById(R.id.registerPasswordConfirm);
        String confirmPassword = tv.getText().toString();
        tv = (EditText) findViewById(R.id.registerEmail);
        String email = tv.getText().toString();
        tv = (EditText) findViewById(R.id.registerEmailConfirm);
        String confirmEmail = tv.getText().toString();

        String[] validationErrors = HTTPClient.validateRegistrationCredentials(username, password, confirmPassword, email, confirmEmail);
        if(validationErrors.length > 0){
            String errors = "";
            for(String error : validationErrors){
                errors += error + "\n";
            }
            toast = Toast.makeText(getApplicationContext(), errors, Toast.LENGTH_LONG);
            toast.show();
        }else{
            RegisterTask registerTask = new RegisterTask();
            registerTask.execute(username,password, email);
        }
    }

    private class RegisterTask extends AsyncTask<String, Integer, Integer> {

        protected Integer doInBackground(String... params) {

            Integer responseCode = 0;
            // attempt login via post request
            try {
                responseCode = new HTTPClient("http://10.0.2.2:8080/geoalertserver/api/v1/user/register").Register(getApplicationContext(),"POST", params);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return responseCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);;
            if(toast == null) {
                toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
            }
            toast.setText("Registering...");
            toast.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        protected void onPostExecute(Integer result) {
            if(result == 200) {
                //SharedPreferencesService.setLoggedIn(getApplicationContext(), true);
                //Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                //startActivity(intent);
            }else{
                toast.setText("Unable to register at this time: " + result);
            }
            toast.show();
        }
    }
}
