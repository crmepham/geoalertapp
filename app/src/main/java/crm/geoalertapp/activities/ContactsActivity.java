package crm.geoalertapp.activities;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import crm.geoalertapp.R;
import crm.geoalertapp.crm.geoalertapp.utilities.BaseHelper;
import crm.geoalertapp.crm.geoalertapp.utilities.RestClient;
import crm.geoalertapp.crm.geoalertapp.utilities.SharedPreferencesService;
import crm.geoalertapp.crm.geoalertapp.utilities.ValidationHelper;

public class ContactsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {


    ProgressDialog progress;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View headerLayout = navigationView.getHeaderView(0);
        TextView tv = (TextView) headerLayout.findViewById(R.id.nav_header_username);
        tv.setText(SharedPreferencesService.getStringProperty(getApplicationContext(), "username"));

        load();
    }

    private void load(){
        if(ValidationHelper.isInternetConnected(getApplicationContext())) {
            progress = ProgressDialog.show(ContactsActivity.this, "", "Retrieving contacts...", true);
            progress.show();
            ContactsTask contactsTask = new ContactsTask();
            contactsTask.execute(SharedPreferencesService.getStringProperty(getApplicationContext(), "username"));
        }else{
            toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
            toast.setText("Could not retrieve contacts. No internet connection.");
            toast.show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(ContactsActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent intent = new Intent();
        int id = item.getItemId();

        if (id == R.id.nav_activation) {
            intent = new Intent(ContactsActivity.this, ActivationActivity.class);
        } else if (id == R.id.nav_profile) {
            intent = new Intent(ContactsActivity.this, ProfileActivity.class);
            intent.putExtra("username", SharedPreferencesService.getStringProperty(getApplicationContext(), "username"));
        } else if (id == R.id.nav_contacts) {
            intent = new Intent(ContactsActivity.this, ContactsActivity.class);
        } else if (id == R.id.nav_settings) {
            intent = new Intent(ContactsActivity.this, SettingsActivity.class);
        } else if (id == R.id.nav_logout) {
            SharedPreferencesService.clearAllProperties(getApplicationContext());
            intent = new Intent(ContactsActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        startActivity(intent);
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.contactDelete){
            // delete this contact!?!?
        }else{
            // view this users profile
        }
    }

    private class ContactsTask extends AsyncTask<String, Integer, String> {

        protected String doInBackground(String... params) {

            String jsonString = null;
            String username = params[0];
            try {
                MultivaluedMap map = new MultivaluedMapImpl();
                map.add("username", username);

                RestClient tc = new RestClient(map);
                jsonString = tc.postForString("user/retrieve/user/contacts");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        protected void onPostExecute(String result) {
            progress.dismiss();
            if(result != null) {
                try {
                    JSONArray array = new JSONArray(result);
                    LinearLayout l = (LinearLayout) findViewById(R.id.contacts_container);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        String fullName = object.getString("fullName");
                        String username = object.getString("username");
                        String status = object.getString("status");
                        int userId = object.getInt("userId");

                        // create wrapper
                        LinearLayout wrapper = new LinearLayout(getApplicationContext());
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        wrapper.setOrientation(LinearLayout.HORIZONTAL);
                        int padding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                        wrapper.setPadding(padding, padding, padding, padding);
                        wrapper.setLayoutParams(lp);
                        l.addView(wrapper);

                        // add Imageview to wrapper
                        ImageView image = new ImageView(getApplicationContext());
                        image.setBackgroundResource(R.drawable.icon_only_dark_crop);
                        lp = new LinearLayout.LayoutParams((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()));
                        lp.setMargins(0, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()), 0);
                        image.setLayoutParams(lp);
                        image.setId(userId);
                        wrapper.addView(image);

                        // add linearLayout text wrapper to main wrapper
                        LinearLayout textWrapper = new LinearLayout(getApplicationContext());
                        textWrapper.setOrientation(LinearLayout.VERTICAL);
                        textWrapper.setPadding(0, 0, 0, 0);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        textWrapper.setLayoutParams(params);
                        textWrapper.setId(userId);
                        textWrapper.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                // when this linearlayout is clicked
                                // get the username of this childs textview with an id of R.id.contactUsername
                                // start new intent and pass the username
                                String name = null;
                                for (int i = 0; i < ((ViewGroup) v).getChildCount(); ++i) {
                                    View nextChild = ((ViewGroup) v).getChildAt(i);
                                    if (nextChild.getId() == R.id.contactUsername) {
                                        TextView text = (TextView) nextChild;
                                        name = text.getText().toString();
                                    }
                                }

                                //TextView tv_id = (TextView) ((View) v.getParent()).findViewById(R.id.contact);
                                Intent intent = new Intent(ContactsActivity.this, ProfileActivity.class);
                                intent.putExtra("username", name);
                                startActivity(intent);
                            }
                        });
                        wrapper.addView(textWrapper);

                        // add username TextView to textWrapper
                        TextView fullNameText = new TextView(getApplicationContext());
                        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        fullNameText.setLayoutParams(lp);
                        fullNameText.setId(i);
                        fullNameText.setText(fullName);
                        fullNameText.setTextColor(Color.parseColor("#FFFFFF"));
                        fullNameText.setTextSize(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()));
                        textWrapper.addView(fullNameText);

                        // add status TextView to textWrapper
                        TextView statusText = new TextView(getApplicationContext());
                        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        statusText.setLayoutParams(lp);
                        statusText.setId(i);
                        statusText.setText(status);
                        statusText.setTextColor(Color.parseColor("#FFFFFF"));
                        statusText.setTextSize(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()));
                        textWrapper.addView(statusText);

                        // add status TextView to textWrapper
                        TextView usernameText = new TextView(getApplicationContext());
                        usernameText.setId(R.id.contactUsername);
                        usernameText.setText(username);
                        usernameText.setVisibility(View.INVISIBLE);
                        textWrapper.addView(usernameText);

                        // add linearLayout delete image wrapper to main wrapper
                        LinearLayout deleteWrapper = new LinearLayout(getApplicationContext());
                        deleteWrapper.setOrientation(LinearLayout.VERTICAL);
                        int paddingTop = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                        int paddingRight = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
                        deleteWrapper.setPadding(0, paddingTop, paddingRight, 0);
                        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        deleteWrapper.setLayoutParams(params);
                        wrapper.addView(deleteWrapper);

                        // add delete button to delete wrapper
                        Button deleteButton = new Button(getApplicationContext());
                        int size = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
                        params = new LinearLayout.LayoutParams(size, size);
                        params.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()), 0, 0, 0);
                        params.gravity= Gravity.RIGHT;
                        deleteButton.setLayoutParams(params);
                        deleteButton.setBackgroundResource(R.drawable.ic_delete_forever_white_24dp);
                        deleteWrapper.addView(deleteButton);

                    }
                }catch(JSONException e){
                    Log.e("", e.getMessage());
                }

            }else{
                if(toast == null) {
                    toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                }
                toast.setText("Could not retrieve contacts.");
                toast.show();
            }

        }
    }
}
