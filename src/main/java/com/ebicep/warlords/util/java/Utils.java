package com.ebicep.warlords.util.java;

import java.util.concurrent.ThreadLocalRandom;

public class Utils {
    public static int generateRandomValueBetweenInclusive(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
