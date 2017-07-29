package com.github.naz013.awcalendar;

import android.graphics.Rect;

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

    private List<Rect> mCells = new ArrayList<>();
    private int mOffsetY;
    public int bottom;
    public int left;
    public int right;
    public int top;

    public WeekRow(List<Rect> mCells) {
        this.mCells = mCells;
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
            rect.top += mOffsetY;
            rect.bottom += mOffsetY;
        }
        calculateDimensions();
    }

    private void calculateDimensions() {
        for (Rect rect : mCells) {
            if (rect.left < left) left = rect.left;
            if (rect.top < top) top = rect.top;
            if (rect.right > right) right = rect.right;
            if (rect.bottom > bottom) bottom = rect.bottom;
        }
    }
}
