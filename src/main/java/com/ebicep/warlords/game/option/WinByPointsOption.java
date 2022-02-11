package com.ebicep.warlords.game.option;

import com.ebicep.warlords.events.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.events.WarlordsPointsChangedEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.player.WarlordsPlayer;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class WinByPointsOption implements Option, Listener {
    public static final int DEFAULT_POINT_LIMIT = 1000;
    private static final int SCOREBOARD_PRIORITY = 5;
    
    private int pointLimit;
    private boolean hasActivated = false;
    private ScoreboardHandler scoreboard;

    public WinByPointsOption() {
        this(DEFAULT_POINT_LIMIT);
    }
    public WinByPointsOption(int pointLimit) {
        this.pointLimit = pointLimit;
    }     

    @Override
    public void register(Game game) {
        game.registerEvents(this);
        game.registerGameMarker(ScoreboardHandler.class, scoreboard = new SimpleScoreboardHandler(SCOREBOARD_PRIORITY, "points") {
            @Override
            public List<String> computeLines(@Nullable WarlordsPlayer player) {
                return TeamMarker.getTeams(game).stream()
                        .map(t -> t.coloredPrefix() + ": " + ChatColor.AQUA + game.getPoints(t) + ChatColor.GOLD + "/" + pointLimit)
                        .collect(Collectors.toList());
            }
        });
    }

    public void setPointLimit(int pointLimit) {
        this.pointLimit = pointLimit;
    }

    public int getPointLimit() {
        return pointLimit;
    }
    
    @EventHandler
    public void onEvent(WarlordsPointsChangedEvent event) {
        if (!hasActivated && event.getNewPoints() >= pointLimit) {
            WarlordsGameTriggerWinEvent e = new WarlordsGameTriggerWinEvent(event.getGame(), this, event.getTeam());
            Bukkit.getPluginManager().callEvent(e);
            if (!e.isCancelled()) {
                hasActivated = true;
            }
        }
    }
}
