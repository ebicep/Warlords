package com.ebicep.warlords.database;

import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.cache.MultipleCacheResolver;
import com.ebicep.warlords.database.configuration.ApplicationConfiguration;
import com.ebicep.warlords.database.leaderboards.PlayerLeaderboardInfo;
import com.ebicep.warlords.database.leaderboards.guilds.GuildLeaderboardManager;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.database.repositories.games.GameService;
import com.ebicep.warlords.database.repositories.games.GamesCollections;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.guild.GuildService;
import com.ebicep.warlords.database.repositories.masterworksfair.MasterworksFairService;
import com.ebicep.warlords.database.repositories.player.PlayerService;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.timings.TimingsService;
import com.ebicep.warlords.database.repositories.timings.pojos.DatabaseTiming;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.menu.PlayerHotBarItemListener;
import com.ebicep.warlords.player.general.*;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.StarterWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase.previousGames;


public class DatabaseManager {

    private static final ConcurrentHashMap<PlayersCollections, Set<DatabasePlayer>> PLAYERS_TO_UPDATE = new ConcurrentHashMap<PlayersCollections, Set<DatabasePlayer>>() {{
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
    public static boolean enabled = true;

    public static void init() {
        if (!enabled) {
            NPCManager.createGameJoinNPCs();
            return;
        }
        if (!StatsLeaderboardManager.enabled) {
            NPCManager.createGameJoinNPCs();
        }

        AbstractApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);

        try {
            playerService = context.getBean("playerService", PlayerService.class);
            gameService = context.getBean("gameService", GameService.class);
            timingsService = context.getBean("timingsService", TimingsService.class);
            masterworksFairService = context.getBean("masterworksFairService", MasterworksFairService.class);
            guildService = context.getBean("guildService", GuildService.class);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        NPCManager.createDatabaseRequiredNPCs();

        try {
            for (String cacheName : MultipleCacheResolver.playersCacheManager.getCacheNames()) {
                Objects.requireNonNull(MultipleCacheResolver.playersCacheManager.getCache(cacheName)).clear();
            }
            ChatUtils.MessageTypes.WARLORDS.sendMessage("Cleared all players cache");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Loading all online players
        Bukkit.getOnlinePlayers().forEach(player -> {
            loadPlayer(player.getUniqueId(), PlayersCollections.LIFETIME, (databasePlayer) -> {
                PlayerHotBarItemListener.giveLobbyHotBarDatabase(player);
            });
        });

        ChatUtils.MessageTypes.GUILD_SERVICE.sendMessage("Storing all guilds");
        long guildStart = System.nanoTime();
        Warlords.newChain()
                .asyncFirst(() -> guildService.findAll())
                .syncLast(GuildManager.GUILDS::addAll)
                .sync(() -> {
                    GuildManager.GUILDS.removeIf(guild -> guild.getDisbandDate() != null);
                    ChatUtils.MessageTypes.GUILD_SERVICE.sendMessage("Stored " + GuildManager.GUILDS.size() + " guilds in " + (System.nanoTime() - guildStart) / 1000000 + "ms");
                    DatabaseTiming.checkStatsTimings();
                    GuildLeaderboardManager.recalculateAllLeaderboards();
                    GuildManager.reloadPlayerCaches();
                })
                .execute();

        //runnable that updates all player that need updating every 10 seconds (prevents spam update)
        new BukkitRunnable() {

            @Override
            public void run() {
                Warlords.newChain()
                        .async(DatabaseManager::updateQueue)
                        .sync(() -> PLAYERS_TO_UPDATE.forEach((playersCollections, databasePlayers) -> databasePlayers.clear()))
                        .execute();
            }
        }.runTaskTimer(Warlords.getInstance(), 20, 20 * 10);

        ChatUtils.MessageTypes.LEADERBOARDS.sendMessage("Loading Leaderboard Holograms - " + StatsLeaderboardManager.enabled);
        Warlords.newChain()
                .async(() -> StatsLeaderboardManager.addHologramLeaderboards(true))
                .execute();

        //Loading last 5 games
        ChatUtils.MessageTypes.GAME_SERVICE.sendMessage("Loading Last Games");
        long gameStart = System.nanoTime();
        Warlords.newChain()
                .asyncFirst(() -> gameService.getLastGames(15))
                .syncLast((games) -> {
                    ChatUtils.MessageTypes.GAME_SERVICE.sendMessage("Loaded Last Games in " + (System.nanoTime() - gameStart) / 1000000 + "ms");
                    previousGames.addAll(games);
                    StatsLeaderboardManager.PLAYER_LEADERBOARD_INFOS.values().forEach(PlayerLeaderboardInfo::resetGameHologram);
                    Bukkit.getOnlinePlayers().forEach(DatabaseGameBase::setGameHologramVisibility);
                    ChatUtils.MessageTypes.GAME_SERVICE.sendMessage("Set Game Hologram Visibility");
                })
                .execute();
    }

    public static void loadPlayer(UUID uuid, PlayersCollections collections, Consumer<DatabasePlayer> callback) {
        if (playerService == null || !enabled) {
            return;
        }
        getPlayer(uuid, collections,
                databasePlayer -> {
                    if (collections == PlayersCollections.LIFETIME) {
                        Warlords.newChain()
                                .sync(() -> {
                                    loadPlayerInfo(uuid, databasePlayer);
                                    callback.accept(databasePlayer);
                                    ChatUtils.MessageTypes.PLAYER_SERVICE.sendMessage("Loaded Player " + uuid);
                                }).execute();
                    }
                },
                () -> {
                    Warlords.newChain()
                            .asyncFirst(() -> playerService.create(new DatabasePlayer(uuid, Bukkit.getOfflinePlayer(uuid).getName()), collections))
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
            PLAYERS_TO_UPDATE.forEach((playersCollections, databasePlayers) -> databasePlayers.forEach(databasePlayer -> playerService.update(databasePlayer,
                    playersCollections
            )));
        }
    }

    public static void getPlayer(UUID uuid, PlayersCollections playersCollections, Consumer<DatabasePlayer> databasePlayerConsumer, Runnable onNotFound) {
        if (playerService == null || !enabled) {
            return;
        }
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(uuid, playersCollections);
        if (databasePlayer != null) {
            databasePlayerConsumer.accept(databasePlayer);
        } else {
            onNotFound.run();
        }
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
        for (AbstractWeapon abstractWeapon : weaponInventory) {
            if (abstractWeapon instanceof AbstractLegendaryWeapon) {
                if (((AbstractLegendaryWeapon) abstractWeapon).getUnlockedTitles().isEmpty()) {
                    for (LegendaryTitles value : LegendaryTitles.VALUES) {
                        if (value.clazz.equals(abstractWeapon.getClass())) {
                            ((AbstractLegendaryWeapon) abstractWeapon).getUnlockedTitles().add(value);
                            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                            break;
                        }
                    }
                }
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

    public static void checkUpdatePlayerName(Player player, DatabasePlayer databasePlayer) {
        if (!Objects.equals(databasePlayer.getName(), player.getName())) {
            databasePlayer.setName(player.getName());
            queueUpdatePlayerAsync(databasePlayer);
        }
    }

    public static void queueUpdatePlayerAsync(DatabasePlayer databasePlayer) {
        if (playerService == null || !enabled) {
            return;
        }
        PLAYERS_TO_UPDATE.get(PlayersCollections.LIFETIME).add(databasePlayer);
        //Warlords.newChain().async(() -> playerService.update(databasePlayer)).execute();
    }

    public static void getPlayer(UUID uuid, Consumer<DatabasePlayer> databasePlayerConsumer, Runnable onNotFound) {
        getPlayer(uuid, PlayersCollections.LIFETIME, databasePlayerConsumer, onNotFound);
    }

    public static void queueUpdatePlayerAsync(DatabasePlayer databasePlayer, PlayersCollections collections) {
        if (playerService == null || !enabled) {
            return;
        }
        PLAYERS_TO_UPDATE.get(collections).add(databasePlayer);
        //Warlords.newChain().async(() -> playerService.update(databasePlayer, collections)).execute();
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

    public static void getPlayer(UUID uuid, Consumer<DatabasePlayer> databasePlayerConsumer) {
        getPlayer(uuid, PlayersCollections.LIFETIME, databasePlayerConsumer, () -> {
        });
    }

    public static Map<UUID, DatabasePlayer> getLoadedPlayers(PlayersCollections playersCollections) {
        CaffeineCache cache = (CaffeineCache) MultipleCacheResolver.playersCacheManager.getCache(playersCollections.cacheName);
        if (cache == null) {
            return new HashMap<>();
        }
        ConcurrentMap<Object, Object> objectMap = cache.getNativeCache().asMap();
        return objectMap.entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> (UUID) entry.getKey(), entry -> (DatabasePlayer) entry.getValue()));
    }

}
