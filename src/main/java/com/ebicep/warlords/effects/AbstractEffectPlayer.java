package com.ebicep.warlords.effects;

public abstract class AbstractEffectPlayer<T> implements EffectPlayer<T> {

    protected boolean needsUpdate = true;

    @Override
    public boolean needsUpdate() {
        return needsUpdate;
    }

}
