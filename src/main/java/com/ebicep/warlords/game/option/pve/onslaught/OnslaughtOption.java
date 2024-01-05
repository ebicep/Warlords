package com.ebicep.warlords.game.option.pve.onslaught;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.events.game.pve.WarlordsMobSpawnEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.cuboid.BoundingBoxOption;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.rewards.PveRewards;
import com.ebicep.warlords.game.option.pve.wavedefense.waves.Wave;
import com.ebicep.warlords.game.option.pve.wavedefense.waves.WaveList;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.guilds.upgrades.AbstractGuildUpgrade;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.commands.MobCommand;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.flags.BossLike;
import com.ebicep.warlords.pve.rewards.RewardInventory;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.java.RandomCollection;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class OnslaughtOption implements PveOption {

    private final Team team;
    private final WaveList mobSet;
    private final AtomicInteger ticksElapsed = new AtomicInteger(200); //start at 200 to account for 10 second start delay
    private final ConcurrentHashMap<AbstractMob, Integer> mobs = new ConcurrentHashMap<>();
    private OnslaughtRewards onslaughtRewards;
    private HashMap<UUID, HashMap<Spendable, Long>> playerSyntheticPouch = new HashMap<>();
    private HashMap<UUID, HashMap<Spendable, Long>> playerAspirantPouch = new HashMap<>();
    private Game game;
    private Wave currentMobSet;
    private int spawnCount = 0;
    private int spawnLimit;
    private Location lastLocation;
    private float integrityCounter = 100;
    private float integrityDecayIncrease = 0;

    public OnslaughtOption(Team team, WaveList waves) {
        this.team = team;
        this.mobSet = waves;
        this.currentMobSet = this.mobSet.getWave(0, new Random());
    }

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        this.onslaughtRewards = new OnslaughtRewards(this);
        for (Option o : game.getOptions()) {
            if (o instanceof BoundingBoxOption boundingBoxOption) {
                lastLocation = boundingBoxOption.getCenter();
            }
        }

        game.registerEvents(getBaseListener());

        game.registerEvents(new Listener() {

            @EventHandler
            public void onEvent(WarlordsDeathEvent event) {
                WarlordsEntity we = event.getWarlordsEntity();
                WarlordsEntity killer = event.getKiller();

                if (we instanceof WarlordsNPC) {
                    AbstractMob mobToRemove = ((WarlordsNPC) we).getMob();
                    if (mobs.containsKey(mobToRemove)) {
                        mobToRemove.onDeath(killer, we.getDeathLocation(), OnslaughtOption.this);
                        new GameRunnable(game) {
                            @Override
                            public void run() {
                                integrityCounter += 1;
                                if (integrityCounter >= 100) {
                                    integrityCounter = 100;
                                }
                                spawnCount--;
                                mobs.remove(mobToRemove);
                                game.getPlayers().remove(we.getUuid());
                                Warlords.removePlayer(we.getUuid());
                            }
                        }.runTaskLater(1);

                        if (killer instanceof WarlordsPlayer) {
                            killer.getMinuteStats().addMobKill(mobToRemove.getName());
                            we.getHitBy().forEach((assisted, value) -> assisted.getMinuteStats().addMobAssist(mobToRemove.getName()));
                        }
                    }
                    MobCommand.SPAWNED_MOBS.remove(mobToRemove);
                } else if (we instanceof WarlordsPlayer && killer instanceof WarlordsNPC) {
                    if (mobs.containsKey(((WarlordsNPC) killer).getMob())) {
                        we.getMinuteStats().addMobDeath(((WarlordsNPC) killer).getMob().getName());
                    }
                }
            }
        });

        game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(5, "percentage") {
            @Nonnull
            @Override
            public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                return Collections.singletonList(Component.text("Difficulty: ").append(currentMobSet.getMessage()));
            }
        });
        game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(5, "percentage") {
            @Nonnull
            @Override
            public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                return Collections.singletonList(integrityScoreboard());
            }
        });
        game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(6, "kills") {
            @Nonnull
            @Override
            public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                return healthScoreboard(game);
            }
        });
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
            int counter = 0;

            @Override
            public void run() {
                ticksElapsed.getAndIncrement();
                counter++;
                if (counter % 20 == 0) {
                    integrityCounter -= (getIntegrityDecay((int) game.warlordsPlayers().count()) + integrityDecayIncrease);
                }

                if (integrityCounter <= 0) {
                    Bukkit.getPluginManager().callEvent(new WarlordsGameTriggerWinEvent(game, OnslaughtOption.this, Team.RED));
                    this.cancel();
                }

                mobTick();

                if (ticksElapsed.get() % 18000 == 0) {
                    game.warlordsPlayers().forEach(wp -> {
                        wp.playSound(wp.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 2, 0.1f);
                        addRewardToPlayerPouch(
                                wp.getUuid(),
                                OnslaughtRewards.ASPIRANT_POUCH_LOOT_POOL,
                                playerAspirantPouch,
                                Component.text("Aspirant Pouch", NamedTextColor.RED)
                        );
                        if (wp.getAbilityTree().getMaxMasterUpgrades() == 5) {
                            return;
                        }
                        wp.getAbilityTree().setMaxMasterUpgrades(wp.getAbilityTree().getMaxMasterUpgrades() + 1);
                        wp.sendMessage(Component.text("+1 Master Upgrade", NamedTextColor.RED, TextDecoration.BOLD));
                    });
                } else if (ticksElapsed.get() % 6000 == 0) {
                    integrityDecayIncrease += 0.1f;
                    game.warlordsPlayers().forEach(wp -> {
                        wp.playSound(wp.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 0.1f);
                        addRewardToPlayerPouch(
                                wp.getUuid(),
                                OnslaughtRewards.SYNTHETIC_POUCH_LOOT_POOL,
                                playerSyntheticPouch,
                                Component.text("Synthetic Pouch", NamedTextColor.AQUA)
                        );
                    });
                }
            }
        }.runTaskTimer(10 * GameRunnable.SECOND, 0);

        new GameRunnable(game) {
            WarlordsEntity lastSpawn = null;

            @Override
            public void run() {
                if (game.getState() instanceof EndState) {
                    this.cancel();
                    return;
                }

                if (spawnCount >= getSpawnLimit(playerCount())) {
                    return;
                }

                if (lastSpawn == null) {
                    lastSpawn = spawn(getSpawnLocation(null));
                } else {
                    lastSpawn = spawn(getSpawnLocation(lastSpawn));
                }
                if (lastSpawn != null) {
                    lastSpawn.getLocation(lastLocation);
                }

                spawnCount++;
            }

            public WarlordsEntity spawn(Location loc) {
                currentMobSet = mobSet.getWave((game.getState().getTicksElapsed() / 20) / 60, new Random());
                AbstractMob abstractMob = currentMobSet.spawnMonster(loc);
                if (abstractMob == null) {
                    return null;
                }
                mobs.put(abstractMob, ticksElapsed.get());
                WarlordsNPC wpc = abstractMob.toNPC(game, team, OnslaughtOption.this::modifyStats);
                game.addNPC(wpc);
                Bukkit.getPluginManager().callEvent(new WarlordsMobSpawnEvent(game, abstractMob));
                return wpc;
            }

            private Location getSpawnLocation(WarlordsEntity entity) {
                Location randomSpawnLocation = getRandomSpawnLocation(entity);
                return randomSpawnLocation != null ? randomSpawnLocation : lastLocation;
            }

        }.runTaskTimer(10 * GameRunnable.SECOND, 6);
    }

    public float getIntegrityDecay(int playerCount) {
        switch (playerCount) {
            case 1 -> {
                return 0.4f;
            }
            case 2 -> {
                return 0.7f;
            }
            case 3 -> {
                return 1;
            }
            case 4 -> {
                return 1.5f;
            }
            case 5 -> {
                return 2;
            }
            case 6 -> {
                return 2.5f;
            }
            default -> {
                return playerCount * 0.5f;
            }
        }
    }

    private void addRewardToPlayerPouch(
            UUID uuid,
            RandomCollection<Pair<Spendable, Long>> pouchLootPool,
            HashMap<UUID, HashMap<Spendable, Long>> playerPouch,
            Component pouchName
    ) {
        Pair<Spendable, Long> reward = pouchLootPool.next();
        if (reward != null) {
            Spendable spendable = reward.getA();
            Long amount = reward.getB();
            playerPouch.computeIfAbsent(uuid, k -> new HashMap<>())
                       .merge(spendable, amount, Long::sum);
            Component rewardString = Component.text("+", spendable.getTextColor()).append(spendable.getCostColoredName(amount));
            RewardInventory.sendRewardMessage(uuid,
                    pouchName.append(Component.text(":")).append(rewardString)
            );
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.showTitle(Title.title(
                        pouchName.append(Component.text(":")),
                        rewardString,
                        Title.Times.times(Ticks.duration(20), Ticks.duration(30), Ticks.duration(20))

                ));
            }
        }
    }

    public int getSpawnLimit(int playerCount) {
        return switch (playerCount) {
            case 1 -> 7;
            case 2 -> 12;
            case 3 -> 15;
            case 4 -> 20;
            case 5 -> 25;
            case 6 -> 30;
            default -> spawnLimit;
        };
    }

    private void modifyStats(WarlordsNPC warlordsNPC) {
        warlordsNPC.getMob().onSpawn(OnslaughtOption.this);
        /*
         * Base scale of 900
         *
         * The higher the scale is the longer it takes to increase per interval.
         */
        double scale = 900.0;
        long playerCount = game.warlordsPlayers().count();
        // Flag check whether mob is a boss.
        boolean bossFlagCheck = playerCount > 1 && warlordsNPC.getMob() instanceof BossLike;
        // Reduce base scale by 50 for each player after 2 or more players in game instance.
        double modifiedScale = scale - (playerCount > 1 ? (100 * playerCount) : 0);
        // Divide scale based on game time.
        double modifier = (game.getState().getTicksElapsed() / 1000f) / modifiedScale + 1;

        // Multiply health & min/max melee damage by waveCounter + 1 ^ base damage.
        int minMeleeDamage = (int) Math.pow(warlordsNPC.getMinMeleeDamage(), modifier);
        int maxMeleeDamage = (int) Math.pow(warlordsNPC.getMaxMeleeDamage(), modifier);
        float health = (float) Math.pow(warlordsNPC.getMaxBaseHealth(), modifier);
        // Increase boss health by 25% for each player in game instance.
        float bossMultiplier = 1 + (0.25f * playerCount);

        // Final health value after applying all modifiers.
        float finalHealth = health * (bossFlagCheck ? bossMultiplier : 1);
        warlordsNPC.setMaxHealthAndHeal(finalHealth);
        warlordsNPC.setMinMeleeDamage(minMeleeDamage);
        warlordsNPC.setMaxMeleeDamage(maxMeleeDamage);
    }

    private Component integrityScoreboard() {
        NamedTextColor color;
        if (integrityCounter >= 50) {
            color = NamedTextColor.AQUA;
        } else if (integrityCounter >= 25) {
            color = NamedTextColor.GOLD;
        } else {
            color = NamedTextColor.RED;
        }

        return Component.text("Soul Energy: ")
                        .append(Component.text(Math.round(integrityCounter) + "%", color));
    }

    @Override
    public Set<AbstractMob> getMobs() {
        return mobs.keySet();
    }

    @Override
    public ConcurrentHashMap<AbstractMob, Integer> getMobsMap() {
        return mobs;
    }

    @Override
    public int getTicksElapsed() {
        return ticksElapsed.get();
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public void spawnNewMob(AbstractMob mob) {
        mob.toNPC(game, Team.RED, this::modifyStats);
        game.addNPC(mob.getWarlordsNPC());
        mobs.put(mob, ticksElapsed.get());
        Bukkit.getPluginManager().callEvent(new WarlordsMobSpawnEvent(game, mob));
    }

    @Override
    public void spawnNewMob(AbstractMob mob, Team team) {
        mob.toNPC(game, team, this::modifyStats);
        game.addNPC(mob.getWarlordsNPC());
        mobs.put(mob, ticksElapsed.get());
        Bukkit.getPluginManager().callEvent(new WarlordsMobSpawnEvent(game, mob));
    }

    @Override
    public PveRewards<?> getRewards() {
        return onslaughtRewards;
    }

    public void setSpawnLimit(int spawnLimit) {
        this.spawnLimit = spawnLimit;
    }

    public HashMap<UUID, HashMap<Spendable, Long>> getPlayerSyntheticPouch() {
        return playerSyntheticPouch;
    }

    public HashMap<UUID, HashMap<Spendable, Long>> getPlayerAspirantPouch() {
        return playerAspirantPouch;
    }

    public void setIntegrityCounter(float integrityCounter) {
        this.integrityCounter = integrityCounter;
    }
}
