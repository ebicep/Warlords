package com.ebicep.warlords.game.option.wavedefense.events;

import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.game.option.wavedefense.CurrencyOnEventOption;
import com.ebicep.warlords.game.option.win.WinByAllDeathOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class EventPointsOption implements Option {

    private final HashMap<UUID, Integer> points = new HashMap<>();
    private final List<Listener> listeners = new ArrayList<>();

    @Override
    public void register(@Nonnull Game game) {
        for (Listener listener : listeners) {
            game.registerEvents(listener);
        }

        game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(CurrencyOnEventOption.SCOREBOARD_PRIORITY + 1, "currency") {
            @Nonnull
            @Override
            public List<String> computeLines(@Nullable WarlordsPlayer player) {
                return Collections.singletonList(player != null ? "Points: " + ChatColor.YELLOW + "✪ " + NumberFormat.addCommas(points.getOrDefault(player.getUuid(),
                        0
                )) : "");
            }
        });
    }

    public EventPointsOption addPointsOnKill(int pointsPerKill) {
        listeners.add(new Listener() {

            @EventHandler
            public void onKill(WarlordsDeathEvent event) {
                WarlordsEntity killer = event.getKiller();
                if (!(killer instanceof WarlordsPlayer)) {
                    return;
                }
                PlayerFilterGeneric
                        .playingGameWarlordsPlayers(killer.getGame())
                        .matchingTeam(killer.getTeam())
                        .forEach(warlordsPlayer -> points.merge(warlordsPlayer.getUuid(), pointsPerKill, Integer::sum));
            }

        });
        return this;
    }

    public EventPointsOption reduceScoreOnAllDeath(int percentage, Team team) {
        double reduceMultiplyBy = (100 - percentage) / 100.0;
        listeners.add(new Listener() {

            @EventHandler
            public void onDeath(WarlordsGameTriggerWinEvent event) {
                Option cause = event.getCause();
                if (cause instanceof WinByAllDeathOption) {
                    boolean allDead = PlayerFilterGeneric
                            .playingGameWarlordsPlayers(event.getGame())
                            .matchingTeam(team)
                            .stream()
                            .allMatch(WarlordsEntity::isDead);
                    if (allDead) {
                        points.replaceAll((uuid, points) -> (int) (points * reduceMultiplyBy));
                    }
                }
            }

        });
        return this;
    }

    public HashMap<UUID, Integer> getPoints() {
        return points;
    }

}
