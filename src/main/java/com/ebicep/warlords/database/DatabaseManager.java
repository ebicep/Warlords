package com.ebicep.warlords.database;

import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.configuration.ApplicationConfiguration;
import com.ebicep.warlords.database.leaderboards.PlayerLeaderboardInfo;
import com.ebicep.warlords.database.leaderboards.guilds.GuildLeaderboardManager;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.database.repositories.events.GameEventsService;
import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.games.GameService;
import com.ebicep.warlords.database.repositories.games.GamesCollections;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.guild.GuildService;
import com.ebicep.warlords.database.repositories.illusionvendor.IllusionVendorService;
import com.ebicep.warlords.database.repositories.items.WeeklyBlessingsService;
import com.ebicep.warlords.database.repositories.items.pojos.WeeklyBlessings;
import com.ebicep.warlords.database.repositories.masterworksfair.MasterworksFairService;
import com.ebicep.warlords.database.repositories.player.PlayerService;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.timings.TimingsService;
import com.ebicep.warlords.database.repositories.timings.pojos.DatabaseTiming;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.player.general.*;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.StarterWeapon;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;


public class DatabaseManager {

    public static final ConcurrentHashMap<PlayersCollections, ConcurrentHashMap<UUID, DatabasePlayer>> CACHED_PLAYERS = new ConcurrentHashMap<>() {{
        for (PlayersCollections value : PlayersCollections.VALUES) {
            put(value, new ConcurrentHashMap<>());
        }
    }};
    private static final AtomicInteger UPDATE_COOLDOWN = new AtomicInteger(0);
    private static final ConcurrentHashMap<PlayersCollections, Set<DatabasePlayer>> PLAYERS_TO_UPDATE = new ConcurrentHashMap<>() {{
        for (PlayersCollections value : PlayersCollections.VALUES) {
            put(value, new HashSet<>());
        }
    }};
    private static final ConcurrentHashMap<PlayersCollections, Set<DatabasePlayer>> PLAYERS_TO_UPDATE_2 = new ConcurrentHashMap<>() {{
        for (PlayersCollections value : PlayersCollections.VALUES) {
            put(value, new HashSet<>());
        }
    }};
    public static MongoClient mongoClient;
    public static MongoDatabase warlordsDatabase;
    public static PlayerService playerService;
    public static GameService gameService;
    public static TimingsService timingsService;
    public static MasterworksFairService masterworksFairService;
    public static GuildService guildService;
    public static GameEventsService gameEventsService;
    public static WeeklyBlessingsService weeklyBlessingsService;
    public static IllusionVendorService illusionVendorService;
    public static boolean enabled = true;

    public static void init() {
        if (!enabled) {
            NPCManager.createGameJoinNPCs();
            return;
        }
        if (!StatsLeaderboardManager.enabled) {
            NPCManager.createGameJoinNPCs();
        }
        if (ApplicationConfiguration.key == null) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage("Database key is null, disabling database");
            enabled = false;
            return;
        }

        AbstractApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);

        try {
            playerService = context.getBean("playerService", PlayerService.class);
            gameService = context.getBean("gameService", GameService.class);
            timingsService = context.getBean("timingsService", TimingsService.class);
            masterworksFairService = context.getBean("masterworksFairService", MasterworksFairService.class);
            guildService = context.getBean("guildService", GuildService.class);
            gameEventsService = context.getBean("gameEventsService", GameEventsService.class);
            weeklyBlessingsService = context.getBean("itemsWeeklyBlessingsService", WeeklyBlessingsService.class);
            illusionVendorService = context.getBean("illusionVendorService", IllusionVendorService.class);
        } catch (Exception e) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage(e.getMessage());
            return;
        }
        NPCManager.createDatabaseRequiredNPCs();
        if (!StatsLeaderboardManager.enabled) {
            DatabaseGameEvent.startGameEvent();
        }

        //Loading all online players
        Bukkit.getOnlinePlayers().forEach(player -> {
            for (PlayersCollections collection : PlayersCollections.ACTIVE_COLLECTIONS) {
                loadPlayer(player.getUniqueId(), collection, (databasePlayer) -> {
                });
            }
        });

        ChatUtils.MessageType.GUILD_SERVICE.sendMessage("Storing all guilds");
        long guildStart = System.nanoTime();
        Warlords.newChain()
                .asyncFirst(() -> guildService.findAll())
                .syncLast(GuildManager.GUILDS::addAll)
                .sync(() -> {
                    GuildManager.GUILDS.removeIf(guild -> guild.getDisbandDate() != null);
                    ChatUtils.MessageType.GUILD_SERVICE.sendMessage("Stored " + GuildManager.GUILDS.size() + " guilds in " + (System.nanoTime() - guildStart) / 1000000 + "ms");
                    DatabaseTiming.checkTimings();
                    GuildLeaderboardManager.recalculateAllLeaderboards();
                    GuildManager.reloadPlayerCaches();
                })
                .execute();

        //runnable that updates all player that need updating every 10 seconds (prevents spam update)
        new BukkitRunnable() {

            @Override
            public void run() {
                UPDATE_COOLDOWN.incrementAndGet();
                if (UPDATE_COOLDOWN.get() % 10 == 0) {
                    PLAYERS_TO_UPDATE_2.forEach((playersCollections, databasePlayers) -> PLAYERS_TO_UPDATE.get(playersCollections).addAll(databasePlayers));
                    PLAYERS_TO_UPDATE_2.forEach((playersCollections, databasePlayers) -> databasePlayers.clear());
                    updateQueue();
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 20, 20);

        ChatUtils.MessageType.LEADERBOARDS.sendMessage("Loading Leaderboard Holograms - " + StatsLeaderboardManager.enabled);
        Warlords.newChain()
                .async(() -> StatsLeaderboardManager.addHologramLeaderboards(true))
                .execute();

        //Loading last 5 games
        ChatUtils.MessageType.GAME_SERVICE.sendMessage("Loading Last Games");
        long gameStart = System.nanoTime();
        Warlords.newChain()
                .asyncFirst(() -> gameService.getLastGames(15))
                .syncLast((games) -> {
                    ChatUtils.MessageType.GAME_SERVICE.sendMessage("Loaded Last Games in " + (System.nanoTime() - gameStart) / 1000000 + "ms");
                    DatabaseGameBase.previousGames.addAll(games);
                    StatsLeaderboardManager.PLAYER_LEADERBOARD_INFOS.values().forEach(PlayerLeaderboardInfo::resetGameHologram);
                    Bukkit.getOnlinePlayers().forEach(DatabaseGameBase::setGameHologramVisibility);
                    ChatUtils.MessageType.GAME_SERVICE.sendMessage("Set Game Hologram Visibility");
                })
                .execute();
    }

    public static void loadPlayer(UUID uuid, PlayersCollections collections, Consumer<DatabasePlayer> callback) {
        if (playerService == null || !enabled) {
            return;
        }
        getPlayer(uuid, collections, databasePlayer -> {
                    if (collections == PlayersCollections.LIFETIME) {
                        Warlords.newChain()
                                .sync(() -> {
                                    loadPlayerInfo(uuid, databasePlayer);
                                    callback.accept(databasePlayer);
                                }).execute();
                    }
                    ChatUtils.MessageType.PLAYER_SERVICE.sendMessage("Loaded Player " + uuid + " in " + collections);
                },
                () -> {
                    DatabasePlayer newDatabasePlayer = new DatabasePlayer(uuid, Bukkit.getOfflinePlayer(uuid).getName());
                    CACHED_PLAYERS.get(collections).put(uuid, newDatabasePlayer);
                    Warlords.newChain()
                            .asyncFirst(() -> playerService.create(newDatabasePlayer, collections))
                            .syncLast((databasePlayer) -> {
                                if (collections == PlayersCollections.LIFETIME) {
                                    loadPlayerInfo(uuid, databasePlayer);
                                    callback.accept(databasePlayer);
                                }
                            }).execute();
                }
        );
    }

    public static void updateQueue() {
        synchronized (PLAYERS_TO_UPDATE) {
            PLAYERS_TO_UPDATE.forEach((playersCollections, databasePlayers) -> {
                databasePlayers.forEach(databasePlayer -> {
                    Warlords.newChain()
                            .async(() -> playerService.update(databasePlayer, playersCollections))
                            .execute();
                });
                databasePlayers.clear();
            });
        }
    }

    public static void getPlayer(UUID uuid, PlayersCollections playersCollections, Consumer<DatabasePlayer> databasePlayerConsumer, Runnable onNotFound) {
        if (playerService == null || !enabled) {
            ConcurrentHashMap<UUID, DatabasePlayer> concurrentHashMap = DatabaseManager.CACHED_PLAYERS.get(playersCollections);
            databasePlayerConsumer.accept(concurrentHashMap.computeIfAbsent(uuid, k -> new DatabasePlayer(uuid, Bukkit.getOfflinePlayer(uuid).getName())));
            return;
        }
        ChatUtils.MessageType.PLAYER_SERVICE.sendMessage("Getting player " + uuid + " in " + playersCollections + " - cached = " + inCache(uuid,
                playersCollections
        ));
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(uuid, playersCollections);
        if (databasePlayer != null) {
            databasePlayerConsumer.accept(databasePlayer);
        } else {
            onNotFound.run();
        }
    }

    @Nonnull
    public static DatabasePlayer getPlayer(UUID uuid, PlayersCollections playersCollections, boolean isAPlayer) {
        if (!isAPlayer || playerService == null || !enabled) {
            ConcurrentHashMap<UUID, DatabasePlayer> concurrentHashMap = DatabaseManager.CACHED_PLAYERS.get(playersCollections);
            if (isAPlayer) {
                return concurrentHashMap.computeIfAbsent(uuid, k -> new DatabasePlayer(uuid, Bukkit.getOfflinePlayer(uuid).getName()));
            } else {
                return concurrentHashMap.getOrDefault(uuid, new DatabasePlayer(uuid, Bukkit.getOfflinePlayer(uuid).getName()));
            }
        }
        ChatUtils.MessageType.PLAYER_SERVICE.sendMessage("Getting player " + uuid + " in " + playersCollections + " - cached = " + inCache(uuid,
                playersCollections
        ));
        return DatabaseManager.playerService.findByUUID(uuid, playersCollections);
    }

    @Nonnull
    public static DatabasePlayer getPlayer(UUID uuid, boolean isAPlayer) {
        return getPlayer(uuid, PlayersCollections.LIFETIME, isAPlayer);
    }

    private static void loadPlayerInfo(UUID uuid, DatabasePlayer databasePlayer) {
        //check weapon inventory
        List<AbstractWeapon> weaponInventory = databasePlayer.getPveStats().getWeaponInventory();
        for (Specializations value : Specializations.VALUES) {
            int count = (int) weaponInventory.stream().filter(w -> w.getSpecializations() == value).count();
            if (count == 0) {
                weaponInventory.add(new StarterWeapon(uuid, value));
                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
            }
        }

        PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(uuid);
        playerSettings.setSelectedSpec(databasePlayer.getLastSpec());

        for (Classes classes : Classes.VALUES) {
            playerSettings.setHelmet(classes, databasePlayer.getClass(classes).getHelmet());
            playerSettings.setArmor(classes, databasePlayer.getClass(classes).getArmor());
        }

        HashMap<Specializations, Weapons> weaponSkins = new HashMap<>();
        for (Specializations spec : Specializations.VALUES) {
            weaponSkins.put(spec, databasePlayer.getSpec(spec).getWeapon());
        }
        weaponSkins.values().removeAll(Collections.singleton(null));
        playerSettings.setWeaponSkins(weaponSkins);

        HashMap<Specializations, SkillBoosts> classesSkillBoosts = new HashMap<>();
        for (Specializations spec : Specializations.VALUES) {
            classesSkillBoosts.put(spec, databasePlayer.getSpec(spec).getSkillBoost());
        }
        classesSkillBoosts.values().removeAll(Collections.singleton(null));
        classesSkillBoosts.forEach((specializations, skillBoosts) -> {
            if (!specializations.skillBoosts.contains(skillBoosts)) {
                classesSkillBoosts.put(specializations, specializations.skillBoosts.get(0));
            }
        });
        playerSettings.setSpecsSkillBoosts(classesSkillBoosts);

        playerSettings.setHotkeyMode(databasePlayer.getHotkeyMode());
        playerSettings.setParticleQuality(databasePlayer.getParticleQuality());
        playerSettings.setFlagMessageMode(databasePlayer.getFlagMessageMode());
    }

    public static boolean inCache(UUID uuid, PlayersCollections collection) {
        return CACHED_PLAYERS.get(collection).containsKey(uuid);
    }

    public static void queueUpdatePlayerAsync(DatabasePlayer databasePlayer) {
        if (playerService == null || !enabled) {
            return;
        }
        if (UPDATE_COOLDOWN.get() < 100) {
            PLAYERS_TO_UPDATE_2.get(PlayersCollections.LIFETIME).add(databasePlayer);
        } else {
            PLAYERS_TO_UPDATE.get(PlayersCollections.LIFETIME).add(databasePlayer);
        }
    }

    public static void clearQueue(PlayersCollections collection) {
        synchronized (PLAYERS_TO_UPDATE) {
            PLAYERS_TO_UPDATE.get(collection).clear();
        }
        synchronized (PLAYERS_TO_UPDATE_2) {
            PLAYERS_TO_UPDATE_2.get(collection).clear();
        }
    }

    public static void getPlayer(UUID uuid, Consumer<DatabasePlayer> databasePlayerConsumer, Runnable onNotFound) {
        getPlayer(uuid, PlayersCollections.LIFETIME, databasePlayerConsumer, onNotFound);
    }

    public static void updateGameAsync(DatabaseGameBase databaseGame) {
        if (playerService == null || !enabled) {
            return;
        }
        Warlords.newChain().async(() -> gameService.save(databaseGame, GamesCollections.ALL)).execute();
        Warlords.newChain().async(() -> gameService.save(databaseGame, databaseGame.getGameMode().gamesCollections)).execute();
    }

    public static void updatePlayer(Player player, Consumer<DatabasePlayer> databasePlayerConsumer) {
        updatePlayer(player.getUniqueId(), databasePlayerConsumer);
    }

    public static void updatePlayer(UUID uuid, Consumer<DatabasePlayer> databasePlayerConsumer) {
        updatePlayer(uuid, PlayersCollections.LIFETIME, databasePlayerConsumer);
    }

    public static void updatePlayer(UUID uuid, PlayersCollections playersCollections, Consumer<DatabasePlayer> databasePlayerConsumer) {
        if (playerService == null || !enabled) {
            ConcurrentHashMap<UUID, DatabasePlayer> concurrentHashMap = DatabaseManager.CACHED_PLAYERS.get(playersCollections);
            databasePlayerConsumer.accept(concurrentHashMap.computeIfAbsent(uuid, k -> new DatabasePlayer(uuid, Bukkit.getOfflinePlayer(uuid).getName())));
            return;
        }
        getPlayer(uuid, playersCollections, databasePlayer -> {
            databasePlayerConsumer.accept(databasePlayer);
            queueUpdatePlayerAsync(databasePlayer, playersCollections);
        });
    }

    public static void getPlayer(UUID uuid, PlayersCollections playersCollections, Consumer<DatabasePlayer> databasePlayerConsumer) {
        getPlayer(uuid, playersCollections, databasePlayerConsumer, () -> {
        });
    }

    public static void queueUpdatePlayerAsync(DatabasePlayer databasePlayer, PlayersCollections collections) {
        if (playerService == null || !enabled) {
            return;
        }
        if (UPDATE_COOLDOWN.get() < 100) {
            PLAYERS_TO_UPDATE_2.get(collections).add(databasePlayer);
        } else {
            PLAYERS_TO_UPDATE.get(collections).add(databasePlayer);
        }
        //Warlords.newChain().async(() -> playerService.update(databasePlayer, collections)).execute();
    }

    public static void getPlayer(UUID uuid, Consumer<DatabasePlayer> databasePlayerConsumer) {
        getPlayer(uuid, PlayersCollections.LIFETIME, databasePlayerConsumer, () -> {
        });
    }

    public static void getPlayer(Player player, Consumer<DatabasePlayer> databasePlayerConsumer) {
        getPlayer(player.getUniqueId(), PlayersCollections.LIFETIME, databasePlayerConsumer, () -> {
        });
    }

    public static ConcurrentHashMap<UUID, DatabasePlayer> getLoadedPlayers(PlayersCollections playersCollections) {
        return CACHED_PLAYERS.get(playersCollections);
    }

    public static void updateWeeklyBlessings(WeeklyBlessings weeklyBlessings) {
        Warlords.newChain()
                .async(() -> weeklyBlessingsService.update(weeklyBlessings))
                .execute();
    }
}
