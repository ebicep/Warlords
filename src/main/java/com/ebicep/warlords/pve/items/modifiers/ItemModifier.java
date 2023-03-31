package com.ebicep.warlords.pve.items.modifiers;

import com.ebicep.warlords.util.java.RandomCollection;

import java.util.HashMap;

public interface ItemModifier<T extends Enum<T>> {

    HashMap<Integer, Double> BLESSING_TIER_CHANCE = new HashMap<>() {{
        put(1, 68.75);
        put(2, 25.0);
        put(3, 5.0);
        put(4, 1.0);
        put(5, 0.25);
    }};
    HashMap<Integer, Double> CURSE_TIER_CHANCE = new HashMap<>() {{
        put(1, 5.0);
        put(2, 15.0);
        put(3, 50.0);
        put(4, 20.5);
        put(5, 10.0);
    }};
    RandomCollection<Integer> GENERATE_BLESSING = new RandomCollection<>() {{
        BLESSING_TIER_CHANCE.forEach((tier, weight) -> add(weight, tier));
    }};
    RandomCollection<Integer> GENERATE_CURSE = new RandomCollection<>() {{
        CURSE_TIER_CHANCE.forEach((tier, weight) -> add(weight, tier));
    }};

    T[] getValues();

    String getName();

    String getDescription();

}
