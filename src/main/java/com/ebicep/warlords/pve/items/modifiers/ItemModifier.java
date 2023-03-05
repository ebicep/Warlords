package com.ebicep.warlords.pve.items.modifiers;

import com.ebicep.warlords.util.java.RandomCollection;

public interface ItemModifier<T extends Enum<T>> {

    RandomCollection<Integer> MICHAEL_BLESSING_TIER_CHANCE = new RandomCollection<Integer>()
            .add(.79, 1)
            .add(.18, 2)
            .add(.0275, 3)
            .add(.0175, 4)
            .add(.0075, 5);
    RandomCollection<Integer> BLESSING_TIER_CHANCE = new RandomCollection<Integer>()
            .add(.29, 1)
            .add(.10, 2)
            .add(.05, 3)
            .add(.025, 4)
            .add(.01, 5);
    RandomCollection<Integer> CURSE_TIER_CHANCE = new RandomCollection<Integer>()
            .add(.05, 1)
            .add(.125, 2)
            .add(.25, 3)
            .add(.75, 4)
            .add(.025, 5);

    T[] getValues();

    String getName();

    String getDescription();

    float getIncreasePerTier();

}
