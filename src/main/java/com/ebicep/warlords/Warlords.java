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
import com.ebicep.jda.BotCommands;
import com.ebicep.jda.BotListener;
import com.ebicep.jda.BotManager;
import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.internal.EnergyPowerup;
import com.ebicep.warlords.classes.internal.HealingPowerup;
import com.ebicep.warlords.classes.internal.Overheal;
import com.ebicep.warlords.classes.rogue.specs.apothecary.Apothecary;
import com.ebicep.warlords.commands.debugcommands.*;
import com.ebicep.warlords.commands.miscellaneouscommands.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.FutureMessageManager;
import com.ebicep.warlords.database.configuration.ApplicationConfiguration;
import com.ebicep.warlords.database.leaderboards.LeaderboardCommand;
import com.ebicep.warlords.events.WarlordsEvents;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameManager;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.menu.MenuEventListener;
import com.ebicep.warlords.party.PartyCommand;
import com.ebicep.warlords.party.PartyListener;
import com.ebicep.warlords.party.PartyManager;
import com.ebicep.warlords.party.StreamCommand;
import com.ebicep.warlords.player.*;
import com.ebicep.warlords.player.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.cooldowns.CooldownManager;
import com.ebicep.warlords.player.cooldowns.cooldowns.PersistentCooldown;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.queuesystem.QueueCommand;
import com.ebicep.warlords.util.*;
import me.filoghost.holographicdisplays.api.beta.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.beta.hologram.Hologram;
import net.minecraft.server.v1_8_R3.PacketPlayInSteerVehicle;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;
import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


public class Warlords extends JavaPlugin {

    public static String VERSION = "";

    private static Warlords instance;

    public static Warlords getInstance() {
        return instance;
    }

    public static String serverIP;

    private static TaskChainFactory taskChainFactory;

    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }

    public static <T> TaskChain<T> newSharedChain(String name) {
        return taskChainFactory.newSharedChain(name);
    }

    private static final HashMap<UUID, WarlordsPlayer> players = new HashMap<>();

    public static HashMap<UUID, WarlordsPlayer> getPlayers() {
        return players;
    }

    public static void addPlayer(@Nonnull WarlordsPlayer warlordsPlayer) {
        players.put(warlordsPlayer.getUuid(), warlordsPlayer);
        for (GameAddon addon : warlordsPlayer.getGame().getAddons()) {
            addon.warlordsPlayerCreated(warlordsPlayer.getGame(), warlordsPlayer);
        }
    }

    @Nullable
    public static WarlordsPlayer getPlayer(@Nullable Entity entity) {
        if (entity != null) {
            Optional<MetadataValue> metadata = entity.getMetadata("WARLORDS_PLAYER").stream().filter(e -> e.value() instanceof WarlordsPlayer).findAny();
            if (metadata.isPresent()) {
                return (WarlordsPlayer) metadata.get().value();
            }
        }
        return null;
    }

    @Nullable
    public static WarlordsPlayer getPlayer(@Nullable OfflinePlayer player) {
        return player == null ? null : getPlayer(player.getUniqueId());
    }

    @Nullable
    public static WarlordsPlayer getPlayer(@Nullable Player player) {
        return getPlayer((OfflinePlayer) player);
    }

    @Nullable
    public static WarlordsPlayer getPlayer(@Nonnull UUID player) {
        return players.get(player);
    }


    public static boolean hasPlayer(@Nonnull OfflinePlayer player) {
        return hasPlayer(player.getUniqueId());
    }

    public static boolean hasPlayer(@Nonnull UUID player) {
        return players.containsKey(player);
    }

    public static void removePlayer(@Nonnull UUID player) {
        WarlordsPlayer wp = players.remove(player);
        if (wp != null) {
            if (!(wp.getEntity() instanceof Player)) {
                wp.getEntity().remove();
            }

            wp.getCooldownManager().clearAllCooldowns();
        }
        Location loc = spawnPoints.remove(player);
        Player p = Bukkit.getPlayer(player);
        if (p != null) {
            p.removeMetadata("WARLORDS_PLAYER", Warlords.getInstance());
            if (loc != null) {
                p.teleport(getRejoinPoint(player));
            }
        }
    }

//    public static HashMap<UUID, WarlordsPlayer> getPlayers() {
//        return players;
//    }

    public final static HashMap<UUID, Location> spawnPoints = new HashMap<>();

    @Nonnull
    public static Location getRejoinPoint(@Nonnull UUID key) {
        return spawnPoints.getOrDefault(key, new LocationBuilder(Bukkit.getWorlds().get(0).getSpawnLocation()).yaw(-90).get());
    }

    public static void setRejoinPoint(@Nonnull UUID key, @Nonnull Location value) {
        spawnPoints.put(key, value);
        Player player = Bukkit.getPlayer(key);
        if (player != null) {
            player.teleport(value);
        }
    }

    private final static HashMap<UUID, PlayerSettings> playerSettings = new HashMap<>();

    @Nonnull
    public static PlayerSettings getPlayerSettings(@Nonnull UUID key) {
        PlayerSettings settings = playerSettings.computeIfAbsent(key, (k) -> new PlayerSettings());
        // TODO update last accessed field on settings
        return settings;
    }

    private final static HashMap<UUID, net.minecraft.server.v1_8_R3.ItemStack> playerHeads = new HashMap<>();

    public static HashMap<UUID, net.minecraft.server.v1_8_R3.ItemStack> getPlayerHeads() {
        return playerHeads;
    }

    public static void updateHeads() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            updateHead(onlinePlayer);
        }
        System.out.println("[Warlords] Heads updated");
    }

    public static void updateHead(Player player) {
        ItemStack playerSkull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta skullMeta = (SkullMeta) playerSkull.getItemMeta();
        skullMeta.setOwner(player.getName());
        playerSkull.setItemMeta(skullMeta);
        playerHeads.put(player.getUniqueId(), CraftItemStack.asNMSCopy(playerSkull));
    }

    public static ItemStack getHead(Player player) {
        return getHead(player.getUniqueId());
    }

    public static ItemStack getHead(UUID uuid) {
        return CraftItemStack.asBukkitCopy(playerHeads.getOrDefault(uuid, CraftItemStack.asNMSCopy(new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal()))));
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
            for (Weapons weapons : Weapons.values()) {
                config.set(weapons.getName(), weapons.isUnlocked);
            }
            config.save(new File(this.getDataFolder(), "weapons.yml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private GameManager gameManager;
    public static boolean holographicDisplaysEnabled;

    public static boolean citizensEnabled;
    public Location npcCTFLocation;

    public static final PartyManager partyManager = new PartyManager();

    public static HashMap<UUID, ChatChannels> playerChatChannels = new HashMap<>();

    public static HashMap<UUID, CustomScoreboard> playerScoreboards = new HashMap<>();

    static {
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.mongodb.driver")).setLevel(ch.qos.logback.classic.Level.ERROR);
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.springframework")).setLevel(ch.qos.logback.classic.Level.ERROR);
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("net.dv8tion.jda")).setLevel(ch.qos.logback.classic.Level.ERROR);
    }

    @Override
    public void onEnable() {
        instance = this;
        VERSION = this.getDescription().getVersion();
        serverIP = this.getServer().getIp();
        taskChainFactory = BukkitTaskChainFactory.create(this);

        gameManager = new GameManager();
        gameManager.addGameHolder("Rift-0", GameMap.RIFT, new LocationFactory(Bukkit.getWorld("Rift")));
        gameManager.addGameHolder("SimulationRift-0", GameMap.SIMULATION_RIFT, new LocationFactory(Bukkit.getWorld("SimulationRift")));
        gameManager.addGameHolder("Arathi-0", GameMap.ARATHI, new LocationFactory(Bukkit.getWorld("Arathi")));
        gameManager.addGameHolder("Crossfire-0", GameMap.CROSSFIRE, new LocationFactory(Bukkit.getWorld("Crossfire")));
        gameManager.addGameHolder("SimulationCrossfire-0", GameMap.SIMULATION_CROSSFIRE, new LocationFactory(Bukkit.getWorld("SimulationCrossfire")));
        gameManager.addGameHolder("Valley-0", GameMap.VALLEY, new LocationFactory(Bukkit.getWorld("Atherrough_Valley")));
        gameManager.addGameHolder("Warsong-0", GameMap.WARSONG, new LocationFactory(Bukkit.getWorld("Warsong")));
        gameManager.addGameHolder("Debug-0", GameMap.DEBUG, new LocationFactory(Bukkit.getWorld("TestWorld")));
        gameManager.addGameHolder("Heaven-0", GameMap.HEAVEN_WILL, new LocationFactory(Bukkit.getWorld("Heaven")));

        Thread.currentThread().setContextClassLoader(getClassLoader());

        ConfigurationSerialization.registerClass(PlayerSettings.class);
        getServer().getPluginManager().registerEvents(new WarlordsEvents(), this);
        getServer().getPluginManager().registerEvents(new MenuEventListener(this), this);
        getServer().getPluginManager().registerEvents(new PartyListener(), this);
        getServer().getPluginManager().registerEvents(new BotListener(), this);
        getServer().getPluginManager().registerEvents(new RecklessCharge(), this);
        getServer().getPluginManager().registerEvents(new FutureMessageManager(), this);

        new GameStartCommand().register(this);
        new GameTerminateCommand().register(this);
        new GameKillCommand().register(this);
        new GameListCommand().register(this);
        new MenuCommand().register(this);
        new ShoutCommand().register(this);
        new HotkeyModeCommand().register(this);
        new DebugCommand().register(this);
        new ClassCommand().register(this);
        new GetPlayersCommand().register(this);
        new TestCommand().register(this);
        new ParticleQualityCommand().register(this);
        new SpawnTestDummyCommand().register(this);
        new PartyCommand().register(this);
        new StreamCommand().register(this);
        new RecordAverageDamage().register(this);
        new ChatChannelCommand().register(this);
        new BotCommands().register(this);
        new LeaderboardCommand().register(this);
        new RecordGamesCommand().register(this);
        new GamesCommand().register(this);
        new SpectateCommand().register(this);
        new DebugModeCommand().register(this);
        new MyLocationCommand().register(this);
        new MessageCommand().register(this);
        new ExperienceCommand().register(this);
        new QueueCommand().register(this);
        new ImposterCommand().register(this);
        new LobbyCommand().register(this);
        new DiscordCommand().register(this);

        updateHeads();

        readKeysConfig();
        readWeaponConfig();
        saveWeaponConfig();

        holographicDisplaysEnabled = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
        citizensEnabled = Bukkit.getPluginManager().isPluginEnabled("Citizens");

        Bukkit.getOnlinePlayers().forEach(player -> {
            playerScoreboards.put(player.getUniqueId(), new CustomScoreboard(player));
        });


        //connects to the database
        Warlords.newChain()
                .async(DatabaseManager::init)
                .execute();

        try {
            BotManager.connect();
        } catch (LoginException e) {
            e.printStackTrace();
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
                                if (counter++ % playerSettings.get(player.getUniqueId()).getParticleQuality().particleReduction == 0) {
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }
                });
        protocolManager.addPacketListener(
                new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Play.Client.STEER_VEHICLE) {
                    @Override
                    public void onPacketReceiving(PacketEvent e) {
                        if (e.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
                            if (e.getPacket().getHandle() instanceof PacketPlayInSteerVehicle) {
                                boolean dismount = e.getPacket().getBooleans().read(1);
                                Field f;
                                try {
                                    f = PacketPlayInSteerVehicle.class.getDeclaredField("d");
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

        startMainLoop();
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] Plugin is enabled");


        for (String command : this.getDescription().getCommands().keySet()) {
            if (getCommand(command).getExecutor() == this) {
                getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "[Warlords] Warning, command " + command + " is specified in plugin.yml, but not defined in the plugins");
            }
        }
    }


    @Override
    public void onDisable() {
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
                HolographicDisplaysAPI.get(instance).getHolograms().forEach(Hologram::delete);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            NPCManager.gameStartNPC.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Bukkit.getWorld("MainLobby").getEntities().stream()
                    .filter(entity -> entity.getName().equals("capture-the-flag"))
                    .forEach(Entity::remove);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            BotManager.deleteStatusMessage();
            BotManager.jda.shutdownNow();
        } catch (Exception e) {
            e.printStackTrace();
        }

        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[Warlords] Plugin is disabled");
        // TODO persist this.playerSettings to a database
    }

    private void startMainLoop() {
        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {

                // Every 1 tick - 0.05 seconds.
                {
                    for (WarlordsPlayer wp : players.values()) {
                        Player player = wp.getEntity() instanceof Player ? (Player) wp.getEntity() : null;
                        if (player != null) {
                            //ACTION BAR
                            if (player.getInventory().getHeldItemSlot() != 8) {
                                wp.displayActionBar();
                            } else {
                                wp.displayFlagActionBar(player);
                            }
                        }

                        // Checks whether the game is paused.
                        if (wp.getGame().isFrozen()) {
                            continue;
                        }

                        // Updating all player speed.
                        wp.getSpeed().updateSpeed();

                        // will add more efficient system later
                        if (wp.getSpec() instanceof Apothecary) {
                            wp.getSpeed().addSpeedModifier("Base Speed", 20, 1, "BASE");
                        }

                        CooldownManager cooldownManager = wp.getCooldownManager();

                        // Setting the flag tracking compass.
                        if (player != null && wp.getCompassTarget() != null) {
                            player.setCompassTarget(wp.getCompassTarget().getLocation());
                        }

                        // Checks whether the player has cooldowns disabled.
                        if (wp.isDisableCooldowns()) {
                            wp.getSpec().getRed().setCurrentCooldown(0);
                            wp.getSpec().getPurple().setCurrentCooldown(0);
                            wp.getSpec().getBlue().setCurrentCooldown(0);
                            wp.getSpec().getOrange().setCurrentCooldown(0);
                            wp.setHorseCooldown(0);
                            wp.updateRedItem();
                            wp.updatePurpleItem();
                            wp.updateBlueItem();
                            wp.updateOrangeItem();
                            wp.updateHorseItem();
                        }

                        // Ability Cooldowns

                        // Decrementing red skill's cooldown.
                        if (wp.getSpec().getRed().getCurrentCooldown() > 0) {
                            wp.getSpec().getRed().subtractCooldown(.05f);
                            if (player != null) {
                                wp.updateRedItem(player);
                            }
                        }

                        // Decrementing purple skill's cooldown.
                        if (wp.getSpec().getPurple().getCurrentCooldown() > 0) {
                            wp.getSpec().getPurple().subtractCooldown(.05f);
                            if (player != null) {
                                wp.updatePurpleItem(player);
                            }
                        }

                        // Decrementing blue skill's cooldown.
                        if (wp.getSpec().getBlue().getCurrentCooldown() > 0) {
                            wp.getSpec().getBlue().subtractCooldown(.05f);
                            if (player != null) {
                                wp.updateBlueItem(player);
                            }
                        }

                        // Decrementing orange skill's cooldown.
                        if (wp.getSpec().getOrange().getCurrentCooldown() > 0) {
                            wp.getSpec().getOrange().subtractCooldown(.05f);
                            if (player != null) {
                                wp.updateOrangeItem(player);
                            }
                        }

                        // Decrementing mount cooldown.
                        if (wp.getHorseCooldown() > 0 && !wp.getEntity().isInsideVehicle()) {
                            wp.setHorseCooldown(wp.getHorseCooldown() - .05f);
                            if (player != null) {
                                wp.updateHorseItem(player);
                            }
                        }

                        wp.getCooldownManager().reduceCooldowns();

                        // Checks whether the player has overheal active and is full health or not.
                        boolean hasOverhealCooldown = wp.getCooldownManager().hasCooldown(Overheal.OVERHEAL_MARKER);
                        boolean hasTooMuchHealth = wp.getHealth() > wp.getMaxHealth();

                        if (hasOverhealCooldown && !hasTooMuchHealth) {
                            wp.getCooldownManager().removeCooldown(Overheal.OVERHEAL_MARKER);
                        }

                        if (!hasOverhealCooldown && hasTooMuchHealth) {
                            wp.setHealth(wp.getMaxHealth());
                        }

                        // Checks whether the player has Vindicate active.
                        if (wp.getCooldownManager().hasCooldownFromName("Vindicate Debuff Immunity")) {
                            wp.getSpeed().removeSlownessModifiers();
                            wp.getCooldownManager().removeDebuffCooldowns();
                        }

                        // Checks whether the displayed health can be above or under 40 health total. (20 hearts.)
                        float newHealth = (float) wp.getHealth() / wp.getMaxHealth() * 40;

                        if (newHealth < 0) {
                            newHealth = 0;
                        } else if (newHealth > 40) {
                            newHealth = 40;
                        }

                        // Checks whether the player has any remaining active Undying Army instances active.
                        if (wp.getCooldownManager().checkUndyingArmy(false) && newHealth <= 0) {

                            for (RegularCooldown undyingArmyCooldown : new CooldownFilter<>(wp, RegularCooldown.class)
                                    .filterCooldownClass(UndyingArmy.class)
                                    .stream()
                                    .collect(Collectors.toList())
                            ) {
                                UndyingArmy undyingArmy = (UndyingArmy) undyingArmyCooldown.getCooldownObject();
                                if (!undyingArmy.isArmyDead(wp.getUuid())) {
                                    undyingArmy.pop(wp.getUuid());

                                    // Drops the flag when popped.
                                    FlagHolder.dropFlagForPlayer(wp);

                                    // Sending the message + check if getFrom is self
                                    if (undyingArmyCooldown.getFrom() == wp) {
                                        wp.sendMessage("§a\u00BB§7 " +
                                                ChatColor.LIGHT_PURPLE +
                                                "Your Undying Army revived you with temporary health. Fight until your death! Your health will decay by " +
                                                ChatColor.RED +
                                                (wp.getMaxHealth() / 10) +
                                                ChatColor.LIGHT_PURPLE +
                                                " every second."
                                        );
                                    } else {
                                        wp.sendMessage("§a\u00BB§7 " +
                                                        ChatColor.LIGHT_PURPLE + undyingArmyCooldown.getFrom().getName() +
                                                        "'s Undying Army revived you with temporary health. Fight until your death! Your health will decay by " +
                                                        ChatColor.RED +
                                                        (wp.getMaxHealth() / 10) +
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

                                    new GameRunnable(wp.getGame()) {
                                        @Override
                                        public void run() {
                                            if (wp.getRespawnTimer() >= 0 || wp.isDead()) {
                                                this.cancel();
                                            } else {
                                                //UNDYING ARMY - dmg 10% of max health each popped army
                                                wp.addDamageInstance(wp, "", wp.getMaxHealth() / 10f, wp.getMaxHealth() / 10f, -1, 100, false);
                                            }
                                        }
                                    }.runTaskTimer(0, 20);

                                    break;
                                }
                            }
                        }

                        if (newHealth <= 0 && wp.getRespawnTimer() == -1) {
                            //checking if all undying armies are popped (this should never be true as last if statement bypasses this) then removing all boners
                            if (!wp.getCooldownManager().checkUndyingArmy(false)) {
                                if (player != null) {
                                    player.getInventory().remove(UndyingArmy.BONE);
                                }
                            }

                            //removing cooldowns here so undying army doesnt get removed
                            cooldownManager.clearCooldowns();

                            // warlordsPlayer.respawn();
                            if (player != null) {
                                player.setGameMode(GameMode.SPECTATOR);
                                //precaution
                            }
                            FlagHolder.dropFlagForPlayer(wp);

                            //giving out assists
                            int lastElementIndex = wp.getHitBy().size() - 1;
                            WarlordsPlayer killedBy = wp.getHitBy().entrySet().stream().skip(lastElementIndex).iterator().next().getKey();
                            final int[] counter = {0};
                            wp.getHitBy().forEach((assisted, value) -> {
                                if (counter[0] != lastElementIndex) {
                                    if (killedBy == assisted || killedBy == wp) {
                                        assisted.sendMessage(
                                                ChatColor.GRAY +
                                                        "You assisted in killing " +
                                                        wp.getColoredName()
                                        );
                                    } else {
                                        assisted.sendMessage(
                                                ChatColor.GRAY +
                                                        "You assisted " +
                                                        killedBy.getColoredName() +
                                                        ChatColor.GRAY + " in killing " +
                                                        wp.getColoredName()
                                        );
                                    }

                                    assisted.addAssist();
                                }
                                counter[0]++;
                            });
                            wp.getHitBy().clear();
                            wp.setRegenTimer(0);
                            wp.heal();
                        } else {
                            if (player != null) {
                                //precaution
                                if (newHealth > 0 && newHealth <= 40) {
                                    player.setHealth(newHealth);
                                }
                            }
                        }

                        // Respawn fix for when a player is stuck or leaves the game.
                        if (player != null) {
                            if (wp.getHealth() <= 0 && player.getGameMode() == GameMode.SPECTATOR) {
                                wp.heal();
                            }
                        }

                        // Energy

                        if (wp.getEnergy() < wp.getMaxEnergy()) {

                            // Standard energy value per second.
                            float energyGainPerTick = wp.getSpec().getEnergyPerSec() / 20f;

                            // Checks whether the player has Avenger's Wrath active.
                            if (cooldownManager.hasCooldown(AvengersWrath.class)) {
                                energyGainPerTick += 1;
                            }

                            // Checks whether the player has Inspiring Presence active.
                            if (cooldownManager.hasCooldown(InspiringPresence.class)) {
                                energyGainPerTick += .5;
                            }

                            // Checks whether the player has been marked by an Avenger.
                            if (cooldownManager.hasCooldown(HolyRadianceAvenger.class)) {
                                energyGainPerTick -= .4;
                            }

                            // Checks whether the player has been marked by a Crusader.
                            if (cooldownManager.hasCooldown(HolyRadianceCrusader.class)) {
                                energyGainPerTick += .25;
                            }

                            // Checks whether the player has Acupressure active.
                            if (cooldownManager.hasCooldown(Acupressure.class)) {
                                energyGainPerTick += 2.5;
                            }

                            // Checks whether the player has the Energy Powerup active.
                            if (cooldownManager.hasCooldown(EnergyPowerup.class)) {
                                energyGainPerTick *= 1.4;
                            }

                            // Setting energy gain to the value after all ability instance multipliers have been applied.
                            float newEnergy = wp.getEnergy() + energyGainPerTick;
                            if (newEnergy > wp.getMaxEnergy()) {
                                newEnergy = wp.getMaxEnergy();
                            }

                            wp.setEnergy(newEnergy);
                        }

                        // Checks whether the player has under 0 energy to avoid infinite energy bugs.
                        if (player != null) {
                            if (wp.getEnergy() < 0) {
                                wp.setEnergy(1);
                            }
                            player.setLevel((int) wp.getEnergy());
                            player.setExp(wp.getEnergy() / wp.getMaxEnergy());
                        }

                        // Melee Cooldown
                        if (wp.getHitCooldown() > 0) {
                            wp.setHitCooldown(wp.getHitCooldown() - 1);
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
                                    orbPosition.distanceSquared(playerPosition) < 1.35 * 1.35 && !wp.isDeath()) {

                                orb.remove();
                                itr.remove();

                                float orbHeal = OrbsOfLife.ORB_HEALING;
                                if (Warlords.getPlayerSettings(orb.getOwner().getUuid()).getSkillBoostForClass() == ClassesSkillBoosts.ORBS_OF_LIFE) {
                                    orbHeal *= 1.2;
                                }

                                // Increasing heal for low long orb lived for (up to +25%)
                                // 6.5 seconds = 130 ticks
                                // 6.5 seconds = 1 + (130/325) = 1.4
                                // 225 *= 1.4 = 315
                                if (orb.getPlayerToMoveTowards() == null) {
                                    orbHeal *= 1 + orb.getTicksLived() / 325f;
                                }

                                wp.addHealingInstance(orb.getOwner(), "Orbs of Life", orbHeal, orbHeal, -1, 100, false, false);
                                if (player != null) {
                                    Utils.playGlobalSound(player.getLocation(), Sound.ORB_PICKUP, 0.2f, 1);
                                }

                                for (WarlordsPlayer nearPlayer : PlayerFilter
                                        .entitiesAround(wp, 6, 6, 6)
                                        .aliveTeammatesOfExcludingSelf(wp)
                                        .limit(2)
                                ) {
                                    nearPlayer.addHealingInstance(orb.getOwner(), "Orbs of Life", orbHeal, orbHeal, -1, 100, false, false);
                                    if (player != null) {
                                        Utils.playGlobalSound(player.getLocation(), Sound.ORB_PICKUP, 0.2f, 1);
                                    }
                                }
                            }

                            // Checks whether the Orb of Life has lived for 8 seconds.
                            if (orb.getBukkitEntity().getTicksLived() > 160 || (orb.getPlayerToMoveTowards() != null && orb.getPlayerToMoveTowards().isDeath())) {
                                orb.remove();
                                itr.remove();
                            }
                        }

                        // Saves the amount of blocks travelled per player.
                        if (player != null) {
                            wp.setBlocksTravelledCM(Utils.getPlayerMovementStatistics(player));
                        }
                    }

                    // Loops every 10 ticks - .5 second.
                    if (counter % 10 == 0) {
                        for (WarlordsPlayer wps : players.values()) {
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
                    if (counter % 20 == 0) {

                        // Removes leftover horses if there are any.
                        RemoveEntities.removeHorsesInGame();

                        for (WarlordsPlayer wps : players.values()) {
                            // Checks whether the game is paused.
                            if (wps.getGame().isFrozen()) {
                                continue;
                            }
                            wps.runEverySecond();

                            Player player = wps.getEntity() instanceof Player ? (Player) wps.getEntity() : null;

                            // Natural Regen
                            if (wps.getRegenTimer() != 0) {
                                wps.setRegenTimer(wps.getRegenTimer() - 1);
                                if (wps.getRegenTimer() == 0) {
                                    wps.getHitBy().clear();
                                }
                            } else {
                                int healthToAdd = (int) (wps.getMaxHealth() / 55.3);
                                wps.setHealth(Math.max(wps.getHealth(),
                                                         Math.min(wps.getHealth() + healthToAdd,
                                                         wps.getMaxHealth()
                                                         )));
                            }

                            // Cooldowns

                            // Checks whether the player has a flag cooldown.
                            if (wps.getFlagCooldown() > 0) {
                                wps.setFlagCooldown(wps.getFlagCooldown() - 1);
                            }

                            // Checks whether the player has the healing powerup active.
                            if (wps.getCooldownManager().hasCooldown(HealingPowerup.class)) {
                                int heal = (int) (wps.getMaxHealth() * .08);
                                if (wps.getHealth() + heal > wps.getMaxHealth()) {
                                    heal = wps.getMaxHealth() - wps.getHealth();
                                }
                                wps.setHealth(wps.getHealth() + heal);
                                wps.sendMessage(WarlordsPlayer.RECEIVE_ARROW + " §7Healed §a" + heal + " §7health.");
                            }

                            // Combat Timer - Logs combat time after 4 seconds.
                            if (wps.getRegenTimer() > 6) {
                                wps.getStats().addTimeInCombat();
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
                    if (counter % 50 == 0) {
                        for (WarlordsPlayer warlordsPlayer : players.values()) {

                            if (warlordsPlayer.getGame().isFrozen()) {
                                continue;
                            }

                            LivingEntity player = warlordsPlayer.getEntity();
                            List<Location> locations = warlordsPlayer.getLocations();

                            if (warlordsPlayer.isDeath() && !locations.isEmpty()) {
                                locations.add(locations.get(locations.size() - 1));
                            } else {
                                locations.add(player.getLocation());
                            }
                        }
                    }

                    // Loops every 100 ticks - 5 seconds.
                    if (counter % 100 == 0) {
                        BotManager.sendStatusMessage(false);
                    }
                }
                counter++;
            }

        }.runTaskTimer(this, 0, 0);
    }

    public static GameManager getGameManager() {
        return getInstance().gameManager;
    }

    public void hideAndUnhidePeople(@Nonnull Player player) {
        WarlordsPlayer wp = getPlayer(player);
        Game game = wp == null ? null : wp.getGame();
        for (Player p : Bukkit.getOnlinePlayers()) {
            WarlordsPlayer wp1 = getPlayer(p);
            Game game1 = wp1 == null ? null : wp1.getGame();
            if (p != player) {
                if(game1 == game) {
                    p.showPlayer(player);
                    player.showPlayer(p);
                } else {
                    p.hidePlayer(player);
                    player.hidePlayer(p);
                }
            }
        }
    }
    public void hideAndUnhidePeople() {
        List<Player> peeps = new ArrayList<>(Bukkit.getOnlinePlayers());
        int length = peeps.size();
        for (int i = 0; i < length - 1; i++) {
            Player player = peeps.get(i);
            WarlordsPlayer wp = getPlayer(player);
            Game game = wp == null ? null : wp.getGame();
            for (int j = i + 1; j < length; j++) {
                Player p = peeps.get(j);
                WarlordsPlayer wp1 = getPlayer(p);
                Game game1 = wp1 == null ? null : wp1.getGame();
                if (game1 == game) {
                    p.showPlayer(player);
                    player.showPlayer(p);
                } else {
                    p.hidePlayer(player);
                    player.hidePlayer(p);
                }
            }
        }
    }
}