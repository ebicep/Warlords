package com.ebicep.warlords.maps.option;

import com.ebicep.warlords.events.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.option.marker.TeamMarker;
import com.ebicep.warlords.maps.option.marker.TimerSkipAbleMarker;
import com.ebicep.warlords.maps.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.maps.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.GameRunnable;
import static com.ebicep.warlords.util.GameRunnable.SECOND;
import com.ebicep.warlords.util.Utils;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.OptionalInt;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;

/**
 * Causes the game to end in a draw after a timeout
 */
public class WinAfterTimeoutOption implements Option {

    public static final int DEFAULT_TIME_REMAINING = 900;
    public static final Team DEFAULT_WINNER = null;
    private static final int SCOREBOARD_PRIORITY = 10;

    private int timeRemaining;
    private int timeInitial;
    private SimpleScoreboardHandler scoreboard;
    private BukkitTask runTaskTimer; 
    private Team winner;

    public WinAfterTimeoutOption() {
        this(DEFAULT_TIME_REMAINING, DEFAULT_WINNER);
    }

    public WinAfterTimeoutOption(int timeRemaining) {
        this(timeRemaining, DEFAULT_WINNER);
    }

    public WinAfterTimeoutOption(Team winner) {
        this(DEFAULT_TIME_REMAINING, winner);
    }

    public WinAfterTimeoutOption(int timeRemaining, Team winner) {
        this.timeRemaining = timeRemaining;
        this.timeInitial = timeRemaining;
    }

    /**
     * Computes the time elapsed in seconds
     *
     * @return The time elapsed
     */
    public int getTimeElapsed() {
        return timeInitial - timeRemaining;
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

    /**
     * Gets the time remaining in second
     * @return the time remaining
     */
    public int getTimeRemaining() {
        return timeRemaining;
    }

    public Team getWinner() {
        return winner;
    }

    public void setWinner(Team winner) {
        this.winner = winner;
    }

    @Override
    public void register(Game game) {
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
        game.registerGameMarker(ScoreboardHandler.class, scoreboard = new SimpleScoreboardHandler(SCOREBOARD_PRIORITY, "timeout") {
            @Override
            public List<String> computeLines(WarlordsPlayer player) {
                final EnumSet<Team> teams = TeamMarker.getTeams(game);
                Team winner = null;
                if (teams.size() > 1) {
                    int highestScore = Integer.MIN_VALUE;
                    for (Team team : teams) {
                        int points = game.getStats(team).points();
                        if (points > highestScore) {
                            highestScore = points;
                            winner = team;
                        } else if (points == highestScore) {
                            winner = null;
                        }
                    }
                }

                StringBuilder message = new StringBuilder(64);
                if (winner != null) {
                    message.append(winner.coloredPrefix()).append(ChatColor.GOLD).append(" Wins in: ");
                } else {
                    message.append(ChatColor.WHITE).append("Time Left: ");
                }
                message.append(ChatColor.GREEN);
                Utils.formatTimeLeft(message, timeRemaining);
                return Collections.singletonList(message.toString());
            }
        });
    }

    @Override
    public void start(Game game) {
        new GameRunnable(game) {
            @Override
            public void run() {
                timeRemaining--;
                if (timeRemaining <= 0) {
                    WarlordsGameTriggerWinEvent event = new WarlordsGameTriggerWinEvent(game, WinAfterTimeoutOption.this, winner);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        cancel();
                    }
                }
                scoreboard.markChanged();
            }
        }.runTaskTimer(1 * SECOND, 1 * SECOND);
    }

    @Override
    public void onGameEnding(Game game) {
        if (runTaskTimer != null) {
            runTaskTimer.cancel();
            runTaskTimer = null;
        }
    }
    
    public static OptionalInt getTimeLeft(@Nonnull Game game) {
        for (Option option : game.getOptions()) {
            if(option instanceof WinAfterTimeoutOption) {
                WinAfterTimeoutOption drawAfterTimeoutOption = (WinAfterTimeoutOption) option;
                return OptionalInt.of(drawAfterTimeoutOption.getTimeRemaining());
            }
        }
        return OptionalInt.empty();
    }
}
