package com.ebicep.warlords.player.ingame.instances.type;

public interface Instance {

    /**
     * if true, only the methods of the first cooldown is applied, the rest are skipped,
     * checks based on class and name
     */
    default boolean distinct() {
        return false;
    }


}
