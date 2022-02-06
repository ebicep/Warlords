package com.ebicep.warlords.maps.state;

/**
 * Class used to mark game states that can be skipped with /wl-debug timer skip
 */
public interface TimerDebugAble {
    void skipTimer() throws IllegalStateException;
    void resetTimer() throws IllegalStateException;
}
