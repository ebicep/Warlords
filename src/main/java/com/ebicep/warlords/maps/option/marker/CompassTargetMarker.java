package com.ebicep.warlords.maps.option.marker;

import com.ebicep.warlords.player.WarlordsPlayer;

public interface CompassTargetMarker extends LocationMarker {
    public String getToolbarName(WarlordsPlayer player);
    
    public default boolean isEnabled() {
        return true;
    }
}
