package com.ebicep.warlords.game.option.win;

import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.events.game.WarlordsPointsChangedEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

public class WinByPointsOption implements Option, Listener {

    public static OptionalInt getPointLimit(@Nonnull Game game) {
        for (Option option : game.getOptions()) {
            if (option instanceof WinByPointsOption winByPointsOption) {
                return OptionalInt.of(winByPointsOption.getPointLimit());
            }
        }
        return OptionalInt.empty();
    }
    public static final int DEFAULT_POINT_LIMIT = 1000;
    private static final int SCOREBOARD_PRIORITY = 5;

    private int pointLimit;
    private boolean hasActivated = false;
    private SimpleScoreboardHandler scoreboard;

    public WinByPointsOption() {
        this(ConfigManager.getGameConfigValue(ConfigManager.DEFAULT_NAMESPACES, "ctf.pointsToWin", int.class, DEFAULT_POINT_LIMIT));
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
                List<Component> components = new ArrayList<>();
                TeamMarker.getTeams(game)
                          .forEach(team -> {
                              Component component = team.coloredPrefix()
                                                        .append(Component.text(": "))
                                                        .append(Component.text(game.getPoints(team), NamedTextColor.AQUA))
                                                        .append(Component.text("/" + pointLimit, NamedTextColor.GOLD));
                              component = modifyScoreboardLine(team, component);
                              components.add(component);
                          });
                return components;
            }
        });
    }

    protected Component modifyScoreboardLine(Team team, Component component) {
        return component;
    }

    public int getPointLimit() {
        return pointLimit;
    }

    public void setPointLimit(int pointLimit) {
        this.pointLimit = pointLimit;
    }

    @EventHandler
    public void onEvent(WarlordsPointsChangedEvent event) {
        scoreboard.markChanged();
        if (!hasActivated && event.getNewPoints() >= pointLimit) {
            WarlordsGameTriggerWinEvent e = new WarlordsGameTriggerWinEvent(event.getGame(), this, event.getTeam());
            Bukkit.getPluginManager().callEvent(e);
            if (!e.isCancelled()) {
                hasActivated = true;
            }
        }
    }
}
