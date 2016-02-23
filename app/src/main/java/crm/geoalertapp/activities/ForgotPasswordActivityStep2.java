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
import android.widget.TextView;
import android.widget.Toast;

import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.core.MultivaluedMap;

import crm.geoalertapp.R;
import crm.geoalertapp.crm.geoalertapp.utilities.RestClient;
import crm.geoalertapp.crm.geoalertapp.utilities.SharedPreferencesHelper;
import crm.geoalertapp.crm.geoalertapp.utilities.StringEncrypter;

public class ForgotPasswordActivityStep2 extends AppCompatActivity {

    String answer;
    Toast toast;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_step2);

        TextView tv = (TextView)findViewById(R.id.forgotPasswordQuestion);
        tv.setText(SharedPreferencesHelper.getStringProperty(getApplicationContext(), "securityQuestion"));
    }

    public void navigateToLoginActivity(View view) {
        Intent intent = new Intent(ForgotPasswordActivityStep2.this, LoginActivity.class);
        startActivity(intent);
    }

    public void navigateToRegisterActivity(View view) {
        Intent intent = new Intent(ForgotPasswordActivityStep2.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void confirmAnswer(View view) {
        EditText et = (EditText) findViewById(R.id.forgotPasswordAnswer);
        answer = et.getText().toString();

        if(answer != null) {
            ConfirmAnswerTask confirmAnswerTask = new ConfirmAnswerTask();
            confirmAnswerTask.execute(answer);

        }else{
            if(toast == null) {
                toast = Toast.makeText(getApplicationContext(), "Answer must not be empty.", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private class ConfirmAnswerTask extends AsyncTask<String, Integer, Integer> {

        protected Integer doInBackground(String... params) {

            Integer responseCode = 0;
            try {
                MultivaluedMap map = new MultivaluedMapImpl();
                map.add("answer", StringEncrypter.encrypt(params[0]));
                map.add("question", SharedPreferencesHelper.getStringProperty(getApplicationContext(), "securityQuestion"));
                map.add("email", SharedPreferencesHelper.getStringProperty(getApplicationContext(), "email"));

                RestClient tc = new RestClient(map);
                responseCode = tc.postForResponseCode("user/confirm/security/answer");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(ForgotPasswordActivityStep2.this, "", "Validating answer...", true);
            progress.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        protected void onPostExecute(Integer result) {
            progress.dismiss();
            if(result == 201) {
                SharedPreferencesHelper.setStringProperty(getApplicationContext(), "securityAnswer", answer);
                Intent intent = new Intent(ForgotPasswordActivityStep2.this, ForgotPasswordActivityStep3.class);
                startActivity(intent);
            }else{
                if(toast == null) {
                    toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                }
                toast.setText("Incorrect answer, please try again.");
                toast.show();
            }

        }
    }
}
