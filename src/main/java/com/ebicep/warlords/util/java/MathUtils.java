package com.ebicep.warlords.util.java;

import java.util.concurrent.ThreadLocalRandom;

public class MathUtils {

    public static int clamp(int value, int min, int max) {
        return value < min ? min : Math.min(value, max);
    }

    public static float clamp(float value, float min, float max) {
        return value < min ? min : Math.min(value, max);
    }

    public static double clamp(double value, double min, double max) {
        return value < min ? min : Math.min(value, max);
    }

    public static int generateRandomValueBetweenInclusive(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    // Linear Interpolation
    // https://en.wikipedia.org/wiki/Linear_interpolation

    public static double lerp(double min, double max, double ratio) {
        return min + ratio * (max - min);
    }

    public static float lerp(float min, float max, float ratio) {
        return min + ratio * (max - min);
    }
}
