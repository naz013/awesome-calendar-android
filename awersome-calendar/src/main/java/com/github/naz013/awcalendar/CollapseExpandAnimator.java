package com.github.naz013.awcalendar;

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

    private static final String TAG = "CollapseExpandAnimator";

    public static final int STATE_EXPANDED = 2;
    public static final int STATE_COLLAPSED = 3;

    private MonthWeekView mView;
    private List<WeekRow> mWeeks = new ArrayList<>();

    private int mTargetIndex = 0;
    private int mLastX;
    private int mLastY;

    public CollapseExpandAnimator(MonthWeekView view) {
        this.mView = view;
        setState(STATE_EXPANDED);
    }

    public void setWeeks(List<WeekRow> weekRowList) {
        this.mWeeks = weekRowList;
        Log.d(TAG, "setWeeks: " + weekRowList);
    }

    public void toggle(int x, int y) {
        if (getState() == STATE_COLLAPSED) {
            expand(x, y);
        } else {
            collapse(x, y);
        }
    }

    public void expand(int x, int y) {

    }

    public void collapse(int x, int y) {

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
        Log.d(TAG, "animate: offset " + offset);
        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;
        for (WeekRow row : mWeeks) {
            row.setOffsetY(offset);
            if (row.left < left) left = row.left;
            if (row.top < top) top = row.top;
            if (row.right > right) right = row.right;
            if (row.bottom > bottom) bottom = row.bottom;
        }
        mLastY = y;
        int height = bottom - top;
        Log.d(TAG, "animate: newHeight " + height);
        mView.getLayoutParams().height = height;
        mView.requestLayout();
        setState(STATE_IDLE);
    }
}
