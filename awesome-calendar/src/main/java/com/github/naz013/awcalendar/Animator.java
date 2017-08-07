package com.github.naz013.awcalendar;

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
