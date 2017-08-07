package com.github.naz013.awcalendar;

import android.graphics.Paint;

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

class Painter {

    private Paint borderPaint;
    private Paint backgroundPaint;
    private Paint textPaint;
    private Paint eventPaint;
    private Paint currentDayPaint;
    private Paint outPaint;
    private Paint weekdayMarkPaint;

    Painter(Paint textPaint) {
        this.textPaint = textPaint;
    }

    Paint getWeekdayMarkPaint() {
        return weekdayMarkPaint;
    }

    void setWeekdayMarkPaint(Paint weekdayMarkPaint) {
        this.weekdayMarkPaint = weekdayMarkPaint;
    }

    Paint getOutPaint() {
        return outPaint;
    }

    void setOutPaint(Paint outPaint) {
        this.outPaint = outPaint;
    }

    Paint getCurrentDayPaint() {
        return currentDayPaint;
    }

    void setCurrentDayPaint(Paint currentDayPaint) {
        this.currentDayPaint = currentDayPaint;
    }

    Paint getBorderPaint() {
        return borderPaint;
    }

    void setBorderPaint(Paint borderPaint) {
        this.borderPaint = borderPaint;
    }

    Paint getBackgroundPaint() {
        return backgroundPaint;
    }

    void setBackgroundPaint(Paint backgroundPaint) {
        this.backgroundPaint = backgroundPaint;
    }

    Paint getTextPaint() {
        return textPaint;
    }

    void setTextPaint(Paint textPaint) {
        this.textPaint = textPaint;
    }

    Paint getEventPaint() {
        return eventPaint;
    }

    void setEventPaint(Paint eventPaint) {
        this.eventPaint = eventPaint;
    }
}
