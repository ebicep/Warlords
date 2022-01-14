package com.ebicep.warlords.maps.option;

import com.ebicep.warlords.maps.option.flags.PlayerFlagLocation;
import com.ebicep.warlords.maps.option.flags.SpawnFlagLocation;
import com.ebicep.warlords.maps.option.flags.FlagRenderer;
import com.ebicep.warlords.maps.option.flags.FlagLocation;
import com.ebicep.warlords.maps.option.flags.FlagInfo;
import com.ebicep.warlords.maps.option.flags.GroundFlagLocation;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.option.Option;
import com.ebicep.warlords.maps.option.flags.FlagInfo;
import com.ebicep.warlords.maps.option.flags.FlagLocation;
import com.ebicep.warlords.maps.option.flags.FlagRenderer;
import com.ebicep.warlords.maps.option.flags.GroundFlagLocation;
import com.ebicep.warlords.maps.option.flags.PlayerFlagLocation;
import com.ebicep.warlords.maps.option.flags.SpawnFlagLocation;
import com.ebicep.warlords.maps.option.marker.FlagCaptureInhibitMarker;
import com.ebicep.warlords.maps.option.marker.FlagCaptureMarker;
import com.ebicep.warlords.maps.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.player.WarlordsPlayer;
import static java.util.Collections.singletonList;
import java.util.List;
import javax.annotation.Nonnull;
import org.bukkit.ChatColor;
import org.bukkit.Location;

/**
 * Module for spawning a flag
 */
public class FlagSpawnPointOption implements Option {
    
    @Nonnull
    private final Team team;
    @Nonnull
    private final FlagInfo info;
    @Nonnull
    private final FlagRenderer renderer;
    @Nonnull
    private SimpleScoreboardHandler scoreboard; 
    private int scoreTick = 0;
    @Nonnull
    private Game game;

    public FlagSpawnPointOption(@Nonnull Team team, @Nonnull Location loc) {
        this.team = team;
        this.info = new FlagInfo(team, loc, this::onFlagUpdate);
        this.renderer = new FlagRenderer(info);
    }

    @Override
    public void register(Game game) {
        this.game = game;
        // We register a gamemarker to prevent any captures for our own team if we lost our flag
        game.registerGameMarker(FlagCaptureInhibitMarker.class, pFlag -> {
            return !(info.getFlag() instanceof SpawnFlagLocation) && team == pFlag.getPlayer().getTeam();
        });
        game.registerScoreboardHandler(scoreboard = new SimpleScoreboardHandler(team == Team.RED ? 20 : 21) {
            @Override
            public List<String> computeLines(WarlordsPlayer player) {
                String flagName = team.coloredPrefix();
                FlagLocation flag = info.getFlag();
                if (flag instanceof SpawnFlagLocation) {
                    return singletonList(flagName + " Flag: " + ChatColor.GREEN + "Safe");
                } else if (flag instanceof PlayerFlagLocation) {
                    PlayerFlagLocation pFlag = (PlayerFlagLocation)flag;
                    String extra = pFlag.getPickUpTicks() == 0 ? "" :  ChatColor.YELLOW + " +" + pFlag.getComputedHumanMultiplier() + "§e%";
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
    
    private boolean flagIsInCaptureZone() {
        if(!(this.info.getFlag() instanceof PlayerFlagLocation)) {
            return false;
        }
        PlayerFlagLocation playerFlagLocation = (PlayerFlagLocation) this.info.getFlag();
        for(FlagCaptureMarker flag : game.getMarkers(FlagCaptureMarker.class)) {
            if(flag.shouldCountAsCapture(playerFlagLocation)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean flagCaptureIsNotBlocked() {
        if(!(this.info.getFlag() instanceof PlayerFlagLocation)) {
            return false;
        }
        PlayerFlagLocation playerFlagLocation = (PlayerFlagLocation) this.info.getFlag();
        for(FlagCaptureInhibitMarker blocker : game.getMarkers(FlagCaptureInhibitMarker.class)) {
            if(blocker.isInhibitingFlagCapture(playerFlagLocation)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void tick(Game game) {
        if (++scoreTick >= 4) {
            scoreTick = 0;
            if(flagIsInCaptureZone() && !flagCaptureIsNotBlocked()) {

            }
        }
    }
    
    private void onFlagUpdate(FlagInfo info, FlagLocation old) {
        scoreboard.markChanged();
        renderer.checkRender();
    }
	
}
