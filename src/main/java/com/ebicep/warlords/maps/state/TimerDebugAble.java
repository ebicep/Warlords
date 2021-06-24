package com.ebicep.warlords.maps.state;

/**
 * Class used to mark game states that can be skipped with /wl-debug timer skip
 */
public interface TimerDebugAble {
    public void skipTimer() throws IllegalStateException;
    public void resetTimer() throws IllegalStateException;
}
