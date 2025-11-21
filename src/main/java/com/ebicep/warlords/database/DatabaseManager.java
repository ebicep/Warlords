package com.ebicep.warlords.database;

import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.configuration.ApplicationConfiguration;
import com.ebicep.warlords.database.leaderboards.PlayerLeaderboardInfo;
import com.ebicep.warlords.database.leaderboards.guilds.GuildLeaderboardManager;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
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
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.database.repositories.timings.TimingsService;
import com.ebicep.warlords.database.repositories.timings.pojos.DatabaseTiming;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.player.general.*;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.StarterWeapon;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


public class DatabaseManager {

    public static final ConcurrentHashMap<PlayersCollections, ConcurrentHashMap<UUID, DatabasePlayer>> CACHED_PLAYERS = new ConcurrentHashMap<>() {{
        for (PlayersCollections value : PlayersCollections.VALUES) {
            put(value, new ConcurrentHashMap<>());
        }
    }};
    public static final DatabasePlayer CACHED_MOB_DATABASEPLAYER = new DatabasePlayer();

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
    public static volatile boolean enabled = true;

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

        Bukkit.getOnlinePlayers().forEach(player -> player.kick(Component.text("Server is restarting, please rejoin in a few minutes!")));

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
            ConfigManager.loadConfigs(warlordsDatabase);
        } catch (Exception e) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
            enabled = false;
            return;
        }

        WeeklyBlessings.loadAllWeeklyBlessings();

        NPCManager.createDatabaseRequiredNPCs();
        if (!StatsLeaderboardManager.enabled) {
            DatabaseGameEvent.startGameEvent();
        }


        ChatUtils.MessageType.GUILD_SERVICE.sendMessage("Storing all guilds");
        long guildStart = System.nanoTime();
        Warlords.newChain()
                .asyncFirst(() -> guildService.findAll())
                .syncLast(GuildManager.GUILDS::addAll)
                .sync(() -> {
                    GuildManager.GUILDS.removeIf(guild -> guild.getDisbandDate() != null);
                    ChatUtils.MessageType.GUILD_SERVICE.sendMessage("Stored " + GuildManager.GUILDS.size() + " guilds in " + (System.nanoTime() - guildStart) / 1000000 + "ms");
                    DatabaseTiming.checkTimings();
                    GuildManager.init();
                    GuildLeaderboardManager.recalculateAllLeaderboards();
                })
                .delay(20, TimeUnit.SECONDS)
                .sync(() -> {
                    if (!StatsLeaderboardManager.enabled) {
                        DatabaseTiming.checkLeaderboardResets();
                    }
                })
                .execute();

        //runnable that updates all player that need updating every 10 seconds (prevents spam update)
        new BukkitRunnable() {

            @Override
            public void run() {
                if (enabled) {
                    DatabaseUpdater.updatePlayers(playerService);
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 20 * 10, 20 * 10);

        ChatUtils.MessageType.LEADERBOARDS.sendMessage("Loading Leaderboard Holograms - " + StatsLeaderboardManager.enabled);
        Warlords.newChain()
                .async(() -> StatsLeaderboardManager.addHologramLeaderboards(true))
                .execute();

        //Loading last 5 games
        ChatUtils.MessageType.GAME_SERVICE.sendMessage("Loading Last Games");
        long gameStart = System.nanoTime();
        Warlords.newChain()
                .asyncFirst(() -> gameService.getLastGames(DatabaseGameBase.MAX_GAMES))
                .syncLast((games) -> {
                    ChatUtils.MessageType.GAME_SERVICE.sendMessage("Loaded Last Games in " + (System.nanoTime() - gameStart) / 1000000 + "ms");
                    DatabaseGameBase.previousGames.addAll(games);
                    StatsLeaderboardManager.PLAYER_LEADERBOARD_INFOS.values().forEach(PlayerLeaderboardInfo::resetGameHologram);
                    Bukkit.getOnlinePlayers().forEach(DatabaseGameBase::setGameHologramVisibility);
                    DatabaseGameBase.createGameSwitcherHologram();
                    ChatUtils.MessageType.GAME_SERVICE.sendMessage("Set Game Hologram Visibility");
                })
                .execute();
    }


    public static void loadPlayer(UUID uuid, PlayersCollections collections, Consumer<DatabasePlayer> callback) {
        if (!enabled) {
            return;
        }
        long start = System.nanoTime();
        Optional<DatabasePlayer> optional = DatabaseManager.playerService.findByUUID(uuid, collections);
        DatabasePlayer databasePlayer = optional.orElseGet(() -> new DatabasePlayer(uuid, Bukkit.getOfflinePlayer(uuid).getName()));
        databasePlayer.loadInCollection(collections);
        if (collections == PlayersCollections.LIFETIME) {
            Warlords.newChain()
                    .sync(() -> {
                        loadPlayerInfo(uuid, databasePlayer);
                        callback.accept(databasePlayer);
                    }).execute();
        }
        if (optional.isEmpty()) {
            CACHED_PLAYERS.get(collections).put(uuid, databasePlayer);
        }
        long end = System.nanoTime();
        ChatUtils.MessageType.PLAYER_SERVICE.sendMessage("Loaded Player " + uuid + " in " + collections + " in " + (end - start) / 1000000 + "ms");
    }

    private static void loadPlayerInfo(UUID uuid, DatabasePlayer databasePlayer) {
        //check weapon inventory
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        List<AbstractWeapon> weaponInventory = pveStats.getWeaponInventory();
        for (Specializations value : Specializations.VALUES) {
            int count = (int) weaponInventory.stream().filter(w -> w.getSpecializations() == value).count();
            if (count == 0) {
                weaponInventory.add(new StarterWeapon(uuid, value));
                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
            }
        }
        // PATCHES
        List<DatabasePlayer.Patches> patchesApplied = databasePlayer.getPatchesApplied();
        for (DatabasePlayer.Patches patch : DatabasePlayer.Patches.VALUES) {
            if (patchesApplied.contains(patch)) {
                continue;
            }
            ChatUtils.MessageType.WARLORDS.sendMessage("Applying " + patch + " patch to " + uuid);
            boolean applied = patch.run(uuid, databasePlayer);
            if (applied) {
                ChatUtils.MessageType.WARLORDS.sendMessage("Applied " + patch + " patch to " + uuid);
                patchesApplied.add(patch);
            } else {
                ChatUtils.MessageType.WARLORDS.sendErrorMessage("Failed to apply " + patch + " patch to " + uuid);
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

        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
    }

    public static void queueUpdatePlayerAsync(DatabasePlayer databasePlayer) {
        queueUpdatePlayerAsync(databasePlayer, PlayersCollections.LIFETIME);
    }

    public static void queueUpdatePlayerAsync(DatabasePlayer databasePlayer, PlayersCollections playersCollections) {
        if (!enabled) {
            return;
        }
        DatabaseUpdater.markPlayerForUpdate(databasePlayer, playersCollections);
    }

    @Nonnull
    public static DatabasePlayer getPlayer(UUID uuid, boolean isAPlayer) {
        return getPlayer(uuid, PlayersCollections.LIFETIME, isAPlayer);
    }

    @Nonnull
    public static DatabasePlayer getPlayer(UUID uuid) {
        return getPlayer(uuid, PlayersCollections.LIFETIME, true);
    }

    @Nonnull
    public static DatabasePlayer getPlayer(UUID uuid, PlayersCollections playersCollections, boolean isAPlayer) {
        if (!isAPlayer || !enabled) {
            ConcurrentHashMap<UUID, DatabasePlayer> concurrentHashMap = DatabaseManager.CACHED_PLAYERS.get(playersCollections);
            if (isAPlayer) {
                return concurrentHashMap.computeIfAbsent(uuid, k -> new DatabasePlayer(uuid, Bukkit.getOfflinePlayer(uuid).getName()));
            } else {
                return CACHED_MOB_DATABASEPLAYER;
            }
        }
        ChatUtils.MessageType.PLAYER_SERVICE.sendMessage("Getting player " + uuid + " in " + playersCollections + " - cached = " + inCache(uuid,
                playersCollections
        ));
        return getPlayer(uuid, playersCollections);
    }

    public static boolean inCache(UUID uuid, PlayersCollections collection) {
        return CACHED_PLAYERS.get(collection).containsKey(uuid);
    }

    public static DatabasePlayer getPlayer(UUID uuid, PlayersCollections playersCollections) {
        if (!enabled) {
            ConcurrentHashMap<UUID, DatabasePlayer> concurrentHashMap = DatabaseManager.CACHED_PLAYERS.get(playersCollections);
            return concurrentHashMap.computeIfAbsent(uuid, k -> new DatabasePlayer(uuid, Bukkit.getOfflinePlayer(uuid).getName()));
        }
        Optional<DatabasePlayer> databasePlayer = DatabaseManager.playerService.findByUUID(uuid, playersCollections);
        if (databasePlayer.isPresent()) {
            return databasePlayer.get();
        } else {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage(new Throwable("Tried to get uncached player"));
            ConcurrentHashMap<UUID, DatabasePlayer> concurrentHashMap = DatabaseManager.CACHED_PLAYERS.get(playersCollections);
            return concurrentHashMap.computeIfAbsent(uuid, k -> new DatabasePlayer(uuid, Bukkit.getOfflinePlayer(uuid).getName()));
        }
    }

    @Nonnull
    public static DatabasePlayer getPlayer(Player player) {
        return getPlayer(player, PlayersCollections.LIFETIME);
    }

    @Nonnull
    public static DatabasePlayer getPlayer(Player player, PlayersCollections collections) {
        return getPlayer(player.getUniqueId(), collections, true);
    }

    public static void updateGameAsync(DatabaseGameBase databaseGame) {
        if (playerService == null || !enabled) {
            return;
        }
        Warlords.newChain().async(() -> gameService.save(databaseGame, GamesCollections.ALL)).execute();
        Warlords.newChain().async(() -> gameService.save(databaseGame, databaseGame.getGameMode().gamesCollections)).execute();
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
