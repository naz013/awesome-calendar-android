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

class MonthCell extends ContainerCell {

    private List<WeekRow> mWeeks = new ArrayList<>();

    private int bottom;
    private int left;
    private int right;
    private int top;

    MonthCell() {
    }

    void setWeeks(List<WeekRow> weekRowList) {
        this.mWeeks = weekRowList;
        calculateDimensions();
    }

    int getExpandDistance() {
        int distance = 0;
        for (WeekRow row : mWeeks) {
            int dist = row.getDistanceToBottom();
            if (dist > distance) distance = dist;
        }
        return distance;
    }

    int getCollapseDistance() {
        int distance = 0;
        for (WeekRow row : mWeeks) {
            int dist = row.getDistanceToTop();
            if (dist > distance) distance = dist;
        }
        return distance;
    }

    @Override
    public void setOffsetY(int offsetY) {
        for (WeekRow row : mWeeks) {
            row.setOffsetY(offsetY);
        }
        calculateDimensions();
    }

    @Override
    public void setOffsetX(int offsetX) {
        for (WeekRow row : mWeeks) {
            row.setOffsetX(offsetX);
        }
        calculateDimensions();
    }

    @Override
    public void onDraw(Canvas canvas, Painter painter) {
        for (WeekRow row : mWeeks) {
            row.onDraw(canvas, painter);
        }
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
        WeekRow zero = mWeeks.get(0);
        left = zero.getLeft();
        top = zero.getTop();
        right = zero.getRight();
        bottom = zero.getBottom();
        for (int i = 1; i < mWeeks.size(); i++) {
            WeekRow cell = mWeeks.get(i);
            if (cell.getLeft() < left) left = cell.getLeft();
            if (cell.getTop() < top) top = cell.getTop();
            if (cell.getRight() > right) right = cell.getRight();
            if (cell.getBottom() > bottom) bottom = cell.getBottom();
        }
    }

    @Override
    public boolean contains(DayCell cell) {
        for (WeekRow weekRow : mWeeks) {
            if (weekRow.contains(cell)) return true;
        }
        return false;
    }

    @Override
    public DateTime get(int x, int y) {
        for (WeekRow row : mWeeks) {
            DateTime dateTime = row.get(x, y);
            if (dateTime != null) {
                return dateTime;
            }
        }
        return null;
    }

    @Override
    public DateTime getMiddle() {
        return mWeeks.get(mWeeks.size() / 2).getMiddle();
    }

    @Override
    public DateTime getHead() {
        return mWeeks.get(0).getHead();
    }

    @Override
    public DateTime getTail() {
        return mWeeks.get(mWeeks.size() - 1).getHead();
    }

    DateTime getRealHead() {
        DateTime dt = mWeeks.get(0).getHead();
        for (int i = 1; i < mWeeks.size(); i++) {
            if (mWeeks.get(i).getHead().lt(dt)) {
                dt = mWeeks.get(i).getHead();
            }
        }
        return dt;
    }

    DateTime getRealTail() {
        DateTime dt = mWeeks.get(0).getTail();
        for (int i = 1; i < mWeeks.size(); i++) {
            if (mWeeks.get(i).getTail().gt(dt)) {
                dt = mWeeks.get(i).getTail();
            }
        }
        return dt;
    }

    @Override
    public String toString() {
        return "[MonthCell: {l - " + left +
                ", t - " + top +
                ", r - " + right +
                ", b - " + bottom +
                ", cells - " + mWeeks + "}]";
    }
}
