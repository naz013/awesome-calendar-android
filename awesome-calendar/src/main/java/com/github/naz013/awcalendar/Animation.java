package com.github.naz013.awcalendar;

import android.support.annotation.IntRange;

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

public class Animation {

    private static final String TAG = "Animation";

    private int leftDistance;
    private int accelerationThreshold;
    private int decelerationThreshold;

    private int currentAcceleration;

    public final void setDistance(int distance) {
        this.leftDistance = distance;
        int accelerationDistance = distance * percentOfAcceleration() / 100;
        this.decelerationThreshold = distance * percentOfDeceleration() / 100;
        this.accelerationThreshold = distance - accelerationDistance;
        this.currentAcceleration = 0;
    }

    public final int getSpeed() {
        int speed;
        if (leftDistance > decelerationThreshold) {
            speed = currentAcceleration;
            if (leftDistance > accelerationThreshold) {
                currentAcceleration += acceleration();
            }
        } else {
            speed = currentAcceleration;
            if (currentAcceleration > 1) {
                currentAcceleration -= deceleration();
                if (currentAcceleration <= 0) {
                    currentAcceleration = 1;
                }
            }
        }
        leftDistance -= speed;
        return speed;
    }

    @IntRange(from = 1, to = 10)
    public int deceleration() {
        return 5;
    }

    @IntRange(from = 1, to = 10)
    public int acceleration() {
        return 5;
    }

    @IntRange(from = 5, to = 45)
    public int percentOfAcceleration() {
        return 20;
    }

    @IntRange(from = 5, to = 45)
    public int percentOfDeceleration() {
        return 5;
    }
}
