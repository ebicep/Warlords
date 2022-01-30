package com.ebicep.warlords.maps.option;

import com.ebicep.warlords.events.WarlordsFlagUpdatedEvent;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.flags.*;
import com.ebicep.warlords.maps.option.marker.*;
import com.ebicep.warlords.maps.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.maps.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.GameRunnable;
import static java.util.Collections.singletonList;
import java.util.List;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * Module for spawning a flag
 */
public class FlagSpawnPointOption implements Option {
    
    public static final boolean DEFAULT_REGISTER_COMPASS_MARKER = true;

    @Nonnull
    private final FlagInfo info;
    @Nonnull
    private final FlagRenderer renderer;
    @Nonnull
    private SimpleScoreboardHandler scoreboard;
    @Nonnull
    private Game game;
    private final boolean registerCompassMarker;

    public FlagSpawnPointOption(@Nonnull Location loc, @Nonnull Team team) {
        this(loc, team, DEFAULT_REGISTER_COMPASS_MARKER);
    }

    public FlagSpawnPointOption(@Nonnull Location loc, @Nonnull Team team, boolean registerCompassMarker) {
        this.info = new FlagInfo(team, loc, this::onFlagUpdate);
        this.renderer = new FlagRenderer(info);
        this.registerCompassMarker = registerCompassMarker;
    }

    @Override
    public void register(Game game) {
        this.game = game;
        // We register a gamemarker to prevent any captures for our own team if we lost our flag
        game.registerGameMarker(FlagCaptureInhibitMarker.class, pFlag -> {
            return !(info.getFlag() instanceof SpawnFlagLocation) && info.getTeam() == pFlag.getPlayer().getTeam();
        });
        game.registerGameMarker(DebugLocationMarker.class, DebugLocationMarker.create(Material.BANNER, 0, this.getClass(),
                "Flag spawn: " + info.getTeam(),
                this.info.getSpawnLocation()
        ));
        game.registerGameMarker(DebugLocationMarker.class, DebugLocationMarker.create(Material.BANNER, 15, this.getClass(),
                "Flag: " + info.getTeam(),
                () -> info.getFlag().getLocation(),
                () -> info.getFlag().getDebugInformation()
        ));
        FlagHolder holder = FlagHolder.create(() -> info);
        game.registerGameMarker(FlagHolder.class, holder);
        if (this.registerCompassMarker) {
            game.registerGameMarker(CompassTargetMarker.class, holder);
        }
        game.registerGameMarker(ScoreboardHandler.class, scoreboard = new SimpleScoreboardHandler(info.getTeam() == Team.RED ? 20 : 21, "flag") {
            @Override
            public List<String> computeLines(WarlordsPlayer player) {
                String flagName = info.getTeam().coloredPrefix();
                FlagLocation flag = info.getFlag();
                if (flag instanceof SpawnFlagLocation) {
                    return singletonList(flagName + " Flag: " + ChatColor.GREEN + "Safe");
                } else if (flag instanceof PlayerFlagLocation) {
                    PlayerFlagLocation pFlag = (PlayerFlagLocation) flag;
                    String extra = pFlag.getPickUpTicks() == 0 ? "" : ChatColor.YELLOW + " +" + pFlag.getComputedHumanMultiplier() + "§e%";
                    return singletonList(flagName + " Flag: " + ChatColor.RED + "Stolen!" + extra);
                } else if (flag instanceof GroundFlagLocation) {
                    GroundFlagLocation gFlag = (GroundFlagLocation) flag;
                    return singletonList(flagName + " Flag: " + ChatColor.YELLOW + "Dropped! " + ChatColor.GRAY + gFlag.getDespawnTimerSeconds());
                } else {
                    return singletonList(flagName + " Flag: " + ChatColor.GRAY + "Respawning...");
                }
            }
        });
    }

    private boolean flagIsInCaptureZone(PlayerFlagLocation playerFlagLocation) {
        for (FlagCaptureMarker flag : game.getMarkers(FlagCaptureMarker.class)) {
            if (flag.shouldCountAsCapture(playerFlagLocation)) {
                return true;
            }
        }
        return false;
    }

    private boolean flagCaptureIsNotBlocked(PlayerFlagLocation playerFlagLocation) {
        for (FlagCaptureInhibitMarker blocker : game.getMarkers(FlagCaptureInhibitMarker.class)) {
            if (blocker.isInhibitingFlagCapture(playerFlagLocation)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void start(Game game) {
        new GameRunnable(game) {
            @Override
            public void run() {
                if (!(info.getFlag() instanceof PlayerFlagLocation)) {
                    return;
                }
                PlayerFlagLocation playerFlagLocation = (PlayerFlagLocation) info.getFlag();
                if (flagIsInCaptureZone(playerFlagLocation) && !flagCaptureIsNotBlocked(playerFlagLocation)) {
                    FlagHolder.update(game, info -> new WaitingFlagLocation(
                            info.getSpawnLocation(),
                            info.getFlag() == playerFlagLocation ? playerFlagLocation.getPlayer() : null
                    ));
                }
            }
        }.runTaskTimer(0, 4);
        new GameRunnable(game) {
            @Override
            public void run() {
                renderer.checkRender();
            }
        }.runTaskTimer(0, 4);
    }

    public FlagInfo getInfo() {
        return info;
    }

    public FlagRenderer getRenderer() {
        return renderer;
    }

    private void onFlagUpdate(FlagInfo info, FlagLocation old) {
        scoreboard.markChanged();
        Bukkit.getPluginManager().callEvent(new WarlordsFlagUpdatedEvent(game, info, old));
        renderer.checkRender();
    }

}
