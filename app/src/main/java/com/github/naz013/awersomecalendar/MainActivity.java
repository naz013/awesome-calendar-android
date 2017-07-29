package com.github.naz013.awersomecalendar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.naz013.awcalendar.MonthWeekView;

import hirondelle.date4j.DateTime;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MonthWeekView calendarView = findViewById(R.id.calendar_view);
        calendarView.setOnDateClickListener(new MonthWeekView.OnDateClickListener() {
            @Override
            public void onDateClicked(DateTime dateTime) {
                Log.d(TAG, "onDateClicked: " + dateTime);
            }
        });
    }
}
