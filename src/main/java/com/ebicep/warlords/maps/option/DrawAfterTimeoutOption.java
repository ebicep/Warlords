package com.ebicep.warlords.maps.option;

import com.ebicep.warlords.events.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.option.marker.TeamMarker;
import com.ebicep.warlords.maps.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.GameRunnable;
import static com.ebicep.warlords.util.GameRunnable.SECOND;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;

/**
 * Causes the game to end in a draw after a timeout
 */
public class DrawAfterTimeoutOption implements Option {

    public static final int DEFAULT_TIMER = 900;
    private static final int SCOREBOARD_PRIORITY = 10;

    private int timeRemaining;
    private int timeInitial;
    private SimpleScoreboardHandler scoreboard;
    private BukkitTask runTaskTimer; 

    public DrawAfterTimeoutOption() {
        this(DEFAULT_TIMER);
    }

    public DrawAfterTimeoutOption(int timeRemaining) {
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

    @Override
    public void register(Game game) {
        game.registerScoreboardHandler(scoreboard = new SimpleScoreboardHandler(SCOREBOARD_PRIORITY) {
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

                int minute = timeRemaining / 60;
                int second = timeRemaining % 60;
                StringBuilder message = new StringBuilder(64);
                if (winner != null) {
                    message.append(winner.coloredPrefix()).append(ChatColor.GOLD).append(" Wins in: ");
                } else {
                    message.append(ChatColor.WHITE).append("Time Left: ");
                }
                message.append(ChatColor.GREEN);
                if (minute < 10) {
                    message.append('0');
                }
                message.append(minute);
                message.append(':');
                if (second < 10) {
                    message.append('0');
                }
                message.append(second);
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
                    WarlordsGameTriggerWinEvent event = new WarlordsGameTriggerWinEvent(game, DrawAfterTimeoutOption.this, null);
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
}
