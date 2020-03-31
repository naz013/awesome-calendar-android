package com.github.naz013.awcalendar;

import hirondelle.date4j.DateTime;

abstract class Animator implements PaintInterface {

    /**
     * Set coordinates of start point for animation.
     * @param x
     * @param y
     */
    public abstract void start(int x, int y);

    /**
     * Move objects on canvas.
     * @param x
     * @param y
     */
    public abstract void animate(int x, int y);

    /**
     * Move objects to one of animator state after removing finger from view.
     * @param x
     * @param y
     */
    public abstract void finishAnimation(int x, int y);

    /**
     * Stop currently running animation.
     * @return - Return true if canceled active animation.
     */
    public abstract boolean cancelAnimation();

    /**
     * Find date time by clicked coordinates.
     * @param x
     * @param y
     * @return DateTime object.
     */
    public abstract DateTime getClicked(int x, int y);

    /**
     * Check if animator has canvas objects.
     * @return
     */
    public abstract boolean isEmpty();
}
