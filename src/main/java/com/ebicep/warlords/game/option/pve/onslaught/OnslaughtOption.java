package com.ebicep.warlords.game.option.pve.onslaught;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.events.game.pve.WarlordsMobSpawnEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.WeaponOption;
import com.ebicep.warlords.game.option.cuboid.BoundingBoxOption;
import com.ebicep.warlords.game.option.marker.SpawnLocationMarker;
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
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.rewards.RewardInventory;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.java.RandomCollection;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
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

public class OnslaughtOption implements Option, PveOption {

    private final Team team;
    private final WaveList mobSet;
    private final AtomicInteger ticksElapsed = new AtomicInteger(200); //start at 200 to account for 10 second start delay
    private final ConcurrentHashMap<AbstractMob<?>, Integer> mobs = new ConcurrentHashMap<>();
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
                    AbstractMob<?> mobToRemove = ((WarlordsNPC) we).getMob();
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
            System.out.println(guilds);
            guilds.forEach((guild, validUUIDs) -> {
                for (AbstractGuildUpgrade<?> upgrade : guild.getUpgrades()) {
                    System.out.println("Upgrading " + upgrade.getUpgrade().getName() + " for " + validUUIDs.size() + " players");
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

                for (AbstractMob<?> mob : new ArrayList<>(mobs.keySet())) {
                    mob.whileAlive(mobs.get(mob) - ticksElapsed.get(), OnslaughtOption.this);
                }

                if (ticksElapsed.get() % 36000 == 0) {
                    game.warlordsPlayers().forEach(wp -> {
                        wp.playSound(wp.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 2, 0.1f);
                        addRewardToPlayerPouch(
                                wp.getUuid(),
                                OnslaughtRewards.ASPIRANT_POUCH_LOOT_POOL,
                                playerAspirantPouch,
                                ChatColor.RED + "Aspirant Pouch"
                        );
                    });
                } else if (ticksElapsed.get() % 6000 == 0) {
                    integrityDecayIncrease += 0.1f;
                    game.warlordsPlayers().forEach(wp -> {
                        wp.playSound(wp.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 0.1f);
                        addRewardToPlayerPouch(
                                wp.getUuid(),
                                OnslaughtRewards.SYNTHETIC_POUCH_LOOT_POOL,
                                playerSyntheticPouch,
                                ChatColor.AQUA + "Synthetic Pouch"
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
                lastSpawn.getLocation(lastLocation);

                spawnCount++;
            }

            public WarlordsEntity spawn(Location loc) {
                currentMobSet = mobSet.getWave((game.getState().getTicksElapsed() / 20) / 60, new Random());
                AbstractMob<?> abstractMob = currentMobSet.spawnRandomMonster(loc);
                mobs.put(abstractMob, ticksElapsed.get());
                WarlordsNPC wpc = abstractMob.toNPC(game, team, UUID.randomUUID(), OnslaughtOption.this::modifyStats);
                Bukkit.getPluginManager().callEvent(new WarlordsMobSpawnEvent(game, abstractMob));
                return wpc;
            }

            private Location getSpawnLocation(WarlordsEntity entity) {
                List<Location> candidates = new ArrayList<>();
                double priority = Double.NEGATIVE_INFINITY;
                for (SpawnLocationMarker marker : getGame().getMarkers(SpawnLocationMarker.class)) {
                    if (entity == null) {
                        return marker.getLocation();
                    }
                    if (candidates.isEmpty()) {
                        candidates.add(marker.getLocation());
                        priority = marker.getPriority(entity);
                    } else {
                        double newPriority = marker.getPriority(entity);
                        if (newPriority >= priority) {
                            if (newPriority > priority) {
                                candidates.clear();
                                priority = newPriority;
                            }
                            candidates.add(marker.getLocation());
                        }
                    }
                }
                if (!candidates.isEmpty()) {
                    return candidates.get((int) (Math.random() * candidates.size()));
                }
                return lastLocation;
            }

        }.runTaskTimer(10 * GameRunnable.SECOND, 6);
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        if (player instanceof WarlordsPlayer) {
            player.setInPve(true);
            if (player.getEntity() instanceof Player) {
                game.setPlayerTeam((OfflinePlayer) player.getEntity(), Team.BLUE);
                player.setTeam(Team.BLUE);
                player.updateArmor();
            }

            DatabaseManager.getPlayer(player.getUuid(), databasePlayer -> {
                Optional<AbstractWeapon> optionalWeapon = databasePlayer
                        .getPveStats()
                        .getWeaponInventory()
                        .stream()
                        .filter(AbstractWeapon::isBound)
                        .filter(abstractWeapon -> abstractWeapon.getSpecializations() == player.getSpecClass())
                        .findFirst();
                optionalWeapon.ifPresent(abstractWeapon -> {
                    WarlordsPlayer wp = (WarlordsPlayer) player;

                    ((WarlordsPlayer) player).getCosmeticSettings().setWeaponSkin(abstractWeapon.getSelectedWeaponSkin());
                    wp.setWeapon(abstractWeapon);
                    abstractWeapon.applyToWarlordsPlayer(wp, this);
                    player.updateEntity();
                    player.getSpec().updateCustomStats();
                });
            });
        }
    }

    @Override
    public void updateInventory(@Nonnull WarlordsPlayer wp, Player player) {
        AbstractWeapon weapon = wp.getWeapon();
        if (weapon == null) {
            WeaponOption.showWeaponStats(wp, player);
        } else {
            WeaponOption.showPvEWeapon(wp, player);
        }

        player.getInventory().setItem(7, new ItemBuilder(Material.GOLD_NUGGET).name(Component.text("Upgrade Talisman", NamedTextColor.GREEN)).get());
        if (wp.getWeapon() instanceof AbstractLegendaryWeapon) {
            ((AbstractLegendaryWeapon) wp.getWeapon()).updateAbilityItem(wp, player);
        }
    }

    @Override
    public void onSpecChange(@Nonnull WarlordsEntity player) {
        if (player instanceof WarlordsPlayer) {
            ((WarlordsPlayer) player).resetAbilityTree();
        }
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

    @Override
    public void spawnNewMob(AbstractMob<?> mob) {
        mob.toNPC(game, Team.RED, UUID.randomUUID(), this::modifyStats);
        game.addNPC(mob.getWarlordsNPC());
        mobs.put(mob, ticksElapsed.get());
        Bukkit.getPluginManager().callEvent(new WarlordsMobSpawnEvent(game, mob));
    }

    private void addRewardToPlayerPouch(
            UUID uuid,
            RandomCollection<Pair<Spendable, Long>> pouchLootPool,
            HashMap<UUID, HashMap<Spendable, Long>> playerPouch,
            String pouchName
    ) {
        Pair<Spendable, Long> reward = pouchLootPool.next();
        if (reward != null) {
            Spendable spendable = reward.getA();
            Long amount = reward.getB();
            playerPouch.computeIfAbsent(uuid, k -> new HashMap<>())
                       .merge(spendable, amount, Long::sum);
            String rewardString = spendable.getTextColor() + "+" + spendable.getCostColoredName(amount);
            RewardInventory.sendRewardMessage(uuid,
                    pouchName + ": " + rewardString
            );
            ChatUtils.sendTitleToGamePlayers(
                    game,
                    pouchName + ":",
                    rewardString
            );
        }
    }

    public int getSpawnLimit(int playerCount) {
        return switch (playerCount) {
            case 1 -> 7;
            case 2 -> 11;
            case 3 -> 15;
            case 4 -> 20;
            case 5 -> 25;
            case 6 -> 30;
            default -> spawnLimit;
        };
    }

    @Override
    public int playerCount() {
        return (int) game.warlordsPlayers().count();
    }

    @Override
    public Set<AbstractMob<?>> getMobs() {
        return mobs.keySet();
    }

    @Override
    public int getTicksElapsed() {
        return ticksElapsed.get();
    }

    @Override
    public void spawnNewMob(AbstractMob<?> mob, Team team) {
        mob.toNPC(game, team, UUID.randomUUID(), this::modifyStats);
        game.addNPC(mob.getWarlordsNPC());
        mobs.put(mob, ticksElapsed.get());
        Bukkit.getPluginManager().callEvent(new WarlordsMobSpawnEvent(game, mob));
    }

    @Override
    public ConcurrentHashMap<AbstractMob<?>, Integer> getMobsMap() {
        return mobs;
    }

    @Override
    public PveRewards<?> getRewards() {
        return onslaughtRewards;
    }

    @Override
    public Game getGame() {
        return game;
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
        boolean bossFlagCheck = playerCount > 1 && warlordsNPC.getMobTier() == MobTier.BOSS;
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
        warlordsNPC.setMaxBaseHealth(finalHealth);
        warlordsNPC.setMaxHealth(finalHealth);
        warlordsNPC.setHealth(finalHealth);

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
