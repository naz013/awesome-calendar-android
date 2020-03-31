package com.github.naz013.awcalendar;

import java.util.List;

import hirondelle.date4j.DateTime;

abstract class ContainerCell extends Cell {

    public abstract List<DayCell> getCells();

    public abstract boolean contains(DayCell cell);

    public abstract DateTime getMiddle();

    public abstract DateTime getHead();

    public abstract DateTime getTail();
}
