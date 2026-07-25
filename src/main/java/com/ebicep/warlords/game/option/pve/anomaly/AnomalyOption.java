package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AnomalyOption extends AbstractAnomalyOption {

    public static final int OBJECTIVE_COUNT = 3;
    public static final int OBJECTIVE_DURATION_TICKS = 120 * GameRunnable.SECOND;
    private static final int MOB_SPAWN_INTERVAL = GameRunnable.SECOND;
    private static final int BASE_RELIC_HEALTH = 25_000;

    private final boolean[] objectiveSuccess = new boolean[OBJECTIVE_COUNT];

    private AnomalyRelic activeRelic;
    private int activeObjective = -1;
    private int objectiveTicks;
    private int preparationTicks = START_DELAY_TICKS;

    @Override
    public void register(@Nonnull Game game) {
        super.register(game);
        game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(5, "anomaly_objective") {
            @Nonnull
            @Override
            public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                return getObjectiveScoreboard();
            }
        });
    }

    @Override
    protected boolean handleSpecialDeath(WarlordsDeathEvent event) {
        WarlordsEntity dead = event.getWarlordsEntity();
        if (!(dead instanceof WarlordsNPC warlordsNPC) || warlordsNPC.getMob() != activeRelic) {
            return false;
        }
        failCurrentObjective();
        return true;
    }

    @Override
    public void start(@Nonnull Game game) {
        new GameRunnable(game) {
            @Override
            public void run() {
                beginObjective(0);
            }
        }.runTaskLater(START_DELAY_TICKS);

        new GameRunnable(game) {
            @Override
            public void run() {
                if (completed) {
                    cancel();
                    return;
                }
                incrementTicks();
                if (preparationTicks > 0) {
                    preparationTicks--;
                }
                if (activeRelic == null) {
                    return;
                }
                mobTick();
                objectiveTicks++;
                if (objectiveTicks % MOB_SPAWN_INTERVAL == 0 && mobCount() < getMaximumMobCount()) {
                    spawnPressureMob();
                }
                if (objectiveTicks % (30 * GameRunnable.SECOND) == 0) {
                    announce(Component.text("Relic " + (activeObjective + 1) + " has " + getSecondsRemaining() + " seconds remaining.", NamedTextColor.YELLOW));
                }
                if (objectiveTicks >= OBJECTIVE_DURATION_TICKS) {
                    completeCurrentObjective();
                }
            }
        }.runTaskTimer(0, 1);
    }

    private void beginObjective(int objectiveIndex) {
        if (objectiveIndex >= OBJECTIVE_COUNT) {
            finishAnomaly(objectiveSuccess, "Opex stabilized.");
            return;
        }
        activeObjective = objectiveIndex;
        objectiveTicks = 0;
        clearHostileMobs();
        AnomalyObjectiveMarker marker = getObjectiveMarker(objectiveIndex);
        int relicHealth = BASE_RELIC_HEALTH + Math.max(0, playerCount() - 1) * 10_000;
        activeRelic = new AnomalyRelic(marker.getLocation().clone(), objectiveIndex, relicHealth);
        WarlordsNPC relicNpc = activeRelic.toNPC(game, Team.BLUE, npc -> activeRelic.onSpawn(this));
        game.addNPC(relicNpc);
        announce(Component.text("Defend Relic " + (objectiveIndex + 1) + " for 120 seconds!", NamedTextColor.AQUA));
        game.forEachOnlinePlayer((player, team) -> player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 2, 1));
    }

    private void completeCurrentObjective() {
        if (activeRelic == null) {
            return;
        }
        objectiveSuccess[activeObjective] = true;
        announce(Component.text("Relic " + (activeObjective + 1) + " secured. Reward cache unlocked!", NamedTextColor.GREEN));
        OpexCurrencyOption.grantRelicReward(game);
        removeActiveRelic();
        clearHostileMobs();
        teleportToNextObjective();
        scheduleNextObjective();
    }

    private void failCurrentObjective() {
        if (activeRelic == null) {
            return;
        }
        objectiveSuccess[activeObjective] = false;
        Utils.playGlobalSound(activeRelic.getSpawnLocation(), "raid.church.dingalt", 2, 0.5f);
        announce(Component.text("Relic " + (activeObjective + 1) + " was lost. Its reward cache is forfeited.", NamedTextColor.RED));
        removeActiveRelic();
        clearHostileMobs();
        teleportToNextObjective();
        scheduleNextObjective();
    }

    private void teleportToNextObjective() {
        int nextObjective = activeObjective + 1;
        if (nextObjective >= OBJECTIVE_COUNT) {
            return;
        }
        Location destination = getObjectiveMarker(nextObjective).getLocation().clone().add(0, 1, 0);
        game.forEachOnlinePlayer((player, team) -> {
            player.teleport(destination);
            player.playSound(destination, Sound.ENTITY_ENDERMAN_TELEPORT, 1.5f, 1.1f);
            player.playSound(destination, "raid.church.ding", 1, 0.5f);
        });
        announce(Component.text("The party has been transported to Relic " + (nextObjective + 1) + ".", NamedTextColor.LIGHT_PURPLE));
    }

    private AnomalyObjectiveMarker getObjectiveMarker(int objectiveIndex) {
        return game.getMarkers(AnomalyObjectiveMarker.class)
                .stream()
                .filter(objective -> objective.getObjectiveIndex() == objectiveIndex)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Missing anomaly objective marker " + objectiveIndex + " for " + game.getMap().getMapName()));
    }

    private void scheduleNextObjective() {
        int nextObjective = activeObjective + 1;
        new GameRunnable(game) {
            @Override
            public void run() {
                beginObjective(nextObjective);
            }
        }.runTaskLater(3 * GameRunnable.SECOND);
    }

    private void spawnPressureMob() {
        List<AnomalySpawnMarker> spawnMarkers = game.getMarkers(AnomalySpawnMarker.class)
                .stream()
                .filter(marker -> marker.getObjectiveIndex() == activeObjective)
                .toList();
        if (spawnMarkers.isEmpty()) {
            return;
        }
        Location location = spawnMarkers.get(ThreadLocalRandom.current().nextInt(spawnMarkers.size())).getLocation().clone();
        Mob[] pool;
        double progress = objectiveTicks / (double) OBJECTIVE_DURATION_TICKS;
        if (progress < 0.34) {
            pool = Mob.BASIC;
        } else if (progress < 0.67) {
            pool = ThreadLocalRandom.current().nextBoolean() ? Mob.BASIC : Mob.INTERMEDIATE;
        } else {
            int roll = ThreadLocalRandom.current().nextInt(3);
            pool = roll == 0 ? Mob.BASIC : roll == 1 ? Mob.INTERMEDIATE : Mob.ADVANCED;
        }
        spawnNewMob(pool[ThreadLocalRandom.current().nextInt(pool.length)].createMob(location), Team.RED);
    }

    private int getMaximumMobCount() {
        return 8 + (playerCount() * 6) + activeObjective * 2;
    }

    private List<Component> getObjectiveScoreboard() {
        List<Component> lines = new ArrayList<>();
        if (completed) {
            lines.add(Component.text("Anomaly Complete", NamedTextColor.GREEN));
            return lines;
        }
        if (activeRelic == null) {
            if (activeObjective < 0) {
                int seconds = Math.max(0, (preparationTicks + GameRunnable.SECOND - 1) / GameRunnable.SECOND);
                lines.add(Component.text("Anomaly starts in: ", NamedTextColor.WHITE)
                        .append(Component.text(seconds + "s", NamedTextColor.YELLOW)));
            } else {
                lines.add(Component.text("Preparing Relic " + Math.min(activeObjective + 2, OBJECTIVE_COUNT) + "...", NamedTextColor.YELLOW));
            }
            return lines;
        }

        lines.add(Component.text("Relic: ", NamedTextColor.WHITE)
                .append(Component.text((activeObjective + 1) + "/" + OBJECTIVE_COUNT, NamedTextColor.AQUA)));
        lines.add(Component.text("Defend for: ", NamedTextColor.WHITE)
                .append(Component.text(getSecondsRemaining() + "s", NamedTextColor.YELLOW)));

        WarlordsNPC relicNpc = activeRelic.getWarlordsNPC();
        float healthRatio = relicNpc.getCurrentHealth() / relicNpc.getMaxHealth();
        NamedTextColor healthColor = healthRatio >= .5f
                ? NamedTextColor.GREEN
                : healthRatio >= .25f ? NamedTextColor.YELLOW : NamedTextColor.RED;
        lines.add(Component.text("Relic Health: ", NamedTextColor.WHITE)
                .append(Component.text("❤ " + Math.round(relicNpc.getCurrentHealth()), healthColor))
                .append(Component.text(" / " + Math.round(relicNpc.getMaxHealth()), NamedTextColor.WHITE)));
        return lines;
    }

    private int getSecondsRemaining() {
        return Math.max(0, (OBJECTIVE_DURATION_TICKS - objectiveTicks) / GameRunnable.SECOND);
    }

    private void removeActiveRelic() {
        if (activeRelic == null) {
            return;
        }
        AnomalyRelic relic = activeRelic;
        activeRelic = null;
        WarlordsNPC npc = relic.getWarlordsNPC();
        relic.cleanup(this);
        npc.cleanup();
        game.getPlayers().remove(npc.getUuid());
        Warlords.removePlayer(npc.getUuid());
    }

    @Override
    public void onGameCleanup(@Nonnull Game game) {
        removeActiveRelic();
        super.onGameCleanup(game);
    }
}
