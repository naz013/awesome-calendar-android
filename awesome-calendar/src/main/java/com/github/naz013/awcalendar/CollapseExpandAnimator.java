package com.github.naz013.awcalendar;

import android.graphics.Canvas;
import android.os.Handler;

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

class CollapseExpandAnimator extends Animator {

    private static final long ANIMATION_DELAY = 15L;

    private static final String TAG = "CollapseExpandAnimator";

    static final int STATE_EXPANDED = 3;
    static final int STATE_COLLAPSED = 4;

    private static final int ANIMATION_EXPAND = 5;
    private static final int ANIMATION_COLLAPSE = 6;

    private AwesomeCalendarView mView;
    private MonthCell mCell;

    private int mLastX;
    private int mLastY;

    private int mAnimationType;
    private Animation mAnimation;
    private int mDistance;

    private int mState = STATE_EXPANDED;
    private OnStateListener mOnStateListener;
    private boolean mIsAnimating;

    private Handler mAnimationHandler = new Handler();
    private Runnable mAnimationRunnable = new Runnable() {
        @Override
        public void run() {
            mAnimationHandler.removeCallbacks(mAnimationRunnable);
            int speed = mAnimation.getSpeed();
            mDistance -= speed;
            if (mAnimationType == ANIMATION_COLLAPSE) {
                animate(mLastX, mLastY - speed);
            } else {
                animate(mLastX, mLastY + speed);
            }
            if (mDistance > 0) {
                mIsAnimating = true;
                mAnimationHandler.postDelayed(mAnimationRunnable, ANIMATION_DELAY);
            } else {
                mIsAnimating = false;
                if (mAnimationType == ANIMATION_COLLAPSE) {
                    setState(STATE_COLLAPSED);
                } else {
                    setState(STATE_EXPANDED);
                }
            }
        }
    };

    CollapseExpandAnimator(AwesomeCalendarView view) {
        this.mView = view;
        mAnimation = new Animation();
        setState(STATE_EXPANDED);
    }

    void setAnimation(Animation animation) {
        this.mAnimation = animation;
    }

    MonthCell getCell() {
        return mCell;
    }

    void setCell(MonthCell cell) {
        this.mCell = cell;
        Utils.log(TAG, "setCell: " + cell);
    }

    public void toggle(int x, int y) {
        if (getState() == STATE_COLLAPSED) {
            expand(x, y);
        } else {
            collapse(x, y);
        }
    }

    private void expand(int x, int y) {
        start(x, y);
        mDistance = mCell.getExpandDistance();
        mAnimationType = ANIMATION_EXPAND;
        if (mDistance > 0) {
            mAnimation.setDistance(mDistance);
            mAnimationHandler.postDelayed(mAnimationRunnable, ANIMATION_DELAY);
        } else {
            setState(STATE_EXPANDED);
        }
    }

    private void collapse(int x, int y) {
        start(x, y);
        mDistance = mCell.getCollapseDistance();
        mAnimationType = ANIMATION_COLLAPSE;
        if (mDistance > 0) {
            mAnimation.setDistance(mDistance);
            mAnimationHandler.postDelayed(mAnimationRunnable, ANIMATION_DELAY);
        } else {
            setState(STATE_COLLAPSED);
        }
    }

    @Override
    public void start(int x, int y) {
        this.mLastX = x;
        this.mLastY = y;
    }

    @Override
    public void finishAnimation(int x, int y) {
        if (y - mLastY > 0) {
            expand(x, y);
        } else {
            collapse(x, y);
        }
    }

    @Override
    public void animate(int x, int y) {
        int offset = y - mLastY;
        mCell.setOffsetY(offset);
        mLastY = y;
        mView.getLayoutParams().height = mCell.getBottom() - mCell.getTop();
        mView.requestLayout();
    }

    @Override
    public boolean cancelAnimation() {
        if (mAnimationHandler != null && mIsAnimating) {
            mAnimationHandler.removeCallbacks(mAnimationRunnable);
            return true;
        }
        return false;
    }

    void setState(int state) {
        this.mState = state;
        if (mOnStateListener != null) {
            mOnStateListener.onStateChanged(state);
        }
    }

    int getState() {
        return mState;
    }

    void setOnStateListener(OnStateListener onStateListener) {
        this.mOnStateListener = onStateListener;
    }

    public OnStateListener getOnStateListener() {
        return mOnStateListener;
    }

    @Override
    public void onDraw(Canvas canvas, Painter painter) {
        mCell.onDraw(canvas, painter);
    }

    @Override
    public DateTime getClicked(int x, int y) {
        return mCell.get(x, y);
    }

    @Override
    public boolean isEmpty() {
        return mCell == null;
    }

    interface OnStateListener {
        void onStateChanged(int state);
    }
}
