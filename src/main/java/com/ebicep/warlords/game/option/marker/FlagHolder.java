package com.ebicep.warlords.game.option.marker;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.flags.FlagInfo;
import com.ebicep.warlords.game.flags.FlagLocation;
import com.ebicep.warlords.game.flags.GroundFlagLocation;
import com.ebicep.warlords.game.flags.PlayerFlagLocation;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Marks a flag spawner, which can get updates remotely
 */
public interface FlagHolder extends CompassTargetMarker, GameMarker {
    
    default FlagLocation getFlag() {
        return getInfo().getFlag();
    }
    
    FlagInfo getInfo();
    
    @Override
    default Location getLocation() {
        return getFlag().getLocation();
    }
    
    default Team getTeam() {
        return getInfo().getTeam();
    }
    
    default void setFlag(FlagLocation newFlag) {
        getInfo().setFlag(newFlag);
    }

    default FlagLocation update(Function<FlagInfo, FlagLocation> updater) {
        FlagInfo info = getInfo();
        FlagLocation old = info.getFlag();
        FlagLocation newFlag = updater.apply(info);
        if (newFlag != null && newFlag != old) {
            info.setFlag(newFlag);
        }
        return newFlag;
    }

    @Override
    public default int getCompassTargetPriority(WarlordsPlayer player) {
        return player.getTeam() == getTeam() ? 20 : 0;
    }
    
    @Override
    default String getToolbarName(WarlordsPlayer player) {
        FlagLocation flag = getFlag();
        Team team = getTeam();
        Team playerTeam = player.getTeam();
        StringBuilder builder = new StringBuilder();
        if (flag.getLocation().getWorld() != player.getLocation().getWorld()) return "";
        double flagDistance = Math.round(flag.getLocation().distance(player.getLocation()) * 10) / 10.0;
        builder.append(team.teamColor()).append(ChatColor.BOLD);
        if (playerTeam != team) {
            builder.append("ENEMY ");
        } else {
            builder.append("YOUR ");
        }
        if (flag instanceof PlayerFlagLocation || flag instanceof GroundFlagLocation) {
            if (flag instanceof GroundFlagLocation && playerTeam != team) {
                builder.append("ENEMY "); // This is directly copied from the older code, but seems wrong...
            }
            builder.append("Flag ");
            if (flag instanceof PlayerFlagLocation) {
                builder.append(ChatColor.WHITE).append("is stolen ");
            } else if (flag instanceof GroundFlagLocation) {
                builder.append(ChatColor.GOLD).append("is dropped ");
            }
            builder.append(ChatColor.RED).append(flagDistance).append("m ").append(ChatColor.WHITE).append("away!");
        } else {
            builder.append(ChatColor.GREEN).append("Flag is safe");
        }
        return builder.toString();
    }
    
    static List<FlagLocation> update(Game game, Function<FlagInfo, FlagLocation> updater) {
        final List<FlagHolder> markers = game.getMarkers(FlagHolder.class);
        List<FlagLocation> newLocations = new ArrayList<>(markers.size());
        for (FlagHolder holder : markers) {
            newLocations.add(holder.update(updater));
        }
        return newLocations;
    }

    static boolean dropFlagForPlayer(WarlordsPlayer player) {
        for (FlagHolder holder : player.getGame().getMarkers(FlagHolder.class)) {
            if(holder.update(i -> i.getFlag() instanceof PlayerFlagLocation
                    && ((PlayerFlagLocation) i.getFlag()).getPlayer().equals(player) ? new GroundFlagLocation((PlayerFlagLocation) i.getFlag())
                    : null) != null) {
                return true;
            }
        }
        return false;
    }

    static boolean isPlayerHolderFlag(WarlordsPlayer player) {
        for (FlagHolder holder : player.getGame().getMarkers(FlagHolder.class)) {
            FlagLocation flag = holder.getFlag();
            if (flag instanceof PlayerFlagLocation && ((PlayerFlagLocation)flag).getPlayer().equals(player)) {
                return true;
            }
        }
        return false;
    }
    
    static FlagHolder create(FlagInfo info) {
        return create(() -> info);
    }
    static FlagHolder create(Supplier<FlagInfo> info) {
        return new FlagHolder() {
            @Override
            public FlagInfo getInfo() {
                return info.get();
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append("FlagHolder.create{");
                sb.append(info.get());
                sb.append('}');
                return sb.toString();
            }
            
        };
    }
}
