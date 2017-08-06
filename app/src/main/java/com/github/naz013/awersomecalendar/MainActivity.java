package com.github.naz013.awersomecalendar;

import android.app.AlarmManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.naz013.awcalendar.Event;
import com.github.naz013.awcalendar.AwesomeCalendarView;
import com.github.naz013.awcalendar.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import hirondelle.date4j.DateTime;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AwesomeCalendarView calendarView = findViewById(R.id.calendar_view);
        calendarView.setOnDateClickListener(new AwesomeCalendarView.OnDateClickListener() {
            @Override
            public void onDateClicked(DateTime dateTime) {
                Log.d(TAG, "onDateClicked: " + dateTime);
            }
        });
        calendarView.setOnCurrentMonthListener(new AwesomeCalendarView.OnCurrentMonthListener() {
            @Override
            public void onMonthSelected(int year, int month) {
                Log.d(TAG, "onMonthSelected: " + year + "-" + month);
            }
        });
        calendarView.setEvents(getEvents());

        RecyclerView rv = findViewById(R.id.list_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new SimpleAdapter());
    }

    private List<Event> getEvents() {
        List<Event> events = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Random r = new Random();
        for (int i = 0; i < 500; i++) {
            int rand = r.nextInt(10);
            while (rand > 0) {
                events.add(new Event(Color.GREEN, "" + i, Utils.toDateTime(calendar.getTimeInMillis())));
                rand--;
                i++;
            }
            calendar.setTimeInMillis(calendar.getTimeInMillis() + AlarmManager.INTERVAL_DAY);
        }
        return events;
    }

    class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.Holder> {

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.tv.setText("Item " + position);
        }

        @Override
        public int getItemCount() {
            return 150;
        }

        class Holder extends RecyclerView.ViewHolder {

            TextView tv;

            Holder(View itemView) {
                super(itemView);
                tv = itemView.findViewById(R.id.text_view);
            }
        }
    }
}
