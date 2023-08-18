package com.ebicep.warlords.util.java;

import java.util.concurrent.ThreadLocalRandom;

public class MathUtils {

    public static float clamp(float value, float min, float max) {
        return value < min ? min : Math.min(value, max);
    }

    public static int generateRandomValueBetweenInclusive(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
