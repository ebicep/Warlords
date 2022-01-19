package com.ebicep.warlords.maps.option.marker;

import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.flags.FlagInfo;
import com.ebicep.warlords.maps.flags.FlagLocation;
import java.util.function.BiFunction;

/**
 * Marks a flag spawner, which can get updates remotely
 */
@FunctionalInterface
public interface FlagHolder extends GameMarker {

    public void update(BiFunction<FlagInfo, Team, FlagLocation> updater);
}
