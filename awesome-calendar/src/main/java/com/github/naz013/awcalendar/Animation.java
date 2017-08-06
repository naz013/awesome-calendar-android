package com.github.naz013.awcalendar;

import android.support.annotation.IntRange;
import android.util.Log;

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
        Log.d(TAG, "getSpeed: " + leftDistance + ", " + speed);
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
