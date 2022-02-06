package com.ebicep.warlords.maps.option.marker;

import com.ebicep.warlords.maps.flags.PlayerFlagLocation;

public interface FlagCaptureInhibitMarker extends GameMarker {

    boolean isInhibitingFlagCapture(PlayerFlagLocation flag);
}
