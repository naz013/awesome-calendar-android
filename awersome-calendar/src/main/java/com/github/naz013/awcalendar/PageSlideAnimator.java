package com.github.naz013.awcalendar;

import android.graphics.Canvas;
import android.os.Handler;

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
    private static final int ANIMATION_SPEED_PIXELS = 15;

    private static final String TAG = "PageSlideAnimator";

    private static final int ANIMATION_RIGHT = 5;
    private static final int ANIMATION_LEFT = 6;

    private ContainerCell mPrevCell;
    private ContainerCell mCurrentCell;
    private ContainerCell mNextCell;

    private int mLastX;
    private int mLastY;
    private MonthWeekView mView;

    private int mAnimation;
    private int mDistance;
    private long mDelay = ANIMATION_DELAY;

    private int mState;
    private OnStateListener mOnStateListener;

    private Handler mAnimationHandler = new Handler();
    private Runnable mAnimationRunnable = new Runnable() {
        @Override
        public void run() {
            mAnimationHandler.removeCallbacks(mAnimationRunnable);
            mDistance -= ANIMATION_SPEED_PIXELS;
            if (mAnimation == ANIMATION_LEFT) {
                animate(mLastX, mLastY - ANIMATION_SPEED_PIXELS);
            } else {
                animate(mLastX, mLastY + ANIMATION_SPEED_PIXELS);
            }
            if (mDistance > 0) {
                mAnimationHandler.postDelayed(mAnimationRunnable, mDelay);
            } else {
                if (mAnimation == ANIMATION_LEFT) {
                    setState(STATE_SLIDE_LEFT);
                } else {
                    setState(STATE_SLIDE_RIGHT);
                }
            }
        }
    };

    PageSlideAnimator(MonthWeekView monthWeekView) {
        this.mView = monthWeekView;
        setState(STATE_IDLE);
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
    }

//    private void expand(int x, int y) {
//        start(x, y);
//        mDistance = mCell.getExpandDistance();
//        mAnimation = ANIMATION_RIGHT;
//        if (mDistance > 0) {
//            float delay = 1000f / (float) mDistance;
//            mDelay = (int) delay;
//            mAnimationHandler.postDelayed(mAnimationRunnable, mDelay);
//        }
//    }
//
//    private void collapse(int x, int y) {
//        start(x, y);
//        mDistance = mCell.getCollapseDistance();
//        mAnimation = ANIMATION_LEFT;
//        if (mDistance > 0) {
//            float delay = 1000f / (float) mDistance;
//            mDelay = (int) delay;
//            mAnimationHandler.postDelayed(mAnimationRunnable, mDelay);
//        }
//    }

    @Override
    public void start(int x, int y) {
        this.mLastX = x;
        this.mLastY = y;
    }

    @Override
    public void finishAnimation(int x, int y) {
//        if (y - mLastY > 0) {
//            expand(x, y);
//        } else {
//            collapse(x, y);
//        }
    }

    @Override
    public void animate(int x, int y) {
        long st = System.currentTimeMillis();
        int offset = x - mLastX;
        mPrevCell.setOffsetX(offset);
        mCurrentCell.setOffsetX(offset);
        mNextCell.setOffsetX(offset);
        mLastX = x;
        mView.invalidate();
    }

    @Override
    public void onDestroy() {
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
