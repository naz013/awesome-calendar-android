package com.github.naz013.awcalendar;

import android.graphics.Canvas;
import android.os.Handler;
import android.util.Log;

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

class PageSlideAnimator extends Animator {

    private static final int STATE_IDLE = 0;
    static final int STATE_SLIDE_RIGHT = 1;
    static final int STATE_SLIDE_LEFT = 2;

    private static final long ANIMATION_DELAY = 20L;

    private static final int ANIMATION_SLIDE_LEFT = 3;
    private static final int ANIMATION_SLIDE_RIGHT = 4;

    private static final String TAG = "PageSlideAnimator";

    private ContainerCell mPrevCell;
    private ContainerCell mCurrentCell;
    private ContainerCell mNextCell;

    private int mLastX;
    private int mLastY;
    private AwesomeCalendarView mView;

    private int mDistance;
    private Animation mAnimation;
    private int mAnimationType;
    private boolean mIsAnimating;

    private int mState;
    private OnStateListener mOnStateListener;

    private Handler mAnimationHandler = new Handler();
    private Runnable mAnimationRunnable = new Runnable() {
        @Override
        public void run() {
            mAnimationHandler.removeCallbacks(mAnimationRunnable);
            int speed = mAnimation.getSpeed();
            mDistance -= speed;
            if (mDistance < 0) {
                speed = speed + mDistance;
            }
            if (mAnimationType == ANIMATION_SLIDE_LEFT) {
                animate(mLastX - speed, mLastY);
            } else {
                animate(mLastX + speed, mLastY);
            }
            if (mDistance > 0) {
                mIsAnimating = true;
                mAnimationHandler.postDelayed(mAnimationRunnable, ANIMATION_DELAY);
            } else {
                mIsAnimating = false;
                if (mPrevCell.getLeft() == 0) {
                    setState(STATE_SLIDE_LEFT);
                } else if (mNextCell.getLeft() == 0) {
                    setState(STATE_SLIDE_RIGHT);
                } else {
                    setState(STATE_IDLE);
                }
            }
        }
    };

    PageSlideAnimator(AwesomeCalendarView awesomeCalendarView) {
        this.mView = awesomeCalendarView;
        this.mAnimation = new Animation();
        setState(STATE_IDLE);
    }

    void setAnimation(Animation animation) {
        this.mAnimation = animation;
    }

    ContainerCell getCurrent() {
        return mCurrentCell;
    }

    ContainerCell getPrevious() {
        return mPrevCell;
    }

    ContainerCell getNext() {
        return mNextCell;
    }

    void setCells(ContainerCell prevCell, ContainerCell currentCell, ContainerCell nextCell) {
        this.mPrevCell = prevCell;
        this.mCurrentCell = currentCell;
        this.mNextCell = nextCell;
        Utils.log(TAG, "setCells: p " + mPrevCell);
        Utils.log(TAG, "setCells: c " + mCurrentCell);
        Utils.log(TAG, "setCells: n " + mNextCell);
    }

    private void slide(int x, int y) {
        boolean slideLeft = mLastX > x;
        start(x, y);
        int dC = Math.abs(mCurrentCell.getLeft());
        int dP = Math.abs(mPrevCell.getLeft());
        int dN = Math.abs(mNextCell.getLeft());
        Utils.log(TAG, "slide: " + slideLeft + ", " + dC + ", " + dP + ", " + dN);
        if (slideLeft) {
            if (dN < dC || mCurrentCell.getLeft() < 0) {
                mDistance = mNextCell.getLeft();
            } else {
                mDistance = mCurrentCell.getLeft();
            }
            mAnimationType = ANIMATION_SLIDE_LEFT;
        } else {
            if (dP < dC || mCurrentCell.getLeft() > 0) {
                mDistance = mPrevCell.getLeft();
            } else {
                mDistance = mCurrentCell.getLeft();
            }
            mAnimationType = ANIMATION_SLIDE_RIGHT;
        }
        mDistance = Math.abs(mDistance);
        if (mDistance > 0) {
            if (mDistance % 30 != 0) {
                int off = (mDistance % 30);
                mDistance -= off;
                if (mAnimationType == ANIMATION_SLIDE_LEFT) {
                    animate(mLastX - off, mLastY);
                } else {
                    animate(mLastX + off, mLastY);
                }
            }
            mAnimation.setDistance(mDistance);
            mAnimationHandler.postDelayed(mAnimationRunnable, ANIMATION_DELAY);
        }
    }

    @Override
    public void start(int x, int y) {
        this.mLastX = x;
        this.mLastY = y;
    }

    @Override
    public void finishAnimation(int x, int y) {
        slide(x, y);
    }

    @Override
    public void animate(int x, int y) {
        int offset = x - mLastX;
        mPrevCell.setOffsetX(offset);
        mCurrentCell.setOffsetX(offset);
        mNextCell.setOffsetX(offset);
        mLastX = x;
        mView.invalidate();
    }

    @Override
    public boolean cancelAnimation() {
        if (mAnimationHandler != null && mIsAnimating) {
            mAnimationHandler.removeCallbacks(mAnimationRunnable);
            return true;
        }
        return false;
    }

    private void setState(int state) {
        this.mState = state;
        if (mOnStateListener != null) {
            mOnStateListener.onStateChanged(state);
        }
    }

    public int getState() {
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
        mPrevCell.onDraw(canvas, painter);
        mNextCell.onDraw(canvas, painter);
        mCurrentCell.onDraw(canvas, painter);
    }

    @Override
    public DateTime getClicked(int x, int y) {
        return mCurrentCell.get(x, y);
    }

    @Override
    public boolean isEmpty() {
        return mPrevCell == null || mCurrentCell == null || mNextCell == null;
    }

    public interface OnStateListener {
        void onStateChanged(int state);
    }
}
