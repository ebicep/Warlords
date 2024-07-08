package com.ebicep.warlords.player.ingame.instances.type;

import org.bukkit.util.Vector;

public interface KnockbackInstance extends Instance {

    /**
     * Called when player takes kb through WarlordsEntity.class.setVelocity()
     */
    default void multiplyKB(Vector currentVector) {
    }

}
