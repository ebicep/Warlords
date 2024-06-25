package com.ebicep.warlords;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.jda.BotListener;
import com.ebicep.jda.BotManager;
import com.ebicep.warlords.abilities.EarthenSpike;
import com.ebicep.warlords.abilities.internal.Shield;
import com.ebicep.warlords.commands.CommandManager;
import com.ebicep.warlords.commands.debugcommands.misc.AdminCommand;
import com.ebicep.warlords.commands.debugcommands.misc.OldTestCommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.events.GeneralEvents;
import com.ebicep.warlords.events.WarlordsEvents;
import com.ebicep.warlords.game.*;
import com.ebicep.warlords.game.option.LobbyGameOption;
import com.ebicep.warlords.game.option.pvp.HorseOption;
import com.ebicep.warlords.guilds.GuildListener;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.menu.MenuEventListener;
import com.ebicep.warlords.menu.PlayerHotBarItemListener;
import com.ebicep.warlords.party.PartyListener;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksOutsideGame;
import com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairManager;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.rewards.types.PatreonReward;
import com.ebicep.warlords.util.bukkit.HeadUtils;
import com.ebicep.warlords.util.bukkit.packets.PacketUtils;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.DateUtil;
import com.ebicep.warlords.util.java.MemoryManager;
import com.ebicep.warlords.util.java.Priority;
import com.ebicep.warlords.util.warlords.ConfigUtil;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R2.CraftServer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ebicep.warlords.util.java.JavaUtils.iterable;

public class Warlords extends JavaPlugin {
    public static final HashMap<UUID, Location> SPAWN_POINTS = new HashMap<>();
    public static final AtomicInteger LOOP_TICK_COUNTER = new AtomicInteger(0);
    public static final AtomicBoolean SENT_HOUR_REMINDER = new AtomicBoolean(false);
    public static final AtomicBoolean SENT_HALF_HOUR_REMINDER = new AtomicBoolean(false);
    public static final AtomicBoolean SENT_FIFTEEN_MINUTE_REMINDER = new AtomicBoolean(false);
    private static final ConcurrentHashMap<UUID, WarlordsEntity> PLAYERS = new ConcurrentHashMap<>();
    public static String VERSION = "";
    public static NamedTextColor VERSION_COLOR = NamedTextColor.RED;
    public static String serverIP;
    public static boolean holographicDisplaysEnabled;
    public static boolean citizensEnabled;
    private static Warlords instance;
    private static TaskChainFactory taskChainFactory;

    static {
        Configurator.setLevel("org.mongodb.driver", Level.ERROR);
        Configurator.setLevel("org.springframework", Level.ERROR);
        Configurator.setLevel("net.dv8tion.jda", Level.ERROR);
    }

    public static <T> TaskChain<T> newSharedChain(String name) {
        return taskChainFactory.newSharedChain(name);
    }

    public static ConcurrentHashMap<UUID, WarlordsEntity> getPlayers() {
        return PLAYERS;
    }

    public static void addPlayer(@Nonnull WarlordsEntity warlordsEntity) {
        PLAYERS.put(warlordsEntity.getUuid(), warlordsEntity);
        for (GameAddon addon : warlordsEntity.getGame().getAddons()) {
            addon.warlordsEntityCreated(warlordsEntity.getGame(), warlordsEntity);
        }

        new ArrayList<>(warlordsEntity.getGame().getOptions())
                .stream()
                .sorted((o1, o2) -> {
                    try {
                        Priority o1Priority = o1.getClass()
                                                .getMethod("onWarlordsEntityCreated", WarlordsEntity.class)
                                                .getAnnotation(Priority.class);
                        Priority o2Priority = o2.getClass()
                                                .getMethod("onWarlordsEntityCreated", WarlordsEntity.class)
                                                .getAnnotation(Priority.class);
                        return Integer.compare(
                                o1Priority == null ? 3 : o1Priority.value(),
                                o2Priority == null ? 3 : o2Priority.value()
                        );
                    } catch (Exception e) {
                        ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
                        return 0;
                    }
                })
                .forEachOrdered(option -> {
                    option.onWarlordsEntityCreated(warlordsEntity);
                });

        //ChatUtils.MessageTypes.GAME_DEBUG.sendMessage("Added player " + warlordsEntity.getName() + " - " + warlordsEntity.getSpecClass
        // ().name);
    }

//    @Nullable
//    public static WarlordsEntity getPlayer(@Nullable Entity entity) {
//        if (entity != null) {
//            return getPlayer(entity.getBukkitEntity());
//        }
//        return null;
//    }

    @Nullable
    public static WarlordsEntity getPlayer(@Nullable Entity entity) {
        if (entity != null) {
            Optional<MetadataValue> metadata = entity.getMetadata(WarlordsEntity.WARLORDS_ENTITY_METADATA)
                                                     .stream()
                                                     .filter(e -> e.value() instanceof WarlordsEntity)
                                                     .findAny();
            if (metadata.isPresent()) {
                return (WarlordsEntity) metadata.get().value();
            }
        }
        return null;
    }

    @Nullable
    public static WarlordsEntity getPlayer(@Nullable Player player) {
        return getPlayer((OfflinePlayer) player);
    }

    @Nullable
    public static WarlordsEntity getPlayer(@Nullable OfflinePlayer player) {
        return player == null ? null : getPlayer(player.getUniqueId());
    }

    @Nullable
    public static WarlordsEntity getPlayer(@Nonnull UUID uuid) {
        return PLAYERS.get(uuid);
    }

    public static void removePlayer(@Nonnull UUID player) {
        WarlordsEntity wp = PLAYERS.remove(player);
        if (wp != null) {
            wp.onRemove();
        }
        Location loc = SPAWN_POINTS.remove(player);
        Player p = Bukkit.getPlayer(player);
        if (p != null) {
            p.removeMetadata(WarlordsEntity.WARLORDS_ENTITY_METADATA, Warlords.getInstance());
            if (loc != null) {
                p.teleport(getRejoinPoint(player));
            }
        }
        EarthenSpike.PLAYER_SPIKE_COOLDOWN.remove(player);
    }

    public static Warlords getInstance() {
        return instance;
    }

    @Nonnull
    public static Location getRejoinPoint(@Nonnull UUID key) {
        return SPAWN_POINTS.getOrDefault(key, StatsLeaderboardManager.MAIN_LOBBY_SPAWN);
    }

    /**
     * Used for removing players from main lobby game
     *
     * @param player
     */
    public static void removePlayer2(@Nonnull UUID player) {
        WarlordsEntity wp = PLAYERS.remove(player);
        if (wp != null) {
            wp.onRemove();
        }
        Player p = Bukkit.getPlayer(player);
        if (p != null) {
            p.removeMetadata(WarlordsEntity.WARLORDS_ENTITY_METADATA, Warlords.getInstance());
        }
        EarthenSpike.PLAYER_SPIKE_COOLDOWN.remove(player);
    }

    public static void setRejoinPoint(@Nonnull UUID key, @Nonnull Location value) {
        SPAWN_POINTS.put(key, value);
        Player player = Bukkit.getPlayer(key);
        if (player != null) {
            player.teleport(value);
        }
    }

    public static GameManager getGameManager() {
        return getInstance().gameManager;
    }

    public static boolean hasPlayer(@Nonnull OfflinePlayer player) {
        return hasPlayer(player.getUniqueId());
    }

    public static boolean hasPlayer(@Nonnull UUID player) {
        return PLAYERS.containsKey(player);
    }

    private GameManager gameManager;

    @Override
    public void onDisable() {
        try {
            if (BotManager.task != null) {
                BotManager.task.cancel();
            }
        } catch (Exception e) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
        }
        if (DatabaseManager.enabled) {
            //updates all queues, locks main thread to ensure update is complete before disabling
            try {
                DatabaseManager.updateQueue();
            } catch (Exception e) {
                ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
            }
            try {
                if (MasterworksFairManager.currentFair != null) {
                    DatabaseManager.masterworksFairService.update(MasterworksFairManager.currentFair);
                }
            } catch (Exception e) {
                ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
            }
            try {
                GuildManager.updateGuilds();
            } catch (Exception e) {
                ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
            }
        }
        try {
            taskChainFactory.shutdown(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
        }
        try {
            // Pre-caution
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.removePotionEffect(PotionEffectType.BLINDNESS);
                player.getActivePotionEffects().clear();
                player.removeMetadata(WarlordsEntity.WARLORDS_ENTITY_METADATA, this);
                player.clearTitle();
            }
        } catch (Exception e) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
        }
        try {
            CraftServer server = (CraftServer) Bukkit.getServer();
            server.getEntityMetadata().invalidateAll(this);
            server.getWorldMetadata().invalidateAll(this);
            server.getPlayerMetadata().invalidateAll(this);
        } catch (Exception e) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
        }
        try {
            gameManager.close();
        } catch (Exception e) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
        }
        try {
            if (holographicDisplaysEnabled) {
                ChatUtils.MessageType.WARLORDS.sendMessage("Deleting holograms...");
                HolographicDisplaysAPI.get(instance).getHolograms().forEach(Hologram::delete);
            }
        } catch (Exception e) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
        }
        try {
            NPCManager.destroyNPCs();
        } catch (Exception e) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
        }
        try {
            BotManager.deleteStatusMessage();
            if (BotManager.jda != null) {
                BotManager.jda.shutdownNow();
            }
        } catch (Exception e) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
        }

        ChatUtils.MessageType.WARLORDS.sendMessage("Plugin is disabled");
    }

    @Override
    public void onEnable() {
        instance = this;
        VERSION = this.getDescription().getVersion();
        serverIP = this.getServer().getIp();
        taskChainFactory = BukkitTaskChainFactory.create(this);

        gameManager = new GameManager();
        GameMap.addGameHolders(gameManager);

        LobbyGameOption.start();

        Thread.currentThread().setContextClassLoader(getClassLoader());

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Player) {
                    continue;
                }
                entity.remove();
            }
        }

        getServer().getPluginManager().registerEvents(new WarlordsEvents(), this);
        getServer().getPluginManager().registerEvents(new GeneralEvents(), this);
        getServer().getPluginManager().registerEvents(new MenuEventListener(this), this);
        getServer().getPluginManager().registerEvents(new PartyListener(), this);
        getServer().getPluginManager().registerEvents(new BotListener(), this);
        getServer().getPluginManager().registerEvents(new WarlordsPlayer(), this);
        getServer().getPluginManager().registerEvents(new PlayerHotBarItemListener(), this);
        getServer().getPluginManager().registerEvents(new GuildListener(), this);
        getServer().getPluginManager().registerEvents(new PatreonReward(), this);
        getServer().getPluginManager().registerEvents(new MemoryManager(), this);
        getServer().getPluginManager().registerEvents(new Shield(), this);
        getServer().getPluginManager().registerEvents(new HorseOption(), this);
        getServer().getPluginManager().registerEvents(TracksOutsideGame.getListener(), this);
        getServer().getPluginManager().registerEvents(new DatabaseGameEvent(), this);

        getCommand("oldtest").setExecutor(new OldTestCommand());

//        ConcurrentHashMap<UUID, Integer> playerClicks = new ConcurrentHashMap<>();
//        getServer().getPluginManager().registerEvents(new Listener() {
//            @EventHandler
//            public void onEvent(PlayerInteractEvent event) {
//                Action action = event.getAction();
//                if(action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
//                    playerClicks.merge(event.getPlayer().getUniqueId(), 1, Integer::sum);
////                    System.out.println("Left click: " + playerClicks.get(event.getPlayer().getUniqueId()));
//                }
//            }
//        }, this);
//        new BukkitRunnable() {
//
//            @Override
//            public void run() {
//                playerClicks.forEach((uuid, integer) -> {
//                    Player player = Bukkit.getPlayer(uuid);
//                    if(player != null) {
//                        Bukkit.broadcastMessage(player.getName() + " - " + (integer));
//                    }
//                });
//                playerClicks.clear();
//            }
//        }.runTaskTimer(this, 0, 21);

        CommandManager.init(this);

        HeadUtils.updateHeads();

        ConfigUtil.loadConfigs(this);

        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));

        holographicDisplaysEnabled = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
        citizensEnabled = Bukkit.getPluginManager().isPluginEnabled("Citizens");
        ChatUtils.MessageType.WARLORDS.sendMessage("citizensEnabled: " + citizensEnabled);
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            ChatUtils.MessageType.WARLORDS.sendMessage("Hooked into LuckPerms");
            LuckPerms api = provider.getProvider();
            EventBus eventBus = api.getEventBus();
            eventBus.subscribe(this, UserDataRecalculateEvent.class, Permissions::listenToNewPatreons);
        }

        Bukkit.getOnlinePlayers().forEach(player -> {
            UUID uuid = player.getUniqueId();
            player.teleport(getRejoinPoint(uuid));
            player.getInventory().clear();
            player.setAllowFlight(true);
            player.setMaxHealth(20);
            player.setHealth(20);
            PlayerHotBarItemListener.giveLobbyHotBar(player, false);
        });

        //connects to the database
        Warlords.newChain()
                .async(DatabaseManager::init)
                .execute();

        if (!BotManager.DISCORD_SERVERS.isEmpty()) {
            try {
                BotManager.connect();
            } catch (LoginException e) {
                ChatUtils.MessageType.DISCORD_BOT.sendErrorMessage(e);
            }
        }

        PacketUtils.init(this);

        startWarlordsEntitiesLoop();
        startRestartReminderLoop();

        //Sending data to mod
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "warlords:warlords");

        MemoryManager.init();

        //cancel swimming, modified from - https://github.com/PaperMC/Paper/issues/1328
        //added player.isInWater() check or else there is no dynamic fov while on land
        new BukkitRunnable() {
            @Override
            public void run() {
                if (AdminCommand.NEW_SWIMMING) {
                    return;
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.isSwimming() && player.isInWater()) {
                        player.setSprinting(true);
                        player.setSprinting(false);
                    }
                }
            }
        }.runTaskTimer(this, 1, 1);

//        TowerRegistry.updateCaches();

        ChatUtils.MessageType.WARLORDS.sendMessage("Plugin is enabled");
    }

    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }

    private void startWarlordsEntitiesLoop() {
        new BukkitRunnable() {

            @Override
            public void run() {
                // Every 1 tick - 0.05 seconds.
                for (WarlordsEntity we : PLAYERS.values()) {
                    // Checks whether the game is paused.
                    if (we.getGame().isFrozen()) {
                        continue;
                    }
                    we.runEveryTick();
                }
                if (LOOP_TICK_COUNTER.get() % 5 == 0) {
                    for (WarlordsEntity we : PLAYERS.values()) {
                        Player player = we.getEntity() instanceof Player ? (Player) we.getEntity() : null;
                        if (player != null) {
                            //ACTION BAR
                            if (player.getInventory().getItemInMainHand().getType() == Material.COMPASS) {
                                we.displayCompassActionBar(player);
                            } else {
                                we.displayActionBar();
                            }
                        }
                    }
                }
                // Every 20 ticks - 1 second.
                if (LOOP_TICK_COUNTER.get() % 20 == 0) {
                    // Removes leftover horses if there are any.
//                    RemoveEntities.removeHorsesInGame();

                    for (WarlordsEntity we : PLAYERS.values()) {
                        // Checks whether the game is paused.
                        if (we.getGame().isFrozen()) {
                            continue;
                        }
                        we.runEverySecond();
                    }

                    // for removing falling blocks that didnt get removed prior
                    GeneralEvents.FALLING_BLOCK_ENTITIES.removeIf(e -> !e.isValid());
                }
                // Loops every 50 ticks - 2.5 seconds.
                if (LOOP_TICK_COUNTER.get() % 50 == 0) {
                    for (WarlordsEntity we : PLAYERS.values()) {
                        if (we.getGame().isFrozen()) {
                            continue;
                        }
                        if (we instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob().getMobRegistry() == Mob.TEST_DUMMY) {
                            continue;
                        }
                        Entity player = we.getEntity();
                        List<Location> locations = we.getLocations();
                        if (we.isDead() && !locations.isEmpty()) {
                            locations.add(locations.get(locations.size() - 1));
                        } else {
                            locations.add(player.getLocation());
                        }
                    }
                }
                LOOP_TICK_COUNTER.getAndIncrement();
            }
        }.runTaskTimer(this, 0, 0);
    }

    private void startRestartReminderLoop() {
        new BukkitRunnable() {

            final Instant nextReset = Instant.now().isAfter(DateUtil.getNextResetDate()) ?
                                      DateUtil.getNextResetDate().plus(24, ChronoUnit.HOURS) :
                                      DateUtil.getNextResetDate();

            @Override
            public void run() {
                Instant now = Instant.now();
                if (!SENT_HOUR_REMINDER.get()) {
                    if (now.plus(1, ChronoUnit.HOURS).isAfter(nextReset)) {
                        Bukkit.broadcast(Component.text("The server will restart in 1 hour.", NamedTextColor.RED));
                        SENT_HOUR_REMINDER.set(true);
                    }
                } else if (!SENT_HALF_HOUR_REMINDER.get()) {
                    if (now.plus(30, ChronoUnit.MINUTES).isAfter(nextReset)) {
                        Bukkit.broadcast(Component.text("The server will restart in 30 minutes.", NamedTextColor.RED));
                        SENT_HALF_HOUR_REMINDER.set(true);
                    }
                } else if (!SENT_FIFTEEN_MINUTE_REMINDER.get()) {
                    if (now.plus(15, ChronoUnit.MINUTES).isAfter(nextReset)) {
                        Bukkit.broadcast(Component.text("The server will restart in 15 minutes.", NamedTextColor.RED));
                        SENT_FIFTEEN_MINUTE_REMINDER.set(true);
                        cancel(); // Can cancel since there are no more checks
                    }
                }

            }
        }.runTaskTimer(this, 20, 1000);
    }

    public void hideAndUnhidePeople(@Nonnull Player player) {
        Map<UUID, Game> players = getPlayersToGame();
        Game game = players.get(player.getUniqueId());
        for (Player p : Bukkit.getOnlinePlayers()) {
            Game game1 = players.get(p.getUniqueId());
            if (p != player) {
                if (game1 == game) {
                    p.showPlayer(Warlords.getInstance(), player);
                    player.showPlayer(Warlords.getInstance(), p);
                } else {
                    p.hidePlayer(Warlords.getInstance(), player);
                    player.hidePlayer(Warlords.getInstance(), p);
                }
            }
        }
    }

    private Map<UUID, Game> getPlayersToGame() {
        Map<UUID, Game> players = new HashMap<>();
        for (GameManager.GameHolder holder : gameManager.getGames()) {
            Game game = holder.getGame();
            if (game != null) {
                //Stream<Map.Entry<UUID, Team>> players()
                for (Map.Entry<UUID, Team> e : iterable(game.players())) {
                    players.put(e.getKey(), game);
                }
            }
        }
        return players;
    }

    public void hideAndUnhidePeople() {
        Map<UUID, Game> players = getPlayersToGame();
        List<Player> peeps = new ArrayList<>(Bukkit.getOnlinePlayers());
        int length = peeps.size();
        for (int i = 0; i < length - 1; i++) {
            Player player = peeps.get(i);
            Game game = players.get(player.getUniqueId());
            for (int j = i + 1; j < length; j++) {
                Player p = peeps.get(j);
                Game game1 = players.get(p.getUniqueId());
                if (game1 == game) {
                    p.showPlayer(Warlords.getInstance(), player);
                    player.showPlayer(Warlords.getInstance(), p);
                } else {
                    p.hidePlayer(Warlords.getInstance(), player);
                    player.hidePlayer(Warlords.getInstance(), p);
                }
            }
        }
    }

}