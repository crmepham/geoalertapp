package crm.geoalertapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class ForgotPasswordActivityStep3 extends AppCompatActivity {

    private String newPassword;
    private String newPasswordConfirm;
    private ProgressDialog progress;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_step3);
    }

    public void navigateToLoginActivity(View view) {
        Intent intent = new Intent(ForgotPasswordActivityStep3.this, LoginActivity.class);
        startActivity(intent);
    }

    public void navigateToRegisterActivity(View view) {
        Intent intent = new Intent(ForgotPasswordActivityStep3.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void saveNewPassword(View view) {
        EditText et = (EditText) findViewById(R.id.forgotPasswordNewPassword);
        newPassword = et.getText().toString();
        et = (EditText) findViewById(R.id.forgotPasswordNewPasswordConfirm);
        newPasswordConfirm = et.getText().toString();

        if(newPassword != null && ValidationHelper.matchStringValues(newPassword, newPasswordConfirm)){
            SaveNewPasswordTask saveNewPasswordTask = new SaveNewPasswordTask();
            saveNewPasswordTask.execute(newPassword);
        }
    }

    private class SaveNewPasswordTask extends AsyncTask<String, Integer, Integer> {

        protected Integer doInBackground(String... params) {

            Integer responseCode = 0;
            try {
                MultivaluedMap map = new MultivaluedMapImpl();
                map.add("newPassword", StringEncrypter.encrypt(params[0]));
                map.add("email", SharedPreferencesService.getStringProperty(getApplicationContext(), "email"));
                map.add("securityQuestion", SharedPreferencesService.getStringProperty(getApplicationContext(), "securityQuestion"));
                map.add("securityAnswer", StringEncrypter.encrypt(SharedPreferencesService.getStringProperty(getApplicationContext(), "securityAnswer")));

                RestClient tc = new RestClient(map);
                responseCode = tc.postForResponseCode("user/save/new/password");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(ForgotPasswordActivityStep3.this, "", "Saving new password and logging in...", true);
            progress.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        protected void onPostExecute(Integer result) {
            progress.dismiss();
            if(result == 201) {
                Intent intent = new Intent(ForgotPasswordActivityStep3.this, ContactsActivity.class);
                startActivity(intent);
            }else{
                if(toast == null) {
                    toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                }
                toast.setText("Could not save new password.");
                toast.show();
            }

        }
    }

}
