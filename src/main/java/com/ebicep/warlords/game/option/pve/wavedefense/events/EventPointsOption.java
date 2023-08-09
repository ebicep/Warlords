package com.ebicep.warlords.game.option.pve.wavedefense.events;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.events.game.pve.WarlordsGameWaveClearEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.game.option.pve.CurrencyOnEventOption;
import com.ebicep.warlords.game.option.win.WinByAllDeathOption;
import com.ebicep.warlords.player.general.Settings;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.Mobs;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EventPointsOption implements Option, Listener {

    private final ConcurrentHashMap<UUID, Integer> points = new ConcurrentHashMap<>();

    private final HashMap<Integer, Integer> perXWaveClear = new HashMap<>();
    private final HashMap<Class<?>, Integer> perMobKill = new HashMap<>();
    private int onKill = 0;
    private int reduceScoreOnAllDeath = 0; //percentage 30 = reduce 30%
    private int cap = Integer.MAX_VALUE;

    public EventPointsOption() {
    }

    public EventPointsOption onKill(int pointsPerKill) {
        onKill = pointsPerKill;
        return this;
    }

    public EventPointsOption reduceScoreOnAllDeath(int percentage, Team team) {
        reduceScoreOnAllDeath = percentage;
        return this;
    }

    public EventPointsOption onPerWaveClear(int wave, int points) {
        perXWaveClear.put(wave, points);
        return this;
    }

    public EventPointsOption onPerMobKill(Mobs mob, int points) {
        perMobKill.put(mob.mobClass, points);
        return this;
    }

    public EventPointsOption cap(int cap) {
        this.cap = cap;
        return this;
    }

    @Override
    public void register(@Nonnull Game game) {
        game.registerEvents(this);

        game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(CurrencyOnEventOption.SCOREBOARD_PRIORITY + 1, "currency") {
            @Nonnull
            @Override
            public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                String playerPoints = null;
                if (player != null) {
                    playerPoints = NumberFormat.addCommas(points.getOrDefault(player.getUuid(), 0));
                }
                if (new HashSet<>(points.values()).size() <= 1) {
                    playerPoints = NumberFormat.addCommas(points.values()
                                                                .stream()
                                                                .findFirst()
                                                                .orElse(0));
                }
                if (playerPoints != null) {
                    return Collections.singletonList(Component.text("Points: ").append(Component.text("✪ " + playerPoints, NamedTextColor.YELLOW)));
                }
                return Collections.singletonList(Component.empty());
            }
        });
    }

    @EventHandler
    public void onKill(WarlordsDeathEvent event) {
        WarlordsEntity deadEntity = event.getWarlordsEntity();
        WarlordsEntity killer = event.getKiller();
        if (!(killer instanceof WarlordsPlayer)) {
            return;
        }
        if (!(deadEntity instanceof WarlordsNPC)) {
            return;
        }
        if (onKill != 0) {
            PlayerFilterGeneric.playingGameWarlordsPlayers(killer.getGame())
                               .matchingTeam(killer.getTeam())
                               .forEach(warlordsPlayer -> addTo(warlordsPlayer, onKill));
        }
        if (!perMobKill.isEmpty()) {
            WarlordsNPC warlordsNPC = (WarlordsNPC) deadEntity;
            Integer mobPoint = perMobKill.get(warlordsNPC.getMob().getClass());
            if (mobPoint != null) {
                PlayerFilterGeneric.playingGameWarlordsPlayers(killer.getGame())
                                   .matchingTeam(killer.getTeam())
                                   .forEach(warlordsPlayer -> addTo(warlordsPlayer, mobPoint));
            }
        }
    }

    public void addTo(WarlordsPlayer warlordsPlayer, int amount) {
        points.merge(warlordsPlayer.getUuid(), amount, Integer::sum);
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(warlordsPlayer.getUuid(), warlordsPlayer.getEntity() instanceof Player);
        if (databasePlayer.getChatEventPointsMode() == Settings.ChatSettings.ChatEventPoints.ALL) {
            warlordsPlayer.sendMessage(Component.text("+" + amount + " ✪ Points", NamedTextColor.YELLOW));
        }
    }

    @EventHandler
    public void onDeath(WarlordsGameTriggerWinEvent event) {
        if (reduceScoreOnAllDeath == 0) {
            return;
        }
        Option cause = event.getCause();
        if (cause instanceof WinByAllDeathOption) {
            double reduceMultiplyBy = (100 - reduceScoreOnAllDeath) / 100.0;
            EnumSet<Team> deadTeams = ((WinByAllDeathOption) cause).getDeadTeams();
            for (Team deadTeam : deadTeams) {
                PlayerFilterGeneric
                        .playingGameWarlordsPlayers(event.getGame())
                        .matchingTeam(deadTeam)
                        .forEach(warlordsPlayer -> {
                            int newScore = (int) (points.getOrDefault(warlordsPlayer.getUuid(), 0) * reduceMultiplyBy);
                            points.put(warlordsPlayer.getUuid(), newScore);
                        });
            }
        }
    }

    @EventHandler
    public void onWaveClear(WarlordsGameWaveClearEvent event) {
        if (perXWaveClear.isEmpty()) {
            return;
        }
        int waveCleared = event.getWaveCleared();
        perXWaveClear
                .keySet()
                .stream()
                .filter(integer -> waveCleared % integer == 0)
                .max(Comparator.naturalOrder())
                .ifPresent(wave -> addToAll(perXWaveClear.get(wave)));

    }

    public void addToAll(int points) {
        this.points.replaceAll((uuid, integer) -> {
            WarlordsEntity warlordsPlayer = Warlords.getPlayer(uuid);
            if (warlordsPlayer != null) {
                DatabasePlayer databasePlayer = DatabaseManager.getPlayer(warlordsPlayer.getUuid(), warlordsPlayer.getEntity() instanceof Player);
                if (databasePlayer.getChatEventPointsMode() == Settings.ChatSettings.ChatEventPoints.ALL) {
                    warlordsPlayer.sendMessage(Component.text("+" + points + " ✪ Points", NamedTextColor.YELLOW));
                }
            }
            return integer + points;
        });
    }

    public ConcurrentHashMap<UUID, Integer> getPoints() {
        return points;
    }

    public int getCap() {
        return cap;
    }
}
