package com.ebicep.warlords.pve.items.modifiers;

import com.ebicep.warlords.util.java.RandomCollection;

import java.util.HashMap;

public interface ItemModifier<T extends Enum<T>> {

    HashMap<Integer, Double> BLESSING_TIER_CHANCE = new HashMap<>() {{
        put(1, 29.0);
        put(2, 10.0);
        put(3, 5.0);
        put(4, 2.5);
        put(5, 1.0);
    }};
    HashMap<Integer, Double> CURSE_TIER_CHANCE = new HashMap<>() {{
        put(1, 5.0);
        put(2, 12.5);
        put(3, 25.0);
        put(4, 7.5);
        put(5, 2.5);
    }};
    RandomCollection<Integer> GENERATE_BLESSING = new RandomCollection<Integer>()
            .add(29, 1)
            .add(10, 2)
            .add(5, 3)
            .add(2.5, 4)
            .add(1, 5);
    RandomCollection<Integer> GENERATE_CURSE = new RandomCollection<Integer>()
            .add(5, 1)
            .add(12.5, 2)
            .add(25, 3)
            .add(7.5, 4)
            .add(2.5, 5);

    T[] getValues();

    String getName();

    String getDescription();

    float getIncreasePerTier();

}
