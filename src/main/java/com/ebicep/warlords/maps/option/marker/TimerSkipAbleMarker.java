package com.ebicep.warlords.maps.option.marker;

public interface TimerSkipAbleMarker extends GameMarker {

    /**
     * Returns the time in tick that can be skipped by this instance
     * @return the time in tick that can be skipped forwards to the next big event
     */
    public default int getDelay() {
        return Integer.MAX_VALUE;
    }

    /**
     * Skip the timer forwards
     * @param delayInTicks time in ticks to skip forwards
     */
    public void skipTimer(int delayInTicks);
}
