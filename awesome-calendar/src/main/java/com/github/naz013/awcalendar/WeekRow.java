package com.github.naz013.awcalendar;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

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

class WeekRow extends ContainerCell {

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
        DayCell cell = mCells.get(0);
        mLeft = cell.getLeft();
        mTop = cell.getTop();
        mRight = cell.getRight();
        mBottom = cell.getBottom();
        for (int i = 1; i < mCells.size(); i++) {
            DayCell dayCell = mCells.get(i);
            if (dayCell.getLeft() < mLeft) mLeft = dayCell.getLeft();
            if (dayCell.getTop() < mTop) mTop = dayCell.getTop();
            if (dayCell.getRight() > mRight) mRight = dayCell.getRight();
            if (dayCell.getBottom() > mBottom) mBottom = dayCell.getBottom();
        }
        left = mLeft;
        top = mTop;
        right = mRight;
        bottom = mBottom;
    }

    @Override
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
        DayCell cell = mCells.get(0);
        left = cell.getLeft();
        top = cell.getTop();
        right = cell.getRight();
        bottom = cell.getBottom();
        for (int i = 1; i < mCells.size(); i++) {
            DayCell dayCell = mCells.get(i);
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
        return "[WeekRow: {l - " + left +
                ", t - " + top +
                ", r - " + right +
                ", b - " + bottom +
                "}, 1Cell - " + mCells.get(0) + "]";
    }
}
