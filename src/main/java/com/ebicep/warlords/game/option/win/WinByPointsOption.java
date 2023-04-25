package com.ebicep.warlords.game.option.win;

import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.events.game.WarlordsPointsChangedEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

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
    public void register(@Nonnull Game game) {
        game.registerEvents(this);
        game.registerGameMarker(ScoreboardHandler.class, scoreboard = new SimpleScoreboardHandler(SCOREBOARD_PRIORITY, "points") {
            @Nonnull
            @Override
            public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                TextComponent.Builder component = Component.text();
                TeamMarker.getTeams(game)
                          .forEach(team -> {
                              component.append(team.coloredPrefix().append(Component.text(": ")))
                                       .append(Component.text(game.getPoints(team), NamedTextColor.AQUA))
                                       .append(Component.text("/" + pointLimit, NamedTextColor.GOLD))
                                       .append(Component.newline());
                          });
                return component.build().children();
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
