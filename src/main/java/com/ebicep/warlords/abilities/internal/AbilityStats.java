package com.ebicep.warlords.abilities.internal;

public interface AbilityStats<T extends AbstractAbility, R extends AbstractAbilityStats<T, R>> {

    String getName();

    R getAbilityStats();

}
