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
import com.ebicep.warlords.database.LeaderboardCommand;
import com.ebicep.warlords.database.LeaderboardRanking;
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

            wp.getCooldownManager().clearCooldowns();
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

    private final static HashMap<UUID, Location> spawnPoints = new HashMap<>();

    @Nonnull
    public static Location getRejoinPoint(@Nonnull UUID key) {
        return spawnPoints.getOrDefault(key, Bukkit.getWorlds().get(0).getSpawnLocation());
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
            ItemStack playerSkull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
            SkullMeta skullMeta = (SkullMeta) playerSkull.getItemMeta();
            skullMeta.setOwner(onlinePlayer.getName());
            playerSkull.setItemMeta(skullMeta);
            playerHeads.put(onlinePlayer.getUniqueId(), CraftItemStack.asNMSCopy(playerSkull));
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

        updateHeads();

        game = new Game();

        holographicDisplaysEnabled = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");

        //gets data then loads scoreboard then loads holograms (all callbacks i think)
        Warlords.newChain()
                .asyncFirst(DatabaseManager::connect)
                .syncLast(input -> {
                    Bukkit.getOnlinePlayers().forEach(CustomScoreboard::giveMainLobbyScoreboard);
                    new LeaderboardRanking();
                    LeaderboardRanking.addHologramLeaderboards();
                })
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
                    for (WarlordsPlayer warlordsPlayer : players.values()) {
                        if (warlordsPlayer.getGame().isGameFreeze()) {
                            continue;
                        }
                        if (warlordsPlayer.getName().equals("sumSmash")) {
                        }

                        // MOVEMENT
                        warlordsPlayer.getSpeed().updateSpeed();

                        CooldownManager cooldownManager = warlordsPlayer.getCooldownManager();
                        Player player = warlordsPlayer.getEntity() instanceof Player ? (Player) warlordsPlayer.getEntity() : null;

                        if (player != null) {
                            player.setCompassTarget(warlordsPlayer
                                    .getGameState()
                                    .flags()
                                    .get(warlordsPlayer.isTeamFlagCompass() ? warlordsPlayer.getTeam() : warlordsPlayer.getTeam().enemy())
                                    .getFlag()
                                    .getLocation()
                            );
                        }

                        if (warlordsPlayer.isDisableCooldowns()) {
                            warlordsPlayer.getSpec().getRed().setCurrentCooldown(0);
                            warlordsPlayer.getSpec().getPurple().setCurrentCooldown(0);
                            warlordsPlayer.getSpec().getBlue().setCurrentCooldown(0);
                            warlordsPlayer.getSpec().getOrange().setCurrentCooldown(0);
                            warlordsPlayer.setHorseCooldown(0);
                            warlordsPlayer.updateRedItem();
                            warlordsPlayer.updatePurpleItem();
                            warlordsPlayer.updateBlueItem();
                            warlordsPlayer.updateOrangeItem();
                            warlordsPlayer.updateHorseItem();
                        }

                        //ABILITY COOLDOWN
                        if (warlordsPlayer.getSpec().getRed().getCurrentCooldown() > 0) {
                            warlordsPlayer.getSpec().getRed().subtractCooldown(.05f);
                            if (player != null) {
                                warlordsPlayer.updateRedItem(player);
                            }
                        }
                        if (warlordsPlayer.getSpec().getPurple().getCurrentCooldown() > 0) {
                            warlordsPlayer.getSpec().getPurple().subtractCooldown(.05f);
                            if (player != null) {
                                warlordsPlayer.updatePurpleItem(player);
                            }
                        }
                        if (warlordsPlayer.getSpec().getBlue().getCurrentCooldown() > 0) {
                            warlordsPlayer.getSpec().getBlue().subtractCooldown(.05f);
                            if (player != null) {
                                warlordsPlayer.updateBlueItem(player);
                            }
                        }
                        if (warlordsPlayer.getSpec().getOrange().getCurrentCooldown() > 0) {
                            warlordsPlayer.getSpec().getOrange().subtractCooldown(.05f);
                            if (player != null) {
                                warlordsPlayer.updateOrangeItem(player);
                            }
                        }
                        if (warlordsPlayer.getHorseCooldown() > 0 && !warlordsPlayer.getEntity().isInsideVehicle()) {
                            warlordsPlayer.setHorseCooldown(warlordsPlayer.getHorseCooldown() - .05f);
                            if (player != null) {
                                warlordsPlayer.updateHorseItem(player);
                            }
                        }

                        warlordsPlayer.getCooldownManager().reduceCooldowns();
                        if (player != null) {
                            //ACTION BAR
                            if (player.getInventory().getHeldItemSlot() != 8) {
                                warlordsPlayer.displayActionBar();
                            } else {
                                warlordsPlayer.displayFlagActionBar(player);
                            }
                        }
                        //respawn
                        if (Math.abs(warlordsPlayer.getRespawnTimer()) < .1 || warlordsPlayer.getRespawnTimer() < -1) {
                            warlordsPlayer.respawn();
                        }
                        float respawn = warlordsPlayer.getRespawnTimer();
                        if (respawn != -1) {
                            warlordsPlayer.setRespawnTimer(respawn - .05f);
                        }
                        //damage or heal
                        float newHealth = (float) warlordsPlayer.getHealth() / warlordsPlayer.getMaxHealth() * 40;
                        //EVEN MORE PRECAUTIONS
                        if (newHealth < 0) {
                            newHealth = 0;
                        } else if (newHealth > 40) {
                            newHealth = 40;
                        }
                        //UNDYING ARMY
                        //check if player has any unpopped armies
                        if (warlordsPlayer.getCooldownManager().checkUndyingArmy(false) && newHealth <= 0) {
                            //set the first unpopped to popped

                            for (Cooldown cooldown : warlordsPlayer.getCooldownManager().getCooldown(UndyingArmy.class)) {
                                if (!((UndyingArmy) cooldown.getCooldownObject()).isArmyDead(warlordsPlayer.getUuid())) {
                                    //DROPPING FLAG
                                    if (warlordsPlayer.getGameState().flags().hasFlag(warlordsPlayer)) {
                                        warlordsPlayer.getGameState().flags().dropFlag(warlordsPlayer);
                                    }
                                    ((UndyingArmy) cooldown.getCooldownObject()).pop(warlordsPlayer.getUuid());
                                    //sending message + check if getFrom is self
                                    if (cooldown.getFrom() == warlordsPlayer) {
                                        warlordsPlayer.sendMessage("§a\u00BB§7 " + ChatColor.LIGHT_PURPLE + "Your Undying Army revived you with temporary health. Fight until your death! Your health will decay by " + ChatColor.RED + (warlordsPlayer.getMaxHealth() / 10) + ChatColor.LIGHT_PURPLE + " every second.");
                                    } else {
                                        warlordsPlayer.sendMessage("§a\u00BB§7 " + ChatColor.LIGHT_PURPLE + cooldown.getFrom().getName() + "'s Undying Army revived you with temporary health. Fight until your death! Your health will decay by " + ChatColor.RED + (warlordsPlayer.getMaxHealth() / 10) + ChatColor.LIGHT_PURPLE + " every second.");
                                    }
                                    Firework firework = warlordsPlayer.getWorld().spawn(warlordsPlayer.getLocation(), Firework.class);
                                    FireworkMeta meta = firework.getFireworkMeta();
                                    meta.addEffects(FireworkEffect.builder()
                                            .withColor(Color.LIME)
                                            .with(FireworkEffect.Type.BALL)
                                            .build());
                                    meta.setPower(0);
                                    firework.setFireworkMeta(meta);
                                    warlordsPlayer.heal();

                                    if (player != null) {
                                        player.getWorld().spigot().strikeLightningEffect(warlordsPlayer.getLocation(), false);
                                        player.getInventory().setItem(5, UndyingArmy.BONE);
                                    }
                                    newHealth = 40;

                                    //gives 50% of max energy if player is less than half
                                    if (warlordsPlayer.getEnergy() < warlordsPlayer.getMaxEnergy() / 2) {
                                        warlordsPlayer.setEnergy(warlordsPlayer.getMaxEnergy() / 2);
                                    }

                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            if (warlordsPlayer.getRespawnTimer() > 0) {
                                                this.cancel();
                                            } else {
                                                //UNDYING ARMY - dmg 10% of max health each popped army
                                                warlordsPlayer.addHealth(warlordsPlayer, "", warlordsPlayer.getMaxHealth() / -10f, warlordsPlayer.getMaxHealth() / -10f, -1, 100, false);
                                            }
                                        }
                                    }.runTaskTimer(Warlords.this, 0, 20);

                                    break;
                                }
                            }
                        }
                        if (newHealth <= 0 && warlordsPlayer.getRespawnTimer() == -1) {
                            //checking if all undying armies are popped (this should never be true as last if statement bypasses this) then removing all boners
                            if (!warlordsPlayer.getCooldownManager().checkUndyingArmy(false)) {
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
                            int lastElementIndex = warlordsPlayer.getHitBy().size() - 1;
                            WarlordsPlayer killedBy = warlordsPlayer.getHitBy().entrySet().stream().skip(lastElementIndex).iterator().next().getKey();
                            final int[] counter = {0};
                            warlordsPlayer.getHitBy().forEach((assisted, value) -> {
                                if (counter[0] != lastElementIndex) {
                                    if (killedBy == assisted || killedBy == warlordsPlayer) {
                                        assisted.sendMessage(
                                                ChatColor.GRAY +
                                                        "You assisted in killing " +
                                                        warlordsPlayer.getColoredName()
                                        );
                                    } else {
                                        assisted.sendMessage(
                                                ChatColor.GRAY +
                                                        "You assisted " +
                                                        killedBy.getColoredName() +
                                                        ChatColor.GRAY + " in killing " +
                                                        warlordsPlayer.getColoredName()
                                        );
                                    }
                                    assisted.addAssist();
                                    assisted.getScoreboard().updateKillsAssists();
                                }
                                counter[0]++;
                            });
                            warlordsPlayer.getHitBy().clear();
                            warlordsPlayer.setRegenTimer(0);
                            warlordsPlayer.giveRespawnTimer();
                            warlordsPlayer.addTotalRespawnTime();

                            warlordsPlayer.heal();
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
                            if (warlordsPlayer.getHealth() <= 0 && player.getGameMode() == GameMode.SPECTATOR) {
                                warlordsPlayer.heal();
                            }
                            if (warlordsPlayer.getRespawnTimer() == -1 && player.getGameMode() == GameMode.SPECTATOR) {
                                warlordsPlayer.giveRespawnTimer();
                            }
                        }


                        //energy
                        if (warlordsPlayer.getEnergy() < warlordsPlayer.getMaxEnergy()) {
                            float energyGainPerTick = warlordsPlayer.getSpec().getEnergyPerSec() / 20f;
                            if (!cooldownManager.getCooldown(AvengersWrath.class).isEmpty()) {
                                energyGainPerTick += 1;
                            }
                            if (!cooldownManager.getCooldown(InspiringPresence.class).isEmpty()) {
                                energyGainPerTick += .5;
                            }
                            if (!cooldownManager.getCooldown(EnergyPowerUp.class).isEmpty()) {
                                energyGainPerTick *= 1.4;
                            }

                            float newEnergy = warlordsPlayer.getEnergy() + energyGainPerTick;
                            if (newEnergy > warlordsPlayer.getMaxEnergy()) {
                                newEnergy = warlordsPlayer.getMaxEnergy();
                            }

                            warlordsPlayer.setEnergy(newEnergy);
                        }

                        if (player != null) {
                            if (warlordsPlayer.getEnergy() < 0) {
                                warlordsPlayer.setEnergy(1);
                            }
                            player.setLevel((int) warlordsPlayer.getEnergy());
                            player.setExp(warlordsPlayer.getEnergy() / warlordsPlayer.getMaxEnergy());
                        }

                        //melee cooldown
                        if (warlordsPlayer.getHitCooldown() > 0) {
                            warlordsPlayer.setHitCooldown(warlordsPlayer.getHitCooldown() - 1);
                        }
                        //orbs
                        Location playerPosition = warlordsPlayer.getLocation();
                        List<OrbsOfLife.Orb> orbs = new ArrayList<>();
                        PlayerFilter.playingGame(warlordsPlayer.getGame()).teammatesOf(warlordsPlayer).forEach(p -> {
                            p.getCooldownManager().getCooldown(OrbsOfLife.class).forEach(cd -> {
                                orbs.addAll(((OrbsOfLife) cd.getCooldownObject()).getSpawnedOrbs());
                            });
                        });
                        Iterator<OrbsOfLife.Orb> itr = orbs.iterator();
                        while (itr.hasNext()) {
                            OrbsOfLife.Orb orb = itr.next();
                            Location orbPosition = orb.getArmorStand().getLocation();
                            if ((orb.getPlayerToMoveTowards() == null || (orb.getPlayerToMoveTowards() != null && orb.getPlayerToMoveTowards() == warlordsPlayer)) &&
                                    orbPosition.distanceSquared(playerPosition) < 1.35 * 1.35 && !warlordsPlayer.isDeath()) {
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
                                /*if (orb.getPlayerToMoveTowards() == null) {
                                    minHeal *= 1 + orb.getTicksLived() / 520f;
                                    maxHeal *= 1 + orb.getTicksLived() / 520f;
                                }*/

                                warlordsPlayer.addHealth(orb.getOwner(), "Orbs of Life", maxHeal, maxHeal, -1, 100, false);
                                if (player != null) {
                                    for (Player player1 : player.getWorld().getPlayers()) {
                                        player1.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.5f, 1);
                                    }
                                }

                                for (WarlordsPlayer nearPlayer : PlayerFilter
                                        .entitiesAround(warlordsPlayer, 6, 6, 6)
                                        .aliveTeammatesOfExcludingSelf(warlordsPlayer)
                                        .limit(2)
                                ) {
                                    nearPlayer.addHealth(orb.getOwner(), "Orbs of Life", minHeal, minHeal, -1, 100, false);
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
                            warlordsPlayer.setBlocksTravelledCM(Utils.getPlayerMovementStatistics(player));
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
                                warlordsPlayer.setHealth(Math.min(warlordsPlayer.getHealth() + healthToAdd, warlordsPlayer.getMaxHealth()));
                            }
                            //RESPAWN DISPLAY
                            float respawn = warlordsPlayer.getRespawnTimer();
                            if (respawn != -1) {
                                if (respawn <= 11) {
                                    if (player != null) {
                                        PacketUtils.sendTitle(player, "", warlordsPlayer.getTeam().teamColor() + "Respawning in... " + ChatColor.YELLOW + Math.round(respawn), 0, 40, 0);
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