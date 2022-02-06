package com.ebicep.warlords.maps.option;

import com.ebicep.warlords.events.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.option.marker.TeamMarker;
import com.ebicep.warlords.util.GameRunnable;
import static com.ebicep.warlords.util.GameRunnable.SECOND;
import java.util.EnumSet;
import org.bukkit.Bukkit;

/**
 * Causes a win for the game to trigger if any team has X amount of points more to any other team
 */
public class MercyWinOption implements Option {
    private static final int DEFAULT_MERCY_LIMIT = 550;
    private int mercyLimit;

    public MercyWinOption() {
        this(DEFAULT_MERCY_LIMIT);
    }
    public MercyWinOption(int mercyLimit) {
        this.mercyLimit = mercyLimit;
    }

    public int getMercyLimit() {
        return mercyLimit;
    }

    public void setMercyLimit(int mercyLimit) {
        this.mercyLimit = mercyLimit;
    }
    
    @Override
    public void start(Game game) {
        EnumSet<Team> teams = TeamMarker.getTeams(game);
        new GameRunnable(game) {
            @Override
            public void run() {
                int higest = Integer.MIN_VALUE;
                int secondHighest = Integer.MIN_VALUE;
                Team winner = null;
                for (Team team : teams) {
                    int points = game.getStats(team).points();
                    if (points > higest) {
                        winner = team;
                        secondHighest = higest;
                        higest = points;
                    } else if (points > secondHighest) {
                        secondHighest = points;
                    }
                }
                if (higest - mercyLimit >= secondHighest) {
                    WarlordsGameTriggerWinEvent event = new WarlordsGameTriggerWinEvent(game, MercyWinOption.this, winner);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        cancel();
                    }
                }
            }
        }.runTaskTimer(1 * SECOND, 1 * SECOND);
    }

}
