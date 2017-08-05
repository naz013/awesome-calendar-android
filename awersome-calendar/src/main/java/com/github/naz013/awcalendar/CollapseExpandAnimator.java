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

class CollapseExpandAnimator extends Animator {

    private static final long ANIMATION_DELAY = 13L;
    private static final int ANIMATION_SPEED_PIXELS = 15;

    private static final String TAG = "CollapseExpandAnimator";

    static final int STATE_EXPANDED = 3;
    static final int STATE_COLLAPSED = 4;

    private static final int ANIMATION_EXPAND = 5;
    private static final int ANIMATION_COLLAPSE = 6;

    private MonthWeekView mView;
    private MonthCell mCell;

    private int mLastX;
    private int mLastY;

    private int mAnimation;
    private int mDistance;
    private long mDelay = ANIMATION_DELAY;

    private int mState = STATE_EXPANDED;
    private OnStateListener mOnStateListener;

    private Handler mAnimationHandler = new Handler();
    private Runnable mAnimationRunnable = new Runnable() {
        @Override
        public void run() {
            mAnimationHandler.removeCallbacks(mAnimationRunnable);
            mDistance -= ANIMATION_SPEED_PIXELS;
            if (mAnimation == ANIMATION_COLLAPSE) {
                animate(mLastX, mLastY - ANIMATION_SPEED_PIXELS);
            } else {
                animate(mLastX, mLastY + ANIMATION_SPEED_PIXELS);
            }
            if (mDistance > 0) {
                mAnimationHandler.postDelayed(mAnimationRunnable, mDelay);
            } else {
                if (mAnimation == ANIMATION_COLLAPSE) {
                    setState(STATE_COLLAPSED);
                } else {
                    setState(STATE_EXPANDED);
                }
            }
        }
    };

    CollapseExpandAnimator(MonthWeekView view) {
        this.mView = view;
        setState(STATE_EXPANDED);
    }

    MonthCell getCell() {
        return mCell;
    }

    void setCell(MonthCell cell) {
        this.mCell = cell;
        Log.d(TAG, "setCell: " + cell);
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
        mAnimation = ANIMATION_EXPAND;
        Log.d(TAG, "expand: " + mDistance);
        if (mDistance > 0) {
            float delay = 1000f / (float) mDistance;
            mDelay = (int) delay;
            mAnimationHandler.postDelayed(mAnimationRunnable, mDelay);
        } else {
            setState(STATE_EXPANDED);
        }
    }

    private void collapse(int x, int y) {
        start(x, y);
        mDistance = mCell.getCollapseDistance();
        mAnimation = ANIMATION_COLLAPSE;
        Log.d(TAG, "collapse: " + mDistance);
        if (mDistance > 0) {
            float delay = 1000f / (float) mDistance;
            mDelay = (int) delay;
            mAnimationHandler.postDelayed(mAnimationRunnable, mDelay);
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
    public void cancelAnimation() {
        if (mAnimationHandler != null) {
            mAnimationHandler.removeCallbacks(mAnimationRunnable);
        }
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
