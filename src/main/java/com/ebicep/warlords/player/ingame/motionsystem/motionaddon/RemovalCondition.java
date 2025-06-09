package com.ebicep.warlords.player.ingame.motionsystem.motionaddon;

public interface RemovalCondition extends MotionAddon {

    default boolean removeAnyMatch() {
        return false;
    }

    default boolean removeAllMatch() {
        return false;
    }

}
