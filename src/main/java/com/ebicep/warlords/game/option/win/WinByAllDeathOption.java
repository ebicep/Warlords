package com.ebicep.warlords.game.option.win;

import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.List;

/**
 * Triggers a win condition when there is only 1 team left where anyone is alive
 */
public class WinByAllDeathOption implements Option {


    private final EnumSet<Team> deadTeams = EnumSet.noneOf(Team.class);
    private final EnumSet<Team> onlyCheckTeam; // if these teams got team wiped at least once and there is only 1 other team, then that team wins, scuffed for pve

    public WinByAllDeathOption(Team... onlyCheckTeam) {
        this.onlyCheckTeam = onlyCheckTeam.length == 0 ? null : EnumSet.copyOf(List.of(onlyCheckTeam));
    }

    @Override
    public void start(@Nonnull Game game) {
        final EnumSet<Team> teams = TeamMarker.getTeams(game);

        game.registerEvents(new Listener() {

            @EventHandler(ignoreCancelled = true)
            public void onDeath(WarlordsDeathEvent event) {
                if (!(event.getWarlordsEntity() instanceof WarlordsPlayer wp)) {
                    return;
                }
                if (onlyCheckTeam != null) {
                    for (Team team : onlyCheckTeam) {
                        List<WarlordsPlayer> teamPlayers = PlayerFilterGeneric
                                .playingGameWarlordsPlayers(game)
                                .excluding(wp)
                                .matchingTeam(team)
                                .toList();
                        if (teamPlayers.isEmpty() || teamPlayers.stream().allMatch(WarlordsEntity::isDead)) {
                            teams.remove(team);
                        }
                    }
                } else {
                    teams.removeIf(team -> {
                        List<WarlordsPlayer> warlordsPlayers = PlayerFilterGeneric
                                .playingGameWarlordsPlayers(game)
                                .excluding(wp)
                                .matchingTeam(team)
                                .toList();
                        if (warlordsPlayers.isEmpty() || warlordsPlayers.stream().allMatch(WarlordsEntity::isDead)) {
                            return true;
                        }
                        deadTeams.add(team);
                        return true;
                    });
                }
                if (teams.size() == 1) {
                    Bukkit.getPluginManager().callEvent(new WarlordsGameTriggerWinEvent(game, WinByAllDeathOption.this, teams.iterator().next()));
                }
            }

        });
    }

    public EnumSet<Team> getDeadTeams() {
        return deadTeams;
    }

}
