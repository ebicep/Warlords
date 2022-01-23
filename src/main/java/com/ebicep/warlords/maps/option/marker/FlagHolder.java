package com.ebicep.warlords.maps.option.marker;

import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.flags.FlagInfo;
import com.ebicep.warlords.maps.flags.FlagLocation;
import com.ebicep.warlords.maps.flags.GroundFlagLocation;
import com.ebicep.warlords.maps.flags.PlayerFlagLocation;
import com.ebicep.warlords.player.WarlordsPlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.bukkit.Location;

/**
 * Marks a flag spawner, which can get updates remotely
 */
public interface FlagHolder extends GameMarker {
    
    public default FlagLocation getFlag() {
        return getInfo().getFlag();
    }
    
    public FlagInfo getInfo();
    
    public default Location getLocation() {
        return getFlag().getLocation();
    }
    
    public default Team getTeam() {
        return getInfo().getTeam();
    }
    
    public default void setFlag(FlagLocation newFlag) {
        getInfo().setFlag(newFlag);
    }

    public default FlagLocation update(Function<FlagInfo, FlagLocation> updater) {
        FlagInfo info = getInfo();
        FlagLocation old = info.getFlag();
        FlagLocation newFlag = updater.apply(info);
        if (newFlag != null && newFlag != old) {
            info.setFlag(newFlag);
        }
        return newFlag;
    }
    
    public static List<FlagLocation> update(Game game, Function<FlagInfo, FlagLocation> updater) {
        final List<FlagHolder> markers = game.getMarkers(FlagHolder.class);
        List<FlagLocation> newLocations = new ArrayList<>(markers.size());
        for (FlagHolder holder : markers) {
            newLocations.add(holder.update(updater));
        }
        return newLocations;
    }

    public static boolean dropFlagForPlayer(WarlordsPlayer player) {
        for (FlagHolder holder : player.getGame().getMarkers(FlagHolder.class)) {
            if(holder.update(i -> i.getFlag() instanceof PlayerFlagLocation
                    && ((PlayerFlagLocation) i.getFlag()).getPlayer().equals(player) ? new GroundFlagLocation((PlayerFlagLocation) i.getFlag())
                    : null) != null) {
                return true;
            }
        }
        return false;
    }
}
