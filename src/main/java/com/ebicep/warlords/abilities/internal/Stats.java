package com.ebicep.warlords.abilities.internal;

public interface Stats<T extends AbilityStats<T>> {

    String getName();

    T getAbilityStats();

}
