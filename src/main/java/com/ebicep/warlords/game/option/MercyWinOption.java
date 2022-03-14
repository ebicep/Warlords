package com.ebicep.warlords.game.option;

import com.ebicep.warlords.events.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.marker.TimerSkipAbleMarker;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.Bukkit;

import java.util.EnumSet;

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

/**
 * Causes a win for the game to trigger if any team has X amount of points more
 * to any other team
 */
public class MercyWinOption implements Option {

    private static final int DEFAULT_LIMIT = 550;
    private static final int DEFAULT_TIMER = 5 * 60;
    private int limit;
    private int timer;

    public MercyWinOption() {
        this(DEFAULT_LIMIT, DEFAULT_TIMER);
    }

    public MercyWinOption(int mercyLimit) {
        this(DEFAULT_LIMIT, DEFAULT_TIMER);
    }

    public MercyWinOption(int mercyLimit, int timer) {
        this.limit = mercyLimit;
        this.timer = timer;
    }

    public int getLimit() {
        return limit;
    }

    public MercyWinOption setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public void register(Game game) {
        game.registerGameMarker(TimerSkipAbleMarker.class, (delayInTicks) -> {
            this.timer -= delayInTicks / 20;
        });
    }

    @Override
    public void start(Game game) {
        EnumSet<Team> teams = TeamMarker.getTeams(game);
        new GameRunnable(game) {
            @Override
            public void run() {
                if (timer > 0) {
                    timer--;
                    return;
                }
                int higest = Integer.MIN_VALUE;
                int secondHighest = Integer.MIN_VALUE;
                Team winner = null;
                for (Team team : teams) {
                    int points = game.getPoints(team);
                    if (points > higest) {
                        winner = team;
                        secondHighest = higest;
                        higest = points;
                    } else if (points > secondHighest) {
                        secondHighest = points;
                    }
                }
                if (higest - limit >= secondHighest) {
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
