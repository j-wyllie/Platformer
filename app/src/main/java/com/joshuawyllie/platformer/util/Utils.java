package com.joshuawyllie.platformer.util;

public abstract class Utils {


    public static float wrap(float value, final float min, final float max) {
        if (value < min) {
            value = max;
        } else if (value > max) {
            value = min;
        }
        return value;
    }

    public static float clamp(float value, final float min, final float max) {
        if (value < min) {
            value = min;
        } else if (value > max) {
            value = max;
        }
        return value;
    }
}
