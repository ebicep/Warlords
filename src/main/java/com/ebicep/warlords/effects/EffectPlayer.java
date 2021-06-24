package com.ebicep.warlords.effects;

public interface EffectPlayer<T> {

    void playEffect(T baseData);

    void updateCachedData(T baseData);

    boolean needsUpdate();
}
