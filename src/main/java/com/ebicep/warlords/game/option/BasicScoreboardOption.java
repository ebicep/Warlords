package com.ebicep.warlords.game.option;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.java.DateUtil;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class BasicScoreboardOption implements Option {

    @Override
    public void register(@Nonnull Game game) {
        game.registerGameMarker(ScoreboardHandler.class, getDateScoreboard(game));
        game.registerGameMarker(ScoreboardHandler.class, getVersionScoreboard(game));
        GameMode gameMode = game.getGameMode();
        switch (gameMode) {
            case INTERCEPTION, SIMULATION_TRIAL, CAPTURE_THE_FLAG, TEAM_DEATHMATCH, DEBUG -> game.registerGameMarker(ScoreboardHandler.class, getStatsScoreboard(game));
        }
        if (gameMode != GameMode.INTERCEPTION) {
            game.registerGameMarker(ScoreboardHandler.class, getSpecScoreboard(game));
        }
    }

    private static SimpleScoreboardHandler getDateScoreboard(Game game) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MM/dd/yyyy - kk:mm");
        SimpleScoreboardHandler simpleScoreboardHandler = new SimpleScoreboardHandler(0, "date") {
            @Nonnull
            @Override
            public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                return Collections.singletonList(Component.text(format.format(DateUtil.getCurrentDateEST()), NamedTextColor.GRAY));
            }
        };
        new GameRunnable(game) {
            @Override
            public void run() {
                simpleScoreboardHandler.markChanged();
            }
        }.runTaskTimer(GameRunnable.SECOND, GameRunnable.SECOND);
        return simpleScoreboardHandler;
    }

    private static SimpleScoreboardHandler getVersionScoreboard(Game game) {
        return new SimpleScoreboardHandler(Integer.MAX_VALUE, "version") {
            @Nonnull
            @Override
            public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                return Collections.singletonList(Component.text(Warlords.VERSION, NamedTextColor.YELLOW));
            }
        };
    }

    private static SimpleScoreboardHandler getStatsScoreboard(Game game) {
        // TODO trigger scoreboard update when the kills/assists changes
        return new SimpleScoreboardHandler(Integer.MAX_VALUE - 10, "player-stats") {
            @Nonnull
            @Override
            public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                if (player == null) {
                    return Collections.singletonList(Component.empty());
                }
                return Collections.singletonList(
                        Component.text(player.getMinuteStats().total().getKills(), NamedTextColor.GREEN)
                                 .append(Component.text(" Kills ", NamedTextColor.WHITE))
                                 .append(Component.text(player.getMinuteStats().total().getAssists()))
                                 .append(Component.text(" Assists", NamedTextColor.WHITE))
                );
            }
        };
    }

    private static SimpleScoreboardHandler getSpecScoreboard(Game game) {
        return new SimpleScoreboardHandler(Integer.MAX_VALUE - 20, "spec") {
            @Nonnull
            @Override
            public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                if (player == null) {
                    return Collections.singletonList(Component.empty());
                }
                return Collections.singletonList(
                        Component.text("Spec: ", NamedTextColor.WHITE)
                                 .append(Component.text(player.getSpec().getClass().getSimpleName(), NamedTextColor.GREEN))
                );
            }
        };
    }

}
