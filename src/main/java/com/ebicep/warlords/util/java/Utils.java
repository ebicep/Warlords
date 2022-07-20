package com.ebicep.warlords.util.java;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {

    private static final Random RANDOM = new Random();

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = RANDOM.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    public static int generateRandomValueBetweenInclusive(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static int generateRandomIndexFromListSize(int size) {
        return ThreadLocalRandom.current().nextInt(size);
    }


}
