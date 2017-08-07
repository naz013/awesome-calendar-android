package com.github.naz013.awcalendar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
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

    public static boolean SHOW_LOGS = true;

    private static final String TAG = "git.MonthWeekView";
    private static final long LONG_CLICK_DURATION = 500;

    private static final int TYPE_BOTH = 0;
    private static final int TYPE_EXPANDED = 1;
    private static final int TYPE_COLLAPSED = 2;

    private DateTime mRealDate;
    private boolean mHighlightOut;
    private boolean mShowWeekdayMark;
    private int mStartDayOfWeek = 1;
    private int mType = TYPE_BOTH;
    static String[] sWeekdayTitles = new String[]{"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};

    private int mWidth;
    private int mHeight;

    private Painter mPainter;

    private int mLastEvent;
    private int mState;
    private boolean mIsLongClick;

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
    private boolean mIsAnimationCanceled;

    private OnDateClickListener mDateClickListener;
    private OnDateLongClickListener mDateLongClickListener;
    private OnCurrentMonthListener mOnCurrentMonthListener;

    private Handler mHandler = new Handler();
    private Runnable mLongClickRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(mLongClickRunnable);
            mIsLongClick = true;
            if (mDateLongClickListener != null) {
                mDateLongClickListener.onDateLongClicked(getSelectedPosition(mStartX, mStartY));
            }
            AwesomeCalendarView.super.performLongClick();
        }
    };

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

    public void setBorderColor(@ColorInt int color) {
        mPainter.getBorderPaint().setColor(color);
    }

    public void setTextColor(@ColorInt int color) {
        mPainter.getTextPaint().setColor(color);
    }

    public void setBackgroundColor(@ColorInt int color) {
        mPainter.getBackgroundPaint().setColor(color);
    }

    public void setOutTextColor(@ColorInt int color) {
        mPainter.getOutPaint().setColor(color);
    }

    public void setCurrentTextColor(@ColorInt int color) {
        mPainter.getCurrentDayPaint().setColor(color);
    }

    public void setEventColor(@ColorInt int color) {
        mPainter.getEventPaint().setColor(color);
    }

    public void setHighlightOut(boolean highlightOut) {
        this.mHighlightOut = highlightOut;
    }

    public void setCurrentDate(DateTime dateTime) {
        this.mRealDate = dateTime;
    }

    /**
     * Call this after finish setting all parameters.
     */
    public void update() {
        this.calculateCalendar(0);
        this.invalidate();
    }

    private void normalizeDate(Event event) {
        event.dateTime = new DateTime(event.dateTime.getYear(), event.dateTime.getMonth(),
                event.dateTime.getDay(), 0, 0, 0, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        int borderColor = Color.BLACK;
        int textColor = Color.BLACK;
        int weekdayTextColor = Color.BLACK;
        int outTextColor = Color.YELLOW;
        int currentTextColor = Color.RED;
        int bgColor = Color.WHITE;
        int eventColor = Color.MAGENTA;
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AwesomeCalendarView,
                    defStyleAttr, defStyleRes);
            borderColor = a.getColor(R.styleable.AwesomeCalendarView_ac_day_border_color, borderColor);
            textColor = a.getColor(R.styleable.AwesomeCalendarView_ac_day_text_color, textColor);
            outTextColor = a.getColor(R.styleable.AwesomeCalendarView_ac_day_unselected_text_color, outTextColor);
            currentTextColor = a.getColor(R.styleable.AwesomeCalendarView_ac_day_current_text_color, currentTextColor);
            bgColor = a.getColor(R.styleable.AwesomeCalendarView_ac_day_bg_color, bgColor);
            eventColor = a.getColor(R.styleable.AwesomeCalendarView_ac_event_color, eventColor);
            weekdayTextColor = a.getColor(R.styleable.AwesomeCalendarView_ac_weekday_mark_text_color, weekdayTextColor);
            mHighlightOut = a.getBoolean(R.styleable.AwesomeCalendarView_ac_highlight_out_of_bounds_days, false);
            mShowWeekdayMark = a.getBoolean(R.styleable.AwesomeCalendarView_ac_show_weekday_mark, false);
            mStartDayOfWeek = a.getInt(R.styleable.AwesomeCalendarView_ac_start_day_of_week, -1);
            mType = a.getInt(R.styleable.AwesomeCalendarView_ac_type, mType);
            if (mStartDayOfWeek == -1) {
                mStartDayOfWeek = 1;
            } else {
                mStartDayOfWeek += 1;
            }
        }
        Paint borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(borderColor);
        borderPaint.setStyle(Paint.Style.STROKE);

        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(textColor);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextSize(40f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        Paint currentPaint = new Paint(textPaint);
        currentPaint.setColor(currentTextColor);

        Paint outPaint = new Paint(textPaint);
        outPaint.setColor(outTextColor);

        Paint weekdayPaint = new Paint(textPaint);
        weekdayPaint.setColor(weekdayTextColor);
        weekdayPaint.setTextSize(20f);
        weekdayPaint.setTextAlign(Paint.Align.LEFT);

        Paint bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(bgColor);
        bgPaint.setStyle(Paint.Style.FILL);

        Paint eventsPaint = new Paint();
        eventsPaint.setAntiAlias(true);
        eventsPaint.setColor(eventColor);
        eventsPaint.setStyle(Paint.Style.FILL);

        mPainter = new Painter(textPaint);
        mPainter.setBackgroundPaint(bgPaint);
        mPainter.setBorderPaint(borderPaint);
        mPainter.setEventPaint(eventsPaint);
        mPainter.setCurrentDayPaint(currentPaint);
        mPainter.setOutPaint(outPaint);
        mPainter.setWeekdayMarkPaint(weekdayPaint);

        mColExpAnimator = new CollapseExpandAnimator(this);
        mColExpAnimator.setOnStateListener(this);
        mSlideAnimator = new PageSlideAnimator(this);
        mSlideAnimator.setOnStateListener(this);

        if (mType == TYPE_BOTH) {
            mAnimator = mColExpAnimator;
        } else {
            if (mType == TYPE_COLLAPSED) {
                mColExpAnimator.setState(CollapseExpandAnimator.STATE_COLLAPSED);
            } else {
                mColExpAnimator.setState(CollapseExpandAnimator.STATE_EXPANDED);
            }
            mAnimator = mSlideAnimator;
        }

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
            if (mType == TYPE_EXPANDED || mType == TYPE_BOTH) {
                MonthCell currentMonth = getMonthCell(mRealDate, mRealDate, 0);
                MonthCell prevMonth = getMonthCell(shiftMonth(mRealDate, -1), mRealDate, -1);
                MonthCell nextMonth = getMonthCell(shiftMonth(mRealDate, 1), mRealDate, 1);
                mColExpAnimator.setCell(currentMonth);
                mSlideAnimator.setCells(prevMonth, currentMonth, nextMonth);
            } else {
                ContainerCell current = getWeekCell(mRealDate, 0);
                ContainerCell prev = getWeekCell(current.getHead().minusDays(7), -1);
                ContainerCell next = getWeekCell(current.getHead().plusDays(7), 1);
                MonthCell cell = getMonthCell(current.getTail(), current.getTail(), 0);
                cell.setOffsetY(-cell.getCollapseDistance());
                mColExpAnimator.setCell(cell);
                mSlideAnimator.setCells(prev, current, next);
            }
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
                prev = getMonthCell(shiftMonth(m, -1), m, -1);
                next = getMonthCell(shiftMonth(m, 1), m, 1);
                if (mState == CollapseExpandAnimator.STATE_COLLAPSED) {
                    current = getMonthCell(t, t, 0);
                } else {
                    current = getMonthCell(m, h, 0);
                }
                mColExpAnimator.setCell((MonthCell) current);
            } else {
                if (slide > 0) {
                    current = getWeekCell(next.getHead(), 0);
                } else if (slide < 0) {
                    current = getWeekCell(prev.getHead(), 0);
                } else {
                    current = getWeekCell(currentMonth.getTail(), 0);
                }
                prev = getWeekCell(current.getHead().minusDays(7), -1);
                next = getWeekCell(current.getHead().plusDays(7), 1);
                MonthCell cell = getMonthCell(current.getTail(), current.getTail(), 0);
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

    private WeekRow getWeekCell(DateTime dt, int oX) {
        int w = mWidth;
        int h = mHeight;
        if (mType != TYPE_COLLAPSED) {
            h = mHeight / CellFactory.ROWS;
        }
        return CellFactory.getWeek(mRealDate, dt, w, h, oX, mEventsMap, mHighlightOut, mShowWeekdayMark);
    }

    private MonthCell getMonthCell(DateTime dt, DateTime anchor, int oX) {
        int w = mWidth;
        int h = mHeight;
        if (mType != TYPE_COLLAPSED) {
            h = mHeight / CellFactory.ROWS;
        }
        return CellFactory.getMonth(mRealDate, dt, anchor, w, h, oX, mEventsMap, mHighlightOut,
                mStartDayOfWeek, mShowWeekdayMark);
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
        mHandler.removeCallbacks(mLongClickRunnable);
        if (!mTouchAnimator) {
            if (!mIsAnimationCanceled) {
                if (Math.abs(mLastX - event.getX()) > Math.abs(mLastY - event.getY())) {
                    mAnimator = mSlideAnimator;
                } else if (mType != TYPE_BOTH) {
                    mAnimator = mSlideAnimator;
                } else {
                    mAnimator = mColExpAnimator;
                }
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
        mHandler.removeCallbacks(mLongClickRunnable);
        if (mLastEvent == MotionEvent.ACTION_DOWN) {
            if (mIsAnimationCanceled) {
                mAnimator.finishAnimation((int) event.getX() + (int) mLastSlide, (int) event.getY() + (int) mLastSlide);
            }
            if (mIsLongClick) {
                return true;
            }
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
        return true;
    }

    private void processDown(MotionEvent event) {
        mIsAnimationCanceled = mAnimator.cancelAnimation();
        mLastX = event.getX();
        mLastY = event.getY();
        mStartX = mLastX;
        mStartY = mLastY;
        mLastEvent = event.getAction();
        mIsLongClick = false;
        mHandler.postDelayed(mLongClickRunnable, LONG_CLICK_DURATION);
    }

    private DateTime getSelectedPosition(float x, float y) {
        return mAnimator.getClicked((int) x, (int) y);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mWidth == 0 && mHeight == 0) {
            int widthPixels = View.MeasureSpec.getSize(widthMeasureSpec);
            int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
            int height = widthPixels / 7;
            if (mType == TYPE_EXPANDED || mType == TYPE_BOTH) {
                height *= 6;
            }
            int newHeightSpec = View.MeasureSpec.makeMeasureSpec(height, widthMode);
            super.onMeasure(widthMeasureSpec, newHeightSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAnimator.cancelAnimation();
        if (mHandler != null) {
            mHandler.removeCallbacks(mLongClickRunnable);
        }
    }

    @Override
    public void onStateChanged(int state) {
        Utils.log(TAG, "onStateChanged: " + state);
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
