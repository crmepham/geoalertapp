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
import crm.geoalertapp.crm.geoalertapp.utilities.SharedPreferencesService;
import crm.geoalertapp.crm.geoalertapp.utilities.RestClient;
import crm.geoalertapp.crm.geoalertapp.utilities.ValidationHelper;

public class ForgotPasswordActivityStep1 extends AppCompatActivity {

    ProgressDialog progress;
    Toast toast;
    String email;
    String securityQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_step1);
    }

    public void navigateToLoginActivity(View view) {
        Intent intent = new Intent(ForgotPasswordActivityStep1.this, LoginActivity.class);
        startActivity(intent);
    }

    public void navigateToRegisterActivity(View view) {
        Intent intent = new Intent(ForgotPasswordActivityStep1.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void sendPasswordEmail(View view) {
        EditText tv = (EditText) findViewById(R.id.inputForgotPasswordEmail);
        email = tv.getText().toString();

        if(ValidationHelper.validateEmail(email)) {
            ConfirmEmailTask confirmEmailTask = new ConfirmEmailTask();
            confirmEmailTask.execute(email);

        }else{
            if(toast == null) {
                toast = Toast.makeText(getApplicationContext(), "Invalid email", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private class ConfirmEmailTask extends AsyncTask<String, Integer, Integer> {

        protected Integer doInBackground(String... params) {

            Integer responseCode = 0;
            try {
                MultivaluedMap map = new MultivaluedMapImpl();
                map.add("email", params[0]);

                RestClient tc = new RestClient(map);
                responseCode = tc.postForResponseCode("user/confirm/email");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(ForgotPasswordActivityStep1.this, "", "Retrieving security question...", true);
            progress.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        protected void onPostExecute(Integer result) {
            progress.dismiss();
            if(result == 201) {
                SharedPreferencesService.setStringProperty(getApplicationContext(), "email", email);
                RetrieveSecurityQuestionTask retrieveSecurityQuestionTask = new RetrieveSecurityQuestionTask();
                retrieveSecurityQuestionTask.execute(email);
            }else{
                if(toast == null) {
                    toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                }
                toast.setText("That email does not exist on our system");
                toast.show();
            }

        }
    }

    private class RetrieveSecurityQuestionTask extends AsyncTask<String, Integer, String> {

        protected String doInBackground(String... params) {

            Integer responseCode = 0;
            String result = "";
            try {
                MultivaluedMap map = new MultivaluedMapImpl();
                map.add("email", params[0]);

                RestClient tc = new RestClient(map);
                result = tc.postForString("user/retrieve/security/question");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(ForgotPasswordActivityStep1.this, "", "Retrieving security question...", true);
            progress.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        protected void onPostExecute(String result) {
            progress.dismiss();

            if(!result.isEmpty()) {
                SharedPreferencesService.setStringProperty(getApplicationContext(), "securityQuestion", result);
                Intent intent = new Intent(ForgotPasswordActivityStep1.this, ForgotPasswordActivityStep2.class);
                startActivity(intent);
            }else{
                if(toast == null) {
                    toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                }
                toast.setText("Could not retrieve security question");
                toast.show();
            }
        }
    }
}
