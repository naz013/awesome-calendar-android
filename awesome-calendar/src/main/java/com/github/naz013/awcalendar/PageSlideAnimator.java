package com.github.naz013.awcalendar;

import android.graphics.Canvas;
import android.os.Handler;
import android.util.Log;

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

class PageSlideAnimator extends Animator {

    private static final int STATE_IDLE = 0;
    static final int STATE_SLIDE_RIGHT = 1;
    static final int STATE_SLIDE_LEFT = 2;

    private static final long ANIMATION_DELAY = 13L;

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
    private long mDelay = ANIMATION_DELAY;

    private int mState;
    private OnStateListener mOnStateListener;

    private Handler mAnimationHandler = new Handler();
    private Runnable mAnimationRunnable = new Runnable() {
        @Override
        public void run() {
            mAnimationHandler.removeCallbacks(mAnimationRunnable);
            int speed = mAnimation.getSpeed();
            mDistance -= speed;
            if (mAnimationType == ANIMATION_SLIDE_LEFT) {
                animate(mLastX - speed, mLastY);
            } else {
                animate(mLastX + speed, mLastY);
            }
            if (mDistance > 0) {
                mAnimationHandler.postDelayed(mAnimationRunnable, mDelay);
            } else {
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
        this.mAnimation = new Animation() {
            @Override
            public int acceleration() {
                return 2;
            }

            @Override
            public int deceleration() {
                return 5;
            }
        };
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
        Log.d(TAG, "setCells: p " + mPrevCell);
        Log.d(TAG, "setCells: c " + mCurrentCell);
        Log.d(TAG, "setCells: n " + mNextCell);
    }

    private void slide(int x, int y) {
        boolean slideLeft = mLastX > x;
        start(x, y);
        int dC = Math.abs(mCurrentCell.getLeft());
        int dP = Math.abs(mPrevCell.getLeft());
        int dN = Math.abs(mNextCell.getLeft());
        Log.d(TAG, "slide: " + slideLeft + ", " + dC + ", " + dP + ", " + dN);
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
            float delay = 1000f / (float) mDistance;
            mDelay = Math.abs((int) delay);
            mAnimation.setDistance(mDistance);
            mAnimationHandler.postDelayed(mAnimationRunnable, mDelay);
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
    public void cancelAnimation() {
        if (mAnimationHandler != null) {
            mAnimationHandler.removeCallbacks(mAnimationRunnable);
        }
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
