package com.github.naz013.awcalendar;

import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hirondelle.date4j.DateTime;

/**
 * Copyright 2017 Nazar Suhovich
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

class CellFactory {

    private static final String TAG = "CellFactory";

    private static final int ROWS = 6;
    private static final int COLS = 7;

    static MonthCell getMonth(DateTime mRealDate, DateTime dt, int w, int h, int oX) {
        List<DateTime> dateTimes = getDateTimes(dt);
        int cellWidth = w / COLS;
        int cellHeight = h / ROWS;
        int offset = calculateOffset(w, oX);
        Log.d(TAG, "getMonth: " + offset);
        List<WeekRow> weekRows = new ArrayList<>();
        for (int i = 0; i < ROWS; i++) {
            List<DayCell> cells = new ArrayList<>();
            int thisWeek = -1;
            for (int j = 0; j < COLS; j++) {
                int top = i * cellHeight;
                int left = j * cellWidth;
                Rect tmp = new Rect(left + offset, top, left + cellWidth + offset, top + cellHeight);
                DayCell dayCell = new DayCell(tmp, dateTimes.get(i * 7 + j));
                if (dayCell.getDateTime().isSameDayAs(mRealDate)) {
                    dayCell.setCurrent(true);
                    thisWeek = i;
                }
                cells.add(dayCell);
            }
            if (thisWeek == i) {
                weekRows.add(0, new WeekRow(cells));
            } else {
                weekRows.add(new WeekRow(cells));
            }
        }
        Collections.reverse(weekRows);
        MonthCell cell = new MonthCell();
        cell.setWeeks(weekRows);
        return cell;
    }

    static WeekRow getWeek(DateTime mRealDate, DateTime dt, int w, int h, int oX) {
        List<DateTime> dateTimes = getWeekDateTimes(dt);
        int cellWidth = w / COLS;
        int cellHeight = h / ROWS;
        int offset = calculateOffset(w, oX);
        List<DayCell> cells = new ArrayList<>();
        for (int j = 0; j < COLS; j++) {
            int left = j * cellWidth;
            Rect tmp = new Rect(left + offset, 0, left + cellWidth + offset, cellHeight);
            DayCell dayCell = new DayCell(tmp, dateTimes.get(j));
            if (dayCell.getDateTime().isSameDayAs(mRealDate)) {
                dayCell.setCurrent(true);
            }
            cells.add(dayCell);
        }
        return new WeekRow(cells);
    }

    private static int calculateOffset(int width, int oX) {
        if (oX > 0) {
            return width + ((int) ((float) width * 0.1f));
        } else if (oX < 0) {
            return -width - ((int) ((float) width * 0.1f));
        } else {
            return 0;
        }
    }

    private static List<DateTime> getWeekDateTimes(DateTime dt) {
        List<DateTime> dateTimes = new ArrayList<>();
        for (int i = 0; i < COLS; i++) {
            dateTimes.add(dt);
            dt = dt.plusDays(1);
        }
        return dateTimes;
    }

    private static List<DateTime> getDateTimes(DateTime dt) {
        List<DateTime> dateTimes = new ArrayList<>();
        DateTime firstDateOfMonth = dt.getStartOfMonth();
        DateTime lastDateOfMonth = dt.getEndOfMonth();
        int weekdayOfFirstDate = firstDateOfMonth.getWeekDay();
        int startDayOfWeek = 2;
        if (weekdayOfFirstDate < startDayOfWeek) {
            weekdayOfFirstDate += 7;
        }
        while (weekdayOfFirstDate > 0) {
            DateTime dateTime = firstDateOfMonth.minusDays(weekdayOfFirstDate - startDayOfWeek);
            if (!dateTime.lt(firstDateOfMonth)) {
                break;
            }
            dateTimes.add(dateTime);
            weekdayOfFirstDate--;
        }
        for (int i = 0; i < lastDateOfMonth.getDay() - 1; i++) {
            dateTimes.add(firstDateOfMonth.plusDays(i));
        }
        int endDayOfWeek = startDayOfWeek - 1;
        if (endDayOfWeek == 0) {
            endDayOfWeek = 7;
        }
        if (lastDateOfMonth.getWeekDay() != endDayOfWeek) {
            int i = 1;
            while (true) {
                DateTime nextDay = lastDateOfMonth.plusDays(i);
                dateTimes.add(nextDay);
                i++;
                if (nextDay.getWeekDay() == endDayOfWeek) {
                    break;
                }
            }
        }
        int size = dateTimes.size();
        int numOfDays = 42 - size;
        DateTime lastDateTime = dateTimes.get(size - 1);
        for (int i = 1; i <= numOfDays; i++) {
            dateTimes.add(lastDateTime.plusDays(i));
        }
        return dateTimes;
    }
}
