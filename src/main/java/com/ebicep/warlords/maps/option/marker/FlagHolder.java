package com.ebicep.warlords.maps.option.marker;

import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.flags.FlagInfo;
import com.ebicep.warlords.maps.flags.FlagLocation;
import com.ebicep.warlords.maps.flags.GroundFlagLocation;
import com.ebicep.warlords.maps.flags.PlayerFlagLocation;
import com.ebicep.warlords.player.WarlordsPlayer;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * Marks a flag spawner, which can get updates remotely
 */
@FunctionalInterface
public interface FlagHolder extends GameMarker {

    public FlagLocation update(BiFunction<FlagInfo, Team, FlagLocation> updater);

    public static boolean dropFlagForPlayer(WarlordsPlayer player) {
        for (FlagHolder holder : player.getGame().getMarkers(FlagHolder.class)) {
            if(holder.update((i, t) -> i.getFlag() instanceof PlayerFlagLocation
                    && ((PlayerFlagLocation) i.getFlag()).getPlayer().equals(player) ? new GroundFlagLocation((PlayerFlagLocation) i.getFlag())
                    : null) != null) {
                return true;
            }
        }
        return false;
    }
}
