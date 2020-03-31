package com.github.naz013.awcalendar;

import android.graphics.Paint;

class Painter {

    private Paint borderPaint;
    private Paint backgroundPaint;
    private Paint textPaint;
    private Paint eventPaint;
    private Paint eventShadowPaint;
    private Paint currentDayPaint;
    private Paint outPaint;
    private Paint weekdayMarkPaint;

    Painter(Paint textPaint) {
        this.textPaint = textPaint;
    }

    Paint getEventShadowPaint() {
        return eventShadowPaint;
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
        this.eventShadowPaint = new Paint(eventPaint);
    }
}
