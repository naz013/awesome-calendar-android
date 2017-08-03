package com.github.naz013.awcalendar;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

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

class WeekRow extends ContainerCell {

    private static final String TAG = "WeekRow";

    private List<DayCell> mCells = new ArrayList<>();

    private int mBottom;
    private int mLeft;
    private int mRight;
    private int mTop;

    private int bottom;
    private int left;
    private int right;
    private int top;

    WeekRow(List<DayCell> mCells) {
        this.mCells = mCells;
        for (DayCell dayCell : mCells) {
            if (dayCell.getLeft() < mLeft) mLeft = dayCell.getLeft();
            if (dayCell.getTop() < mTop) mTop = dayCell.getTop();
            if (dayCell.getRight() > mRight) mRight = dayCell.getRight();
            if (dayCell.getBottom() > mBottom) mBottom = dayCell.getBottom();
        }
    }

    public List<DayCell> getCells() {
        return mCells;
    }

    public void setCells(List<DayCell> cells) {
        this.mCells = cells;
    }

    @Override
    public void setOffsetX(int offsetX) {
        for (DayCell dayCell : mCells) {
            dayCell.setOffsetX(offsetX);
        }
        calculateDimensions();
    }

    int getDistanceToBottom() {
        int offsetY = 0;
        for (Cell cell : mCells) {
            int dist = mBottom - cell.getBottom();
            if (dist > offsetY) offsetY = dist;
        }
        return offsetY;
    }

    int getDistanceToTop() {
        int offsetY = 0;
        for (Cell cell : mCells) {
            if (cell.getTop() > offsetY) offsetY = cell.getTop();
        }
        return offsetY;
    }

    @Override
    public void setOffsetY(int offsetY) {
        for (DayCell dayCell : mCells) {
            dayCell.setOffsetY(offsetY);
        }
        calculateDimensions();
    }

    @Override
    public int getLeft() {
        return left;
    }

    @Override
    public int getTop() {
        return top;
    }

    @Override
    public int getRight() {
        return right;
    }

    @Override
    public int getBottom() {
        return bottom;
    }

    private void calculateDimensions() {
        left = 0;
        top = 0;
        right = 0;
        bottom = 0;
        for (DayCell dayCell : mCells) {
            if (dayCell.getLeft() < left) left = dayCell.getLeft();
            if (dayCell.getTop() < top) top = dayCell.getTop();
            if (dayCell.getRight() > right) right = dayCell.getRight();
            if (dayCell.getBottom() > bottom) bottom = dayCell.getBottom();
        }
    }

    @Override
    public void onDraw(Canvas canvas, Painter painter) {
        for (int i = 0; i < mCells.size(); i++) {
            mCells.get(i).onDraw(canvas, painter);
        }
    }

    @Override
    public boolean contains(DayCell cell) {
        for (DayCell dayCell : mCells) {
            if (dayCell.getDateTime().isSameDayAs(cell.getDateTime())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public DateTime get(int x, int y) {
        for (DayCell cell : mCells) {
            DateTime dateTime = cell.get(x, y);
            if (dateTime != null) return dateTime;
        }
        return null;
    }

    @Override
    public DateTime getMiddle() {
        return mCells.get(mCells.size() / 2).getDateTime();
    }

    @Override
    public DateTime getTail() {
        return mCells.get(mCells.size() - 1).getDateTime();
    }

    @Override
    public DateTime getHead() {
        return mCells.get(0).getDateTime();
    }

    @Override
    public String toString() {
        return "WeekRow: " + mCells;
    }
}
