package com.github.naz013.awcalendar;

import hirondelle.date4j.DateTime;

abstract class Cell implements PaintInterface {

    private int offsetX;
    private int offsetY;

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public abstract int getLeft();

    public abstract int getTop();

    public abstract int getRight();

    public abstract int getBottom();

    public abstract DateTime get(int x, int y);
}
