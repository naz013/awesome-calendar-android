package com.github.naz013.awcalendar;

import android.util.Log;

import java.util.Calendar;

import hirondelle.date4j.DateTime;

public class Utils {

    public static DateTime toDateTime(long mills) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mills);
        return new DateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), 0);
    }

    static void log(String tag, String message) {
        if (AwesomeCalendarView.SHOW_LOGS) Log.d(tag, message);
    }
}
