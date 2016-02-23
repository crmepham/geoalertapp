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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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

public class RegisterActivity extends AppCompatActivity {

    Toast toast;
    ProgressDialog progress;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        addSecurityQuestions();
    }

    private void addSecurityQuestions() {
        spinner = (Spinner) findViewById(R.id.securityQuestions);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.security_questions, R.layout.spinner_item);
        spinner.setAdapter(adapter);
    }

    public void navigateToLoginActivity(View view) {
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
        Spinner sp = (Spinner) findViewById(R.id.securityQuestions);
        String securityQuestion = sp.getSelectedItem().toString();
        tv = (EditText) findViewById(R.id.securityAnswer);
        String securityAnswer = tv.getText().toString();
        String t = securityAnswer;

        String[] validationErrors = ValidationHelper.validateRegistrationCredentials(username, password, confirmPassword, email, confirmEmail, securityAnswer);
        if(validationErrors.length > 0){
            String errors = "";
            for(String error : validationErrors){
                errors += error + "\n";
            }
            toast = Toast.makeText(getApplicationContext(), errors, Toast.LENGTH_LONG);
            toast.show();
        }else{
            progress = ProgressDialog.show(RegisterActivity.this, "",
                    "Registering. Please wait...", true);
            progress.show();
            String contactNumber = BaseHelper.getContactNumber(getApplicationContext());
            String lang = BaseHelper.getLanguage();
            String encryptedSecurityAnswer = StringEncrypter.encrypt(securityAnswer);
            String encryptedPassword = StringEncrypter.encrypt(password);

            RegisterTask registerTask = new RegisterTask();
            registerTask.execute(username, encryptedPassword, contactNumber, email, lang, securityQuestion, encryptedSecurityAnswer);
        }
    }

    private class RegisterTask extends AsyncTask<String, Integer, Integer> {

        protected Integer doInBackground(String... params) {

            Integer responseCode = 0;
            try {
                MultivaluedMap map = new MultivaluedMapImpl();
                map.add("username", params[0]);
                map.add("password", params[1]);
                map.add("contactNumber", params[2]);
                map.add("email", params[3]);
                map.add("lang", params[4]);
                map.add("securityQuestion", params[5]);
                map.add("securityAnswer", params[6]);

                RestClient tc = new RestClient(map);
                responseCode = tc.postForResponseCode("user/register");
            } catch (Exception e) {
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
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        protected void onPostExecute(Integer result) {
            progress.dismiss();
            if(result == 201) {
                EditText tv = (EditText) findViewById(R.id.registerUsername);
                String username = tv.getText().toString();
                SharedPreferencesHelper.setStringProperty(getApplicationContext(), "username", username);
                SharedPreferencesHelper.setBooleanProperty(getApplicationContext(), "loggedIn", true);

                if(SharedPreferencesHelper.getStringProperty(getApplicationContext(), "sensitivity") == null) {
                    SharedPreferencesHelper.setStringProperty(getApplicationContext(), "sensitivity", "15");
                }

                if(SharedPreferencesHelper.getStringProperty(getApplicationContext(), "displayProfileMap") == null) {
                    SharedPreferencesHelper.setStringProperty(getApplicationContext(), "displayProfileMap", "Enabled");
                    LocationUpdateReceiver.SetAlarm(getApplicationContext(), BaseHelper.INTERVAL_FIFTEEN_MINUTES); // 30 mins approx.
                }

                Intent intent = new Intent(RegisterActivity.this, ProfileActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }else{
                toast.setText("Unable to register, that user already exists.");
                toast.show();
            }

        }
    }
}
