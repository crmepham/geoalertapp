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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.core.MultivaluedMap;

import crm.geoalertapp.R;
import crm.geoalertapp.crm.geoalertapp.utilities.RestClient;
import crm.geoalertapp.crm.geoalertapp.utilities.SharedPreferencesService;

public class AddContactActivity extends AppCompatActivity {

    ProgressDialog progress;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    public void addContact(View view) {
        EditText e = (EditText)findViewById(R.id.addContactUsername);
        String contactUsername = e.getText().toString();

        if(!contactUsername.equals(SharedPreferencesService.getStringProperty(getApplicationContext(), "username"))){
            AddContactTask addContactTask = new AddContactTask();
            addContactTask.execute(contactUsername);
        }else{
            if(toast == null) {
                toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
            }
            toast.setText("Cannot add yourself.");
            toast.show();
        }


    }

    private class AddContactTask extends AsyncTask<String, Integer, Integer> {

        protected Integer doInBackground(String... params) {

            Integer responseCode = 0;
            try {
                MultivaluedMap map = new MultivaluedMapImpl();
                map.add("username", SharedPreferencesService.getStringProperty(getApplicationContext(), "username"));
                map.add("contactUsername", params[0]);

                RestClient tc = new RestClient(map);
                responseCode = tc.postForResponseCode("user/add/contact");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseCode;
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(AddContactActivity.this, "", "Please wait...", true);
            progress.show();
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        protected void onPostExecute(Integer result) {
            progress.dismiss();
            if(result == 201) {
                if(toast == null) {
                    toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                }
                toast.setText("Contact request was sent.");
                toast.show();

            }else if(result == 202){
                if(toast == null) {
                    toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                }
                toast.setText("Contact was added.");
                toast.show();
            }else if(result == 401){
                if(toast == null) {
                    toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                }
                toast.setText("Could not find user.");
                toast.show();
            }else if(result == 402){
                if(toast == null) {
                    toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                }
                toast.setText("A request has already been sent to this user.");
                toast.show();
            }else if(result == 403){
                if(toast == null) {
                    toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                }
                toast.setText("That user is already a contact.");
                toast.show();
            }else{
                if(toast == null) {
                    toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                }
                toast.setText("There was an error trying to add user, please try again later.");
                toast.show();
            }

        }
    }

}
