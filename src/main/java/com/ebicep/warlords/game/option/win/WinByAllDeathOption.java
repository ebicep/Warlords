package com.ebicep.warlords.game.option.win;

import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.EnumSet;
import java.util.List;

/**
 * Triggers a win condition when there is only 1 team left where anyone is alive
 */
public class WinByAllDeathOption implements Option {

    @Override
    public void start(Game game) {
        final EnumSet<Team> teams = TeamMarker.getTeams(game);

        game.registerEvents(new Listener() {

            @EventHandler
            public void onDeath(WarlordsDeathEvent event) {
                if (event.getPlayer() instanceof WarlordsPlayer) {
                    teams.removeIf(team -> {
                        List<WarlordsPlayer> warlordsPlayers = PlayerFilterGeneric.playingGameWarlordsPlayers(game)
                                .matchingTeam(team)
                                .toList();
                                if (warlordsPlayers.isEmpty()) {
                                    return false;
                                }
                                for (WarlordsPlayer warlordsPlayer : warlordsPlayers) {
                                    if (warlordsPlayer.isAlive()) {
                                        return false;
                                    }
                                }
                                return true;
                            }
                    );
                    if (teams.size() == 1) {
                        Bukkit.getPluginManager().callEvent(new WarlordsGameTriggerWinEvent(game, WinByAllDeathOption.this, teams.iterator().next()));
                    }
                }
            }

        });
    }

}
