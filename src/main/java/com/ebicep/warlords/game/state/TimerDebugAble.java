package com.ebicep.warlords.game.state;

/**
 * Class used to mark game states that can be skipped with /wl-debug timer skip
 */
public interface TimerDebugAble {
    void skipTimer() throws IllegalStateException;
    void resetTimer() throws IllegalStateException;
}
