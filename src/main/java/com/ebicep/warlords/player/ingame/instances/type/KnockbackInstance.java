package com.ebicep.warlords.player.ingame.instances.type;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface KnockbackInstance extends Instance {

    @Nullable
    default List<KnockbackInstance> getExtraKnockbackInstances() {
        return null;
    }

    /**
     * Called when player takes kb through WarlordsEntity.class.setVelocity()
     */
    default void multiplyKB(Vector currentVector) {
    }

}
