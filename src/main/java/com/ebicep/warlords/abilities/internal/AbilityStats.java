package com.ebicep.warlords.abilities.internal;

public interface AbilityStats<T extends AbstractAbilityStats<T>> {

    String getName();

    T getAbilityStats();

}
