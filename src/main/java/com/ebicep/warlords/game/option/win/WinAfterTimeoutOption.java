package com.ebicep.warlords.game.option.win;

import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.PointPredicterMarker;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.marker.TimerSkipAbleMarker;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.java.StringUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.OptionalInt;

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

/**
 * Causes the game to end in a draw after a timeout
 */
public class WinAfterTimeoutOption implements Option {

    public static final int DEFAULT_TIME_REMAINING = 900;
    public static final Team DEFAULT_WINNER = null;

    public static OptionalInt getTimeRemaining(@Nonnull Game game) {
        for (Option option : game.getOptions()) {
            if (option instanceof WinAfterTimeoutOption drawAfterTimeoutOption) {
                return OptionalInt.of(drawAfterTimeoutOption.getTimeRemaining());
            }
        }
        return OptionalInt.empty();
    }

    /**
     * Gets the time remaining in second
     *
     * @return the time remaining
     */
    public int getTimeRemaining() {
        return timeRemaining;
    }

    /**
     * Sets the time remaining in seconds
     *
     * @param timeRemaining the time remaining
     */
    public void setTimeRemaining(int timeRemaining) {
        this.timeRemaining = timeRemaining;
        this.timeInitial = timeRemaining;
    }

    private int scoreboardPriority = 10;
    private String scoreboardGroup = "timeout";
    private int timeRemaining;
    private int timeInitial;
    private SimpleScoreboardHandler scoreboard;
    private BukkitTask runTaskTimer;
    private Team winner;

    public WinAfterTimeoutOption() {
        this(DEFAULT_TIME_REMAINING, DEFAULT_WINNER);
    }

    public WinAfterTimeoutOption(int timeRemaining, Team winner) {
        this.timeRemaining = timeRemaining;
        this.timeInitial = timeRemaining;
    }

    public WinAfterTimeoutOption(int timeRemaining) {
        this(timeRemaining, DEFAULT_WINNER);
    }

    public WinAfterTimeoutOption(int timeRemaining, int scoreboardPriority, String scoreboardGroup) {
        this(timeRemaining, DEFAULT_WINNER);
        this.scoreboardPriority = scoreboardPriority;
        this.scoreboardGroup = scoreboardGroup;
    }

    public WinAfterTimeoutOption(Team winner) {
        this(DEFAULT_TIME_REMAINING, winner);
    }

    /**
     * Computes the time elapsed in seconds
     *
     * @return The time elapsed
     */
    public int getTimeElapsed() {
        return timeInitial - timeRemaining;
    }

    public Team getWinner() {
        return winner;
    }

    public void setWinner(Team winner) {
        this.winner = winner;
    }

    @Override
    public void register(@Nonnull Game game) {
        new TimerSkipAbleMarker() {
            @Override
            public int getDelay() {
                return timeRemaining * 20;
            }

            @Override
            public void skipTimer(int delay) {
                timeRemaining -= delay / 20;
            }

        }.register(game);
        game.registerGameMarker(ScoreboardHandler.class, scoreboard = new SimpleScoreboardHandler(scoreboardPriority, scoreboardGroup) {
            @Nonnull
            @Override
            public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                final EnumSet<Team> teams = TeamMarker.getTeams(game);

                Team winner = null;
                int highestScore = Integer.MIN_VALUE;
                int highestWinInSeconds = Integer.MAX_VALUE;
                if (teams.size() > 1) {
                    List<PointPredicterMarker> predictionMarkers = game
                            .getMarkers(PointPredicterMarker.class);
                    int scoreNeededToEndGame = game.getOptions()
                                                   .stream()
                                                   .filter(e -> e instanceof WinByPointsOption)
                                                   .mapToInt(e -> ((WinByPointsOption) e).getPointLimit())
                                                   .sorted()
                                                   .findFirst()
                                                   .orElse(Integer.MAX_VALUE);
                    for (Team team : teams) {
                        int points = game.getPoints(team);
                        int winInSeconds;
                        if (predictionMarkers.isEmpty()) {
                            winInSeconds = Integer.MAX_VALUE;
                        } else {
                            double pointsPerMinute = predictionMarkers
                                    .stream()
                                    .mapToDouble(e -> e.predictPointsNextMinute(team))
                                    .sum();
                            int pointsRemaining = scoreNeededToEndGame - points;
                            int winInSecondsCalculated = pointsPerMinute <= 0 ? Integer.MAX_VALUE : (int) (pointsRemaining / pointsPerMinute * 60);
                            int pointsAfterTimeIsOver = (int) (points + timeRemaining * pointsPerMinute / 60);

                            if (winInSecondsCalculated >= 0 && winInSecondsCalculated < timeRemaining) {
                                // This teamis going to win before the timer is over
                                winInSeconds = winInSecondsCalculated;
                                points = scoreNeededToEndGame;
                            } else {
                                winInSeconds = timeRemaining;
                                points = pointsAfterTimeIsOver;
                            }
                        }

                        if (points > highestScore) {
                            highestScore = points;
                            highestWinInSeconds = winInSeconds;
                            winner = team;
                        } else if (points == highestScore) {
                            if (winInSeconds < highestWinInSeconds) {
                                highestWinInSeconds = winInSeconds;
                                winner = team;
                            } else if (winInSeconds == highestWinInSeconds) {
                                winner = null;
                            }
                        }
                    }
                } else {
                    highestWinInSeconds = timeRemaining;
                }

                TextComponent.Builder message = Component.text();
                if (winner != null) {
                    message.append(winner.coloredPrefix())
                           .append(Component.text(" Wins in: ", NamedTextColor.GOLD));
                } else {
                    message.append(Component.text("Time Left: ", NamedTextColor.WHITE));
                }
                message.append(Component.text(StringUtils.formatTimeLeft(highestWinInSeconds == Integer.MAX_VALUE ? timeRemaining : highestWinInSeconds), NamedTextColor.GREEN));
                return Collections.singletonList(message.build());
            }
        });
    }

    @Override
    public void start(@Nonnull Game game) {
        this.runTaskTimer = new GameRunnable(game) {
            @Override
            public void run() {
                if (game.isState(EndState.class)) {
                    cancel();
                    return;
                }
                timeRemaining--;
                if (timeRemaining <= 0) {
                    Team leader;
                    if (winner == null) {
                        int higest = Integer.MIN_VALUE;
                        int secondHighest = Integer.MIN_VALUE;
                        leader = null;
                        for (Team team : TeamMarker.getTeams(game)) {
                            int points = game.getPoints(team);
                            if (points > higest) {
                                leader = team;
                                secondHighest = higest;
                                higest = points;
                            } else if (points > secondHighest) {
                                secondHighest = points;
                            }
                        }
                        if (higest <= secondHighest) {
                            leader = null;
                        }
                    } else {
                        leader = winner;
                    }
                    WarlordsGameTriggerWinEvent event = new WarlordsGameTriggerWinEvent(game, WinAfterTimeoutOption.this, leader);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        cancel();
                    }
                }
                scoreboard.markChanged();
            }
        }.runTaskTimer(SECOND, SECOND);
    }

    @Override
    public void onGameEnding(@Nonnull Game game) {
        if (runTaskTimer != null) {
            runTaskTimer.cancel();
            runTaskTimer = null;
        }
    }
}
