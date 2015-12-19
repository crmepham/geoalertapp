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

import java.net.MalformedURLException;

import crm.geoalertapp.R;
import crm.geoalertapp.crm.geoalertapp.utilities.HTTPClient;
import crm.geoalertapp.crm.geoalertapp.utilities.SharedPreferencesService;

public class ForgotPasswordActivity extends AppCompatActivity {

    ProgressDialog progress;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
    }

    public void navigateToLoginActivity(View view) {
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void onClickSend() {
        EditText tv = (EditText) findViewById(R.id.inputForgotPasswordEmail);
        String email = tv.getText().toString();

        if(HTTPClient.validateEmail(email)) {
            //AccountRecoverTask accountRecoverTask = new AccountRecoverTask();
            //accountRecoverTask.execute(email);

        }
    }

    private class AccountRecoverTask extends AsyncTask<String, Integer, Integer> {

        protected Integer doInBackground(String... params) {

            Integer responseCode = 0;
            try {
                responseCode = new HTTPClient("http://10.0.2.2:8080/geoalertserver/api/v1/user/account/recover").RecoverAccount(params[0], "POST");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return responseCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);;
            progress = ProgressDialog.show(ForgotPasswordActivity.this, "", "Attempting password recovery...", true);
            progress.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        protected void onPostExecute(Integer result) {
            progress.dismiss();
            if(toast == null) {
                toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
            }
            if(result == 201) {
                toast.setText("Your password has been emailed to you");
            }else{
                toast.setText("Could not retrieve password");
            }
            toast.show();
        }
    }
}
