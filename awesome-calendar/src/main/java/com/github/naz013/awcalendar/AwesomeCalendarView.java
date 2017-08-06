package com.github.naz013.awcalendar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hirondelle.date4j.DateTime;

/**
 * MIT License
 *
 * Copyright (c) 2017 Nazar Suhovich
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

public class AwesomeCalendarView extends View implements PageSlideAnimator.OnStateListener,
        CollapseExpandAnimator.OnStateListener {

    private static final String TAG = "git.MonthWeekView";

    private DateTime mRealDate;

    private int mWidth;
    private int mHeight;

    private Painter mPainter;

    private int mLastEvent;
    private int mState;

    private float mLastX;
    private float mStartX;
    private float mLastY;
    private float mStartY;
    private float mLastSlide;

    private Map<DateTime, List<Event>> mEventsMap = new HashMap<>();

    private Animator mAnimator;
    private boolean mTouchAnimator;
    private CollapseExpandAnimator mColExpAnimator;
    private PageSlideAnimator mSlideAnimator;

    private OnDateClickListener mDateClickListener;
    private OnDateLongClickListener mDateLongClickListener;
    private OnCurrentMonthListener mOnCurrentMonthListener;

    public AwesomeCalendarView(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public AwesomeCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public AwesomeCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AwesomeCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setOnDateClickListener(OnDateClickListener listener) {
        this.mDateClickListener = listener;
    }

    public void setOnDateLongClickListener(OnDateLongClickListener listener) {
        this.mDateLongClickListener = listener;
    }

    public OnDateClickListener getOnDateClickListener() {
        return mDateClickListener;
    }

    public OnDateLongClickListener getOnDateLongClickListener() {
        return mDateLongClickListener;
    }

    public OnCurrentMonthListener getOnCurrentMonthListener() {
        return mOnCurrentMonthListener;
    }

    public void setOnCurrentMonthListener(OnCurrentMonthListener onCurrentMonthListener) {
        this.mOnCurrentMonthListener = onCurrentMonthListener;
    }

    public void setCollapseExpandAnimation(Animation animation) {
        mColExpAnimator.setAnimation(animation);
    }

    public void setPageSlideAnimation(Animation animation) {
        mColExpAnimator.setAnimation(animation);
    }

    public void setEvents(List<Event> events) {
        for (Event event : events) {
            normalizeDate(event);
            List<Event> evList;
            if (mEventsMap.containsKey(event.dateTime)) {
                evList = mEventsMap.get(event.dateTime);
            } else {
                evList = new ArrayList<>();
            }
            evList.add(event);
            mEventsMap.put(event.dateTime, evList);
        }
        this.calculateCalendar(0);
        this.invalidate();
    }

    private void normalizeDate(Event event) {
        event.dateTime = new DateTime(event.dateTime.getYear(), event.dateTime.getMonth(),
                event.dateTime.getDay(), 0, 0, 0, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AwesomeCalendarView,
                    defStyleAttr, defStyleRes);

        }
        Paint borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStyle(Paint.Style.STROKE);

        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.BLACK);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextSize(50f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        Paint currentPaint = new Paint();
        currentPaint.setAntiAlias(true);
        currentPaint.setColor(Color.RED);
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setTextSize(50f);
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

        mColExpAnimator = new CollapseExpandAnimator(this);
        mColExpAnimator.setOnStateListener(this);
        mSlideAnimator = new PageSlideAnimator(this);
        mSlideAnimator.setOnStateListener(this);

        mAnimator = mColExpAnimator;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        mRealDate = new DateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH), 12, 0, 0, 0);
    }

    private void calculateCalendar(int slide) {
        if (mWidth == 0 || mHeight == 0) {
            return;
        }
        if (mAnimator.isEmpty()) {
            MonthCell currentMonth = CellFactory.getMonth(mRealDate, mRealDate, mRealDate, mWidth, mHeight, 0, mEventsMap);
            MonthCell prevMonth = CellFactory.getMonth(mRealDate, shiftMonth(mRealDate, -1), mRealDate, mWidth, mHeight, -1, mEventsMap);
            MonthCell nextMonth = CellFactory.getMonth(mRealDate, shiftMonth(mRealDate, 1), mRealDate, mWidth, mHeight, 1, mEventsMap);
            mColExpAnimator.setCell(currentMonth);
            mSlideAnimator.setCells(prevMonth, currentMonth, nextMonth);
        } else {
            ContainerCell prev = mSlideAnimator.getPrevious();
            ContainerCell current = mSlideAnimator.getCurrent();
            ContainerCell next = mSlideAnimator.getNext();
            MonthCell currentMonth = mColExpAnimator.getCell();
            boolean isExpanded = mColExpAnimator.getState() == CollapseExpandAnimator.STATE_EXPANDED;
            if (isExpanded) {
                ContainerCell c = current;
                if (slide > 0) {
                    c = next;
                } else if (slide < 0) {
                    c = prev;
                }
                DateTime m = c.getMiddle();
                DateTime h = c.getHead();
                DateTime t = c.getTail();
                if (c instanceof MonthCell) {
                    h = ((MonthCell) c).getRealHead();
                    t = ((MonthCell) c).getRealTail();
                }
                prev = CellFactory.getMonth(mRealDate, shiftMonth(m, -1), m, mWidth, mHeight, -1, mEventsMap);
                next = CellFactory.getMonth(mRealDate, shiftMonth(m, 1), m, mWidth, mHeight, 1, mEventsMap);
                if (mState == CollapseExpandAnimator.STATE_COLLAPSED) {
                    current = CellFactory.getMonth(mRealDate, t, t, mWidth, mHeight, 0, mEventsMap);
                } else {
                    current = CellFactory.getMonth(mRealDate, m, h, mWidth, mHeight, 0, mEventsMap);
                }
                mColExpAnimator.setCell((MonthCell) current);
            } else {
                if (slide > 0) {
                    current = CellFactory.getWeek(mRealDate, next.getHead(), mWidth, mHeight, 0, mEventsMap);
                } else if (slide < 0) {
                    current = CellFactory.getWeek(mRealDate, prev.getHead(), mWidth, mHeight, 0, mEventsMap);
                } else {
                    current = CellFactory.getWeek(mRealDate, currentMonth.getTail(), mWidth, mHeight, 0, mEventsMap);
                }
                prev = CellFactory.getWeek(mRealDate, current.getHead().minusDays(7), mWidth, mHeight, -1, mEventsMap);
                next = CellFactory.getWeek(mRealDate, current.getHead().plusDays(7), mWidth, mHeight, 1, mEventsMap);
                MonthCell cell = CellFactory.getMonth(mRealDate, current.getTail(), current.getTail(), mWidth, mHeight, 0, mEventsMap);
                cell.setOffsetY(-cell.getCollapseDistance());
                mColExpAnimator.setCell(cell);
            }
            mSlideAnimator.setCells(prev, current, next);
        }
        if (mOnCurrentMonthListener != null && !mAnimator.isEmpty()) {
            DateTime dt = mColExpAnimator.getCell().getMiddle();
            mOnCurrentMonthListener.onMonthSelected(dt.getYear(), dt.getMonth());
        }
    }

    private DateTime shiftMonth(DateTime dateTime, int offset) {
        DateTime dt = new DateTime(dateTime.getYear(), dateTime.getMonth(),
                dateTime.getDay(), 15, 0, 0, 0);
        if (offset > 0) {
            return dt.plusDays(30);
        } else {
            return dt.minusDays(30);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(mPainter.getBackgroundPaint().getColor());
        if (this.mWidth == 0 && mHeight == 0) {
            this.mWidth = getWidth();
            this.mHeight = getHeight();
        }
        if (mAnimator.isEmpty()) {
            calculateCalendar(0);
        }
        mAnimator.onDraw(canvas, mPainter);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
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
        if (!mTouchAnimator) {
            if (Math.abs(mLastX - event.getX()) > Math.abs(mLastY - event.getY())) {
                mAnimator = mSlideAnimator;
            } else {
                mAnimator = mColExpAnimator;
            }
            mAnimator.start((int) mLastX, (int) mLastY);
            mTouchAnimator = true;
        }
        if (mAnimator instanceof CollapseExpandAnimator) {
            mLastSlide = event.getY() - mLastY;
        } else {
            mLastSlide = event.getX() - mLastX;
        }
        mLastX = event.getX();
        mLastY = event.getY();
        mAnimator.animate((int) event.getX(), (int) event.getY());
        mLastEvent = event.getAction();
    }

    private boolean processUp(MotionEvent event) {
        if (mLastEvent == MotionEvent.ACTION_DOWN) {
            DateTime touchedPosition = getSelectedPosition(mStartX, mStartY);
            DateTime releasedPosition = getSelectedPosition(event.getX(), event.getY());
            if (touchedPosition != null && releasedPosition != null && touchedPosition.isSameDayAs(releasedPosition)) {
                if (mDateClickListener != null) {
                    mDateClickListener.onDateClicked(releasedPosition);
                }
                return super.performClick();
            }
        } else if (mLastEvent == MotionEvent.ACTION_MOVE) {
            mAnimator.finishAnimation((int) event.getX() + (int) mLastSlide, (int) event.getY() + (int) mLastSlide);
        }
        mTouchAnimator = false;
        return false;
    }

    private void processDown(MotionEvent event) {
        mLastX = event.getX();
        mLastY = event.getY();
        mStartX = mLastX;
        mStartY = mLastY;
        mLastEvent = event.getAction();
    }

    private DateTime getSelectedPosition(float x, float y) {
        return mAnimator.getClicked((int) x, (int) y);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mWidth == 0 && mHeight == 0) {
            int widthPixels = View.MeasureSpec.getSize(widthMeasureSpec);
            int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
            int height = widthPixels / 7 * 6;
            int newHeightSpec = View.MeasureSpec.makeMeasureSpec(height, widthMode);
            super.onMeasure(widthMeasureSpec, newHeightSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mColExpAnimator.cancelAnimation();
    }

    @Override
    public void onStateChanged(int state) {
        Log.d(TAG, "onStateChanged: " + state);
        if (state == PageSlideAnimator.STATE_SLIDE_LEFT) {
            calculateCalendar(-1);
        } else if (state == PageSlideAnimator.STATE_SLIDE_RIGHT) {
            calculateCalendar(1);
        } else if (state == CollapseExpandAnimator.STATE_COLLAPSED) {
            calculateCalendar(0);
        } else if (state == CollapseExpandAnimator.STATE_EXPANDED) {
            calculateCalendar(0);
        }
        mState = state;
    }

    public interface OnDateClickListener {
        void onDateClicked(DateTime dateTime);
    }

    public interface OnDateLongClickListener {
        void onDateLongClicked(DateTime dateTime);
    }

    public interface OnCurrentMonthListener {
        /**
         * Returns current year and month in view.
         * @param year - year;
         * @param month - month in range 1 - 12;
         */
        void onMonthSelected(int year, int month);
    }
}
