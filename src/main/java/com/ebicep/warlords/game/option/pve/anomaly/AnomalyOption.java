package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.events.game.pve.WarlordsMobSpawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.rewards.PveRewards;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.commands.MobCommand;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.newitems.NewItem;
import com.ebicep.warlords.pve.newitems.NewItemsUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class AnomalyOption implements PveOption {

    public static final int OBJECTIVE_COUNT = 3;
    public static final int OBJECTIVE_DURATION_TICKS = 120 * GameRunnable.SECOND;
    private static final int MOB_SPAWN_INTERVAL = 2 * GameRunnable.SECOND;
    private static final int BASE_RELIC_HEALTH = 25_000;

    private final ConcurrentHashMap<AbstractMob, MobData> mobs = new ConcurrentHashMap<>();
    private final AtomicInteger ticksElapsed = new AtomicInteger();
    private final boolean[] objectiveSuccess = new boolean[OBJECTIVE_COUNT];
    private final Anomalies currentAnomaly = AnomalyRotation.getCurrentAnomaly();

    private Game game;
    private AnomalyRewards rewards;
    private AnomalyRelic activeRelic;
    private int activeObjective = -1;
    private int objectiveTicks;
    private boolean completed;

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
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
    }

    @Override
    public void start(@Nonnull Game game) {
        beginObjective(0);
        new GameRunnable(game) {
            @Override
            public void run() {
                if (completed) {
                    cancel();
                    return;
                }
                ticksElapsed.incrementAndGet();
                mobTick();
                if (activeRelic == null) {
                    return;
                }
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
        announce(Component.text("Relic " + (activeObjective + 1) + " secured. Reward pool unlocked!", NamedTextColor.GREEN));
        removeActiveRelic();
        int nextObjective = activeObjective + 1;
        new GameRunnable(game) {
            @Override
            public void run() {
                beginObjective(nextObjective);
            }
        }.runTaskLater(3 * GameRunnable.SECOND);
    }

    private void failCurrentObjective() {
        if (activeRelic == null) {
            return;
        }
        objectiveSuccess[activeObjective] = false;
        announce(Component.text("Relic " + (activeObjective + 1) + " was lost. Its reward pool is forfeited.", NamedTextColor.RED));
        removeActiveRelic();
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
        NewItem guaranteedPreview = new NewItem(AnomalyRotation.getGuaranteedLegendarySet());
        game.warlordsPlayers().forEach(warlordsPlayer -> {
            DatabasePlayer databasePlayer = DatabaseManager.getPlayer(warlordsPlayer.getUuid());
            int poolsGranted = 0;
            for (int i = 0; i < OBJECTIVE_COUNT; i++) {
                if (!objectiveSuccess[i]) {
                    continue;
                }
                poolsGranted++;
                NewItem item = currentAnomaly.getRewardPools().get(i).grant(databasePlayer);
                if (item != null) {
                    NewItemsUtils.sendItemMessage(warlordsPlayer, Component.text("Your " + currentAnomaly.getRewardPools().get(i).getName() + " contained ", NamedTextColor.GRAY).append(item.getHoverComponent()));
                }
            }
            NewItem guaranteedItem = new NewItem(guaranteedPreview);
            databasePlayer.getPveStats().getNewItemsManager().addItem(guaranteedItem);
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
            warlordsPlayer.sendMessage(Component.text("Anomaly complete: " + poolsGranted + "/3 reward pools claimed.", NamedTextColor.GREEN));
            NewItemsUtils.sendItemMessage(warlordsPlayer, Component.text("Guaranteed anomaly set reward: ", NamedTextColor.GOLD).append(guaranteedItem.getHoverComponent()));
            warlordsPlayer.playSound(warlordsPlayer.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2, 1);
        });
        Bukkit.getPluginManager().callEvent(new WarlordsGameTriggerWinEvent(game, this, Team.BLUE));
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
        WarlordsNPC npc = activeRelic.getWarlordsNPC();
        activeRelic.cleanup(this);
        npc.cleanup();
        game.getPlayers().remove(npc.getUuid());
        Warlords.removePlayer(npc.getUuid());
        activeRelic = null;
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
