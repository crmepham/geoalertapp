package crm.geoalertapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;

import crm.geoalertapp.R;
import crm.geoalertapp.crm.geoalertapp.utilities.BaseHelper;

public class DatePickerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void returnDate(View view) {
        Intent intent = new Intent(DatePickerActivity.this, EditProfileActivity.class);
        DatePicker d = (DatePicker) findViewById(R.id.datePicker);
        Date date = BaseHelper.getDateFromDatePicker(d);
        SimpleDateFormat s = new SimpleDateFormat("yyy-mm-dd hh:mm:ss");
        intent.putExtra("date", s.format(date));
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

}
