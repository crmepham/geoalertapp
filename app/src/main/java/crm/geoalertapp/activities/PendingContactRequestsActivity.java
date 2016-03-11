package crm.geoalertapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sun.jersey.core.util.MultivaluedMapImpl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.core.MultivaluedMap;

import crm.geoalertapp.R;
import crm.geoalertapp.crm.geoalertapp.utilities.RestClient;
import crm.geoalertapp.crm.geoalertapp.utilities.SharedPreferencesHelper;

public class PendingContactRequestsActivity extends AppCompatActivity {

    JSONArray contactRequests;
    Intent intent;
    Toast toast;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_contact_requests);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));

        intent = getIntent();
        try {
            contactRequests = new JSONArray(intent.getStringExtra("contactRequests"));
        }catch(Exception e){
            Log.d("", e.getMessage());
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        loadList();
    }

    private void loadList(){
        try{
            final LinearLayout l = (LinearLayout) findViewById(R.id.contact_requests_container);
            for (int i = 0; i < contactRequests.length(); i++) {
                JSONObject object = contactRequests.getJSONObject(i);

                final String username = object.getString("username");
                final String fullName = (object.isNull("fullName") || object.getString("fullName").trim().equals("")) ? username : object.getString("fullName");
                final int userId = object.getInt("userId");

                // create wrapper
                final LinearLayout wrapper = new LinearLayout(getApplicationContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                wrapper.setOrientation(LinearLayout.HORIZONTAL);
                int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                wrapper.setPadding(padding, padding, padding, padding);
                wrapper.setLayoutParams(lp);
                l.addView(wrapper);

                // add username TextView to textWrapper
                TextView fullNameText = new TextView(getApplicationContext());
                lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13, getResources().getDisplayMetrics()), 0, 0);
                fullNameText.setLayoutParams(lp);
                fullNameText.setText(fullName);
                fullNameText.setTextColor(Color.parseColor("#FFFFFF"));
                fullNameText.setTextSize(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()));
                wrapper.addView(fullNameText);

                // add delete button to delete wrapper
                final Button deleteButton = new Button(getApplicationContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()), 0, 0, 0);
                params.gravity= Gravity.RIGHT;
                deleteButton.setLayoutParams(params);
                deleteButton.setText("Decline");
                deleteButton.setBackgroundColor(Color.RED);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // delete this contact relationship in db
                        PendingContactTask pendingContactTask = new PendingContactTask(l, wrapper, "decline");
                        pendingContactTask.execute(userId);
                    }
                });
                wrapper.addView(deleteButton);

                // add delete button to delete wrapper
                final Button acceptButton = new Button(getApplicationContext());
                params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()), 0, 0, 0);
                params.gravity= Gravity.RIGHT;
                acceptButton.setLayoutParams(params);
                acceptButton.setText("Accept");
                acceptButton.setBackgroundColor(Color.GREEN);
                acceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // set this contact relationship to accepted=1 in db
                        PendingContactTask pendingContactTask = new PendingContactTask(l, wrapper, "accept");
                        pendingContactTask.execute(userId);
                    }
                });
                wrapper.addView(acceptButton);

            }
        }catch(JSONException e){
            Log.d("", e.getMessage());
        }
    }

    private class PendingContactTask extends AsyncTask<Integer, Integer, Integer> {

        LinearLayout wrapper;
        LinearLayout l;
        String method;
        public PendingContactTask(LinearLayout l, LinearLayout wrapper, String method) {
            this.l = l;
            this.wrapper = wrapper;
            this.method = method;
        }

        protected Integer doInBackground(Integer... params) {

            Integer responseCode = 0;
            try {
                MultivaluedMap map = new MultivaluedMapImpl();
                map.add("username", SharedPreferencesHelper.getStringProperty(getApplicationContext(), "username"));
                map.add("userId", params[0].toString());
                RestClient tc = new RestClient(map);

                switch(method) {
                    case "decline":
                        responseCode = tc.postForResponseCode("user/decline/contact/request");
                        break;
                    case "accept":
                        responseCode = tc.postForResponseCode("user/accept/contact/request");
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseCode;
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(PendingContactRequestsActivity.this, "", "Please wait...", true);
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
                int count = l.getChildCount();
                l.removeView(wrapper);
                if(count == 1){
                    Intent intent = new Intent(PendingContactRequestsActivity.this, ContactsActivity.class);
                    startActivity(intent);
                }
            }else if(result == 401){
                if(toast == null) {
                    toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                }
                toast.setText("Could not complete request at this time.");
                toast.show();
            }else{
                if(toast == null) {
                    toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                }
                toast.setText("There was a server error, please try again later.");
                toast.show();
            }

        }
    }
}
