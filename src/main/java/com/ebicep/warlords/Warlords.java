package com.ebicep.warlords;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.jda.BotListener;
import com.ebicep.jda.BotManager;
import com.ebicep.warlords.abilties.OrbsOfLife;
import com.ebicep.warlords.abilties.Soulbinding;
import com.ebicep.warlords.abilties.UndyingArmy;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.DamageCheck;
import com.ebicep.warlords.abilties.internal.HealingPowerup;
import com.ebicep.warlords.abilties.internal.Overheal;
import com.ebicep.warlords.commands.CommandManager;
import com.ebicep.warlords.commands.debugcommands.misc.OldTestCommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.configuration.ApplicationConfiguration;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.events.WarlordsEvents;
import com.ebicep.warlords.events.player.ingame.WarlordsUndyingArmyPopEvent;
import com.ebicep.warlords.game.*;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.game.option.pvp.FlagSpawnPointOption;
import com.ebicep.warlords.guilds.GuildListener;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.menu.MenuEventListener;
import com.ebicep.warlords.menu.PlayerHotBarItemListener;
import com.ebicep.warlords.party.PartyListener;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PersistentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairManager;
import com.ebicep.warlords.pve.rewards.types.PatreonReward;
import com.ebicep.warlords.util.bukkit.HeadUtils;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.bukkit.RemoveEntities;
import com.ebicep.warlords.util.bukkit.signgui.SignGUI;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.DateUtil;
import com.ebicep.warlords.util.java.MemoryManager;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.bukkit.GameMode;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_19_R2.CraftServer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;
import java.io.File;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ebicep.warlords.util.warlords.Utils.iterable;

public class Warlords extends JavaPlugin {
    public static final HashMap<UUID, Location> SPAWN_POINTS = new HashMap<>();
    public static final AtomicInteger LOOP_TICK_COUNTER = new AtomicInteger(0);
    public static final AtomicBoolean SENT_HOUR_REMINDER = new AtomicBoolean(false);
    public static final AtomicBoolean SENT_HALF_HOUR_REMINDER = new AtomicBoolean(false);
    public static final AtomicBoolean SENT_FIFTEEN_MINUTE_REMINDER = new AtomicBoolean(false);
    private static final ConcurrentHashMap<UUID, WarlordsEntity> PLAYERS = new ConcurrentHashMap<>();
    public static String VERSION = "";
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
        for (Option option : warlordsEntity.getGame().getOptions()) {
            option.onWarlordsEntityCreated(warlordsEntity);
        }
        //ChatUtils.MessageTypes.GAME_DEBUG.sendMessage("Added player " + warlordsEntity.getName() + " - " + warlordsEntity.getSpecClass
        // ().name);
    }

    @Nullable
    public static WarlordsEntity getPlayer(@Nullable Entity entity) {
        if (entity != null) {
            Optional<MetadataValue> metadata = entity.getMetadata("WARLORDS_PLAYER")
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
    public static WarlordsEntity getPlayer(@Nullable net.minecraft.server.v1_8_R3.Entity entity) {
        if (entity != null) {
            return getPlayer(entity.getBukkitEntity());
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
            p.removeMetadata("WARLORDS_PLAYER", Warlords.getInstance());
            if (loc != null) {
                p.teleport(getRejoinPoint(player));
            }
        }
    }

    public static Warlords getInstance() {
        return instance;
    }

    @Nonnull
    public static Location getRejoinPoint(@Nonnull UUID key) {
        return SPAWN_POINTS.getOrDefault(key, new LocationBuilder(Bukkit.getWorlds().get(0).getSpawnLocation()).yaw(-90));
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

    private GameManager gameManager;

    @Override
    public void onDisable() {
        try {
            if (BotManager.task != null) {
                BotManager.task.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DatabaseManager.enabled) {
            //updates all queues, locks main thread to ensure update is complete before disabling
            try {
                DatabaseManager.updateQueue();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (MasterworksFairManager.currentFair != null) {
                    DatabaseManager.masterworksFairService.update(MasterworksFairManager.currentFair);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                GuildManager.updateGuilds();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            taskChainFactory.shutdown(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            // Pre-caution
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.removePotionEffect(PotionEffectType.BLINDNESS);
                player.getActivePotionEffects().clear();
                player.removeMetadata("WARLORDS_PLAYER", this);
                PacketUtils.sendTitle(player, "", "", 0, 0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            CraftServer server = (CraftServer) Bukkit.getServer();
            server.getEntityMetadata().invalidateAll(this);
            server.getWorldMetadata().invalidateAll(this);
            server.getPlayerMetadata().invalidateAll(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            gameManager.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (holographicDisplaysEnabled) {
                ChatUtils.MessageTypes.WARLORDS.sendMessage("Deleting holograms...");
                HolographicDisplaysAPI.get(instance).getHolograms().forEach(Hologram::delete);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            NPCManager.destroyNPCs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            BotManager.deleteStatusMessage();
            if (BotManager.jda != null) {
                BotManager.jda.shutdownNow();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            SignGUI.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ChatUtils.MessageTypes.WARLORDS.sendMessage("Plugin is disabled");
    }

    @Override
    public void onEnable() {
        instance = this;
        VERSION = this.getDescription().getVersion();
        serverIP = this.getServer().getIp();
        taskChainFactory = BukkitTaskChainFactory.create(this);

        gameManager = new GameManager();
        GameMap.addGameHolders(gameManager);

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
        getServer().getPluginManager().registerEvents(new MenuEventListener(this), this);
        getServer().getPluginManager().registerEvents(new PartyListener(), this);
        getServer().getPluginManager().registerEvents(new BotListener(), this);
        getServer().getPluginManager().registerEvents(new WarlordsPlayer(), this);
        getServer().getPluginManager().registerEvents(new PlayerHotBarItemListener(), this);
        getServer().getPluginManager().registerEvents(new GuildListener(), this);
        getServer().getPluginManager().registerEvents(new PatreonReward(), this);
        getServer().getPluginManager().registerEvents(new MemoryManager(), this);

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

        readKeysConfig();
        readWeaponConfig();
        saveWeaponConfig();
        readBotConfig();

        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));

        holographicDisplaysEnabled = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
        citizensEnabled = Bukkit.getPluginManager().isPluginEnabled("Citizens");
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            ChatUtils.MessageTypes.WARLORDS.sendMessage("Hooked into LuckPerms");
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
                e.printStackTrace();
            }
        }

        ProtocolManager protocolManager;
        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.removePacketListeners(this);
        protocolManager.addPacketListener(
                new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Play.Server.WORLD_PARTICLES) {
                    int counter = 0;

                    @Override
                    public void onPacketSending(PacketEvent event) {
                        // Item packets (id: 0x29)
                        if (event.getPacketType() == PacketType.Play.Server.WORLD_PARTICLES) {
                            Player player = event.getPlayer();
                            if (Warlords.hasPlayer(player)) {
                                if (counter++ % PlayerSettings.PLAYER_SETTINGS.get(player.getUniqueId()).getParticleQuality().particleReduction == 0) {
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }
                });
        protocolManager.addPacketListener(
                new PacketAdapter(this, ListenerPriority.LOWEST, PacketType.Play.Client.STEER_VEHICLE) {
                    @Override
                    public void onPacketReceiving(PacketEvent e) {
                        if (e.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
                            if (e.getPacket().getHandle() instanceof ServerboundPlayerInputPacket) {
                                boolean dismount = ((ServerboundPlayerInputPacket) e.getPacket().getHandle()).isShiftKeyDown();
                                Field f;
                                try {
                                    f = ServerboundPlayerInputPacket.class.getDeclaredField("isShiftKeyDown");
                                    f.setAccessible(true);
                                    f.set(e.getPacket().getHandle(), false);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                                if (dismount && e.getPlayer().getVehicle() != null) {
                                    e.getPlayer().getVehicle().remove();
                                }
                            }
                        }
                    }
                }
        );

        SignGUI.init(this);

        Warlords.newChain()
                .sync(NPCManager::createSupplyDropFairNPC)
                .execute();

        startWarlordsEntitiesLoop();
        startRestartReminderLoop();

        //Sending data to mod
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "warlords:warlords");

        MemoryManager.init();

        ChatUtils.MessageTypes.WARLORDS.sendMessage("Plugin is enabled");
    }

    public void readKeysConfig() {
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "keys.yml"));
            ApplicationConfiguration.key = config.getString("database_key");
            BotManager.botToken = config.getString("botToken");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readWeaponConfig() {
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "weapons.yml"));
            for (String key : config.getKeys(false)) {
                Weapons.getWeapon(key).isUnlocked = config.getBoolean(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveWeaponConfig() {
        try {
            YamlConfiguration config = new YamlConfiguration();
            for (Weapons weapons : Weapons.VALUES) {
                config.set(weapons.getName(), weapons.isUnlocked);
            }
            config.save(new File(this.getDataFolder(), "weapons.yml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readBotConfig() {
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "bot.yml"));
            for (String key : config.getKeys(false)) {
                BotManager.DiscordServer discordServer = new BotManager.DiscordServer(
                        key,
                        config.getString(key + ".id"),
                        config.getString(key + ".statusChannel"),
                        config.getString(key + ".queueChannel")
                );
                BotManager.DISCORD_SERVERS.add(discordServer);
                ChatUtils.MessageTypes.DISCORD_BOT.sendMessage("Added server " + key + " = " + discordServer.getId() + ", " + discordServer.getStatusChannel() + ", " + discordServer.getQueueChannel());
            }
            /*
            server1
                id
                statusChannel
                waitingChannel
            server2
                id
                statusChannel
                waitingChannel
             */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }

    public static boolean hasPlayer(@Nonnull OfflinePlayer player) {
        return hasPlayer(player.getUniqueId());
    }

    private void startWarlordsEntitiesLoop() {
        new BukkitRunnable() {

            @Override
            public void run() {
                // Every 1 tick - 0.05 seconds.
                for (WarlordsEntity wp : PLAYERS.values()) {
                    Player player = wp.getEntity() instanceof Player ? (Player) wp.getEntity() : null;
                    if (player != null) {
                        //ACTION BAR
                        if (!player.getInventory().getItemInMainHand().equals(FlagSpawnPointOption.COMPASS)) {
                            wp.displayActionBar();
                        } else {
                            wp.displayFlagActionBar(player);
                        }
                    }

                    // Checks whether the game is paused.
                    if (wp.getGame().isFrozen()) {
                        continue;
                    }

                    wp.updateHealth();
                    // Updating all player speed.
                    wp.getSpeed().updateSpeed();
                    wp.runEveryTick();

                    // Setting the flag tracking compass.
                    if (player != null && wp.getCompassTarget() != null) {
                        player.setCompassTarget(wp.getCompassTarget().getLocation());
                    }

                    // Ability Cooldowns

                    // Decrementing red skill's cooldown.
                    if (wp.getRedAbility().getCurrentCooldown() > 0) {
                        wp.getRedAbility().subtractCooldown(.05f);
                        if (player != null) {
                            wp.updateRedItem(player);
                        }
                    }

                    // Decrementing purple skill's cooldown.
                    if (wp.getPurpleAbility().getCurrentCooldown() > 0) {
                        wp.getPurpleAbility().subtractCooldown(.05f);
                        if (player != null) {
                            wp.updatePurpleItem(player);
                        }
                    }

                    // Decrementing blue skill's cooldown.
                    if (wp.getBlueAbility().getCurrentCooldown() > 0) {
                        wp.getBlueAbility().subtractCooldown(.05f);
                        if (player != null) {
                            wp.updateBlueItem(player);
                        }
                    }

                    // Decrementing orange skill's cooldown.
                    if (wp.getOrangeAbility().getCurrentCooldown() > 0) {
                        wp.getOrangeAbility().subtractCooldown(.05f);
                        if (player != null) {
                            wp.updateOrangeItem(player);
                        }
                    }

                    wp.getCooldownManager().reduceCooldowns();

                    for (AbstractAbility ability : wp.getSpec().getAbilities()) {
                        ability.checkSecondaryAbilities();
                    }

                    wp.setWasSneaking(wp.isSneaking());

                    // Checks whether the player has overheal active and is full health or not.
                    boolean hasOverhealCooldown = wp.getCooldownManager().hasCooldown(Overheal.OVERHEAL_MARKER);
                    boolean hasTooMuchHealth = wp.getHealth() > wp.getMaxHealth();

                    if (hasOverhealCooldown && !hasTooMuchHealth) {
                        wp.getCooldownManager().removeCooldownByObject(Overheal.OVERHEAL_MARKER);
                    }

                    if (!hasOverhealCooldown && hasTooMuchHealth) {
                        wp.setHealth(wp.getMaxHealth());
                    }

                    // Checks whether the displayed health can be above or under 40 health total. (20 hearts.)
                    float newHealth = wp.getHealth() / wp.getMaxHealth() * 40;

                    if (newHealth < 0) {
                        newHealth = 0;
                    } else if (newHealth > 40) {
                        newHealth = 40;
                    }

                    // Checks whether the player has any remaining active Undying Army instances active.
                    if (wp.getCooldownManager().checkUndyingArmy(false) && newHealth <= 0) {

                        for (RegularCooldown<?> undyingArmyCooldown : new CooldownFilter<>(wp, RegularCooldown.class)
                                .filterCooldownClass(UndyingArmy.class)
                                .stream()
                                .toList()
                        ) {
                            UndyingArmy undyingArmy = (UndyingArmy) undyingArmyCooldown.getCooldownObject();
                            if (!undyingArmy.isArmyDead(wp)) {
                                undyingArmy.pop(wp);

                                // Drops the flag when popped.
                                FlagHolder.dropFlagForPlayer(wp);

                                // Sending the message + check if getFrom is self
                                if (undyingArmyCooldown.getFrom() == wp) {
                                    wp.sendMessage("§a»§7 " +
                                            ChatColor.LIGHT_PURPLE +
                                            "Your Undying Army revived you with temporary health. Fight until your death! Your health will decay by " +
                                            ChatColor.RED +
                                            (wp.getMaxHealth() * (undyingArmy.getMaxHealthDamage() / 100f)) +
                                            ChatColor.LIGHT_PURPLE +
                                            " every second."
                                    );
                                } else {
                                    wp.sendMessage("§a»§7 " +
                                            ChatColor.LIGHT_PURPLE + undyingArmyCooldown.getFrom().getName() +
                                            "'s Undying Army revived you with temporary health. Fight until your death! Your health will decay by " +
                                            ChatColor.RED +
                                            Math.round(wp.getMaxHealth() * (undyingArmy.getMaxHealthDamage() / 100f)) +
                                            ChatColor.LIGHT_PURPLE +
                                            " every second."
                                    );
                                }

                                FireWorkEffectPlayer.playFirework(wp.getLocation(), FireworkEffect.builder()
                                                                                                  .withColor(Color.LIME)
                                                                                                  .with(FireworkEffect.Type.BALL)
                                                                                                  .build());

                                wp.heal();

                                if (player != null) {
                                    player.getWorld().spigot().strikeLightningEffect(wp.getLocation(), false);
                                    player.getInventory().setItem(5, UndyingArmy.BONE);
                                }
                                newHealth = 40;

                                //gives 50% of max energy if player is less than half
                                if (wp.getEnergy() < wp.getMaxEnergy() / 2) {
                                    wp.setEnergy(wp.getMaxEnergy() / 2);
                                }

                                if (undyingArmy.isPveUpgrade()) {
                                    wp.addSpeedModifier(wp, "ARMY", 40, 16 * 20, "BASE");
                                }

                                undyingArmyCooldown.setNameAbbreviation("POPPED");
                                undyingArmyCooldown.setTicksLeft(16 * 20);
                                undyingArmyCooldown.setOnRemove(cooldownManager -> {
                                    if (wp.getEntity() instanceof Player) {
                                        if (cooldownManager.checkUndyingArmy(true)) {
                                            ((Player) wp.getEntity()).getInventory().remove(UndyingArmy.BONE);
                                        }
                                    }
                                });
                                undyingArmyCooldown.addTriConsumer((cooldown, ticksLeft, ticksElapsed) -> {
                                    if (ticksElapsed % 20 == 0) {
                                        wp.addDamageInstance(
                                                wp,
                                                "",
                                                wp.getMaxHealth() * (undyingArmy.getMaxHealthDamage() / 100f),
                                                wp.getMaxHealth() * (undyingArmy.getMaxHealthDamage() / 100f),
                                                0,
                                                100,
                                                false
                                        );

                                        if (undyingArmy.isPveUpgrade() && ticksElapsed % 40 == 0) {
                                            PlayerFilter.entitiesAround(wp, 6, 6, 6)
                                                        .aliveEnemiesOf(wp)
                                                        .forEach(enemy -> {
                                                            float healthDamage = enemy.getMaxHealth() * .02f;
                                                            if (healthDamage < DamageCheck.MINIMUM_DAMAGE) {
                                                                healthDamage = DamageCheck.MINIMUM_DAMAGE;
                                                            }
                                                            if (healthDamage > DamageCheck.MAXIMUM_DAMAGE) {
                                                                healthDamage = DamageCheck.MAXIMUM_DAMAGE;
                                                            }
                                                            enemy.addDamageInstance(
                                                                    wp,
                                                                    "Undying Army",
                                                                    458 + healthDamage,
                                                                    612 + healthDamage,
                                                                    0,
                                                                    100,
                                                                    false
                                                            );
                                                        });

                                        }
                                    }
                                });

                                Bukkit.getPluginManager().callEvent(new WarlordsUndyingArmyPopEvent(wp, undyingArmy));

                                break;
                            }
                        }
                    }

                    // Energy
                    if (wp.getEnergy() < wp.getMaxEnergy()) {
                        // Standard energy value per second.
                        float energyGainPerTick = wp.getSpec().getEnergyPerSec() / 20;

                        for (AbstractCooldown<?> abstractCooldown : wp.getCooldownManager().getCooldownsDistinct()) {
                            energyGainPerTick = abstractCooldown.addEnergyGainPerTick(energyGainPerTick);
                        }
                        for (AbstractCooldown<?> abstractCooldown : wp.getCooldownManager().getCooldownsDistinct()) {
                            energyGainPerTick = abstractCooldown.multiplyEnergyGainPerTick(energyGainPerTick);
                        }

                        // Setting energy gain to the value after all ability instance multipliers have been applied.
                        float newEnergy = wp.getEnergy() + energyGainPerTick;
                        if (newEnergy > wp.getMaxEnergy()) {
                            newEnergy = wp.getMaxEnergy();
                        }

                        wp.setEnergy(newEnergy);
                    }

                    if (player != null) {
                        //precaution
                        if (newHealth > 0) {
                            player.setHealth(newHealth);
                        }

                        // Respawn fix for when a player is stuck or leaves the game.
                        if (wp.getHealth() <= 0 && player.getGameMode() == GameMode.SPECTATOR) {
                            wp.heal();
                        }

                        // Checks whether the player has under 0 energy to avoid infinite energy bugs.
                        if (wp.getEnergy() < 0) {
                            wp.setEnergy(1);
                        }
                        player.setLevel((int) wp.getEnergy());
                        player.setExp(wp.getEnergy() / wp.getMaxEnergy());

                        // Saves the amount of blocks travelled per player.
                        wp.setBlocksTravelledCM(Utils.getPlayerMovementStatistics(player));
                    }

                    // Melee Cooldown
                    if (wp.getHitCooldown() > 0) {
                        wp.setHitCooldown(wp.getHitCooldown() - 1);
                    }

                    // Natural Regen
                    if (wp instanceof WarlordsPlayer) {
                        int regenTickTimer = wp.getRegenTickTimer();
                        wp.setRegenTickTimer(regenTickTimer - 1);
                        if (regenTickTimer == 0) {
                            wp.getHitBy().clear();
                        }
                        //negative regen tick timer means the player is regenning, cant check per second because not fine enough
                        if (regenTickTimer <= 0 && -regenTickTimer % 20 == 0) {
                            int healthToAdd = (int) (wp.getMaxHealth() / 55.3);
                            wp.setHealth(Math.max(wp.getHealth(), Math.min(wp.getHealth() + healthToAdd, wp.getMaxHealth())));
                        }
                    }

                    //NPC STUN
                    if (wp instanceof WarlordsNPC npc) {
                        if (npc.getStunTicks() > 0) {
                            npc.setStunTicks(npc.getStunTicks() - 1, true);
                        }
                    }

                    // Orbs of Life
                    Location playerPosition = wp.getLocation();
                    List<OrbsOfLife.Orb> orbs = new ArrayList<>();
                    PlayerFilter.playingGame(wp.getGame()).teammatesOf(wp).forEach(p -> {
                        new CooldownFilter<>(p, PersistentCooldown.class)
                                .filterCooldownClassAndMapToObjectsOfClass(OrbsOfLife.class)
                                .forEachOrdered(orbsOfLife -> orbs.addAll(orbsOfLife.getSpawnedOrbs()));
                    });

                    Iterator<OrbsOfLife.Orb> itr = orbs.iterator();

                    while (itr.hasNext()) {
                        OrbsOfLife.Orb orb = itr.next();
                        Location orbPosition = orb.getArmorStand().getLocation();
                        if ((orb.getPlayerToMoveTowards() == null || (orb.getPlayerToMoveTowards() != null && orb.getPlayerToMoveTowards() == wp)) &&
                                orbPosition.distanceSquared(playerPosition) < 1.35 * 1.35 && !wp.isDead()) {

                            orb.remove();
                            itr.remove();

                            float orbHeal = orb.getCooldown().getCooldownObject().getMinDamageHeal();
                            WarlordsEntity owner = orb.getOwner();

                            // Increasing heal for low long orb lived for (up to +25%)
                            // 6.5 seconds = 130 ticks
                            // 6.5 seconds = 1 + (130/325) = 1.4
                            // 225 *= 1.4 = 315
                            if (orb.getPlayerToMoveTowards() == null) {
                                orbHeal *= 1 + orb.getTicksLived() / 325f;
                            }

                            wp.addHealingInstance(owner, "Orbs of Life", orbHeal, orbHeal, 0, 100, false, false);
                            if (player != null) {
                                Utils.playGlobalSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.2f, 1);
                            }

                            for (WarlordsEntity nearPlayer : PlayerFilter
                                    .entitiesAround(wp, 6, 6, 6)
                                    .aliveTeammatesOfExcludingSelf(wp)
                                    .leastAliveFirst()
                                    .limit(2)
                            ) {
                                nearPlayer.addHealingInstance(owner, "Orbs of Life", orbHeal, orbHeal, 0, 100, false, false);
                                if (player != null) {
                                    Utils.playGlobalSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.2f, 1);
                                }
                            }
                        } else {

                            // Checks whether the Orb of Life has lived for 8 seconds.
                            if (orb.getTicksLived() > orb.getTicksToLive() || (orb.getPlayerToMoveTowards() != null && orb.getPlayerToMoveTowards().isDead())) {
                                orb.remove();
                                itr.remove();
                            }
                        }
                    }
                }

                // Loops every 10 ticks - .5 second.
                if (LOOP_TICK_COUNTER.get() % 10 == 0) {
                    for (WarlordsEntity wps : PLAYERS.values()) {
                        // Soulbinding Weapon - decrementing time left on the ability.
                        new CooldownFilter<>(wps, PersistentCooldown.class)
                                .filterCooldownClassAndMapToObjectsOfClass(Soulbinding.class)
                                .forEachOrdered(soulbinding -> soulbinding.getSoulBindedPlayers().forEach(Soulbinding.SoulBoundPlayer::decrementTimeLeft));
                        // Soulbinding Weapon - Removing bound players.
                        new CooldownFilter<>(wps, PersistentCooldown.class)
                                .filterCooldownClassAndMapToObjectsOfClass(Soulbinding.class)
                                .forEachOrdered(soulbinding -> soulbinding.getSoulBindedPlayers()
                                                                          .removeIf(boundPlayer -> boundPlayer.getTimeLeft() == 0 || (boundPlayer.isHitWithSoul() && boundPlayer.isHitWithLink())));
                    }
                }

                // Loops every 20 ticks - 1 second.
                if (LOOP_TICK_COUNTER.get() % 20 == 0) {

                    // Removes leftover horses if there are any.
                    RemoveEntities.removeHorsesInGame();

                    for (WarlordsEntity wps : PLAYERS.values()) {
                        // Checks whether the game is paused.
                        if (wps.getGame().isFrozen()) {
                            continue;
                        }
                        wps.runEverySecond();
                        // Cooldowns

                        // Checks whether the player has a flag cooldown.
                        if (wps.getFlagDropCooldown() > 0) {
                            wps.setFlagDropCooldown(wps.getFlagDropCooldown() - 1);
                        }
                        if (wps.getFlagPickCooldown() > 0) {
                            wps.setFlagPickCooldown(wps.getFlagPickCooldown() - 1);
                        }

                        // Checks whether the player has the healing powerup active.
                        if (wps.getCooldownManager().hasCooldown(HealingPowerup.class)) {
                            float heal = wps.getMaxHealth() * .08f;
                            if (wps.getHealth() + heal > wps.getMaxHealth()) {
                                heal = wps.getMaxHealth() - wps.getHealth();
                            }

                            if (heal > 0) {
                                wps.setHealth(wps.getHealth() + heal);
                                wps.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN + " §7Healed §a" + Math.round(heal) + " §7health.");
                            }
                        }

                        // Combat Timer - Logs combat time after 4 seconds.
                        if (wps.getRegenTickTimer() > 6 * 20) {
                            wps.getMinuteStats().addTimeInCombat();
                        }

                        // Assists - 10 seconds timer.
                        wps.getHitBy().replaceAll((wp, integer) -> integer - 1);
                        wps.getHealedBy().replaceAll((wp, integer) -> integer - 1);
                        wps.getHitBy().entrySet().removeIf(p -> p.getValue() <= 0);
                        wps.getHealedBy().entrySet().removeIf(p -> p.getValue() <= 0);
                    }

                    WarlordsEvents.entityList.removeIf(e -> !e.isValid());
                }

                // Loops every 50 ticks - 2.5 seconds.
                if (LOOP_TICK_COUNTER.get() % 50 == 0) {
                    for (WarlordsEntity warlordsPlayer : PLAYERS.values()) {

                        if (warlordsPlayer.getGame().isFrozen()) {
                            continue;
                        }

                        LivingEntity player = warlordsPlayer.getEntity();
                        List<Location> locations = warlordsPlayer.getLocations();

                        if (warlordsPlayer.isDead() && !locations.isEmpty()) {
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
                        Bukkit.broadcastMessage(ChatColor.RED + "The server will restart in 1 hour.");
                        SENT_HOUR_REMINDER.set(true);
                    }
                } else if (!SENT_HALF_HOUR_REMINDER.get()) {
                    if (now.plus(30, ChronoUnit.MINUTES).isAfter(nextReset)) {
                        Bukkit.broadcastMessage(ChatColor.RED + "The server will restart in 30 minutes.");
                        SENT_HALF_HOUR_REMINDER.set(true);
                    }
                } else if (!SENT_FIFTEEN_MINUTE_REMINDER.get()) {
                    if (now.plus(15, ChronoUnit.MINUTES).isAfter(nextReset)) {
                        Bukkit.broadcastMessage(ChatColor.RED + "The server will restart in 15 minutes.");
                        SENT_FIFTEEN_MINUTE_REMINDER.set(true);
                        cancel(); // Can cancel since there are no more checks
                    }
                }

            }
        }.runTaskTimer(this, 20, 1000);
    }

    public static boolean hasPlayer(@Nonnull UUID player) {
        return PLAYERS.containsKey(player);
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