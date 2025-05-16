package com.ebicep.warlords.abilities.internal;

public interface Duration {

    default void multiplyTickDuration(float multiplier) {
        setTickDuration((int) (getTickDuration() * multiplier));
    }

    int getTickDuration();

    void setTickDuration(int tickDuration);

}
