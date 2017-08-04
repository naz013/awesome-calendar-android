package com.github.naz013.awcalendar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;

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

public class MonthWeekView extends View implements PageSlideAnimator.OnStateListener,
        CollapseExpandAnimator.OnStateListener {

    private static final String TAG = "git.MonthWeekView";

    private DateTime mRealDate;

    private int mWidth;
    private int mHeight;

    private Painter mPainter;

    private int mLastEvent;
    private float mLastX;
    private float mStartX;
    private float mLastY;
    private float mStartY;
    private float mLastSlide;

    private Animator mAnimator;
    private boolean mTouchAnimator;
    private CollapseExpandAnimator mColExpAnimator;
    private PageSlideAnimator mSlideAnimator;

    private OnDateClickListener mDateClickListener;
    private OnDateLongClickListener mDateLongClickListener;

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

        mColExpAnimator = new CollapseExpandAnimator(this);
        mColExpAnimator.setOnStateListener(this);
        mSlideAnimator = new PageSlideAnimator(this);
        mSlideAnimator.setOnStateListener(this);

        mAnimator = mColExpAnimator;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        mRealDate = new DateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0, 0);
    }

    private void calculateCalendar(int slide) {
        if (mWidth == 0 || mHeight == 0) {
            return;
        }
        if (mAnimator.isEmpty()) {
            MonthCell currentMonth = CellFactory.getMonth(mRealDate, mRealDate, mWidth, mHeight, 0);
            MonthCell prevMonth = CellFactory.getMonth(mRealDate, shiftMonth(mRealDate, -1), mWidth, mHeight, -1);
            MonthCell nextMonth = CellFactory.getMonth(mRealDate, shiftMonth(mRealDate, 1), mWidth, mHeight, 1);
            mColExpAnimator.setCell(currentMonth);
            mSlideAnimator.setCells(prevMonth, currentMonth, nextMonth);
        } else {
            ContainerCell prev = mSlideAnimator.getPrevious();
            ContainerCell current = mSlideAnimator.getCurrent();
            ContainerCell next = mSlideAnimator.getNext();
            MonthCell currentMonth = mColExpAnimator.getCell();
            boolean isExpanded = mColExpAnimator.getState() == CollapseExpandAnimator.STATE_EXPANDED;
            if (slide == 0) {
                if (isExpanded) {
                    prev = CellFactory.getMonth(mRealDate, shiftMonth(currentMonth.getMiddle(), -1), mWidth, mHeight, -1);
                    current = CellFactory.getMonth(mRealDate, currentMonth.getMiddle(), mWidth, mHeight, 0);
                    next = CellFactory.getMonth(mRealDate, shiftMonth(currentMonth.getMiddle(), 1), mWidth, mHeight, 1);
                } else {
                    current = CellFactory.getWeek(mRealDate, currentMonth.getTail(), mWidth, mHeight, 0);
                    prev = CellFactory.getWeek(mRealDate, current.getHead().minusDays(7), mWidth, mHeight, -1);
                    next = CellFactory.getWeek(mRealDate, current.getHead().plusDays(7), mWidth, mHeight, 1);
                }
            } else {
                if (slide > 0) {
                    next = current;
                    current = prev;
                    if (isExpanded) {
                        prev = CellFactory.getMonth(mRealDate, shiftMonth(current.getMiddle(), -1), mWidth, mHeight, -1);
                    } else {
                        prev = CellFactory.getWeek(mRealDate, current.getHead().minusDays(7), mWidth, mHeight, -1);
                    }
                } else if (slide < 0) {
                    prev = current;
                    current = next;
                    if (isExpanded) {
                        next = CellFactory.getMonth(mRealDate, shiftMonth(current.getMiddle(), 1), mWidth, mHeight, 1);
                    } else {
                        next = CellFactory.getWeek(mRealDate, current.getHead().plusDays(7), mWidth, mHeight, 1);
                    }
                }
            }
            mSlideAnimator.setCells(prev, current, next);
            if (slide != 0) {
                mColExpAnimator.setCell(CellFactory.getMonth(mRealDate, current.getMiddle(), mWidth, mHeight, 0));
            }
        }
    }

    private DateTime shiftMonth(DateTime dateTime, int offset) {
        if (offset > 0) {
            return dateTime.plusDays(30);
        } else {
            return dateTime.minusDays(30);
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
        mAnimator.cancelAnimation();
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
        }
    }

    public interface OnDateClickListener {
        void onDateClicked(DateTime dateTime);
    }

    public interface OnDateLongClickListener {
        void onDateLongClicked(DateTime dateTime);
    }
}
