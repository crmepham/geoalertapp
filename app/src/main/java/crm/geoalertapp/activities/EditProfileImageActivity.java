package crm.geoalertapp.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import crm.geoalertapp.R;
import crm.geoalertapp.crm.geoalertapp.utilities.BaseHelper;
import crm.geoalertapp.crm.geoalertapp.utilities.RestClient;
import crm.geoalertapp.crm.geoalertapp.utilities.SharedPreferencesHelper;

public class EditProfileImageActivity extends AppCompatActivity {

    ProgressDialog progress;
    Toast toast;
    Button btn;
    TextView t;
    ImageView v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));

        btn = (Button) findViewById(R.id.updateProfileImageButton);
        btn.setVisibility(View.INVISIBLE);
        t = (TextView) findViewById(R.id.profile_edit_img_error);
        t.setVisibility(View.INVISIBLE);
        v = (ImageView) findViewById(R.id.profile_edit_img);
    }

    public void selectImage(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    public void updateProfileImage(View view) {
        ImageView img = (ImageView) findViewById(R.id.profile_edit_img);
        Bitmap bitmap = ((BitmapDrawable)img.getDrawable()).getBitmap();

        UpdateProfileImageTask updateProfileImageTask = new UpdateProfileImageTask(bitmap);
        updateProfileImageTask.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
                    try {
                        final Uri imageUri = imageReturnedIntent.getData();
                        //final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        //final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        final Bitmap selectedImage = BaseHelper.scaleImage(this, imageUri);

                        ImageView image = (ImageView) findViewById(R.id.profile_edit_img);
                        image.setImageBitmap(selectedImage);

                        int size = selectedImage.getByteCount();
                        int width = selectedImage.getWidth();
                        int height = selectedImage.getHeight();


                        v.setVisibility(View.VISIBLE);
                        if(size <= 200000) {
                            if(width < height) {
                                t.setVisibility(View.INVISIBLE);
                                btn.setVisibility(View.VISIBLE);
                            }else{
                                btn.setVisibility(View.INVISIBLE);
                                t.setText("This image cannot be uploaded. Only portrait images are allowed.");
                                t.setVisibility(View.VISIBLE);
                            }
                        }else{
                            btn.setVisibility(View.INVISIBLE);
                            t.setText("This image is above the maximum file size limit of 5MB and cannot be uploaded.");
                            t.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    private class UpdateProfileImageTask extends AsyncTask<Integer, Integer, Integer> {

        private final Bitmap bitmap;

        public UpdateProfileImageTask(Bitmap bitmap){
            this.bitmap = bitmap;
        }

        protected Integer doInBackground(Integer... params) {

            int responseCode = 0;
            try {
                RestClient tc = new RestClient();
                responseCode = tc.postFileForResponseCode(SharedPreferencesHelper.getStringProperty(getApplicationContext(), "username"), "user/upload/profile/image", bitmap);
            } catch (Exception e) {
                Log.e("", e.getMessage());
            }
            return responseCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);;
            progress = ProgressDialog.show(EditProfileImageActivity.this, "", "Updating profile. Please wait...", true);
            progress.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        protected void onPostExecute(Integer result) {
            progress.dismiss();

            if(result == 201) {
                Intent intent = new Intent(EditProfileImageActivity.this, ProfileActivity.class);
                intent.putExtra("username", SharedPreferencesHelper.getStringProperty(getApplicationContext(), "username"));
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
