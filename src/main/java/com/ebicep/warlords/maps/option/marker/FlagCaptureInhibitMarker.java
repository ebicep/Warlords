package com.ebicep.warlords.maps.option.marker;

import com.ebicep.warlords.maps.flags.PlayerFlagLocation;

public interface FlagCaptureInhibitMarker extends GameMarker {

    public boolean isInhibitingFlagCapture(PlayerFlagLocation flag);
}
