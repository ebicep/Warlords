package com.ebicep.warlords.game.option.marker;

import com.ebicep.warlords.game.flags.PlayerFlagLocation;

public interface FlagCaptureInhibitMarker extends GameMarker {

    boolean isInhibitingFlagCapture(PlayerFlagLocation flag);
}
