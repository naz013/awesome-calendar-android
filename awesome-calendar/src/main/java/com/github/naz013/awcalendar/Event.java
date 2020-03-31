package com.github.naz013.awcalendar;

import androidx.annotation.ColorInt;

import hirondelle.date4j.DateTime;

public class Event {

    /**
     * If color parameter == -1 library use default event color.
     */
    @ColorInt
    public int color = -1;
    public String title = "";
    public DateTime dateTime;
    public Shape shape = Shape.CIRCLE;

    public Event(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Event(DateTime dateTime, Shape shape) {
        this.dateTime = dateTime;
        this.shape = shape;
    }

    public Event(String title, DateTime dateTime) {
        this.title = title;
        this.dateTime = dateTime;
    }

    public Event(int color, String title, DateTime dateTime) {
        this.color = color;
        this.title = title;
        this.dateTime = dateTime;
    }
}
