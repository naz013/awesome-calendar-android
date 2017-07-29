package com.github.naz013.awcalendar;

import android.graphics.Rect;
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

public class WeekRow {

    private static final String TAG = "WeekRow";

    private List<Rect> mCells = new ArrayList<>();
    private int mOffsetY;

    private int mBottom;
    private int mLeft;
    private int mRight;
    private int mTop;

    int bottom;
    int left;
    int right;
    int top;

    public WeekRow(List<Rect> mCells) {
        this.mCells = mCells;
        for (Rect rect : mCells) {
            if (rect.left < mLeft) mLeft = rect.left;
            if (rect.top < mTop) mTop = rect.top;
            if (rect.right > mRight) mRight = rect.right;
            if (rect.bottom > mBottom) mBottom = rect.bottom;
        }
        setOffsetY(0);
    }

    public List<Rect> getCells() {
        return mCells;
    }

    public void setCells(List<Rect> cells) {
        this.mCells = cells;
    }

    public int getOffsetY() {
        return mOffsetY;
    }

    public void setOffsetY(int offsetY) {
        this.mOffsetY = offsetY;
        for (Rect rect : mCells) {
            if (rect.top >= 0) rect.top += mOffsetY;
            if (rect.bottom <= mBottom) rect.bottom += mOffsetY;
            if (rect.top < 0) rect.top = 0;
            if (rect.bottom > mBottom) rect.bottom = mBottom;
        }
        calculateDimensions();
    }

    private void calculateDimensions() {
        left = 0;
        top = 0;
        right = 0;
        bottom = 0;
        for (Rect rect : mCells) {
            if (rect.left < left) left = rect.left;
            if (rect.top < top) top = rect.top;
            if (rect.right > right) right = rect.right;
            if (rect.bottom > bottom) bottom = rect.bottom;
        }
        Log.d(TAG + this, "calculateDimensions: l - " + left + ", t - " + top + ", r - " + right + ", b - " + bottom);
    }
}
