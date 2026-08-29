package com.ebicep.warlords.database;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.configuration.ApplicationConfiguration;
import com.ebicep.warlords.database.leaderboards.PlayerLeaderboardInfo;
import com.ebicep.warlords.database.leaderboards.guilds.GuildLeaderboardManager;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.database.repositories.events.GameEventsService;
import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.games.GameService;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.guild.GuildService;
import com.ebicep.warlords.database.repositories.illusionvendor.IllusionVendorService;
import com.ebicep.warlords.database.repositories.items.WeeklyBlessingsService;
import com.ebicep.warlords.database.repositories.items.pojos.WeeklyBlessings;
import com.ebicep.warlords.database.repositories.masterworksfair.MasterworksFairService;
import com.ebicep.warlords.database.repositories.player.PlayerService;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.cache.StatPushUp;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayerPatches;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.database.repositories.timings.TimingsService;
import com.ebicep.warlords.database.repositories.timings.pojos.DatabaseTiming;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.rewards.types.CompensationReward;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.StarterWeapon;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
        if (ApplicationConfiguration.key == null) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage("Database key is null, disabling database");
            enabled = false;
        }
        if (!enabled) {
            ConfigManager.loadConfigsFromFolder();
            return;
        }

        if (!pingDatabase()) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage("Database unreachable at startup, falling back to file config");
            enabled = false;
            ConfigManager.loadConfigsFromFolder();
            return;
        }

        Bukkit.getOnlinePlayers().forEach(player -> player.kick(Component.text("Server is restarting, please rejoin in a few minutes!")));

        AbstractApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);

        try {
            ConfigManager.loadConfigs(warlordsDatabase);
            playerService = context.getBean("playerService", PlayerService.class);
            gameService = context.getBean("gameService", GameService.class);
            timingsService = context.getBean("timingsService", TimingsService.class);
            masterworksFairService = context.getBean("masterworksFairService", MasterworksFairService.class);
            guildService = context.getBean("guildService", GuildService.class);
            gameEventsService = context.getBean("gameEventsService", GameEventsService.class);
            weeklyBlessingsService = context.getBean("itemsWeeklyBlessingsService", WeeklyBlessingsService.class);
            illusionVendorService = context.getBean("illusionVendorService", IllusionVendorService.class);
        } catch (Exception e) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
            enabled = false;
            ConfigManager.loadConfigsFromFolder();
            return;
        }

        DatabaseHealth.markHealthy();
        startHealthProbe();

        WeeklyBlessings.loadAllWeeklyBlessings();

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
                if (DatabaseHealth.isOperational()) {
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

    private static boolean pingDatabase() {
        try (MongoClient probe = MongoClients.create(ApplicationConfiguration.buildClientSettings())) {
            probe.getDatabase("Warlords").runCommand(new Document("ping", 1));
            return true;
        } catch (Exception e) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage("Database ping failed: " + e.getMessage());
            return false;
        }
    }

    private static void startHealthProbe() {
        new BukkitRunnable() {

            @Override
            public void run() {
                Warlords.newChain().async(DatabaseHealth::probe).execute();
            }
        }.runTaskTimer(Warlords.getInstance(), 20L * 30, 20L * 30);
    }

    public static void loadPlayer(UUID uuid, PlayersCollections collections, Consumer<DatabasePlayer> callback) {
        if (!DatabaseHealth.isOperational()) {
            return;
        }
        long start = System.nanoTime();
        Optional<DatabasePlayer> optional = DatabaseManager.playerService.findByUUID(uuid, collections, true);
        DatabasePlayer databasePlayer;
        if (optional.isPresent()) {
            databasePlayer = optional.get();
        } else {
            DatabasePlayer stub = stubPlayer(uuid);
            databasePlayer = cachePlayer(collections, stub);
        }
        databasePlayer.loadInCollection(collections);
        if (collections == PlayersCollections.LIFETIME) {
            Warlords.newChain()
                    .sync(() -> {
                        loadPlayerInfo(uuid, databasePlayer);
                        callback.accept(databasePlayer);
                    }).execute();
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

        applyPrestigeOrbLoginPatch(uuid, databasePlayer);

        // PATCHES
        List<DatabasePlayerPatches> patchesApplied = databasePlayer.getPatchesApplied();
        for (DatabasePlayerPatches patch : DatabasePlayerPatches.VALUES) {
            if (patchesApplied.contains(patch)) {
                continue;
            }
            ChatUtils.MessageType.WARLORDS.sendMessage("Applying " + patch + " patch to " + uuid);
            try {
                boolean applied = patch.run(uuid, databasePlayer);
                if (applied) {
                    ChatUtils.MessageType.WARLORDS.sendMessage("Applied " + patch + " patch to " + uuid);
                    patchesApplied.add(patch);
                } else {
                    ChatUtils.MessageType.WARLORDS.sendErrorMessage("Failed to apply " + patch + " patch to " + uuid);
                }
            } catch (Exception e) {
                ChatUtils.MessageType.WARLORDS.sendErrorMessage("Failed to apply " + patch + " patch to " + uuid);
                ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
            }
        }

        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
    }

    private static void applyPrestigeOrbLoginPatch(UUID uuid, DatabasePlayer databasePlayer) {
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        boolean alreadyApplied = pveStats.getCompensationRewards()
                .stream()
                .anyMatch(CompensationReward.PrestigeOrbLoginPatch.class::isInstance);
        if (alreadyApplied) {
            return;
        }

        int totalPrestige = 0;
        for (Specializations specialization : Specializations.VALUES) {
            totalPrestige += databasePlayer.getSpec(specialization).getPrestige();
        }
        long prestigeOrbs = totalPrestige * 2L;
        if (prestigeOrbs > 0) {
            pveStats.addCurrency(Currencies.PRESTIGE_ORB, prestigeOrbs);
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(Component.text("You received ", NamedTextColor.GREEN)
                        .append(Currencies.PRESTIGE_ORB.getCostColoredName(prestigeOrbs))
                        .append(Component.text(" for your " + totalPrestige + " total Prestige ranks.", NamedTextColor.GREEN)));
            }
        }

        CompensationReward.PrestigeOrbLoginPatch marker = new CompensationReward.PrestigeOrbLoginPatch();
        marker.setTimeClaimed();
        pveStats.getCompensationRewards().add(marker);
        ChatUtils.MessageType.WARLORDS.sendMessage(
                "Applied Prestige Orb login patch to " + uuid + ": " + totalPrestige + " prestige, " + prestigeOrbs + " orbs"
        );
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
    public static DatabasePlayer getPlayer(UUID uuid, PlayersCollections playersCollections, boolean isAPlayer) {
        if (!isAPlayer || !enabled) {
            if (isAPlayer) {
                return cachePlayer(playersCollections, stubPlayer(uuid));
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

    /**
     * Inserts or upgrades a cache entry. Never replaces a persisted player (id != null)
     * with a stub (id == null). Prefer an existing real entry over a newly loaded one.
     *
     * @return the instance that remains in the cache (may differ from {@code candidate})
     */
    public static DatabasePlayer cachePlayer(PlayersCollections collection, DatabasePlayer candidate) {
        ConcurrentHashMap<UUID, DatabasePlayer> cache = CACHED_PLAYERS.get(collection);
        UUID uuid = candidate.getUuid();
        return cache.compute(uuid, (k, existing) -> {
            DatabasePlayer selected;
            if (existing == null) {
                selected = candidate;
            } else if (existing.getId() == null && candidate.getId() != null) {
                selected = candidate;
            } else {
                selected = existing;
            }
            StatPushUp.warmAll(selected);
            return selected;
        });
    }

    private static DatabasePlayer stubPlayer(UUID uuid) {
        return new DatabasePlayer(uuid, Bukkit.getOfflinePlayer(uuid).getName());
    }

    public static DatabasePlayer getPlayer(UUID uuid, PlayersCollections playersCollections) {
        if (!enabled) {
            return cachePlayer(playersCollections, stubPlayer(uuid));
        }
        boolean onMainThread = Bukkit.isPrimaryThread();
        Optional<DatabasePlayer> found = playerService.findByUUID(uuid, playersCollections, !onMainThread);
        if (found.isPresent()) {
            DatabasePlayer player = found.get();
            if (onMainThread && player.getId() == null && DatabaseHealth.isOperational()) {
                Warlords.newChain()
                        .async(() -> playerService.findByUUID(uuid, playersCollections, true))
                        .execute();
            }
            return player;
        }
        ChatUtils.MessageType.WARLORDS.sendErrorMessage(new Throwable("Tried to get uncached player"));
        DatabasePlayer stub = cachePlayer(playersCollections, stubPlayer(uuid));
        if (DatabaseHealth.isOperational()) {
            Warlords.newChain()
                    .async(() -> playerService.findByUUID(uuid, playersCollections, true))
                    .execute();
        }
        return stub;
    }

    @Nonnull
    public static DatabasePlayer getPlayer(UUID uuid) {
        return getPlayer(uuid, PlayersCollections.LIFETIME, true);
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
        DatabaseUpdater.updateGameAsync(databaseGame);
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
