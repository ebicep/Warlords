package com.ebicep.warlords.util.java;

public class MathUtils {

    public static float clamp(float value, float min, float max) {
        return value < min ? min : Math.min(value, max);
    }

}
