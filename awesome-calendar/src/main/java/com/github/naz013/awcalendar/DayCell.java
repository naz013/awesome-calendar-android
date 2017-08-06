package com.github.naz013.awcalendar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

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

class DayCell extends Cell {

    private static final String TAG = "DayCell";
    private static final int H_DOTS = 3;
    private static final int W_DOTS = 3;

    private Rect rect;
    private DateTime dateTime;
    private List<Event> events = new ArrayList<>();

    private int mLeft;
    private int mTop;
    private int mRight;
    private int mBottom;

    private boolean isCurrent;

    DayCell(Rect rect, DateTime dateTime, List<Event> events) {
        this.rect = rect;
        this.dateTime = dateTime;
        this.events = events;
        extractInitValues();
        setOffsetY(0);
    }

    void setCurrent(boolean current) {
        isCurrent = current;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    private void extractInitValues() {
        mLeft = rect.left;
        mTop = rect.top;
        mRight = rect.right;
        mBottom = rect.bottom;
    }

    @Override
    public int getLeft() {
        return rect.left;
    }

    @Override
    public int getTop() {
        return rect.top;
    }

    @Override
    public int getRight() {
        return rect.right;
    }

    @Override
    public int getBottom() {
        return rect.bottom;
    }

    @Override
    public void setOffsetX(int offsetX) {
        rect.left += offsetX;
        rect.right += offsetX;
        normalize();
        super.setOffsetX(rect.left - mLeft);
    }

    @Override
    public void setOffsetY(int offsetY) {
        rect.top += offsetY;
        rect.bottom += offsetY;
        normalize();
        super.setOffsetY(rect.top - mTop);
    }

    private void normalize() {
        if (rect.top < 0) rect.top = 0;
        else if (rect.top > mTop) rect.top = mTop;

        if (rect.bottom > mBottom) rect.bottom = mBottom;
        else if (rect.bottom < mBottom - mTop) rect.bottom = mBottom - mTop;
    }

    DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public void onDraw(Canvas canvas, Painter painter) {
        canvas.drawRect(rect, painter.getBackgroundPaint());
        canvas.drawRect(rect, painter.getBorderPaint());
        drawEvents(canvas, painter);
        drawRectText("" + dateTime.getDay(), canvas, rect, painter);
    }

    private void drawEvents(Canvas canvas, Painter painter) {
        if (events == null || events.isEmpty()) return;
        int circleWidth = rect.width() / W_DOTS;
        int circleHeight = rect.height() / H_DOTS;
        int rectTop = rect.top;
        int rectLeft = rect.left;
        for (int i = 0; i < H_DOTS; i++) {
            for (int j = 0; j < W_DOTS; j++) {
                int index = i * 7 + j;
                if (index >= events.size()) {
                    break;
                }
                int top = i * circleHeight + rectTop;
                int left = j * circleWidth + rectLeft;
                Rect r = new Rect(left, top, left + circleWidth, top + circleHeight);
                Paint p = painter.getEventPaint();
                if (events.get(index).color != -1) {
                    p.setColor(events.get(index).color);
                }
                canvas.drawCircle(r.centerX(), r.centerY(), r.width() / 3f, p);
            }
        }
    }

    private void drawRectText(String text, Canvas canvas, Rect r, Painter painter) {
        Paint paint = painter.getTextPaint();
        if (isCurrent) paint = painter.getCurrentDayPaint();
        int numOfChars = paint.breakText(text, true, r.width(), null);
        int start = (text.length() - numOfChars) / 2;
        canvas.drawText(text, start, start + numOfChars, r.exactCenterX(), r.exactCenterY(), paint);
    }

    @Override
    public DateTime get(int x, int y) {
        if (x >= mLeft && x < mRight && y >= mTop && y < mBottom) {
            return dateTime;
        }
        return null;
    }

    @Override
    public String toString() {
        return "[DayCell: {l - " + rect.left +
                ", t - " + rect.top +
                ", r - " + rect.right +
                ", b - " + rect.bottom +
                ", iL - " + mLeft +
                ", iT - " + mTop +
                ", iR - " + mRight +
                ", iB - " + mBottom +
                ", dt - " + dateTime + "}]";
    }
}