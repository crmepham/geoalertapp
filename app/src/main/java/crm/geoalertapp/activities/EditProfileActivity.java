package crm.geoalertapp.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sun.jersey.core.util.MultivaluedMapImpl;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import crm.geoalertapp.R;
import crm.geoalertapp.crm.geoalertapp.utilities.BaseHelper;
import crm.geoalertapp.crm.geoalertapp.utilities.RestClient;
import crm.geoalertapp.crm.geoalertapp.utilities.SharedPreferencesService;
import crm.geoalertapp.crm.geoalertapp.utilities.ValidationHelper;

public class EditProfileActivity extends AppCompatActivity {

    Toast toast;
    String dob;
    ProgressDialog progress;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        addSpinnerEntries();

        String profile = SharedPreferencesService.getStringProperty(getApplicationContext(), "profile");
        try{
            JSONObject obj = new JSONObject(profile);
            EditText t = (EditText) findViewById(R.id.editProfileFullName);
            t.setText(obj.getString("fullName"));
            Button btn = (Button) findViewById(R.id.pickDateButton);
            btn.setText( (obj.getString("dob").equals("") || obj.getString("dob").equals("1970-01-01 01:01:01"))? "Pick a date":
            BaseHelper.formatDateString("yyy-mm-dd hh:mm:ss", "d MMM, yyyy", obj.getString("dob")));
            t = (EditText) findViewById(R.id.editProfileHeight);
            t.setText(obj.getString("height"));
            t = (EditText) findViewById(R.id.editProfileWeight);
            t.setText(obj.getString("weight"));
            t = (EditText) findViewById(R.id.editProfileClothingTop);
            t.setText(obj.getString("clothingTop"));
            t = (EditText) findViewById(R.id.editProfileClothingBottom);
            t.setText(obj.getString("clothingBottom"));
            t = (EditText) findViewById(R.id.editProfileClothingShoes);
            t.setText(obj.getString("clothingShoes"));
            t = (EditText) findViewById(R.id.editProfileNextOfKinFullName);
            t.setText(obj.getString("nextOfKinFullName"));
            t = (EditText) findViewById(R.id.editProfileNextOfKinContactNumber);
            t.setText(obj.getString("nextOfKinContactNumber"));
        }catch(JSONException e){
            Log.e("", e.getMessage());
        }

    }

    private void addSpinnerEntries() {
        spinner = (Spinner) findViewById(R.id.editProfileGender);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.edit_profile_gender, R.layout.spinner_item);
        spinner.setAdapter(adapter);
        spinner = (Spinner) findViewById(R.id.editProfileBloodType);
        adapter = ArrayAdapter.createFromResource(this, R.array.edit_profile_blood_type, R.layout.spinner_item);
        spinner.setAdapter(adapter);
        spinner = (Spinner) findViewById(R.id.editProfileNextOfKinRelationship);
        adapter = ArrayAdapter.createFromResource(this, R.array.relationship, R.layout.spinner_item);
        spinner.setAdapter(adapter);
    }

    public void updateProfile(View view) {

        List<String> info = new ArrayList<>();
        EditText t = (EditText) findViewById(R.id.editProfileFullName);
        info.add((t.getText().toString().equals(""))? " " : t.getText().toString());
        spinner = (Spinner) findViewById(R.id.editProfileGender);
        info.add((spinner.getSelectedItem().toString()));
        Button btn = (Button) findViewById(R.id.pickDateButton);
        info.add((btn.getText().toString().equals("Pick a date"))? "1970-01-01 01:01:01":BaseHelper.formatDateString("d MMM, yyyy", "yyyy-MM-dd hh:mm:ss", btn.getText().toString()));
        spinner = (Spinner) findViewById(R.id.editProfileBloodType);
        info.add((spinner.getSelectedItem().toString()));
        t = (EditText) findViewById(R.id.editProfileHeight);
        info.add((t.getText().toString().equals(""))? " " : t.getText().toString());
        t = (EditText) findViewById(R.id.editProfileWeight);
        info.add((t.getText().toString().equals(""))? " " : t.getText().toString());
        t = (EditText) findViewById(R.id.editProfileClothingTop);
        info.add((t.getText().toString().equals(""))? " " : t.getText().toString());
        t = (EditText) findViewById(R.id.editProfileClothingBottom);
        info.add((t.getText().toString().equals(""))? " " : t.getText().toString());
        t = (EditText) findViewById(R.id.editProfileClothingShoes);
        info.add((t.getText().toString().equals(""))? " " : t.getText().toString());
        t = (EditText) findViewById(R.id.editProfileNextOfKinFullName);
        info.add((t.getText().toString().equals(""))? " " : t.getText().toString());
        spinner = (Spinner) findViewById(R.id.editProfileNextOfKinRelationship);
        info.add((spinner.getSelectedItem().toString()));
        t = (EditText) findViewById(R.id.editProfileNextOfKinContactNumber);
        info.add((t.getText().toString().equals(""))? " " : t.getText().toString());
        String[] validationErrors = ValidationHelper.validateProfileInformation(info);

        if(validationErrors.length > 0) {
            String errors = "";
            for(String error : validationErrors){
                errors += error + "\n";
            }
            if(toast == null) {
                toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
            }
            toast.setText(errors);
            toast.show();

        }else if(!BaseHelper.isInternetConnected(this)){
            if(toast == null) {
                toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
            }
            toast.setText("Could not update profile at this time. No internet connection.");
            toast.show();
        }else{
            UpdateProfileTask updateProfileTask = new UpdateProfileTask();
            String paramString = BaseHelper.createStringFromList(info);
            updateProfileTask.execute(paramString);
        }
    }

    public void pickDate(View view) {
        Intent intent = new Intent(EditProfileActivity.this, DatePickerActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle b = data.getExtras();
                if (b != null) {
                    dob = b.getString("date");
                    Button btn = (Button) findViewById(R.id.pickDateButton);
                    btn.setText(BaseHelper.formatDateString("yyy-mm-dd hh:mm:ss", "d MMM, yyyy", dob));

                }
            } else if (resultCode == 0) {
                System.out.println("RESULT CANCELLED");
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
        intent.putExtra("username", SharedPreferencesService.getStringProperty(getApplicationContext(), "username"));
        setResult(Activity.RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }

    private class UpdateProfileTask extends AsyncTask<String, Integer, Integer> {

        protected Integer doInBackground(String... params) {

            int responseCode = 0;
            String input = params[0];
            try {
                MultivaluedMap map = new MultivaluedMapImpl();
                map.add("username", SharedPreferencesService.getStringProperty(getApplicationContext(), "username"));
                map.add("profileInfo", input);

                RestClient tc = new RestClient(map);
                responseCode = tc.postForResponseCode("user/update/profile/information");
            } catch (Exception e) {
                Log.e("", e.getMessage());
            }
            return responseCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(EditProfileActivity.this, "", "Updating profile. Please wait...", true);
            progress.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        protected void onPostExecute(Integer result) {
            progress.dismiss();

            if(result == 201) {
                Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                intent.putExtra("username", SharedPreferencesService.getStringProperty(getApplicationContext(), "username"));
                setResult(Activity.RESULT_OK, intent);
                finish();
            }else{
                if(toast == null) {
                    toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                }
                toast.setText("Could not update profile at this time.");
                toast.show();
            }

        }
    }
}
