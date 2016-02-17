package crm.geoalertapp.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.util.Log;
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
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sun.jersey.core.util.MultivaluedMapImpl;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.core.MultivaluedMap;

import crm.geoalertapp.R;
import crm.geoalertapp.crm.geoalertapp.utilities.BaseHelper;
import crm.geoalertapp.crm.geoalertapp.utilities.RestClient;
import crm.geoalertapp.crm.geoalertapp.utilities.SharedPreferencesService;
import crm.geoalertapp.crm.geoalertapp.utilities.StringEncrypter;
import crm.geoalertapp.crm.geoalertapp.utilities.ValidationHelper;

public class ProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ProgressDialog progress;
    Toast toast;
    Intent intent;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        intent = getIntent();
        load();



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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



    }

    private void load(){
        if(ValidationHelper.isInternetConnected(getApplicationContext())) {
            LinearLayout l = (LinearLayout) findViewById(R.id.profile_container);
            l.setVisibility(View.VISIBLE);
            username = intent.getStringExtra("username");

            String displayLocation = SharedPreferencesService.getStringProperty(getApplicationContext(), "displayProfileMap");

            if(displayLocation.equals("Enabled")) {
                Button btn = (Button) findViewById(R.id.profileLocationButton);
                btn.setVisibility(View.VISIBLE);
            }

            if(username != null){
                ProfilePicturetask profilePicturetask = new ProfilePicturetask();
                profilePicturetask.execute(username);
            }

        }else{
            LinearLayout l = (LinearLayout) findViewById(R.id.profile_container);
            l.setVisibility(View.INVISIBLE);
            Button btn = (Button) findViewById(R.id.profileRetryButton);
            btn.setVisibility(View.VISIBLE);
            btn = (Button) findViewById(R.id.profileLocationButton);
            btn.setVisibility(View.INVISIBLE);
            toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
            toast.setText("Could not retrieve profile data. No internet connection.");
            toast.show();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
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
        getMenuInflater().inflate(R.menu.settings, menu);
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
            Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
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
            intent = new Intent(ProfileActivity.this, ActivationActivity.class);
        } else if (id == R.id.nav_profile) {
            intent = new Intent(ProfileActivity.this, ProfileActivity.class);
            intent.putExtra("username", SharedPreferencesService.getStringProperty(getApplicationContext(), "username"));
        } else if (id == R.id.nav_contacts) {
            intent = new Intent(ProfileActivity.this, ContactsActivity.class);
        } else if (id == R.id.nav_settings) {
            intent = new Intent(ProfileActivity.this, SettingsActivity.class);
        } else if (id == R.id.nav_logout) {
            SharedPreferencesService.clearAllProperties(getApplicationContext());
            intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        startActivity(intent);
        return true;
    }

    public void retryProfile(View view) {
        load();
    }

    public void editProfile(View view) {
        Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
        startActivityForResult(intent, 1);
    }

    public void viewLocation(View view) {
        Intent intent = new Intent(ProfileActivity.this, LocationActivity.class);
        startActivity(intent);
    }

    public void editProfileImage(View view) {
        if(username.equals(SharedPreferencesService.getStringProperty(getApplicationContext(), "username"))) {
            Intent intent = new Intent(ProfileActivity.this, EditProfileImageActivity.class);
            startActivityForResult(intent, 2);
        }else{
            if(toast == null) {
                toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
            }
            toast.setText("You cannot edit this user's profile image.");
            toast.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle b = data.getExtras();
                if (b != null) {
                    if(BaseHelper.isInternetConnected(this)) {
                        ProfileTask profileTask = new ProfileTask();
                        profileTask.execute(intent.getStringExtra("username"));
                    }else{
                        Button btn = (Button) findViewById(R.id.profileEditButton);
                        btn.setVisibility(View.VISIBLE);
                        if(toast == null) {
                            toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                        }
                        toast.setText("Could not update profile. No internet connection.");
                        toast.show();
                    }

                }
            } else {
                System.out.println("RESULT CANCELLED");
            }
        }else if(requestCode == 2){
            if (resultCode == Activity.RESULT_OK) {
                Bundle b = data.getExtras();
                if (b != null) {
                    if(BaseHelper.isInternetConnected(this)) {
                        ProfilePicturetask profilePicturetask = new ProfilePicturetask();
                        profilePicturetask.execute(intent.getStringExtra("username"));
                    }else{
                        if(toast == null) {
                            toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                        }
                        toast.setText("Could not update profile image. No internet connection.");
                        toast.show();
                    }

                }
            } else if (resultCode == 0) {
                System.out.println("RESULT CANCELLED");
            }
        }
    }

    private class ProfilePicturetask extends AsyncTask<String, Integer, byte[]> {

        protected byte[] doInBackground(String... params) {

            byte[] image = null;
            String username = params[0];
            try {
                MultivaluedMap map = new MultivaluedMapImpl();
                map.add("username", username);

                RestClient tc = new RestClient(map);
                image = tc.postForImage("user/retrieve/profile/image");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return image;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(ProfileActivity.this, "", "Retrieving profile...", true);
            progress.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        protected void onPostExecute(byte[] result) {
            if(result.length > 0) {

                try {
                   Bitmap bmp = BitmapFactory.decodeByteArray(result, 0, result.length);
                    if(bmp != null){
                        ImageView image = (ImageView) findViewById(R.id.profile_img);
                        image.setImageBitmap(bmp);
                    }
                } catch(Exception e) {
                    Log.e("", e.getMessage());
                }

            }else{
                ImageView image = (ImageView)findViewById(R.id.profile_img);
                image.setBackgroundResource(R.drawable.icon_only_dark_crop);
                if(toast == null) {
                    toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                }
                toast.setText("Could not retrieve profile information.");
                toast.show();
            }

            ProfileTask profileTask = new ProfileTask();
            profileTask.execute(username);

        }
    }

    private class ProfileTask extends AsyncTask<String, Integer, String> {

        protected String doInBackground(String... params) {

            String jsonString = null;
            String username = params[0];
            try {
                MultivaluedMap map = new MultivaluedMapImpl();
                map.add("username", username);

                RestClient tc = new RestClient(map);
                jsonString = tc.postForString("user/retrieve/profile/information");
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
                SharedPreferencesService.setStringProperty(getApplication(), "profile", result);

                try {
                    JSONObject profile = new JSONObject(result);
                    if(profile.length() > 0){
                        String username = SharedPreferencesService.getStringProperty(getApplicationContext(), "username");
                        if(username.equals(profile.getString("username"))) {
                            Button b = (Button) findViewById(R.id.profileEditButton);
                            b.setVisibility(View.VISIBLE);
                            b = (Button) findViewById(R.id.profileLocationButton);
                            BaseHelper.setMargins(b, 0, 0, 310, 0);
                        }

                        TextView t = (TextView)findViewById(R.id.profile_status);
                        switch(profile.getString("status")) {
                            case "Inactive":
                                t.setTextColor(Color.GRAY);
                                break;
                            case "Active":
                                t.setTextColor(Color.GREEN);
                                break;
                            case "Alert":
                                t.setTextColor(Color.RED);
                                break;
                        }
                        t.setText(profile.getString("status"));
                        t = (TextView)findViewById(R.id.profile_name);
                        t.setText(profile.getString("fullName"));
                        t = (TextView)findViewById(R.id.profile_dob);
                        t.setText(BaseHelper.formatDateString("yyyy-mm-dd hh:mm:ss", "d MMM, yyyy", profile.getString("dob")));
                        t = (TextView)findViewById(R.id.profileGender);
                        t.setText(profile.getString("gender"));
                        t = (TextView)findViewById(R.id.profileBloodType);
                        t.setText(profile.getString("bloodType"));
                        t = (TextView)findViewById(R.id.profileHeight);
                        t.setText(profile.getString("height"));
                        t = (TextView)findViewById(R.id.profileWeight);
                        t.setText(profile.getString("weight"));
                        t = (TextView)findViewById(R.id.profileClothingTop);
                        t.setText(profile.getString("clothingTop"));
                        t = (TextView)findViewById(R.id.profileClothingBottom);
                        t.setText(profile.getString("clothingBottom"));
                        t = (TextView)findViewById(R.id.profileClothingShoes);
                        t.setText(profile.getString("clothingShoes"));
                        t = (TextView)findViewById(R.id.profileNextOfKinName);
                        t.setText(profile.getString("nextOfKinFullName"));
                        t = (TextView)findViewById(R.id.profileNextOfKinRelationship);
                        t.setText(profile.getString("nextOfKinRelationship"));
                        t = (TextView)findViewById(R.id.profileNextOfKinContactNumber);
                        t.setText(profile.getString("nextOfKinContactNumber"));

                        Button btn = (Button)findViewById(R.id.profileRetryButton);
                        btn.setVisibility(View.INVISIBLE);
                    }
                } catch(Exception e) {
                    Log.e("", e.getMessage());
                }



            }else{
                if(toast == null) {
                    toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                }
                toast.setText("Could not retrieve profile information.");
                toast.show();
            }

        }
    }
}
