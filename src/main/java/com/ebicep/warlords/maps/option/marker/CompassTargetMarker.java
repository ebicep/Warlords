package com.ebicep.warlords.maps.option.marker;

import com.ebicep.warlords.player.WarlordsPlayer;

public interface CompassTargetMarker extends LocationMarker {
    int getCompassTargetPriority(WarlordsPlayer player);
    
    String getToolbarName(WarlordsPlayer player);
    
    default boolean isEnabled() {
        return true;
    }
}
