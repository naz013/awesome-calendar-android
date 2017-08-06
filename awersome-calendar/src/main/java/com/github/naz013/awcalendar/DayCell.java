package com.github.naz013.awcalendar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

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
