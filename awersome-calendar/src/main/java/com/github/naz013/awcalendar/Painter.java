package com.github.naz013.awcalendar;

import android.graphics.Paint;

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

class Painter {

    private Paint borderPaint;
    private Paint backgroundPaint;
    private Paint textPaint;
    private Paint eventPaint;
    private Paint currentDayPaint;

    Painter(Paint textPaint) {
        this.textPaint = textPaint;
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

    public void setTextPaint(Paint textPaint) {
        this.textPaint = textPaint;
    }

    public Paint getEventPaint() {
        return eventPaint;
    }

    void setEventPaint(Paint eventPaint) {
        this.eventPaint = eventPaint;
    }
}
