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
import com.ebicep.warlords.commands.debugcommands.*;
import com.ebicep.warlords.commands.miscellaneouscommands.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.FutureMessageManager;
import com.ebicep.warlords.database.LeaderboardCommand;
import com.ebicep.warlords.database.LeaderboardManager;
import com.ebicep.warlords.events.WarlordsEvents;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.menu.MenuEventListener;
import com.ebicep.warlords.party.*;
import com.ebicep.warlords.player.*;
import com.ebicep.warlords.powerups.EnergyPowerUp;
import com.ebicep.warlords.util.*;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Warlords extends JavaPlugin {

    public static String VERSION = "";

    private static Warlords instance;

    public static Warlords getInstance() {
        return instance;
    }

    private static TaskChainFactory taskChainFactory;

    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }

    public static <T> TaskChain<T> newSharedChain(String name) {
        return taskChainFactory.newSharedChain(name);
    }

    private static final HashMap<UUID, WarlordsPlayer> players = new HashMap<>();

    public static void addPlayer(@Nonnull WarlordsPlayer warlordsPlayer) {
        players.put(warlordsPlayer.getUuid(), warlordsPlayer);
    }

    @Deprecated // This method is useless, but handles the parts of the code that are slow with updating
    @Nullable
    public static WarlordsPlayer getPlayer(@Nullable WarlordsPlayer player) {
        return player;
    }

    @Nullable
    public static WarlordsPlayer getPlayer(@Nullable Entity entity) {
        if (entity != null) {
            Optional<MetadataValue> metadata = entity.getMetadata("WARLORDS_PLAYER").stream().findAny();
            if (metadata.isPresent()) {
                return (WarlordsPlayer) metadata.get().value();
            }
        }
        return null;
    }

    @Nullable
    public static WarlordsPlayer getPlayer(@Nonnull OfflinePlayer player) {
        return getPlayer(player.getUniqueId());
    }

    @Nullable
    public static WarlordsPlayer getPlayer(@Nonnull Player player) {
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

    public static HashMap<UUID, WarlordsPlayer> getPlayers() {
        return players;
    }

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
        System.out.println("[WARLORDS] Heads updated");
    }

    public static void updateHead(Player player) {
        ItemStack playerSkull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta skullMeta = (SkullMeta) playerSkull.getItemMeta();
        skullMeta.setOwner(player.getName());
        playerSkull.setItemMeta(skullMeta);
        playerHeads.put(player.getUniqueId(), CraftItemStack.asNMSCopy(playerSkull));
    }

    public void readKeysConfig() {
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "keys.yml"));
            DatabaseManager.key = config.getString("database_key");
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


    public static Game game;
    public static boolean holographicDisplaysEnabled;

    public static boolean citizensEnabled;
    public static NPCManager npcManager = new NPCManager();
    public Location npcCTFLocation;

    public static final int SPAWN_PROTECTION_RADIUS = 5;

    public static final PartyManager partyManager = new PartyManager();

    public static HashMap<UUID, ChatChannels> playerChatChannels = new HashMap<>();

    public static HashMap<UUID, CustomScoreboard> playerScoreboards = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        VERSION = this.getDescription().getVersion();
        taskChainFactory = BukkitTaskChainFactory.create(this);

        ConfigurationSerialization.registerClass(PlayerSettings.class);
        getServer().getPluginManager().registerEvents(new WarlordsEvents(), this);
        getServer().getPluginManager().registerEvents(new MenuEventListener(this), this);
        getServer().getPluginManager().registerEvents(new PartyListener(), this);
        getServer().getPluginManager().registerEvents(new BotListener(), this);
        getServer().getPluginManager().registerEvents(new RecklessCharge(), this);
        getServer().getPluginManager().registerEvents(new FutureMessageManager(), this);
        //getServer().getPluginManager().registerEvents(new NPCEvents(), this);

        new StartCommand().register(this);
        new EndgameCommand().register(this);
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

        updateHeads();

        readKeysConfig();
        readWeaponConfig();
        saveWeaponConfig();

        game = new Game();

        holographicDisplaysEnabled = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");

        Bukkit.getOnlinePlayers().forEach(player -> {
            playerScoreboards.put(player.getUniqueId(), new CustomScoreboard(player));
        });

        LeaderboardManager.init();

        //connects to the database
        Warlords.newChain()
                .async(DatabaseManager::connect)
                .execute();

        try {
            BotManager.connect();
        } catch (LoginException e) {
            e.printStackTrace();
        }

        ProtocolManager protocolManager;

        protocolManager = ProtocolLibrary.getProtocolManager();

        protocolManager.addPacketListener(
                new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Play.Server.WORLD_PARTICLES) {
                    int counter = 0;

                    @Override
                    public void onPacketSending(PacketEvent event) {
                        // Item packets (id: 0x29)
                        if (event.getPacketType() == PacketType.Play.Server.WORLD_PARTICLES) {
                            Player player = event.getPlayer();
                            if (Warlords.game.getPlayers().containsKey(player.getUniqueId())) {
                                if (counter++ % playerSettings.get(player.getUniqueId()).getParticleQuality().particleReduction == 0) {
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }
                });
//        citizensEnabled = Bukkit.getPluginManager().isPluginEnabled("Citizens");
//        npcCTFLocation = new LocationBuilder(Bukkit.getWorlds().get(0).getSpawnLocation())
//                .add(Bukkit.getWorlds().get(0).getSpawnLocation().getDirection().multiply(12))
//                .yaw(180)
//                .get();
//        if (citizensEnabled) {
//            CitizensAPI.getNPCRegistries().forEach(NPCRegistry::deregisterAll);
//            List<String> ctfInfo = new ArrayList<>();
//            ctfInfo.add(ChatColor.YELLOW + ChatColor.BOLD.toString() + "CLICK TO PLAY");
//            ctfInfo.add(ChatColor.AQUA + "Capture The Flag");
//            ctfInfo.add("");
//            ctfInfo.add(ChatColor.GRAY.toString() + game.playersCount() + " in Queue");
//            ctfInfo.add(ChatColor.YELLOW.toString() + game.playersCount() + " Players");
//            npcManager.createNPC(npcCTFLocation,
//                    UUID.fromString("28470830-94bf-20ce-a843-cb95a6235a2b"),
//                    "capture-the-flag",
//                    false,
//                    ctfInfo
//            );
//        }
        gameLoop();
        getServer().getScheduler().runTaskTimer(this, game, 1, 1);
        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] Plugin is enabled");
    }


    @Override
    public void onDisable() {
        game.clearAllPlayers();
        if (holographicDisplaysEnabled) {
            HologramsAPI.getHolograms(instance).forEach(Hologram::delete);
        }
        BotManager.jda.shutdownNow();
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[Warlords] Plugin is disabled");
        // TODO persist this.playerSettings to a database
    }

    public void gameLoop() {
        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                // EVERY TICK
                {
                    for (WarlordsPlayer wp : players.values()) {
                        if (wp.getGame().isGameFreeze()) {
                            continue;
                        }
                        if (wp.getName().equals("sumSmash")) {
                        }

                        // MOVEMENT
                        wp.getSpeed().updateSpeed();

                        CooldownManager cooldownManager = wp.getCooldownManager();
                        Player player = wp.getEntity() instanceof Player ? (Player) wp.getEntity() : null;

                        if (player != null) {
                            player.setCompassTarget(wp
                                    .getGameState()
                                    .flags()
                                    .get(wp.isTeamFlagCompass() ? wp.getTeam() : wp.getTeam().enemy())
                                    .getFlag()
                                    .getLocation()
                            );
                        }

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

                        //ABILITY COOLDOWN
                        if (wp.getSpec().getRed().getCurrentCooldown() > 0) {
                            wp.getSpec().getRed().subtractCooldown(.05f);
                            if (player != null) {
                                wp.updateRedItem(player);
                            }
                        }
                        if (wp.getSpec().getPurple().getCurrentCooldown() > 0) {
                            wp.getSpec().getPurple().subtractCooldown(.05f);
                            if (player != null) {
                                wp.updatePurpleItem(player);
                            }
                        }
                        if (wp.getSpec().getBlue().getCurrentCooldown() > 0) {
                            wp.getSpec().getBlue().subtractCooldown(.05f);
                            if (player != null) {
                                wp.updateBlueItem(player);
                            }
                        }
                        if (wp.getSpec().getOrange().getCurrentCooldown() > 0) {
                            wp.getSpec().getOrange().subtractCooldown(.05f);
                            if (player != null) {
                                wp.updateOrangeItem(player);
                            }
                        }
                        if (wp.getHorseCooldown() > 0 && !wp.getEntity().isInsideVehicle()) {
                            wp.setHorseCooldown(wp.getHorseCooldown() - .05f);
                            if (player != null) {
                                wp.updateHorseItem(player);
                            }
                        }

                        wp.getCooldownManager().reduceCooldowns();
                        if (player != null) {
                            //ACTION BAR
                            if (player.getInventory().getHeldItemSlot() != 8) {
                                wp.displayActionBar();
                            } else {
                                wp.displayFlagActionBar(player);
                            }
                        }
                        //respawn
                        if (wp.getRespawnTimer().doubleValue() == 0) {
                            wp.respawn();
                        }
                        BigDecimal respawn = wp.getRespawnTimer();
                        if (respawn.doubleValue() != -1) {
//                            System.out.println("-----------");
//                            System.out.println(warlordsPlayer.getGameState().getTimer() / 20.0);
//                            System.out.println(warlordsPlayer.getGameState().getTimer() / 20.0 % 12);
//                            System.out.println(warlordsPlayer.getRespawnTimer());
                            wp.setRespawnTimer(respawn.subtract(BigDecimal.valueOf(.05)));
//                            System.out.println(warlordsPlayer.getRespawnTimer());
                        }

                        boolean hasOverhealCooldown = wp.getCooldownManager().hasCooldown(Utils.OVERHEAL_MARKER);
                        boolean hasTooMuchHealth = wp.getHealth() > wp.getMaxHealth();

                        if (hasOverhealCooldown && !hasTooMuchHealth) {
                            wp.getCooldownManager().removeCooldown(Utils.OVERHEAL_MARKER);
                        }

                        if (!hasOverhealCooldown && hasTooMuchHealth) {
                            wp.setHealth(wp.getMaxHealth());
                        }

                        //damage or heal
                        float newHealth = (float) wp.getHealth() / wp.getMaxHealth() * 40;
                        //EVEN MORE PRECAUTIONS
                        if (newHealth < 0) {
                            newHealth = 0;
                        } else if (newHealth > 40) {
                            newHealth = 40;
                        }
                        //UNDYING ARMY
                        //check if player has any unpopped armies
                        if (wp.getCooldownManager().checkUndyingArmy(false) && newHealth <= 0) {
                            //set the first unpopped to popped

                            for (Cooldown cooldown : wp.getCooldownManager().getCooldown(UndyingArmy.class)) {
                                if (!((UndyingArmy) cooldown.getCooldownObject()).isArmyDead(wp.getUuid())) {
                                    ((UndyingArmy) cooldown.getCooldownObject()).pop(wp.getUuid());
                                    //DROPPING FLAG
                                    if (wp.getGameState().flags().hasFlag(wp)) {
                                        wp.getGameState().flags().dropFlag(wp);
                                    }
                                    //sending message + check if getFrom is self
                                    if (cooldown.getFrom() == wp) {
                                        wp.sendMessage("§a\u00BB§7 " + ChatColor.LIGHT_PURPLE + "Your Undying Army revived you with temporary health. Fight until your death! Your health will decay by " + ChatColor.RED + (wp.getMaxHealth() / 10) + ChatColor.LIGHT_PURPLE + " every second.");
                                    } else {
                                        wp.sendMessage("§a\u00BB§7 " + ChatColor.LIGHT_PURPLE + cooldown.getFrom().getName() + "'s Undying Army revived you with temporary health. Fight until your death! Your health will decay by " + ChatColor.RED + (wp.getMaxHealth() / 10) + ChatColor.LIGHT_PURPLE + " every second.");
                                    }
                                    Firework firework = wp.getWorld().spawn(wp.getLocation(), Firework.class);
                                    FireworkMeta meta = firework.getFireworkMeta();
                                    meta.addEffects(FireworkEffect.builder()
                                            .withColor(Color.LIME)
                                            .with(FireworkEffect.Type.BALL)
                                            .build());
                                    meta.setPower(0);
                                    firework.setFireworkMeta(meta);
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

                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            if (wp.getRespawnTimer().doubleValue() > 0) {
                                                this.cancel();
                                            } else {
                                                //UNDYING ARMY - dmg 10% of max health each popped army
                                                wp.damageHealth(wp, "", wp.getMaxHealth() / 10f, wp.getMaxHealth() / 10f, -1, 100, false);
                                            }
                                        }
                                    }.runTaskTimer(Warlords.this, 0, 20);

                                    break;
                                }
                            }
                        }
                        if (newHealth <= 0 && wp.getRespawnTimer().doubleValue() == -1) {
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
                            }
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
                            wp.giveRespawnTimer();
                            wp.addTotalRespawnTime();

                            wp.heal();
                        } else {
                            if (player != null) {
                                //precaution
                                if (newHealth >= 0 && newHealth <= 40) {
                                    player.setHealth(newHealth);
                                }
                            }
                        }

                        //respawn fix after leaving or stuck
                        if (player != null) {
                            if (wp.getHealth() <= 0 && player.getGameMode() == GameMode.SPECTATOR) {
                                wp.heal();
                            }
                            if (wp.getRespawnTimer().doubleValue() == -1 && player.getGameMode() == GameMode.SPECTATOR) {
                                wp.giveRespawnTimer();
                            }
                        }


                        //energy
                        if (wp.getEnergy() < wp.getMaxEnergy()) {
                            float energyGainPerTick = wp.getSpec().getEnergyPerSec() / 20f;
                            if (!cooldownManager.getCooldown(AvengersWrath.class).isEmpty()) {
                                energyGainPerTick += 1;
                            }
                            if (!cooldownManager.getCooldown(InspiringPresence.class).isEmpty()) {
                                energyGainPerTick += .5;
                            }
                            if (!cooldownManager.getCooldown(HolyRadiance.class).isEmpty()) {
                                energyGainPerTick += .25;
                            }
                            if (!cooldownManager.getCooldown(EnergyPowerUp.class).isEmpty()) {
                                energyGainPerTick *= 1.4;
                            }

                            float newEnergy = wp.getEnergy() + energyGainPerTick;
                            if (newEnergy > wp.getMaxEnergy()) {
                                newEnergy = wp.getMaxEnergy();
                            }

                            wp.setEnergy(newEnergy);
                        }

                        if (player != null) {
                            if (wp.getEnergy() < 0) {
                                wp.setEnergy(1);
                            }
                            player.setLevel((int) wp.getEnergy());
                            player.setExp(wp.getEnergy() / wp.getMaxEnergy());
                        }

                        //melee cooldown
                        if (wp.getHitCooldown() > 0) {
                            wp.setHitCooldown(wp.getHitCooldown() - 1);
                        }
                        //orbs
                        Location playerPosition = wp.getLocation();
                        List<OrbsOfLife.Orb> orbs = new ArrayList<>();
                        PlayerFilter.playingGame(wp.getGame()).teammatesOf(wp).forEach(p -> {
                            p.getCooldownManager().getCooldown(OrbsOfLife.class).forEach(cd -> {
                                orbs.addAll(((OrbsOfLife) cd.getCooldownObject()).getSpawnedOrbs());
                            });
                        });
                        Iterator<OrbsOfLife.Orb> itr = orbs.iterator();
                        while (itr.hasNext()) {
                            OrbsOfLife.Orb orb = itr.next();
                            Location orbPosition = orb.getArmorStand().getLocation();
                            if ((orb.getPlayerToMoveTowards() == null || (orb.getPlayerToMoveTowards() != null && orb.getPlayerToMoveTowards() == wp)) &&
                                    orbPosition.distanceSquared(playerPosition) < 1.35 * 1.35 && !wp.isDeath()) {
                                orb.remove();
                                itr.remove();

                                float minHeal = 250;
                                float maxHeal = 375;
                                if (Warlords.getPlayerSettings(orb.getOwner().getUuid()).getClassesSkillBoosts() == ClassesSkillBoosts.ORBS_OF_LIFE) {
                                    minHeal *= 1.2;
                                    maxHeal *= 1.2;
                                }
                                //BASE                           = 240 - 360
                                //BASE + WEAP BOOST              = 288 - 432 (x1.2)
                                //BASE + TIME LIVED              = 300 - 450 (x1.25 = 6.5 seconds)
                                //BASE + WEAP BOOST + TIME LIVED = 360 - 540 (x1.5 = x1.2 * x1.25)

                                //increasing heal for low long orb lived for (up to +25%)
                                //6.5 seconds = 130 ticks
                                //6.5 seconds = 1 + (130/520) = 1.25
                                //432 *= 1.25 = 540
                                //288 *= 1.25 = 360
                                if (orb.getPlayerToMoveTowards() == null) {
                                    minHeal *= 1 + orb.getTicksLived() / 520f;
                                    maxHeal *= 1 + orb.getTicksLived() / 520f;
                                }

                                wp.healHealth(orb.getOwner(), "Orbs of Life", maxHeal, maxHeal, -1, 100, false);
                                if (player != null) {
                                    for (Player player1 : player.getWorld().getPlayers()) {
                                        player1.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.5f, 1);
                                    }
                                }

                                for (WarlordsPlayer nearPlayer : PlayerFilter
                                        .entitiesAround(wp, 6, 6, 6)
                                        .aliveTeammatesOfExcludingSelf(wp)
                                        .limit(2)
                                ) {
                                    nearPlayer.healHealth(orb.getOwner(), "Orbs of Life", minHeal, minHeal, -1, 100, false);
                                    if (player != null) {
                                        for (Player player1 : player.getWorld().getPlayers()) {
                                            player1.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.5f, 1);
                                        }
                                    }
                                }
                            }
                            //8 seconds until orb expires
                            if (orb.getBukkitEntity().getTicksLived() > 160 || (orb.getPlayerToMoveTowards() != null && orb.getPlayerToMoveTowards().isDeath())) {
                                orb.remove();
                                itr.remove();
                            }
                        }

                        if (player != null) {
                            wp.setBlocksTravelledCM(Utils.getPlayerMovementStatistics(player));
                        }
                    }

                    //EVERY SECOND
                    if (counter % 20 == 0) {
                        RemoveEntities.removeHorsesInGame();
                        for (WarlordsPlayer warlordsPlayer : players.values()) {
                            if (warlordsPlayer.getGame().isGameFreeze()) {
                                continue;
                            }
                            Player player = warlordsPlayer.getEntity() instanceof Player ? (Player) warlordsPlayer.getEntity() : null;
                            //REGEN
                            if (warlordsPlayer.getRegenTimer() != 0) {
                                warlordsPlayer.setRegenTimer(warlordsPlayer.getRegenTimer() - 1);
                                if (warlordsPlayer.getRegenTimer() == 0) {
                                    warlordsPlayer.getHitBy().clear();
                                }
                            } else {
                                int healthToAdd = (int) (warlordsPlayer.getMaxHealth() / 55.3);
                                warlordsPlayer.setHealth(Math.max(warlordsPlayer.getHealth(),
                                                         Math.min(warlordsPlayer.getHealth() + healthToAdd,
                                                         warlordsPlayer.getMaxHealth()
                                                         )));
                            }
                            //RESPAWN DISPLAY
                            BigDecimal respawn = warlordsPlayer.getRespawnTimer();
                            if (respawn.doubleValue() != -1) {
                                if (respawn.doubleValue() <= 11) {
                                    if (player != null) {
                                        PacketUtils.sendTitle(player, "", warlordsPlayer.getTeam().teamColor() + "Respawning in... " + ChatColor.YELLOW + Math.round(respawn.doubleValue()), 0, 40, 0);
                                    }
                                }
                            }
                            //COOLDOWNS
                            if (warlordsPlayer.getSpawnProtection() > 0) {
                                warlordsPlayer.setSpawnProtection(warlordsPlayer.getSpawnProtection() - 1);
                            }
                            if (warlordsPlayer.getSpawnDamage() > 0) {
                                warlordsPlayer.setSpawnDamage(warlordsPlayer.getSpawnDamage() - 1);
                            }
                            if (warlordsPlayer.getFlagCooldown() > 0) {
                                warlordsPlayer.setFlagCooldown(warlordsPlayer.getFlagCooldown() - 1);
                            }
                            //SoulBinding - decrementing time left
                            warlordsPlayer.getCooldownManager().getCooldown(Soulbinding.class).stream()
                                    .map(Cooldown::getCooldownObject)
                                    .map(Soulbinding.class::cast)
                                    .forEach(soulbinding -> soulbinding.getSoulBindedPlayers().forEach(Soulbinding.SoulBoundPlayer::decrementTimeLeft));
                            //SoulBinding - removing bound players
                            warlordsPlayer.getCooldownManager().getCooldown(Soulbinding.class).stream()
                                    .map(Cooldown::getCooldownObject)
                                    .map(Soulbinding.class::cast)
                                    .forEach(soulbinding -> soulbinding.getSoulBindedPlayers()
                                            .removeIf(boundPlayer -> boundPlayer.getTimeLeft() == 0 || (boundPlayer.isHitWithSoul() && boundPlayer.isHitWithLink())));
                            if (warlordsPlayer.isPowerUpHeal()) {
                                int heal = (int) (warlordsPlayer.getMaxHealth() * .1);
                                if (warlordsPlayer.getHealth() + heal > warlordsPlayer.getMaxHealth()) {
                                    heal = warlordsPlayer.getMaxHealth() - warlordsPlayer.getHealth();
                                }
                                warlordsPlayer.setHealth(warlordsPlayer.getHealth() + heal);
                                warlordsPlayer.sendMessage("§a\u00BB §7Healed §a" + heal + " §7health.");

                                if (warlordsPlayer.getHealth() == warlordsPlayer.getMaxHealth()) {
                                    warlordsPlayer.setPowerUpHeal(false);
                                }
                            }

                            //COMBAT TIMER - counts dmg taken within 4 seconds
                            if (warlordsPlayer.getRegenTimer() > 6) {
                                warlordsPlayer.addTimeInCombat();
                            }

                            //ASSISTS - 10 SECOND COOLDOWN
                            warlordsPlayer.getHitBy().forEach(((wp, integer) -> warlordsPlayer.getHitBy().put(wp, integer - 1)));
                            warlordsPlayer.getHealedBy().forEach(((wp, integer) -> warlordsPlayer.getHealedBy().put(wp, integer - 1)));
                            warlordsPlayer.getHitBy().entrySet().removeIf(p -> p.getValue() <= 0);
                            warlordsPlayer.getHealedBy().entrySet().removeIf(p -> p.getValue() <= 0);

                            if (warlordsPlayer.getName().equals("sumSmash")) {

                            }
                        }
                        WarlordsEvents.entityList.removeIf(e -> !e.isValid());
                    }

                    //EVERY 2.5 SECONDS
                    if (counter % 50 == 0) {
                        for (WarlordsPlayer warlordsPlayer : players.values()) {
                            if (warlordsPlayer.getGame().isGameFreeze()) {
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
                }
                counter++;
            }

        }.runTaskTimer(this, 0, 0);
    }
}