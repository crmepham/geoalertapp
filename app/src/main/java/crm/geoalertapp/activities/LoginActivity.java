package crm.geoalertapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
import java.net.MalformedURLException;

import crm.geoalertapp.R;
import crm.geoalertapp.crm.geoalertapp.utilities.HTTPClient;
import crm.geoalertapp.crm.geoalertapp.utilities.SharedPreferencesService;

public class LoginActivity extends AppCompatActivity {

    Toast toast;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        boolean loggedIn = SharedPreferencesService.getLoggedIn(getApplicationContext());
        SharedPreferencesService.removeKey(getApplicationContext(), "loggedIn");
        // check loggedIn here
        if(loggedIn){
            setContentView(R.layout.activity_register);
        }else{
            setContentView(R.layout.activity_login);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void navigateToRegisterActivity(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void navigateToForgotPasswordActivity(View view) {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    public void loginButton(View view) {

        EditText tv = (EditText) findViewById(R.id.inputUsername);
        String username = tv.getText().toString();
        tv = (EditText) findViewById(R.id.inputPassword);
        String password = tv.getText().toString();

        LoginTask loginTask = new LoginTask();
        loginTask.execute(username,password);
    }


    private class LoginTask extends AsyncTask<String, Integer, Integer>{

        protected Integer doInBackground(String... params) {

            Integer responseCode = 0;
            // attempt login via post request
            try {
               responseCode = new HTTPClient("http://10.0.2.2:8080/geoalertserver/api/v1/user/authenticate").Login(params[0], params[1], "POST");
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
            toast.setText("Logging in.");
            toast.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        protected void onPostExecute(Integer result) {
            if(result == 200) {
                //SharedPreferences.Editor editor = getSharedPreferences("prefs",MODE_PRIVATE).edit();
                //editor.putBoolean("loggedIn", true);
                SharedPreferencesService.setLoggedIn(getApplicationContext(), true);

                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }else{
                toast.setText("Incorrect login details: " + result);
            }
            toast.show();
        }
    }
}
