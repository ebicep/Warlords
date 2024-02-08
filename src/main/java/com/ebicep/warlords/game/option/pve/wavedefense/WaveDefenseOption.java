package com.ebicep.warlords.game.option.pve.wavedefense;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.events.game.pve.WarlordsGameWaveClearEvent;
import com.ebicep.warlords.events.game.pve.WarlordsGameWaveEditEvent;
import com.ebicep.warlords.events.game.pve.WarlordsGameWaveRespawnEvent;
import com.ebicep.warlords.events.game.pve.WarlordsMobSpawnEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.cuboid.BoundingBoxOption;
import com.ebicep.warlords.game.option.marker.SpawnLocationMarker;
import com.ebicep.warlords.game.option.marker.TimerSkipAbleMarker;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.rewards.CoinGainOption;
import com.ebicep.warlords.game.option.pve.rewards.PveRewards;
import com.ebicep.warlords.game.option.pve.wavedefense.waves.Wave;
import com.ebicep.warlords.game.option.pve.wavedefense.waves.WaveList;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.guilds.upgrades.AbstractGuildUpgrade;
import com.ebicep.warlords.player.general.Settings;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.commands.MobCommand;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.flags.BossLike;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ebicep.warlords.util.chat.ChatUtils.sendMessage;
import static com.ebicep.warlords.util.java.JavaUtils.iterable;

public class WaveDefenseOption implements PveOption {
    protected static final int SCOREBOARD_PRIORITY = 5;
    private final ConcurrentHashMap<AbstractMob, MobData> mobs = new ConcurrentHashMap<>();
    private final Team team;
    private final WaveList waves;
    private final DifficultyIndex difficulty;
    private final int maxWave;
    private final AtomicInteger ticksElapsed = new AtomicInteger(0);
    private WaveDefenseRewards waveDefenseRewards;
    private int waveCounter = 0;
    private int spawnCount = 0;
    private Wave currentWave;
    @Nonnull
    private Game game;
    private Location lastLocation = new Location(null, 0, 0, 0);
    @Nullable
    private BukkitTask spawner;
    private boolean pauseMobSpawn = false;
    private int currentDelay = 0;

    public WaveDefenseOption(Team team, WaveList waves, DifficultyIndex difficulty) {
        this(team, waves, difficulty, difficulty.getMaxWaves());
    }

    public WaveDefenseOption(Team team, WaveList waves, DifficultyIndex difficulty, int maxWave) {
        this.team = team;
        this.waves = waves;
        this.difficulty = difficulty;
        this.maxWave = maxWave;
    }

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        this.waveDefenseRewards = new WaveDefenseRewards(this);
        for (Option o : game.getOptions()) {
            if (o instanceof BoundingBoxOption boundingBoxOption) {
                lastLocation = boundingBoxOption.getCenter();
            }
        }
        CoinGainOption coinGainOption = game
                .getOptions()
                .stream()
                .filter(CoinGainOption.class::isInstance)
                .map(CoinGainOption.class::cast)
                .findAny()
                .orElse(null);

        game.registerEvents(getBaseListener());
        game.registerEvents(new Listener() {

            @EventHandler
            public void onFinalDamageHeal(WarlordsDamageHealingFinalEvent event) {
                WarlordsEntity attacker = event.getAttacker();
                waveDefenseRewards.getPlayerRewards(attacker.getUuid())
                                  .getWaveDamage()
                                  .merge(waveCounter, (long) event.getValue(), Long::sum);
            }

            @EventHandler
            public void onDeath(WarlordsDeathEvent event) {
                WarlordsEntity we = event.getWarlordsEntity();
                WarlordsEntity killer = event.getKiller();

                if (we instanceof WarlordsNPC) {
                    AbstractMob mobToRemove = ((WarlordsNPC) we).getMob();
                    if (mobs.containsKey(mobToRemove)) {
                        mobToRemove.onDeath(killer, we.getDeathLocation(), WaveDefenseOption.this);
                        new GameRunnable(game) {
                            @Override
                            public void run() {
                                mobs.remove(mobToRemove);
                                game.getPlayers().remove(we.getUuid());
                                Warlords.removePlayer(we.getUuid());
                                //game.removePlayer(we.getUuid());
                            }
                        }.runTaskLater(1);

                        if (killer instanceof WarlordsPlayer) {
                            killer.getMinuteStats().addMobKill(mobToRemove.getName());
                            we.getHitBy().forEach((assisted, value) -> assisted.getMinuteStats().addMobAssist(mobToRemove.getName()));

                            if (coinGainOption == null) {
                                return;
                            }
                            if (coinGainOption.getMobCoinValues()
                                              .values()
                                              .stream()
                                              .anyMatch(stringLongLinkedHashMap -> stringLongLinkedHashMap.containsKey(mobToRemove.getName()))
                            ) {
                                waveDefenseRewards.getMobsKilled().merge(mobToRemove.getName(), 1L, Long::sum);
                            }
                        }

                    }
                    MobCommand.SPAWNED_MOBS.remove(mobToRemove);
                } else if (we instanceof WarlordsPlayer && killer instanceof WarlordsNPC) {
                    if (mobs.containsKey(((WarlordsNPC) killer).getMob())) {
                        we.getMinuteStats().addMobDeath(((WarlordsNPC) killer).getMob().getName());
                    }
                }
            }

            @EventHandler
            public void onNewWave(WarlordsGameWaveClearEvent event) {
                WarlordsGameWaveRespawnEvent respawnEvent = new WarlordsGameWaveRespawnEvent(game);
                Bukkit.getPluginManager().callEvent(respawnEvent);
                if (respawnEvent.isCancelled()) {
                    return;
                }
                game.warlordsPlayers().forEach(warlordsPlayer -> {
                    if (warlordsPlayer.isDead()) {
                        warlordsPlayer.respawn();
                    }
                });
            }


        });
        game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(SCOREBOARD_PRIORITY - 1, "wave") {
            @Nonnull
            @Override
            public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                return getWaveScoreboard(player);
            }
        });
        game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(SCOREBOARD_PRIORITY, "wave") {
            @Nonnull
            @Override
            public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                return Collections.singletonList(
                        Component.text("Monsters Left: ")
                                 .append(Component.text(mobCount(), (spawnCount > 0 || ticksElapsed.get() < 10 ? NamedTextColor.RED : NamedTextColor.GREEN)))
                );
            }
        });
        game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(6, "kills") {
            @Nonnull
            @Override
            public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                return healthScoreboard(game);
            }
        });

        new TimerSkipAbleMarker() {
            @Override
            public int getDelay() {
                return 0;
            }

            @Override
            public void skipTimer(int delay) {
                newWave();
                Bukkit.getPluginManager().callEvent(new WarlordsGameWaveEditEvent(game, waveCounter - 1));
            }

        }.register(game);
    }

    public List<Component> getWaveScoreboard(WarlordsPlayer player) {
        TextComponent.Builder waveScoreboard = Component.text();
        waveScoreboard.append(Component.text("Wave: "))
                      .append(Component.text(waveCounter, NamedTextColor.GREEN));
        if (maxWave != Integer.MAX_VALUE) {
            waveScoreboard.append(Component.text("/"))
                          .append(Component.text(maxWave, NamedTextColor.GREEN));
        }
        if (currentWave != null && currentWave.getMessage() != null) {
            waveScoreboard.append(Component.text(" ("))
                          .append(currentWave.getMessage())
                          .append(Component.text(")"));
        }
        return Collections.singletonList(waveScoreboard.build());
    }

    public void newWave() {
        if (currentWave != null) {
            TextComponent.Builder message = Component.text("Wave Complete!", NamedTextColor.GREEN).toBuilder();
            if (currentWave.getMessage() != null) {
                message.append(Component.text(" ("))
                       .append(currentWave.getMessage())
                       .append(Component.text(")"));
            }

            for (Map.Entry<Player, Team> entry : iterable(game.onlinePlayers())) {
                sendMessage(entry.getKey(), false, message.build());
                entry.getKey().playSound(entry.getKey().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2);
            }
        }
        waveCounter++;
        currentWave = waves.getWave(waveCounter, new Random());
        spawnCount = currentWave.getMonsterCount();
        int spawns = spawnCount;
        spawns *= getSpawnCountMultiplier((int) game.warlordsPlayers().count());

        for (Map.Entry<Player, Team> entry : iterable(game.onlinePlayers())) {
            if (currentWave.getMessage() != null) {
                sendMessage(entry.getKey(),
                        false,
                        Component.text("A boss will spawn in ", NamedTextColor.YELLOW)
                                 .append(Component.text(getWaveDelay() / 20, NamedTextColor.RED))
                                 .append(Component.text(" seconds!"))
                );
            } else {
                sendMessage(entry.getKey(),
                        false,
                        Component.text("A wave of ", NamedTextColor.YELLOW)
                                 .append(Component.text(spawns, NamedTextColor.RED, TextDecoration.BOLD))
                                 .append(Component.text(" monsters will spawn in "))
                                 .append(Component.text(getWaveDelay() / 20, NamedTextColor.RED))
                                 .append(Component.text(" seconds!"))
                );
            }
            Pair<Float, Component> waveOpening = getWaveOpening();
            entry.getKey().playSound(entry.getKey().getLocation(), Sound.ENTITY_WITHER_SPAWN, 500, waveOpening.getA());
            entry.getKey().showTitle(Title.title(
                    waveOpening.getB(),
                    Component.empty(),
                    Title.Times.times(Ticks.duration(20), Ticks.duration(60), Ticks.duration(20))
            ));
        }
        startSpawnTask();
    }

    public float getSpawnCountMultiplier(int playerCount) {
        return switch (playerCount) {
            case 1 -> 1;
            case 2 -> 1.25f;
            case 3, 4 -> 1.5f;
            case 5, 6 -> 2;
            case 7, 8 -> 2.5f;
            default -> 2.5f + (playerCount - 8) * 0.5f;
        };
    }

    protected Pair<Float, Component> getWaveOpening() {
        float soundPitch = 0.8f;
        Component wavePrefix = Component.text("Wave " + waveCounter, NamedTextColor.YELLOW);
        if (waveCounter >= 101) {
            wavePrefix = Component.text("W ", NamedTextColor.BLACK)
                                  .append(Component.text("a").decorate(TextDecoration.BOLD, TextDecoration.OBFUSCATED))
                                  .append(Component.text("v").decorate(TextDecoration.BOLD))
                                  .append(Component.text("e " + waveCounter).decorate(TextDecoration.BOLD, TextDecoration.OBFUSCATED));
        } else if (waveCounter == 100) {
            soundPitch = 0.1f;
            wavePrefix = Component.text("W", NamedTextColor.DARK_RED)
                                  .append(Component.text("a").decorate(TextDecoration.BOLD, TextDecoration.OBFUSCATED))
                                  .append(Component.text("ve " + waveCounter).decorate(TextDecoration.BOLD));
        } else if (waveCounter >= 90) {
            soundPitch = 0.2f;
            wavePrefix = Component.text("W", NamedTextColor.DARK_PURPLE)
                                  .append(Component.text("a").decorate(TextDecoration.BOLD, TextDecoration.OBFUSCATED))
                                  .append(Component.text("ve " + waveCounter).decorate(TextDecoration.BOLD));
        } else if (waveCounter >= 80) {
            soundPitch = 0.3f;
            wavePrefix = Component.text("Wave " + waveCounter, NamedTextColor.DARK_PURPLE, TextDecoration.BOLD);
        } else if (waveCounter >= 70) {
            soundPitch = 0.4f;
            wavePrefix = Component.text("Wave " + waveCounter, NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD);
        } else if (waveCounter >= 60) {
            soundPitch = 0.5f;
            wavePrefix = Component.text("Wave " + waveCounter, NamedTextColor.DARK_GRAY, TextDecoration.BOLD);
        } else if (waveCounter >= 50) {
            soundPitch = 0.65f;
            wavePrefix = Component.text("Wave " + waveCounter, NamedTextColor.GRAY);
        } else if (waveCounter >= 25) {
            soundPitch = 0.7f;
            wavePrefix = Component.text("Wave " + waveCounter, NamedTextColor.GOLD);
        } else if (waveCounter >= 10) {
            soundPitch = 0.75f;
            wavePrefix = Component.text("Wave " + waveCounter, NamedTextColor.YELLOW);
        }
        return new Pair<>(soundPitch, wavePrefix);
    }

    public void startSpawnTask() {
        if (spawner != null) {
            spawner.cancel();
            spawner = null;
        }

        if (spawnCount == 0) {
            return;
        }

        if (currentWave.getMessage() == null) {
            spawnCount *= getSpawnCountMultiplier((int) game.warlordsPlayers().count());
        }

        int spawnTickPeriod = currentWave.getSpawnTickPeriod();
        currentDelay = getWaveDelay();
        System.out.println("currentDelay: " + currentDelay);
        spawner = new GameRunnable(game) {
            WarlordsEntity lastSpawn = null;
            int spawnTaskTicksElapsed = 0;

            @Override
            public void run() {
                if (game.getState() instanceof EndState) {
                    this.cancel();
                    return;
                }

                if (pauseMobSpawn) {
                    return;
                }

                if (currentDelay > 0) {
                    currentDelay--;
                    onSpawnDelayChange(currentDelay);
                    return;
                }

                if (spawnTaskTicksElapsed++ % spawnTickPeriod == 0) {
                    if (spawnTickPeriod < 0) {
                        for (int i = 0; i < spawnCount; i++) {
                            spawnMob();
                        }
                    } else {
                        spawnMob();
                    }
                }

                currentWave.tick(WaveDefenseOption.this, spawnTaskTicksElapsed);

                if (spawnCount <= 0) {
                    spawner.cancel();
                    spawner = null;
                }
            }

            private void spawnMob() {
                if (lastSpawn == null) {
                    lastSpawn = spawn(getSpawnLocation(null));
                } else {
                    lastSpawn = spawn(getSpawnLocation(lastSpawn));
                }
                if (lastSpawn != null) {
                    lastSpawn.getLocation(lastLocation);
                    spawnCount--;
                }
            }

            public WarlordsEntity spawn(Location loc) {
                AbstractMob abstractMob = currentWave.spawnMonster(loc);
                if (abstractMob == null) {
                    return null;
                }
                WarlordsNPC npc = abstractMob.toNPC(game, team, WaveDefenseOption.this::modifyStats);
                game.addNPC(npc);
                mobs.put(abstractMob, new MobData(ticksElapsed.get()));
                Bukkit.getPluginManager().callEvent(new WarlordsMobSpawnEvent(game, abstractMob));
                return npc;
            }

            private Location getSpawnLocation(WarlordsEntity entity) {
                Location randomSpawnLocation = getRandomSpawnLocation(entity);
                return randomSpawnLocation != null ? randomSpawnLocation : lastLocation;
            }

        }.runTaskTimer(0, 0);
    }

    protected int getWaveDelay() {
        if (currentWave != null) {
            AtomicBoolean fastWave = new AtomicBoolean(true);
            getGame().warlordsPlayers().forEach(warlordsPlayer -> {
                DatabaseManager.getPlayer(warlordsPlayer.getUuid(), databasePlayer -> {
                    if (databasePlayer.getFastWaveMode() == Settings.FastWaveMode.OFF) {
                        fastWave.set(false);
                    }
                });
            });
            int waveDelay = currentWave.getDelay();
            return fastWave.get() ? waveDelay / 2 : waveDelay;
        }
        return 0;
    }

    protected void onSpawnDelayChange(int newTickDelay) {

    }

    protected void modifyStats(WarlordsNPC warlordsNPC) {
        warlordsNPC.getMob().onSpawn(WaveDefenseOption.this);

        boolean isEndless = difficulty == DifficultyIndex.ENDLESS;
        /*
         * Base scale of 600
         *
         * The higher the scale is the longer it takes to increase per interval.
         */
        double scale = isEndless ? 1200.0 : 600.0;
        int playerCount = playerCount();
        // Flag check whether mob is a boss.
        boolean bossFlagCheck = playerCount > 1 && warlordsNPC.getMob() instanceof BossLike;
        // Reduce base scale by 75/100 for each player after 2 or more players in game instance.
        double modifiedScale = scale - (playerCount > 1 ? (isEndless ? 100 : 75) * playerCount : 0);
        // Divide scale based on wave count.
        double modifier = waveCounter / modifiedScale + 1;
        // Multiply health & min/max melee damage by waveCounter + 1 ^ base damage.
        int minMeleeDamage = (int) Math.pow(warlordsNPC.getMinMeleeDamage(), modifier);
        int maxMeleeDamage = (int) Math.pow(warlordsNPC.getMaxMeleeDamage(), modifier);
        float health = (float) Math.pow(warlordsNPC.getMaxBaseHealth(), modifier);
        // Increase boss health by 25% for each player in game instance.
        float bossMultiplier = 1 + (0.25f * playerCount);

        // Multiply damage/health by given difficulty.
        float difficultyHealthMultiplier;
        float difficultyDamageMultiplier;
        switch (difficulty) {
            case EASY -> {
                difficultyHealthMultiplier = 0.75f;
                difficultyDamageMultiplier = 0.75f;
            }
            case HARD -> {
                difficultyHealthMultiplier = 1.5f;
                difficultyDamageMultiplier = 1.5f;
            }
            case EXTREME -> {
                difficultyHealthMultiplier = 2;
                difficultyDamageMultiplier = 1.75f;
            }
            default -> {
                difficultyHealthMultiplier = 1;
                difficultyDamageMultiplier = 1;
            }
        }

        // Final health value after applying all modifiers.
        float finalHealth = (health * difficultyHealthMultiplier) * (bossFlagCheck ? bossMultiplier : 1);
        warlordsNPC.setMaxHealthAndHeal(finalHealth);

        int endlessFlagCheckMin = isEndless ? minMeleeDamage : (int) (warlordsNPC.getMinMeleeDamage() * difficultyDamageMultiplier);
        int endlessFlagCheckMax = isEndless ? maxMeleeDamage : (int) (warlordsNPC.getMaxMeleeDamage() * difficultyDamageMultiplier);
        warlordsNPC.setMinMeleeDamage(endlessFlagCheckMin);
        warlordsNPC.setMaxMeleeDamage(endlessFlagCheckMax);
    }

    @Override
    public void start(@Nonnull Game game) {
        if (DatabaseManager.guildService != null) {
            HashMap<Guild, HashSet<UUID>> guilds = new HashMap<>();
            List<UUID> uuids = game.playersWithoutSpectators().map(Map.Entry::getKey).toList();
            for (Guild guild : GuildManager.GUILDS) {
                for (UUID uuid : uuids) {
                    Optional<GuildPlayer> guildPlayer = guild.getPlayerMatchingUUID(uuid);
                    if (guildPlayer.isPresent() && guildPlayer.get().getJoinDate().isBefore(Instant.now().minus(2, ChronoUnit.DAYS))) {
                        guilds.computeIfAbsent(guild, k -> new HashSet<>()).add(uuid);
                    }
                }
            }
            guilds.forEach((guild, validUUIDs) -> {
                for (AbstractGuildUpgrade<?> upgrade : guild.getUpgrades()) {
                    upgrade.getUpgrade().onGame(game, validUUIDs, upgrade.getTier());
                }
            });
        }
        new GameRunnable(game) {

            @Override
            public void run() {
                if (game.getState() instanceof EndState) {
                    this.cancel();
                    return;
                }

                if (mobCount() == 0 && spawnCount == 0) {
                    newWave();

                    if (waveCounter > 1) {
                        Bukkit.getPluginManager().callEvent(new WarlordsGameWaveClearEvent(game, waveCounter - 1));
                    }

                    if (difficulty == DifficultyIndex.ENDLESS) {
                        switch (waveCounter) {
                            case 50, 75 -> getGame().forEachOnlineWarlordsPlayer(wp -> {
                                wp.getAbilityTree().setMaxMasterUpgrades(wp.getAbilityTree().getMaxMasterUpgrades() + 1);
                                wp.sendMessage(Component.text("+1 Master Upgrade", NamedTextColor.RED, TextDecoration.BOLD));
                            });
                        }
                    }
                }

                mobTick();

                //check every 10 seconds for mobs in void
                if (ticksElapsed.get() % 200 == 0) {
                    for (SpawnLocationMarker marker : getGame().getMarkers(SpawnLocationMarker.class)) {
                        Location location = marker.getLocation();
                        for (AbstractMob mob : new ArrayList<>(mobs.keySet())) {
                            if (mob.getWarlordsNPC().getLocation().getY() < -50) {
                                mob.getWarlordsNPC().teleport(location);
                            }
                        }
                        break;
                    }
                }

                ticksElapsed.getAndIncrement();
            }
        }.runTaskTimer(20, 0);
    }

    @Override
    @Nonnull
    public Game getGame() {
        return game;
    }

    @Override
    public Set<AbstractMob> getMobs() {
        return mobs.keySet();
    }

    @Override
    public int getTicksElapsed() {
        return ticksElapsed.get();
    }

    @Override
    public ConcurrentHashMap<AbstractMob, ? extends MobData> getMobsMap() {
        return mobs;
    }

    @Override
    public int getWaveCounter() {
        return waveCounter;
    }

    public void setWaveCounter(int waveCounter) {
        this.waveCounter = waveCounter - 1;
        newWave();
    }

    @Override
    public DifficultyIndex getDifficulty() {
        return difficulty;
    }

    @Override
    public void spawnNewMob(AbstractMob mob, Team team) {
        game.addNPC(mob.toNPC(game, team, this::modifyStats));
        mobs.put(mob, new MobData(ticksElapsed.get()));
        Bukkit.getPluginManager().callEvent(new WarlordsMobSpawnEvent(game, mob));
    }

    @Override
    public boolean isPauseMobSpawn() {
        return pauseMobSpawn;
    }

    @Override
    public void setPauseMobSpawn(boolean pauseMobSpawn) {
        this.pauseMobSpawn = pauseMobSpawn;
    }

    @Override
    public PveRewards<?> getRewards() {
        return waveDefenseRewards;
    }

    public int getWavesCleared() {
        if (waveCounter <= 1) {
            return 0;
        }
        return waveCounter - 1;
    }

    public Wave getCurrentWave() {
        return currentWave;
    }

    public WaveList getWaves() {
        return waves;
    }

    public int getMaxWave() {
        return maxWave;
    }

    public int getSpawnCount() {
        return spawnCount;
    }

    public void setSpawnCount(int spawnCount) {
        this.spawnCount = spawnCount;
    }

    public void setCurrentDelay(int currentDelay) {
        this.currentDelay = currentDelay;
    }
}
