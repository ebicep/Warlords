package com.ebicep.warlords.game.option;

import com.ebicep.warlords.events.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Bukkit;

/**
 * Triggers a win condition when there is only 1 team left where anyone is alive
 */
public class WinByAllDeathOption implements Option {

    @Override
    public void start(Game game) {
        new GameRunnable(game) {
            @Override
            public void run() {
                Team winner = null;
                for (WarlordsPlayer player : Utils.iterable(game.warlordsPlayers())) {
                    if (!player.isAlive()) {
                        continue;
                    }
                    if (winner == null || winner == player.getTeam()) {
                        winner = player.getTeam();
                        continue;
                    }
                    return;
                }
                Bukkit.getPluginManager().callEvent(new WarlordsGameTriggerWinEvent(game, WinByAllDeathOption.this, winner));
            }
        }.runTaskTimer(7, 20);
    }

}
