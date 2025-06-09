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

    public static double generateRandomValueBetweenInclusive(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max + .000001);
    }

    // Linear Interpolation
    // https://en.wikipedia.org/wiki/Linear_interpolation

    public static double lerp(double min, double max, double ratio) {
        return min + ratio * (max - min);
    }

    public static float lerp(float min, float max, float ratio) {
        return min + ratio * (max - min);
    }

    /**
     * Calculates the max distance to check based on player's pitch to stay within a vertical cylinder.
     *
     * @param pitchDegrees Pitch angle in degrees (0 = horizontal, +90 = straight up, -90 = straight down)
     * @param maxHoriz     Maximum horizontal distance (cylinder radius)
     * @param maxVert      Maximum vertical distance (cylinder height/2)
     *
     * @return The maximum spherical distance to use for raycasting
     */
    public static double calculateMaxDistance(double pitchDegrees, double maxHoriz, double maxVert) {
        double pitchRadians = Math.toRadians(pitchDegrees);
        double sin = Math.sin(pitchRadians);
        double cos = Math.cos(pitchRadians);

        double dVert = (sin == 0.0) ? Double.POSITIVE_INFINITY : maxVert / Math.abs(sin);
        double dHoriz = (cos == 0.0) ? 0.0 : maxHoriz / Math.abs(cos);

        return Math.min(dVert, dHoriz);
    }
}
