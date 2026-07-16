package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.events.game.pve.WarlordsMobSpawnEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.rewards.PveRewards;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.commands.MobCommand;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.newitems.setbonus.NewItemsSetBonus;
import com.ebicep.warlords.pve.rewards.RewardInventory;
import com.ebicep.warlords.util.warlords.GameRunnable;
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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class AnomalyOption implements PveOption {

    public static final int OBJECTIVE_COUNT = 3;
    public static final int OBJECTIVE_DURATION_TICKS = 120 * GameRunnable.SECOND;
    private static final int START_DELAY_TICKS = 10 * GameRunnable.SECOND;
    private static final int MOB_SPAWN_INTERVAL = 2 * GameRunnable.SECOND;
    private static final int BASE_RELIC_HEALTH = 25_000;

    private final ConcurrentHashMap<AbstractMob, MobData> mobs = new ConcurrentHashMap<>();
    private final AtomicInteger ticksElapsed = new AtomicInteger();
    private final boolean[] objectiveSuccess = new boolean[OBJECTIVE_COUNT];

    private Game game;
    private AnomalyRewards rewards;
    private Anomalies currentAnomaly;
    private NewItemsSetBonus featuredLegendarySet;
    private AnomalyRelic activeRelic;
    private long rotationStart;
    private int activeObjective = -1;
    private int objectiveTicks;
    private int preparationTicks = START_DELAY_TICKS;
    private boolean completed;

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        this.currentAnomaly = AnomalyRotation.getCurrentAnomaly();
        for (Anomalies anomaly : Anomalies.VALUES) {
            if (anomaly.getMap() == game.getMap()) {
                this.currentAnomaly = anomaly;
                break;
            }
        }
        this.rotationStart = AnomalyRotation.getRotationStart().getEpochSecond();
        this.featuredLegendarySet = AnomalyRotation.getGuaranteedLegendarySet();
        this.rewards = new AnomalyRewards(this);
        game.registerEvents(getBaseListener());
        game.registerEvents(new Listener() {
            @EventHandler(ignoreCancelled = true)
            public void onDeath(WarlordsDeathEvent event) {
                WarlordsEntity dead = event.getWarlordsEntity();
                if (!(dead instanceof WarlordsNPC warlordsNPC)) {
                    return;
                }
                AbstractMob mob = warlordsNPC.getMob();
                if (mob == activeRelic) {
                    failCurrentObjective();
                    return;
                }
                if (!mobs.containsKey(mob)) {
                    return;
                }
                mob.onDeath(event.getKiller(), dead.getLocation(), AnomalyOption.this);
                new GameRunnable(game) {
                    @Override
                    public void run() {
                        removeHostileMob(mob);
                    }
                }.runTaskLater(1);
            }
        });
        game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(5, "anomaly_objective") {
            @Nonnull
            @Override
            public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                return getObjectiveScoreboard();
            }
        });
        game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(6, "anomaly_players") {
            @Nonnull
            @Override
            public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                return healthScoreboard(game);
            }
        });
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
                ticksElapsed.incrementAndGet();
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
            completeAnomaly();
            return;
        }
        activeObjective = objectiveIndex;
        objectiveTicks = 0;
        clearHostileMobs();
        AnomalyObjectiveMarker marker = game.getMarkers(AnomalyObjectiveMarker.class)
                .stream()
                .filter(objective -> objective.getObjectiveIndex() == objectiveIndex)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Missing anomaly objective marker " + objectiveIndex + " for " + game.getMap().getMapName()));
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
        removeActiveRelic();
        scheduleNextObjective();
    }

    private void failCurrentObjective() {
        if (activeRelic == null) {
            return;
        }
        objectiveSuccess[activeObjective] = false;
        announce(Component.text("Relic " + (activeObjective + 1) + " was lost. Its reward cache is forfeited.", NamedTextColor.RED));
        removeActiveRelic();
        scheduleNextObjective();
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

    private void completeAnomaly() {
        completed = true;
        clearHostileMobs();
        game.warlordsPlayers().forEach(warlordsPlayer -> {
            DatabasePlayer databasePlayer = DatabaseManager.getPlayer(warlordsPlayer.getUuid());
            int cachesGranted = 0;
            for (int i = 0; i < OBJECTIVE_COUNT; i++) {
                if (!objectiveSuccess[i]) {
                    continue;
                }
                AnomalyRewardCache cache = currentAnomaly.getRewardPools().get(i)
                        .createCache(featuredLegendarySet, rotationStart);
                databasePlayer.getPveStats().getGameEventRewards().add(cache);
                cachesGranted++;
            }
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
            warlordsPlayer.sendMessage(Component.text("Anomaly complete: " + cachesGranted + "/3 reward caches added to your Reward Inventory.", NamedTextColor.GREEN));
            if (cachesGranted > 0) {
                RewardInventory.sendRewardMessage(
                        warlordsPlayer.getUuid(),
                        Component.text(cachesGranted + " Anomaly Reward " + (cachesGranted == 1 ? "Cache is" : "Caches are") + " ready to claim.", NamedTextColor.AQUA)
                );
            }
            warlordsPlayer.playSound(warlordsPlayer.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2, 1);
        });
        Bukkit.getPluginManager().callEvent(new WarlordsGameTriggerWinEvent(game, this, Team.BLUE));
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
                lines.add(Component.text("Anomaly starts in: ", NamedTextColor.GRAY)
                        .append(Component.text(seconds + "s", NamedTextColor.YELLOW)));
            } else {
                lines.add(Component.text("Preparing Relic " + Math.min(activeObjective + 2, OBJECTIVE_COUNT) + "...", NamedTextColor.YELLOW));
            }
            return lines;
        }

        lines.add(Component.text("Relic: ", NamedTextColor.GRAY)
                .append(Component.text((activeObjective + 1) + "/" + OBJECTIVE_COUNT, NamedTextColor.AQUA)));
        lines.add(Component.text("Defend for: ", NamedTextColor.GRAY)
                .append(Component.text(getSecondsRemaining() + "s", NamedTextColor.YELLOW)));

        WarlordsNPC relicNpc = activeRelic.getWarlordsNPC();
        float healthRatio = relicNpc.getCurrentHealth() / relicNpc.getMaxHealth();
        NamedTextColor healthColor = healthRatio >= .5f
                ? NamedTextColor.GREEN
                : healthRatio >= .25f ? NamedTextColor.YELLOW : NamedTextColor.RED;
        lines.add(Component.text("Relic Health: ", NamedTextColor.GRAY)
                .append(Component.text("❤ " + Math.round(relicNpc.getCurrentHealth()), healthColor))
                .append(Component.text(" / " + Math.round(relicNpc.getMaxHealth()), NamedTextColor.GRAY)));
        return lines;
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
        return 8 + playerCount() * 4 + activeObjective * 2;
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

    private void clearHostileMobs() {
        for (AbstractMob mob : new ArrayList<>(mobs.keySet())) {
            removeHostileMob(mob);
        }
    }

    private void removeHostileMob(AbstractMob mob) {
        if (!mobs.containsKey(mob)) {
            return;
        }
        WarlordsNPC npc = mob.getWarlordsNPC();
        mob.cleanup(this);
        npc.cleanup();
        mobs.remove(mob);
        game.getPlayers().remove(npc.getUuid());
        Warlords.removePlayer(npc.getUuid());
        MobCommand.SPAWNED_MOBS.remove(mob);
    }

    private void announce(Component component) {
        game.forEachOnlinePlayer((player, team) -> player.sendMessage(component));
    }

    public int getSecondsRemaining() {
        return Math.max(0, (OBJECTIVE_DURATION_TICKS - objectiveTicks) / GameRunnable.SECOND);
    }

    public int getActiveObjective() {
        return activeObjective;
    }

    public boolean[] getObjectiveSuccess() {
        return objectiveSuccess.clone();
    }

    public Anomalies getCurrentAnomaly() {
        return currentAnomaly;
    }

    @Override
    public Set<AbstractMob> getMobs() {
        return mobs.keySet();
    }

    @Override
    public ConcurrentHashMap<AbstractMob, ? extends MobData> getMobsMap() {
        return mobs;
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public int getTicksElapsed() {
        return ticksElapsed.get();
    }

    @Override
    public void spawnNewMob(AbstractMob mob, Team team) {
        WarlordsNPC npc = mob.toNPC(game, team, warlordsNPC -> mob.onSpawn(this));
        game.addNPC(npc);
        mobs.put(mob, new MobData(ticksElapsed.get()));
        Bukkit.getPluginManager().callEvent(new WarlordsMobSpawnEvent(game, mob));
    }

    @Override
    public PveRewards<?> getRewards() {
        return rewards;
    }

    @Override
    public void onGameCleanup(@Nonnull Game game) {
        removeActiveRelic();
        PveOption.super.onGameCleanup(game);
    }
}