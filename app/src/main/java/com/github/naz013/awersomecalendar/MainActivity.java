package com.github.naz013.awersomecalendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        RecyclerView rv = findViewById(R.id.list_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new SimpleAdapter());
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
