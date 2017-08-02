package com.github.naz013.awcalendar;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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

public class CollapseExpandAnimator extends Animator {

    private static final long ANIMATION_DELAY = 13L;
    private static final int ANIMATION_SPEED_PIXELS = 15;

    private static final String TAG = "CollapseExpandAnimator";

    public static final int STATE_EXPANDED = 2;
    public static final int STATE_COLLAPSED = 3;

    private static final int ANIMATION_EXPAND = 5;
    private static final int ANIMATION_COLLAPSE = 6;

    private MonthWeekView mView;
    private List<WeekRow> mWeeks = new ArrayList<>();

    private int mTargetIndex = 0;
    private int mLastX;
    private int mLastY;

    private int mAnimation;
    private int mDistance;
    private long mDelay = ANIMATION_DELAY;

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
            }
        }
    };

    public CollapseExpandAnimator(MonthWeekView view) {
        this.mView = view;
        setState(STATE_EXPANDED);
    }

    public void setWeeks(List<WeekRow> weekRowList) {
        this.mWeeks = weekRowList;
    }

    public void toggle(int x, int y) {
        if (getState() == STATE_COLLAPSED) {
            expand(x, y);
        } else {
            collapse(x, y);
        }
    }

    public void expand(int x, int y) {
        setStart(x, y);
        mDistance = 0;
        mAnimation = ANIMATION_EXPAND;
        for (WeekRow row : mWeeks) {
            int dist = row.getDistanceToBottom();
            if (dist > mDistance) mDistance = dist;
        }
        if (mDistance > 0) {
            float delay = 1000f / (float) mDistance;
            mDelay = (int) delay;
            mAnimationHandler.postDelayed(mAnimationRunnable, mDelay);
        }
    }

    public void collapse(int x, int y) {
        setStart(x, y);
        mDistance = 0;
        mAnimation = ANIMATION_COLLAPSE;
        for (WeekRow row : mWeeks) {
            int dist = row.getDistanceToTop();
            if (dist > mDistance) mDistance = dist;
        }
        if (mDistance > 0) {
            float delay = 1000f / (float) mDistance;
            mDelay = (int) delay;
            mAnimationHandler.postDelayed(mAnimationRunnable, mDelay);
        }
    }

    public void setStart(int x, int y) {
        this.mLastX = x;
        this.mLastY = y;
    }

    @Override
    public void animate(int x, int y) {
        long st = System.currentTimeMillis();
        setState(STATE_ANIMATING);
        int offset = y - mLastY;
        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;
        for (WeekRow row : mWeeks) {
            row.setOffsetY(offset);
            if (row.getLeft() < left) left = row.getLeft();
            if (row.getTop() < top) top = row.getTop();
            if (row.getRight() > right) right = row.getRight();
            if (row.getBottom() > bottom) bottom = row.getBottom();
        }
        mLastY = y;
        mView.getLayoutParams().height = bottom - top;
        mView.requestLayout();
        setState(STATE_IDLE);
    }

    @Override
    public void onDestroy() {
        if (mAnimationHandler != null) {
            mAnimationHandler.removeCallbacks(mAnimationRunnable);
        }
    }
}
