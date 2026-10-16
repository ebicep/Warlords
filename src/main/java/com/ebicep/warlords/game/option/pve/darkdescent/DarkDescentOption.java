package com.ebicep.warlords.game.option.pve.darkdescent;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.events.pojos.GameEventReward;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.events.game.pve.WarlordsGameWaveClearEvent;
import com.ebicep.warlords.events.game.pve.WarlordsMobSpawnEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsAddCurrencyFinalEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.darkdescent.objectives.BossRoomObjective;
import com.ebicep.warlords.game.option.pve.darkdescent.objectives.DarkDescentObjective;
import com.ebicep.warlords.game.option.pve.rewards.PveRewards;
import com.ebicep.warlords.game.option.pve.wavedefense.waves.Wave;
import com.ebicep.warlords.game.option.pve.wavedefense.waves.WaveList;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.rewards.RewardInventory;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.util.java.DateUtil;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class DarkDescentOption implements PveOption {

    public static final int FLOORS = 20;
    private static final int START_DELAY_TICKS = 10 * GameRunnable.SECOND;
    private static final int NEXT_FLOOR_DELAY_TICKS = 3 * GameRunnable.SECOND;
    private static final double OBJECTIVE_CHANCE = 0.2;
    private static final double MOB_SPAWN_OFFSET = 8;

    private final ConcurrentHashMap<AbstractMob, MobData> mobs = new ConcurrentHashMap<>();
    private final List<Location> floorCenters;
    private final WaveList floorMobs;
    private final List<Mob> miniBosses;
    private final Map<Integer, Mob> floorBosses;

    private Game game;
    private DarkDescentRewards rewards;
    private List<Pair<Spendable, Long>> weeklyRewards;
    private SimpleScoreboardHandler scoreboardHandler;
    private int ticksElapsed;
    private int floor;
    private boolean floorActive;
    private Wave currentWave;
    private int spawnsRemaining;
    private int spawnDelay;
    private int spawnTicks;
    @Nullable
    private DarkDescentObjective objective;

    public DarkDescentOption(List<Location> floorCenters, WaveList floorMobs, List<Mob> miniBosses, Map<Integer, Mob> floorBosses) {
        if (floorCenters.size() != FLOORS) {
            throw new IllegalArgumentException("Dark Descent requires " + FLOORS + " floors, got " + floorCenters.size());
        }
        this.floorCenters = floorCenters;
        this.floorMobs = floorMobs;
        this.miniBosses = miniBosses;
        this.floorBosses = floorBosses;
    }

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        this.rewards = new DarkDescentRewards(this);
        this.weeklyRewards = DarkDescentChest.getWeeklyRewards();

        game.registerEvents(getBaseListener());
        game.registerEvents(new Listener() {
            @EventHandler(ignoreCancelled = true)
            public void onDeath(WarlordsDeathEvent event) {
                if (!(event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC)) {
                    return;
                }
                AbstractMob mob = warlordsNPC.getMob();
                if (!mobs.containsKey(mob)) {
                    return;
                }
                mob.onDeath(event.getKiller(), warlordsNPC.getLocation(), DarkDescentOption.this);
                new GameRunnable(game) {
                    @Override
                    public void run() {
                        if (mobs.containsKey(mob)) {
                            despawnMob(mob);
                        }
                    }
                }.runTaskLater(1);
            }

            @EventHandler
            public void onAddCurrency(WarlordsAddCurrencyFinalEvent event) {
                AbilityTree.handleAutoUpgrade(event.getWarlordsEntity());
            }

            @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
            public void onWin(WarlordsGameTriggerWinEvent event) {
                if (event.getDeclaredWinner() == Team.RED) {
                    recordFailedAttempt();
                }
            }
        });
        game.registerGameMarker(ScoreboardHandler.class, scoreboardHandler = new SimpleScoreboardHandler(5, "dark_descent") {
            @Nonnull
            @Override
            public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                return getScoreboard();
            }
        });
    }

    @Override
    public void start(@Nonnull Game game) {
        new GameRunnable(game) {
            @Override
            public void run() {
                startFloor(1);
            }
        }.runTaskLater(START_DELAY_TICKS);

        new GameRunnable(game) {
            @Override
            public void run() {
                if (game.getState() instanceof EndState) {
                    cancel();
                    return;
                }
                ticksElapsed++;
                mobTick();
                if (floorActive) {
                    tickFloor();
                }
                if (ticksElapsed % 10 == 0) {
                    scoreboardHandler.markChanged();
                }
            }
        }.runTaskTimer(0, 1);
    }

    private void startFloor(int floor) {
        this.floor = floor;
        Mob boss = floorBosses.get(floor);
        if (boss != null) {
            currentWave = null;
            spawnsRemaining = 0;
            objective = new BossRoomObjective(this, boss);
        } else {
            currentWave = floorMobs.getWave(floor, ThreadLocalRandom.current());
            spawnsRemaining = currentWave.getMonsterCount();
            objective = ThreadLocalRandom.current().nextDouble() < OBJECTIVE_CHANCE ? DarkDescentObjective.random(this) : null;
        }
        spawnDelay = currentWave == null ? 0 : currentWave.getDelay();
        spawnTicks = 0;
        floorActive = true;

        Location center = getFloorCenter();
        Component subtitle = objective == null ? Component.text("Slay the monsters", NamedTextColor.GRAY) : objective.getTitle();
        game.forEachOnlinePlayer((player, team) -> {
            player.teleport(center);
            player.playSound(center, Sound.ENTITY_ENDERMAN_TELEPORT, 1.5f, 0.6f);
            player.showTitle(Title.title(Component.text("Floor " + floor, NamedTextColor.DARK_PURPLE), subtitle));
        });
    }

    private void tickFloor() {
        if (currentWave != null && tickSpawns()) {
            return;
        }
        if (objective != null) {
            objective.tick();
        }
        if (spawnsRemaining == 0 && mobCount() == 0 && (objective == null || objective.isComplete())) {
            completeFloor();
        }
    }

    private boolean tickSpawns() {
        if (spawnDelay > 0) {
            spawnDelay--;
            return true;
        }
        int spawnTickPeriod = currentWave.getSpawnTickPeriod();
        if (spawnTickPeriod < 0) {
            while (spawnsRemaining > 0) {
                spawnFloorMob();
            }
        } else if (spawnsRemaining > 0 && spawnTicks++ % spawnTickPeriod == 0) {
            spawnFloorMob();
        }
        return false;
    }

    private void spawnFloorMob() {
        spawnsRemaining--;
        AbstractMob mob = currentWave.spawnMonster(randomArenaLocation(MOB_SPAWN_OFFSET));
        if (mob != null) {
            spawnNewMob(mob, Team.RED);
        }
    }

    private void completeFloor() {
        floorActive = false;
        if (objective != null) {
            objective.cleanup();
            objective = null;
        }
        Bukkit.getPluginManager().callEvent(new WarlordsGameWaveClearEvent(game, floor));
        game.warlordsPlayers().forEach(this::grantFloorChest);
        if (floor == FLOORS) {
            Bukkit.getPluginManager().callEvent(new WarlordsGameTriggerWinEvent(game, this, Team.BLUE));
            return;
        }
        int nextFloor = floor + 1;
        game.forEachOnlinePlayer((player, team) -> {
            player.sendMessage(Component.text("Floor " + floor + " cleared! Descending to floor " + nextFloor + "...", NamedTextColor.GREEN));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 0.8f);
        });
        new GameRunnable(game) {
            @Override
            public void run() {
                startFloor(nextFloor);
            }
        }.runTaskLater(NEXT_FLOOR_DELAY_TICKS);
    }

    private void grantFloorChest(WarlordsPlayer warlordsPlayer) {
        DatabasePlayer databasePlayer = warlordsPlayer.getDatabasePlayer();
        DarkDescentProgress progress = databasePlayer.getPveStats().getDarkDescentProgress();
        if (!DarkDescentChest.isRewardFloor(floor)) {
            return;
        }
        if (!progress.hasEarned(floor)) {
            Pair<Spendable, Long> reward = weeklyRewards.get(floor - 1);
            LinkedHashMap<Spendable, Long> chest = new LinkedHashMap<>();
            chest.put(reward.getA(), reward.getB());
            databasePlayer.getPveStats()
                          .getGameEventRewards()
                          .add(new GameEventReward(chest, "Dark Descent Floor " + floor, DateUtil.getResetDateCurrentWeek().getEpochSecond()));
            progress.earnFloor(floor);
            RewardInventory.sendRewardMessage(
                    warlordsPlayer.getUuid(),
                    Component.text("The Floor " + floor + " chest is ready to claim.", NamedTextColor.DARK_PURPLE)
            );
        }
        if (floor == FLOORS) {
            progress.setCompleted(ticksElapsed - START_DELAY_TICKS);
            warlordsPlayer.sendMessage(Component.text("You conquered Dark Descent in " + DarkDescentLeaderboard.formatTime(progress.getCompletionTicks()) + "!",
                    NamedTextColor.GOLD
            ));
        }
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
    }

    private void recordFailedAttempt() {
        game.warlordsPlayers().forEach(warlordsPlayer -> {
            DatabasePlayer databasePlayer = warlordsPlayer.getDatabasePlayer();
            DarkDescentProgress progress = databasePlayer.getPveStats().getDarkDescentProgress();
            progress.addFailedAttempt();
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
            warlordsPlayer.sendMessage(Component.text("You fell on floor " + floor + ". Attempts remaining this week: " + progress.getAttemptsRemaining(),
                    NamedTextColor.RED
            ));
        });
    }

    private List<Component> getScoreboard() {
        List<Component> lines = new ArrayList<>();
        if (floor == 0) {
            int seconds = Math.max(0, (START_DELAY_TICKS - ticksElapsed) / GameRunnable.SECOND);
            lines.add(Component.text("Descending in: ").append(Component.text(seconds + "s", NamedTextColor.YELLOW)));
            return lines;
        }
        lines.add(Component.text("Floor: ").append(Component.text(floor + "/" + FLOORS, NamedTextColor.DARK_PURPLE)));
        lines.add(Component.text("Monsters Left: ").append(Component.text(mobCount() + spawnsRemaining, NamedTextColor.RED)));
        if (objective != null) {
            lines.add(objective.getScoreboardLine());
        }
        return lines;
    }

    public Location getFloorCenter() {
        return floorCenters.get(floor - 1).clone();
    }

    public Location randomArenaLocation(double maxOffset) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return getFloorCenter().add(random.nextDouble(-maxOffset, maxOffset), 0, random.nextDouble(-maxOffset, maxOffset));
    }

    public List<Mob> getMiniBosses() {
        return miniBosses;
    }

    @Override
    public void spawnNewMob(AbstractMob mob, Team team) {
        game.addNPC(mob.toNPC(game, team, warlordsNPC -> mob.onSpawn(this)));
        mobs.put(mob, new MobData(ticksElapsed));
        Bukkit.getPluginManager().callEvent(new WarlordsMobSpawnEvent(game, mob));
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
        return ticksElapsed;
    }

    @Override
    public int getWaveCounter() {
        return floor;
    }

    @Override
    public PveRewards<?> getRewards() {
        return rewards;
    }

    @Override
    public void onGameCleanup(@Nonnull Game game) {
        if (objective != null) {
            objective.cleanup();
            objective = null;
        }
        PveOption.super.onGameCleanup(game);
    }
}
