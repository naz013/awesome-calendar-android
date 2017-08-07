[![](https://jitpack.io/v/naz013/awesome-calendar-android.svg)](https://jitpack.io/#naz013/awesome-calendar-android)

# awesome-calendar-android
Powerful calendar widget for android. Fully written on canvas.
Main idea: write light-weight widget without using additional Android widgets (ViewPager, RecyclerView, TextView, etc.).

<img src="https://raw.githubusercontent.com/naz013/awesome-calendar-android/master/res/screenshot.png" width="200" alt="Screenshot">![](https://media.giphy.com/media/hmeYhSgHl7h8k/200w_d.gif)

## Installation
Download latest version with Gradle:
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compile 'com.github.naz013:awesome-calendar-android:1.0.1'
}
```
## Usage
Via XML
```xml
<com.github.naz013.awcalendar.AwesomeCalendarView
        android:id="@+id/calendar_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:ac_day_bg_color="@color/colorPrimary"
        app:ac_day_border_color="#ffffff"
        app:ac_day_current_text_color="#ff0000"
        app:ac_day_text_color="#ffffff"
        app:ac_day_unselected_text_color="#40ffffff"
        app:ac_event_color="#acacac"
        app:ac_highlight_out_of_bounds_days="true"
        app:ac_show_weekday_mark="true"
        app:ac_start_day_of_week="sunday"
        app:ac_type="both"
        app:ac_weekday_mark_text_color="#77ff55"
        app:ac_weekday_titles="@array/weekday_titles" />
```

Also you can customize view in code:

Border color (ac_day_border_color):
```java
calendarView.setBorderColor(@ColorInt int color)
```

Background color (ac_day_bg_color):
```java
calendarView.setBackgroundColor(@ColorInt int color)
```

Day title text color (ac_day_text_color):
```java
calendarView.setTextColor(@ColorInt int color)
```

Current day title text color (ac_day_current_text_color):
```java
calendarView.setCurrentTextColor(@ColorInt int color)
```

Text color for days from another month (ac_day_unselected_text_color):
```java
calendarView.setOutTextColor(@ColorInt int color)
```

To enable different text color for another month use (ac_highlight_out_of_bounds_days):
```java
calendarView.setHighlightOut(boolean highlightOut)
```

Event mark color (ac_event_color):
```java
calendarView.setEventColor(@ColorInt int color)
```

Set start day of week (ac_start_day_of_week):
```java
calendarView.setStartDayOfWeek(@IntRange(from = 1, to = 7) int startDayOfWeek)
```

To enable weekday mark in day cell (ac_show_weekday_mark):
```java
calendarView.setShowWeekdayMark(boolean showWeekdayMark)
```

Weekday mark text color (ac_weekday_mark_text_color):
```java
calendarView.setWeekdayMarkColor(@ColorInt int color)
```

Set weekday mark title text (ac_weekday_titles), array or list of strings (must have 7 items):
```java
calendarView.setWeekdayTitles(List<String> weekdayTitles)
calendarView.setWeekdayTitles(String[] weekdayTitles)
```

Set type of view (ac_type), only via XML:
 - expanded - shows full month;
 - collapsed - shows only one week row;
 - both - shows expanded and collapsed states, depends on user interaction;
 
When you finish setting parameters in code call:
-----
```java
calendarView.update();
```

To set events mark to each day cell use (Each Event object has own color parameter):
```java
calendarView.setEvents(List<Event> events)
```

Also you can set listener for color picker:
```java
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
        calendarView.setOnDateLongClickListener(new AwesomeCalendarView.OnDateLongClickListener() {
            @Override
            public void onDateLongClicked(DateTime dateTime) {
                Log.d(TAG, "onDateLongClicked: " + dateTime);
            }
        });
```

## Inspiration
Caldroid - https://github.com/roomorama/Caldroid

## Contribution
Library is in development, so feel free to contribute to this project.

License
-------
    MIT License

    Copyright (c) 2017 Nazar Suhovich

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
