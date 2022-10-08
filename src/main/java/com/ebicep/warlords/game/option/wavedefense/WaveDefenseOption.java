package com.ebicep.warlords.game.option.wavedefense;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.events.game.pve.WarlordsGameWaveClearEvent;
import com.ebicep.warlords.events.game.pve.WarlordsGameWaveEditEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsPlayerAddCurrencyEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsPlayerGiveWeaponEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.BoundingBoxOption;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.WeaponOption;
import com.ebicep.warlords.game.option.marker.SpawnLocationMarker;
import com.ebicep.warlords.game.option.marker.TimerSkipAbleMarker;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.game.option.wavedefense.mobs.AbstractMob;
import com.ebicep.warlords.game.option.wavedefense.waves.Wave;
import com.ebicep.warlords.game.option.wavedefense.waves.WaveList;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.guilds.upgrades.AbstractGuildUpgrade;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.ebicep.warlords.util.chat.ChatUtils.sendMessage;
import static com.ebicep.warlords.util.warlords.Utils.iterable;


public class WaveDefenseOption implements Option {
    private static final int SCOREBOARD_PRIORITY = 5;
    private final Set<AbstractMob<?>> mobs = new HashSet<>();
    private final Team team;
    private final WaveList waves;
    private final DifficultyIndex difficulty;
    private final int maxWave;
    private final WaveDefenseStats waveDefenseStats = new WaveDefenseStats();
    SimpleScoreboardHandler scoreboard;
    private int waveCounter = 0;
    private int spawnCount = 0;
    private Wave currentWave;
    @Nonnull
    private Game game;
    private Location lastLocation = new Location(null, 0, 0, 0);
    @Nullable
    private BukkitTask spawner;

    public WaveDefenseOption(Team team, WaveList waves, DifficultyIndex difficulty) {
        this.team = team;
        this.waves = waves;
        this.difficulty = difficulty;
        this.maxWave = difficulty.getMaxWaves();
    }

    @Override
    public void register(Game game) {
        this.game = game;
        for (Option o : game.getOptions()) {
            if (o instanceof BoundingBoxOption) {
                BoundingBoxOption boundingBoxOption = (BoundingBoxOption) o;
                lastLocation = boundingBoxOption.getCenter();
            }
        }
        game.registerEvents(new Listener() {

            @EventHandler
            public void onEvent(WarlordsDamageHealingEvent event) {
                WarlordsEntity attacker = event.getAttacker();
                WarlordsEntity receiver = event.getPlayer();

                if (event.isDamageInstance()) {
                    if (attacker instanceof WarlordsNPC) {
                        AbstractMob<?> mob = ((WarlordsNPC) attacker).getMob();
                        if (mobs.contains(mob)) {
                            mob.onAttack(attacker, receiver, event);
                        }
                    }

                    if (receiver instanceof WarlordsNPC) {
                        AbstractMob<?> mob = ((WarlordsNPC) receiver).getMob();
                        if (mobs.contains(mob)) {
                            mob.onDamageTaken(receiver, attacker, event);
                        }
                    }
                }
            }

            @EventHandler
            public void onEvent(WarlordsDeathEvent event) {
                WarlordsEntity we = event.getPlayer();
                WarlordsEntity killer = event.getKiller();

                if (we instanceof WarlordsNPC) {
                    AbstractMob<?> mobToRemove = ((WarlordsNPC) we).getMob();
                    if (mobs.contains(mobToRemove)) {
                        mobs.remove(mobToRemove);
                        new GameRunnable(game) {
                            @Override
                            public void run() {
                                mobToRemove.onDeath(killer, we.getDeathLocation(), WaveDefenseOption.this);
                                game.removePlayer(we.getUuid());
                            }
                        }.runTask();

                        if (killer instanceof WarlordsPlayer) {
                            killer.getMinuteStats().addMobKill(mobToRemove.getName());
                            we.getHitBy().forEach((assisted, value) -> assisted.getMinuteStats().addMobAssist(mobToRemove.getName()));
                        }

                        if (WaveDefenseStats.BOSS_COIN_VALUES.containsKey(mobToRemove.getName())) {
                            waveDefenseStats.getBossesKilled().merge(mobToRemove.getName(), 1L, Long::sum);
                        }
                    }
                } else if (we instanceof WarlordsPlayer && killer instanceof WarlordsNPC) {
                    if (mobs.contains(((WarlordsNPC) killer).getMob())) {
                        we.getMinuteStats().addMobDeath(((WarlordsNPC) killer).getMob().getName());
                    }
                }
            }

            @EventHandler
            public void onWeaponDrop(WarlordsPlayerGiveWeaponEvent event) {
                waveDefenseStats.getPlayerWeaponsFound()
                        .computeIfAbsent(event.getPlayer().getUuid(), v -> new ArrayList<>())
                        .add(event.getWeapon());
            }

            @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
            public void onWin(WarlordsGameTriggerWinEvent event) {
                waveDefenseStats.cacheBaseCoinSummary(WaveDefenseOption.this);
                waveDefenseStats.storeWeaponFragmentGain(WaveDefenseOption.this);
            }

        });
        game.registerGameMarker(ScoreboardHandler.class, scoreboard = new SimpleScoreboardHandler(SCOREBOARD_PRIORITY, "wave") {
            @Nonnull
            @Override
            public List<String> computeLines(@Nullable WarlordsPlayer player) {
                return Collections.singletonList("Wave: " + ChatColor.GREEN + waveCounter + ChatColor.RESET + (maxWave < 10000 ? "/" + ChatColor.GREEN + maxWave : "") + ChatColor.RESET + (currentWave != null && currentWave.getMessage() != null ? " (" + currentWave.getMessage() + ")" : ""));
            }
        });
        game.registerGameMarker(ScoreboardHandler.class, scoreboard = new SimpleScoreboardHandler(SCOREBOARD_PRIORITY, "wave") {
            @Nonnull
            @Override
            public List<String> computeLines(@Nullable WarlordsPlayer player) {
                return Collections.singletonList("Monsters left: " + ChatColor.GREEN + mobs.size());
            }
        });
        game.registerGameMarker(ScoreboardHandler.class, scoreboard = new SimpleScoreboardHandler(6, "kills") {
            @Nonnull
            @Override
            public List<String> computeLines(@Nullable WarlordsPlayer player) {
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

    @Override
    public void start(@Nonnull Game game) {
        if (DatabaseManager.guildService != null) {
            HashMap<Guild, HashSet<UUID>> guilds = new HashMap<>();
            List<UUID> uuids = game.players().map(Map.Entry::getKey).collect(Collectors.toList());
            for (Guild guild : GuildManager.GUILDS) {
                for (UUID uuid : uuids) {
                    Optional<GuildPlayer> playerMatchingUUID = guild.getPlayerMatchingUUID(uuid);
                    if (playerMatchingUUID.isPresent()) {
                        guilds.computeIfAbsent(guild, k -> new HashSet<>()).add(uuid);
                        break;
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
                if (game.getState() instanceof EndState) {
                    this.cancel();
                    return;
                }

                if (mobs.isEmpty() && spawnCount == 0) {
                    newWave();

                    if (waveCounter > 1) {
                        getGame().forEachOnlineWarlordsPlayer(wp -> {
                            AtomicInteger currency = new AtomicInteger();
                            if (waveCounter % 5 == 1) {
                                currency.set(1000);
                            } else {
                                currency.set(200);
                            }
                            Bukkit.getPluginManager().callEvent(new WarlordsPlayerAddCurrencyEvent(wp, currency));
                            wp.addCurrency(currency.get());
                            wp.sendMessage(ChatColor.GOLD + "+" + currency + " ❂ Insignia");
                        });
                        Bukkit.getPluginManager().callEvent(new WarlordsGameWaveClearEvent(game, waveCounter - 1));
                    }
                }

                for (AbstractMob<?> mob : new ArrayList<>(mobs)) {
                    mob.whileAlive(counter, WaveDefenseOption.this);
                }

                counter++;
            }
        }.runTaskTimer(20, 0);
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
                Optional<AbstractWeapon> optionalWeapon = databasePlayer.getPveStats().getWeaponInventory()
                        .stream()
                        .filter(AbstractWeapon::isBound)
                        .filter(abstractWeapon -> abstractWeapon.getSpecializations() == player.getSpecClass())
                        .findFirst();
                optionalWeapon.ifPresent(abstractWeapon -> {
                    WarlordsPlayer warlordsPlayer = (WarlordsPlayer) player;

                    ((WarlordsPlayer) player).getCosmeticSettings().setWeaponSkin(abstractWeapon.getSelectedWeaponSkin());
                    warlordsPlayer.setWeapon(abstractWeapon);
                    abstractWeapon.applyToWarlordsPlayer(warlordsPlayer);
                    player.updateEntity();
                });
            });
        }
    }

    @Override
    public void updateInventory(@Nonnull WarlordsPlayer warlordsPlayer, Player player) {
        AbstractWeapon weapon = warlordsPlayer.getAbstractWeapon();
        if (weapon == null) {
            WeaponOption.showWeaponStats(warlordsPlayer, player);
        } else {
            WeaponOption.showPvEWeapon(warlordsPlayer, player);
        }

        player.getInventory().setItem(7, new ItemBuilder(Material.GOLD_NUGGET).name(ChatColor.GREEN + "Upgrade Talisman").get());
    }

    @Override
    public void onSpecChange(@Nonnull WarlordsEntity player) {
        if (player instanceof WarlordsPlayer) {
            ((WarlordsPlayer) player).resetAbilityTree();
        }
    }

    private List<String> healthScoreboard(Game game) {
        List<String> list = new ArrayList<>();
        for (WarlordsEntity we : PlayerFilter.playingGame(game).filter(e -> e instanceof WarlordsPlayer)) {
            float healthRatio = we.getHealth() / we.getMaxHealth();
            ChatColor healthColor;
            String name = we.getName();
            String newName;

            if (healthRatio >= .5) {
                healthColor = ChatColor.GREEN;
            } else if (healthRatio >= .25) {
                healthColor = ChatColor.YELLOW;
            } else {
                healthColor = ChatColor.RED;
            }

            if (name.length() >= 8) {
                newName = name.substring(0, 8);
            } else {
                newName = name;
            }

            list.add(newName + ": " + (we.isDead() ? ChatColor.DARK_RED + "DEAD" : healthColor + "❤ " + (int) we.getHealth()) + ChatColor.RESET + " / " + ChatColor.RED + "⚔ " + we.getMinuteStats()
                    .total()
                    .getKills());
        }

        return list;
    }

    public void newWave() {
        if (currentWave != null) {
            String message;
            if (currentWave.getMessage() != null) {
                message = ChatColor.GREEN + "Wave complete! (" + currentWave.getMessage() + ")";
            } else {
                message = ChatColor.GREEN + "Wave complete!";
            }

            for (Map.Entry<Player, Team> entry : iterable(game.onlinePlayers())) {
                sendMessage(entry.getKey(), false, message);
                entry.getKey().playSound(entry.getKey().getLocation(), Sound.LEVEL_UP, 500, 2);
            }
        }
        waveCounter++;
        currentWave = waves.getWave(waveCounter, new Random());
        spawnCount = currentWave.getMonsterCount();

        for (Map.Entry<Player, Team> entry : iterable(game.onlinePlayers())) {
            if (currentWave.getMessage() != null) {
                sendMessage(entry.getKey(),
                        false,
                        ChatColor.YELLOW + "A boss will spawn in §c" + currentWave.getDelay() / 20 + " §eseconds!"
                );
            } else {
                int playerCount = (int) game.warlordsPlayers().count();
                switch (playerCount) {
                    case 2:
                        spawnCount *= 1.06f;
                        break;
                    case 3:
                        spawnCount *= 1.1f;
                        break;
                    case 4:
                        spawnCount *= 1.16f;
                        break;
                }

                sendMessage(entry.getKey(),
                        false,
                        ChatColor.YELLOW + "A wave of §c§l" + spawnCount + "§e monsters will spawn in §c" + currentWave.getDelay() / 20 + " §eseconds!"
                );
            }

            float soundPitch = 0.8f;
            String wavePrefix = "§eWave ";
            if (waveCounter >= 20) {
                soundPitch = 0.75f;
                wavePrefix = "§eWave ";
            }
            if (waveCounter >= 40) {
                soundPitch = 0.7f;
                wavePrefix = "§6Wave ";
            }
            if (waveCounter >= 60) {
                soundPitch = 0.65f;
                wavePrefix = "§7Wave ";
            }
            if (waveCounter >= 80) {
                soundPitch = 0.5f;
                wavePrefix = "§8§lWave ";
            }
            if (waveCounter >= 100) {
                soundPitch = 0.4f;
                wavePrefix = "§d§lWave ";
            }
            if (waveCounter >= 150) {
                soundPitch = 0.3f;
                wavePrefix = "§5§lWave ";
            }
            if (waveCounter >= 200) {
                soundPitch = 0.2f;
                wavePrefix = "§5W§5§k§la§5§lve ";
            }
            if (waveCounter >= 250) {
                soundPitch = 0.1f;
                wavePrefix = "§4W§4§k§la§4§lve ";
            }
            if (waveCounter >= 300) {
                wavePrefix = "§0W§0§k§la§0§lv§0§k§le§4§l ";
            }

            entry.getKey().playSound(entry.getKey().getLocation(), Sound.WITHER_SPAWN, 500, soundPitch);
            PacketUtils.sendTitle(entry.getKey(), wavePrefix + waveCounter, "", 20, 60, 20);
        }
        startSpawnTask();
    }

    public void startSpawnTask() {
        if (spawner != null) {
            spawner.cancel();
            spawner = null;
        }

        if (spawnCount == 0) {
            return;
        }

        // temp
        if (currentWave.getMessage() == null) {
            int playerCount = (int) game.warlordsPlayers().count();
            switch (playerCount) {
                case 2:
                    spawnCount *= 1.06f;
                    break;
                case 3:
                    spawnCount *= 1.1f;
                    break;
                case 4:
                    spawnCount *= 1.16f;
                    break;
            }
        }

        spawner = new GameRunnable(game) {
            WarlordsEntity lastSpawn = null;
            int counter = 0;

            @Override
            public void run() {
                if (game.getState() instanceof EndState) {
                    this.cancel();
                    return;
                }

                counter++;
                if (lastSpawn == null) {
                    lastSpawn = spawn(lastLocation);
                    if (lastSpawn != null) {
                        Location newLoc = getSpawnLocation(lastSpawn);
                        lastSpawn.teleport(newLoc);
                        lastSpawn.getLocation(lastLocation);
                    }
                } else {
                    lastSpawn = spawn(getSpawnLocation(lastSpawn));
                    lastSpawn.getLocation(lastLocation);
                }

                spawnCount--;
                if (spawnCount <= 0) {
                    spawner.cancel();
                    spawner = null;
                }
            }

            public WarlordsEntity spawn(Location loc) {
                AbstractMob<?> abstractMob = currentWave.spawnRandomMonster(loc);
                mobs.add(abstractMob);
                return abstractMob.toNPC(game, team, UUID.randomUUID());
            }

            private Location getSpawnLocation(WarlordsEntity entity) {
                List<Location> candidates = new ArrayList<>();
                double priority = Double.NEGATIVE_INFINITY;
                for (SpawnLocationMarker marker : getGame().getMarkers(SpawnLocationMarker.class)) {
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

        }.runTaskTimer(currentWave.getDelay(), 13);
    }

    public void spawnNewMob(AbstractMob<?> abstractMob) {
        abstractMob.toNPC(game, Team.RED, UUID.randomUUID());
        game.addNPC(abstractMob.getWarlordsNPC());
        mobs.add(abstractMob);
    }

    public Set<AbstractMob<?>> getMobs() {
        return mobs;
    }

    public int getWaveCounter() {
        return waveCounter;
    }

    public void setWaveCounter(int waveCounter) {
        this.waveCounter = waveCounter - 1;
        newWave();
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

    @Nonnull
    public Game getGame() {
        return game;
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

    public WaveDefenseStats getWaveDefenseStats() {
        return waveDefenseStats;
    }

    public DifficultyIndex getDifficulty() {
        return difficulty;
    }
}
