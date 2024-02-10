package com.ebicep.warlords.game.option.marker;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.flags.*;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

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
    default int getCompassTargetPriority(WarlordsEntity player) {
        return player.getTeam() == getTeam() ? 20 : 0;
    }

    @Override
    default Component getToolbarName(WarlordsEntity player) {
        FlagLocation flag = getFlag();
        Team team = getTeam();
        Team playerTeam = player.getTeam();
        TextComponent.Builder builder = Component.text()
                                                 .color(team.getTeamColor())
                                                 .decorate(TextDecoration.BOLD);
        if (flag.getLocation().getWorld() != player.getLocation().getWorld()) {
            return Component.empty();
        }
        double flagDistance = Math.round(flag.getLocation().distance(player.getLocation()) * 10) / 10.0;
        if (playerTeam != team) {
            builder.append(Component.text("ENEMY "));
        } else {
            builder.append(Component.text("YOUR "));
        }
        if (flag instanceof PlayerFlagLocation || flag instanceof GroundFlagLocation) {
            builder.append(Component.text("Flag "));
            if (flag instanceof PlayerFlagLocation) {
                builder.append(Component.text("is stolen ", NamedTextColor.WHITE));
            } else {
                builder.append(Component.text("is dropped ", NamedTextColor.GOLD));
            }
            builder.append(Component.text(flagDistance + "m ", NamedTextColor.RED))
                   .append(Component.text("away!", NamedTextColor.WHITE));
        } else {
            builder.append(Component.text("Flag is safe", NamedTextColor.GREEN));
        }
        return builder.build();
    }
    
    static List<FlagLocation> update(Game game, Function<FlagInfo, FlagLocation> updater) {
        final List<FlagHolder> markers = game.getMarkers(FlagHolder.class);
        List<FlagLocation> newLocations = new ArrayList<>(markers.size());
        for (FlagHolder holder : markers) {
            newLocations.add(holder.update(updater));
        }
        return newLocations;
    }

    static boolean dropFlagForPlayer(WarlordsEntity player) {
        for (FlagHolder holder : player.getGame().getMarkers(FlagHolder.class)) {
            FlagInfo info = holder.getInfo();
            boolean drop = info.getFlag() instanceof PlayerFlagLocation && ((PlayerFlagLocation) info.getFlag()).getPlayer().equals(player);
            if (drop) {
                if (!(player.getGame().getState() instanceof EndState)) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            holder.update(i ->
                                    i.getFlag() instanceof PlayerFlagLocation &&
                                    ((PlayerFlagLocation) i.getFlag()).getPlayer().equals(player) ? new GroundFlagLocation((PlayerFlagLocation) i.getFlag()) : null
                            );
                        }
                    }.runTaskLater(Warlords.getInstance(), 1);
                } else {
                    holder.update(i ->
                            i.getFlag() instanceof PlayerFlagLocation &&
                            ((PlayerFlagLocation) i.getFlag()).getPlayer().equals(player) ? new GroundFlagLocation((PlayerFlagLocation) i.getFlag()) : null
                    );
                }
                return true;
            }
        }
        return false;
    }

    static boolean isPlayerHolderFlag(WarlordsEntity player) {
        for (FlagHolder holder : player.getGame().getMarkers(FlagHolder.class)) {
            FlagLocation flag = holder.getFlag();
            if (flag instanceof PlayerFlagLocation && ((PlayerFlagLocation) flag).getPlayer().equals(player)) {
                return true;
            }
        }
        return false;
    }

    static boolean playerTryingToPick(WarlordsEntity player) {
        for (FlagHolder flagHolder : player.getGame().getMarkers(FlagHolder.class)) {
            FlagInfo flagInfo = flagHolder.getInfo();
            if (flagInfo.getFlag() instanceof SpawnFlagLocation && flagInfo.getTeam() != player.getTeam()) {
                if (flagInfo.getFlag().getLocation().distanceSquared(player.getLocation()) < 12 * 12) {
                    return true;
                }
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
                return "FlagHolder.create{" +
                        info.get() +
                        '}';
            }
            
        };
    }
}
