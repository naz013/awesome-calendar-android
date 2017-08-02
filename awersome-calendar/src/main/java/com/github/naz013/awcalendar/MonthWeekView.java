package com.github.naz013.awcalendar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
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

public class MonthWeekView extends View {

    private static final String TAG = "git.MonthWeekView";
    private static final int ROWS = 6;
    private static final int COLS = 7;

    private DateTime mRealDate;

    private int mWidth;
    private int mHeight;

    private List<DateTime> mDateTimes = new ArrayList<>();
    private int mWeek = -1;

    private Painter mPainter;

    private List<Rect> mDayCells = new ArrayList<>();
    private List<WeekRow> mWeekCells = new ArrayList<>();

    private int mLastEvent;
    private float mLastX;
    private float mLastY;

    private CollapseExpandAnimator mAnimator;

    private OnDateClickListener mDateClickListener;
    private OnDateLongClickListener mDateLongClickListener;

    private int mWidthSpecs;
    private int mHeightSpecs;

    public MonthWeekView(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public MonthWeekView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public MonthWeekView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MonthWeekView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setOnDateClickListener(OnDateClickListener listener) {
        this.mDateClickListener =  listener;
    }

    public void setOnDateLongClickListener(OnDateLongClickListener listener) {
        this.mDateLongClickListener =  listener;
    }

    public OnDateClickListener getOnDateClickListener() {
        return mDateClickListener;
    }

    public OnDateLongClickListener getOnDateLongClickListener() {
        return mDateLongClickListener;
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        Paint borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStyle(Paint.Style.STROKE);

        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.BLACK);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextSize(30f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        Paint currentPaint = new Paint();
        currentPaint.setAntiAlias(true);
        currentPaint.setColor(Color.RED);
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setTextSize(30f);
        currentPaint.setTextAlign(Paint.Align.CENTER);

        Paint bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(Color.WHITE);
        bgPaint.setStyle(Paint.Style.FILL);

        Paint eventsPaint = new Paint();
        eventsPaint.setAntiAlias(true);
        eventsPaint.setColor(Color.BLACK);
        eventsPaint.setStyle(Paint.Style.FILL);

        mPainter = new Painter(textPaint);
        mPainter.setBackgroundPaint(bgPaint);
        mPainter.setBorderPaint(borderPaint);
        mPainter.setEventPaint(eventsPaint);
        mPainter.setCurrentDayPaint(currentPaint);

        mAnimator = new CollapseExpandAnimator(this);

        calculateCalendar();
    }

    private void calculateCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        mRealDate = new DateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0, 0);
        DateTime mDate = mRealDate;

        DateTime firstDateOfMonth = mDate.getStartOfMonth();
        DateTime lastDateOfMonth = mDate.getEndOfMonth();
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
            mDateTimes.add(dateTime);
            weekdayOfFirstDate--;
        }
        for (int i = 0; i < lastDateOfMonth.getDay() - 1; i++) {
            mDateTimes.add(firstDateOfMonth.plusDays(i));
        }
        int endDayOfWeek = startDayOfWeek - 1;
        if (endDayOfWeek == 0) {
            endDayOfWeek = 7;
        }
        if (lastDateOfMonth.getWeekDay() != endDayOfWeek) {
            int i = 1;
            while (true) {
                DateTime nextDay = lastDateOfMonth.plusDays(i);
                mDateTimes.add(nextDay);
                i++;
                if (nextDay.getWeekDay() == endDayOfWeek) {
                    break;
                }
            }
        }
        int size = mDateTimes.size();
        int numOfDays = 42 - size;
        DateTime lastDateTime = mDateTimes.get(size - 1);
        for (int i = 1; i <= numOfDays; i++) {
            mDateTimes.add(lastDateTime.plusDays(i));
        }
        Log.d(TAG, "calculateCalendar: " + mRealDate + ", array " + mDateTimes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(mPainter.getBackgroundPaint().getColor());
        long start = System.currentTimeMillis();
        this.mWidth = getWidth();
        this.mHeight = getHeight();
        if (mDayCells.isEmpty()) {
            measureCells();
        }
        drawCells(canvas);
//        Log.d(TAG, "onDraw: " + (System.currentTimeMillis() - start) + ", s " + mDayCells.size());
    }

    private void drawCells(Canvas canvas) {
        for (int i = 0; i < mWeekCells.size(); i++) {
            Cell cell = mWeekCells.get(i);
            cell.onDraw(canvas, mPainter);
        }
    }

    private void measureCells() {
        int w = getWidth();
        int h = getHeight();
        if (w == 0 && h == 0) {
            return;
        }
        int cellWidth = w / COLS;
        int cellHeight = h / ROWS;
        mDayCells.clear();
        mWeekCells.clear();
        int c = 0;
        for (int i = 0; i < ROWS; i++) {
            List<DayCell> cells = new ArrayList<>();
            for (int j = 0; j < COLS; j++) {
                int top = i * cellHeight;
                int left = j * cellWidth;
                Rect tmp = new Rect(left, top, left + cellWidth, top + cellHeight);
                DayCell dayCell = new DayCell(tmp, mDateTimes.get(i * 7 + j));
                if (dayCell.getDateTime().isSameDayAs(mRealDate)) {
                    dayCell.setCurrent(true);
                    mWeek = i;
                }
                mDayCells.add(tmp);
                cells.add(dayCell);
            }
            if (mWeek == i) {
                mWeekCells.add(0, new WeekRow(cells));
            } else {
                mWeekCells.add(new WeekRow(cells));
            }
        }
        Collections.reverse(mWeekCells);
        mAnimator.setWeeks(mWeekCells);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: " + event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                processDown(event);
                return true;
            case MotionEvent.ACTION_UP:
                return processUp(event);
            case MotionEvent.ACTION_MOVE:
                processMove(event);
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void processMove(MotionEvent event) {
        mAnimator.animate((int) event.getX(), (int) event.getY());
        mLastEvent = event.getAction();
    }

    private boolean processUp(MotionEvent event) {
        if (mLastEvent == MotionEvent.ACTION_DOWN) {
            int touchedPosition = getSelectedPosition(mLastX, mLastY);
            int releasedPosition = getSelectedPosition(event.getX(), event.getY());
            if (touchedPosition != -1 && releasedPosition != -1 && touchedPosition == releasedPosition) {
                if (mDateClickListener != null) {
                    mDateClickListener.onDateClicked(mDateTimes.get(releasedPosition));
                }
                return super.performClick();
            }
        } else if (mLastEvent == MotionEvent.ACTION_MOVE) {
            if (event.getY() - mLastY > 0) {
                mAnimator.expand((int) event.getX(), (int) event.getY());
            } else {
                mAnimator.collapse((int) event.getX(), (int) event.getY());
            }
        }
        return false;
    }

    private void processDown(MotionEvent event) {
        mLastX = event.getX();
        mLastY = event.getY();
        mLastEvent = event.getAction();
        mAnimator.setStart((int) mLastX, (int) mLastY);
    }

    private int getSelectedPosition(float x, float y) {
        int position = -1;
        for (int i = 0; i < mDayCells.size(); i++) {
            if (mDayCells.get(i).contains((int) x, (int) y)) {
                position = i;
                break;
            }
        }
        return position;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidthSpecs = widthMeasureSpec;
        mHeightSpecs = heightMeasureSpec;
        if (mAnimator.getState() == CollapseExpandAnimator.STATE_EXPANDED) {
            int widthPixels = View.MeasureSpec.getSize(widthMeasureSpec);
            int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
            int height = widthPixels / 7 * 6;
            int newHeightSpec = View.MeasureSpec.makeMeasureSpec(height, widthMode);
            super.onMeasure(widthMeasureSpec, newHeightSpec);
        } else if (mAnimator.getState() == CollapseExpandAnimator.STATE_COLLAPSED) {
            int widthPixels = View.MeasureSpec.getSize(widthMeasureSpec);
            int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
            int height = widthPixels / 7;
            int newHeightSpec = View.MeasureSpec.makeMeasureSpec(height, widthMode);
            super.onMeasure(widthMeasureSpec, newHeightSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
        if (mDayCells.isEmpty()) {
            measureCells();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAnimator.onDestroy();
    }

    public interface OnDateClickListener {
        void onDateClicked(DateTime dateTime);
    }

    public interface OnDateLongClickListener {
        void onDateLongClicked(DateTime dateTime);
    }
}
