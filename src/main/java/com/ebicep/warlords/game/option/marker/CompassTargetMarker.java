package com.ebicep.warlords.game.option.marker;

import com.ebicep.warlords.player.ingame.AbstractWarlordsEntity;

public interface CompassTargetMarker extends LocationMarker {
    /**
     * Gets the initial priority for this compass target, higher values win
     *
     * @param player the player to check for
     * @return the priority
     */
    int getCompassTargetPriority(AbstractWarlordsEntity player);

    String getToolbarName(AbstractWarlordsEntity player);
    
    default boolean isEnabled() {
        return true;
    }
}
