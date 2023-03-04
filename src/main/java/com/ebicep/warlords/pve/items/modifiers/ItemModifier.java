package com.ebicep.warlords.pve.items.modifiers;

public interface ItemModifier<T extends Enum<T>> {

    T[] getValues();

    String getName();

    float getIncreasePerTier();

}
