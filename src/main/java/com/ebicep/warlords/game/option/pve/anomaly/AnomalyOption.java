package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsRespawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AnomalyOption extends AbstractAnomalyOption {

    public static final int OBJECTIVE_COUNT = 3;
    public static final int OBJECTIVE_DURATION_TICKS = 120 * GameRunnable.SECOND;
    private static final int MOB_SPAWN_INTERVAL = 10;
    private static final int BASE_RELIC_HEALTH = 25_000;
    private static final int[] BOSS_TRIGGER_TICKS = {
            30 * GameRunnable.SECOND,
            60 * GameRunnable.SECOND,
            90 * GameRunnable.SECOND
    };
    private static final Mob[] RELIC_BOSSES = {
            Mob.GHOULCALLER,
            Mob.MITHRA,
            Mob.BOLTARO,
            Mob.NARMER,
            Mob.CHESSKING
    };

    private final boolean[] objectiveSuccess = new boolean[OBJECTIVE_COUNT];
    private final List<Mob> availableRelicBosses = new ArrayList<>();

    private AnomalyRelic activeRelic;
    private AbstractMob activeBoss;
    private Location playerRespawnLocation;
    private int activeObjective = -1;
    private int objectiveTicks;
    private int bossPhasesStarted;
    private int preparationTicks = START_DELAY_TICKS;
    private boolean defeated;
    private SimpleScoreboardHandler objectiveScoreboardHandler;

    @Override
    public void register(@Nonnull Game game) {
        super.register(game);
        game.registerEvents(new Listener() {
            @EventHandler(ignoreCancelled = true)
            public void onRespawn(WarlordsRespawnEvent event) {
                WarlordsEntity warlordsEntity = event.getWarlordsEntity();
                if (!(warlordsEntity instanceof WarlordsPlayer) || warlordsEntity.getGame() != game || playerRespawnLocation == null) {
                    return;
                }

                Location respawnLocation = event.getRespawnLocation();
                respawnLocation.setWorld(playerRespawnLocation.getWorld());
                respawnLocation.setX(playerRespawnLocation.getX());
                respawnLocation.setY(playerRespawnLocation.getY());
                respawnLocation.setZ(playerRespawnLocation.getZ());
                respawnLocation.setYaw(playerRespawnLocation.getYaw());
                respawnLocation.setPitch(playerRespawnLocation.getPitch());
            }
        });
        game.registerGameMarker(ScoreboardHandler.class, objectiveScoreboardHandler = new SimpleScoreboardHandler(5, "anomaly_objective") {
            @Nonnull
            @Override
            public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                return getObjectiveScoreboard();
            }
        });
    }

    @Override
    protected boolean[] getCacheEligibility() {
        return objectiveSuccess;
    }

    @Override
    protected boolean handleSpecialDeath(WarlordsDeathEvent event) {
        WarlordsEntity dead = event.getWarlordsEntity();
        if (!(dead instanceof WarlordsNPC warlordsNPC) || warlordsNPC.getMob() != activeRelic) {
            return false;
        }
        failAnomaly();
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
                if (completed || defeated) {
                    cancel();
                    return;
                }
                incrementTicks();
                if (objectiveScoreboardHandler != null && getTicksElapsed() % 20 == 0) {
                    objectiveScoreboardHandler.markChanged();
                }
                if (preparationTicks > 0) {
                    preparationTicks--;
                }
                if (activeRelic == null) {
                    return;
                }

                mobTick();
                if (handleActiveBossPhase()) {
                    return;
                }

                objectiveTicks++;
                if (shouldStartBossPhase()) {
                    startBossPhase();
                    return;
                }
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
        bossPhasesStarted = 0;
        activeBoss = null;
        availableRelicBosses.clear();
        for (Mob relicBoss : RELIC_BOSSES) {
            if (!availableRelicBosses.contains(relicBoss)) {
                availableRelicBosses.add(relicBoss);
            }
        }
        clearHostileMobs();
        AnomalyObjectiveMarker marker = getObjectiveMarker(objectiveIndex);
        playerRespawnLocation = marker.getLocation().clone().add(0, 1, 0);
        int relicHealth = BASE_RELIC_HEALTH + Math.max(0, playerCount() - 1) * 5_000;
        activeRelic = new AnomalyRelic(marker.getLocation().clone(), objectiveIndex, relicHealth);
        WarlordsNPC relicNpc = activeRelic.toNPC(game, Team.BLUE, npc -> activeRelic.onSpawn(this));
        game.addNPC(relicNpc);
        announce(Component.text("Defend Relic " + (objectiveIndex + 1) + " for 2 minutes!", NamedTextColor.YELLOW));
        game.forEachOnlinePlayer((player, team) -> player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 2, 1));
        markObjectiveScoreboardChanged();
    }

    private void markObjectiveScoreboardChanged() {
        if (objectiveScoreboardHandler != null) {
            objectiveScoreboardHandler.markChanged();
        }
    }

    private void completeCurrentObjective() {
        if (activeRelic == null) {
            return;
        }
        objectiveSuccess[activeObjective] = true;
        activeBoss = null;
        announce(Component.text("Relic " + (activeObjective + 1) + " secured. Reward cache unlocked!", NamedTextColor.GREEN));
        OpexCurrencyOption.grantRelicReward(game);
        removeActiveRelic();
        clearHostileMobs();
        teleportToNextObjective();
        scheduleNextObjective();
        markObjectiveScoreboardChanged();
    }

    private void failAnomaly() {
        if (activeRelic == null || defeated) {
            return;
        }
        defeated = true;
        objectiveSuccess[activeObjective] = false;
        activeBoss = null;
        Location relicLocation = activeRelic.getSpawnLocation().clone();
        Utils.playGlobalSound(relicLocation, "raid.church.dingalt", 2, 0.5f);
        announce(Component.text("Relic " + (activeObjective + 1) + " was destroyed. The Opex has fallen!", NamedTextColor.RED));
        removeActiveRelic();
        clearHostileMobs();
        Bukkit.getPluginManager().callEvent(new WarlordsGameTriggerWinEvent(game, this, Team.RED));
        markObjectiveScoreboardChanged();
    }

    private boolean shouldStartBossPhase() {
        return bossPhasesStarted < BOSS_TRIGGER_TICKS.length && objectiveTicks >= BOSS_TRIGGER_TICKS[bossPhasesStarted];
    }

    private void startBossPhase() {
        if (availableRelicBosses.isEmpty()) {
            bossPhasesStarted = BOSS_TRIGGER_TICKS.length;
            return;
        }

        clearHostileMobs();

        Mob bossType = availableRelicBosses.remove(ThreadLocalRandom.current().nextInt(availableRelicBosses.size()));
        Location spawnLocation = getBossSpawnLocation();
        activeBoss = bossType.createMob(spawnLocation);
        bossPhasesStarted++;
        spawnNewMob(activeBoss, Team.RED);

        String bossName = activeBoss.getWarlordsNPC().getName();
        announce(Component.text("Relic stabilization halted! ", NamedTextColor.RED)
                .append(Component.text("Defeat " + bossName + " to continue.", NamedTextColor.GOLD)));
        announce(Component.text("The defense timer is frozen at " + getSecondsRemaining() + " seconds.", NamedTextColor.YELLOW));
        Utils.playGlobalSound(spawnLocation, Sound.ENTITY_WITHER_SPAWN, 2, 0.75f);
    }

    private boolean handleActiveBossPhase() {
        if (activeBoss == null) {
            return false;
        }

        WarlordsNPC bossNpc = activeBoss.getWarlordsNPC();
        if (bossNpc != null && bossNpc.isAlive()) {
            return true;
        }

        String bossName = bossNpc == null ? "Boss" : bossNpc.getName();
        activeBoss = null;
        clearHostileMobs();
        announce(Component.text(bossName + " defeated. Relic stabilization resumed.", NamedTextColor.GREEN));
        game.forEachOnlinePlayer((player, team) -> player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.5f, 1.2f));
        return false;
    }

    private Location getBossSpawnLocation() {
        List<AnomalySpawnMarker> spawnMarkers = getActiveSpawnMarkers();
        if (!spawnMarkers.isEmpty()) {
            return spawnMarkers.get(ThreadLocalRandom.current().nextInt(spawnMarkers.size())).getLocation().clone();
        }
        return activeRelic.getSpawnLocation().clone().add(4, 0, 0);
    }

    private void teleportToNextObjective() {
        int nextObjective = activeObjective + 1;
        if (nextObjective >= OBJECTIVE_COUNT) {
            return;
        }
        Location destination = getObjectiveMarker(nextObjective).getLocation().clone().add(0, 1, 0);
        playerRespawnLocation = destination.clone();
        game.forEachOnlinePlayer((player, team) -> {
            player.teleport(destination);
            player.playSound(destination, Sound.ENTITY_ENDERMAN_TELEPORT, 1.5f, 1.1f);
            player.playSound(destination, "raid.church.ding", 1.5f, 0.5f);
        });
        announce(Component.text("The party has been transported to Relic " + (nextObjective + 1) + ".", NamedTextColor.YELLOW));
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

    private List<AnomalySpawnMarker> getActiveSpawnMarkers() {
        return game.getMarkers(AnomalySpawnMarker.class)
                .stream()
                .filter(marker -> marker.getObjectiveIndex() == activeObjective)
                .toList();
    }

    private void spawnPressureMob() {
        List<AnomalySpawnMarker> spawnMarkers = getActiveSpawnMarkers();
        if (spawnMarkers.isEmpty()) {
            return;
        }
        Location location = spawnMarkers.get(ThreadLocalRandom.current().nextInt(spawnMarkers.size())).getLocation().clone();
        Mob[] pool;
        double progress = objectiveTicks / (double) OBJECTIVE_DURATION_TICKS;
        if (progress < 0.34) {
            pool = Mob.BASIC;
        } else if (progress < 0.67) {
            pool = Mob.INTERMEDIATE;
        } else {
            int roll = ThreadLocalRandom.current().nextInt(3);
            pool = roll == 0 ? Mob.BASIC : roll == 1 ? Mob.ADVANCED : Mob.ELITE;
        }
        spawnNewMob(pool[ThreadLocalRandom.current().nextInt(pool.length)].createMob(location), Team.RED);
    }

    private int getMaximumMobCount() {
        return 10 + (playerCount() * 10) + activeObjective * 2;
    }

    private List<Component> getObjectiveScoreboard() {
        List<Component> lines = new ArrayList<>();
        if (defeated) {
            lines.add(Component.text("Anomaly Failed", NamedTextColor.RED));
            return lines;
        }
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

        boolean bossPhase = isActiveBossAlive();
        lines.add(Component.text("Defend for: ", NamedTextColor.WHITE)
                .append(Component.text(getSecondsRemaining() + "s", NamedTextColor.YELLOW))
                .append(bossPhase ? Component.text("  PAUSED", NamedTextColor.RED) : Component.empty()));
        if (bossPhase) {
            lines.add(Component.text("Boss: ", NamedTextColor.WHITE)
                    .append(Component.text(activeBoss.getWarlordsNPC().getName(), NamedTextColor.RED)));
        }

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

    private boolean isActiveBossAlive() {
        if (activeBoss == null) {
            return false;
        }
        WarlordsNPC bossNpc = activeBoss.getWarlordsNPC();
        return bossNpc != null && bossNpc.isAlive();
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
        activeBoss = null;
        availableRelicBosses.clear();
        playerRespawnLocation = null;
        removeActiveRelic();
        super.onGameCleanup(game);
    }
}