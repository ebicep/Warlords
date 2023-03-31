package com.ebicep.warlords.abilties.internal;

public interface Duration {

    int getTickDuration();

    void setTickDuration(int tickDuration);

    default void multiplyTickDuration(float multiplier) {
        setTickDuration((int) (getTickDuration() * multiplier));
    }

}
