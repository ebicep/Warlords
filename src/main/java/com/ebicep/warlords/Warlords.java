package com.ebicep.warlords;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.commands.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.events.WarlordsEvents;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.menu.MenuEventListener;
import com.ebicep.warlords.player.*;
import com.ebicep.warlords.powerups.EnergyPowerUp;
import com.ebicep.warlords.util.*;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    public static DatabaseManager databaseManager;
    public static boolean holographicDisplaysEnabled;
    public static boolean citizensEnabled;
    public static NPCManager npcManager = new NPCManager();

    public Location npcCTFLocation;

    private static final int SPAWN_PROTECTION_RADIUS = 5;

    @Override
    public void onEnable() {
        VERSION = this.getDescription().getVersion();
        ConfigurationSerialization.registerClass(PlayerSettings.class);
        taskChainFactory = BukkitTaskChainFactory.create(this);
        instance = this;
        getServer().getPluginManager().registerEvents(new WarlordsEvents(), this);
        getServer().getPluginManager().registerEvents(new MenuEventListener(this), this);
        //getServer().getPluginManager().registerEvents(new NPCEvents(), this);

        new StartCommand().register(this);
        new EndgameCommand().register(this);
        new MenuCommand().register(this);
        new ShoutCommand().register(this);
        new HotkeyModeCommand().register(this);
        new DebugCommand().register(this);
        new ClassCommand().register(this);
        new GetPlayersCommand().register(this);

        updateHeads();

        game = new Game();

        holographicDisplaysEnabled = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");

        //gets data then loads scoreboard then loads holograms (all callbacks i think)
        Warlords.newChain()
                .asyncFirst(() -> databaseManager = new DatabaseManager())
                .syncLast(input -> {
                    Bukkit.getOnlinePlayers().forEach(CustomScoreboard::giveMainLobbyScoreboard);
                    addHologramLeaderboards();
                })
                .execute();

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
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : getServer().getOnlinePlayers()) {
                    player.setFoodLevel(20);
                    player.setSaturation(2);
                }
            }
        }.runTaskTimer(this, 30, 90);

        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords]: Plugin is enabled");
    }


    @Override
    public void onDisable() {
        game.clearAllPlayers();
        if (holographicDisplaysEnabled) {
            HologramsAPI.getHolograms(instance).forEach(Hologram::delete);
        }
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[Warlords]: Plugin is disabled");
        // TODO persist this.playerSettings to a database
    }

    public static void addHologramLeaderboards() {
        if (databaseManager != null && databaseManager.isConnected() && holographicDisplaysEnabled) {
            HologramsAPI.getHolograms(instance).forEach(Hologram::delete);

            System.out.println("Adding Holograms");
            Location spawnPoint = Bukkit.getWorlds().get(0).getSpawnLocation().clone();
            Location lifeTimeWinsLB = new LocationBuilder(spawnPoint.clone()).forward(12).left(3).addY(6).get();
            Location lifeTimeKillsLB = new LocationBuilder(spawnPoint.clone()).forward(12).right(3).addY(6).get();
            Location srLB = new LocationBuilder(spawnPoint.clone()).forward(12).addY(10).get();
            Location srLBMage = new LocationBuilder(spawnPoint.clone()).backward(6).right(6).addY(6).left(7).get();
            Location srLBWarrior = new LocationBuilder(spawnPoint.clone()).backward(6).right(2).addY(6).left(7).get();
            Location srLBPaladin = new LocationBuilder(spawnPoint.clone()).backward(6).left(2).addY(6).left(7).get();
            Location srLBShaman = new LocationBuilder(spawnPoint.clone()).backward(6).left(6).addY(6).left(7).get();
            Warlords.newChain()
                    .asyncFirst(() -> databaseManager.getPlayersSortedByKey("wins"))
                    .abortIfNull()
                    .syncLast((topWinners) -> {
                        List<String> hologramLines = new ArrayList<>();
                        for (int i = 0; i < 10 && i < topWinners.size(); i++) {
                            Document player = topWinners.get(i);
                            hologramLines.add(ChatColor.YELLOW.toString() + (i + 1) + ". " + ChatColor.AQUA + player.get("name") + ChatColor.GRAY + " - " + ChatColor.YELLOW + (Utils.addCommaAndRound((Integer) player.get("wins"))));
                        }
                        createLeaderboard(lifeTimeWinsLB, ChatColor.AQUA + ChatColor.BOLD.toString() + "Lifetime Wins", hologramLines);
                    })
                    .execute();
            Warlords.newChain()
                    .asyncFirst(() -> databaseManager.getPlayersSortedByKey("kills"))
                    .abortIfNull()
                    .syncLast((topKillers) -> {
                        List<String> hologramLines = new ArrayList<>();
                        for (int i = 0; i < 10 && i < topKillers.size(); i++) {
                            Document player = topKillers.get(i);
                            hologramLines.add(ChatColor.YELLOW.toString() + (i + 1) + ". " + ChatColor.AQUA + player.get("name") + ChatColor.GRAY + " - " + ChatColor.YELLOW + (Utils.addCommaAndRound((Integer) player.get("kills"))));
                        }
                        createLeaderboard(lifeTimeKillsLB, ChatColor.AQUA + ChatColor.BOLD.toString() + "Lifetime Kills", hologramLines);
                    })
                    .execute();
            Warlords.newChain()
                    .asyncFirst(() -> databaseManager.getPlayersSortedBySR(""))
                    .abortIfNull()
                    .syncLast((topSR) -> {
                        createLeaderboard(srLB, ChatColor.AQUA + ChatColor.BOLD.toString() + "SR Ranking", getHologramLines(topSR));
                    })
                    .execute();
            Warlords.newChain()
                    .asyncFirst(() -> databaseManager.getPlayersSortedBySR("mage"))
                    .abortIfNull()
                    .syncLast((topSRMage) -> {
                        createLeaderboard(srLBMage, ChatColor.AQUA + ChatColor.BOLD.toString() + "Mage SR Ranking", getHologramLines(topSRMage));
                    })
                    .execute();
            Warlords.newChain()
                    .asyncFirst(() -> databaseManager.getPlayersSortedBySR("warrior"))
                    .abortIfNull()
                    .syncLast((topSRWarrior) -> {
                        createLeaderboard(srLBWarrior, ChatColor.AQUA + ChatColor.BOLD.toString() + "Warrior SR Ranking", getHologramLines(topSRWarrior));
                    })
                    .execute();
            Warlords.newChain()
                    .asyncFirst(() -> databaseManager.getPlayersSortedBySR("paladin"))
                    .abortIfNull()
                    .syncLast((topSRPaladin) -> {
                        createLeaderboard(srLBPaladin, ChatColor.AQUA + ChatColor.BOLD.toString() + "Paladin SR Ranking", getHologramLines(topSRPaladin));
                    })
                    .execute();
            Warlords.newChain()
                    .asyncFirst(() -> databaseManager.getPlayersSortedBySR("shaman"))
                    .abortIfNull()
                    .syncLast((topSRShaman) -> {
                        createLeaderboard(srLBShaman, ChatColor.AQUA + ChatColor.BOLD.toString() + "Shaman SR Ranking", getHologramLines(topSRShaman));
                    })
                    .execute();
        }
    }

    private static List<String> getHologramLines(HashMap<Document, Integer> players) {
        List<Document> sorted = getDocumentInSortedList(players);
        List<String> hologramLines = new ArrayList<>();
        for (int i = 0; i < 10 && i < sorted.size(); i++) {
            Document player = sorted.get(i);
            hologramLines.add(ChatColor.YELLOW.toString() + (i + 1) + ". " + ChatColor.AQUA + player.get("name") + ChatColor.GRAY + " - " + ChatColor.YELLOW + (Utils.addCommaAndRound(players.get(player))));
        }
        return hologramLines;
    }

    private static List<Document> getDocumentInSortedList(HashMap<Document, Integer> map) {
        List<Document> sorted = new ArrayList<>();
        map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(documentIntegerEntry -> sorted.add(documentIntegerEntry.getKey()));
        Collections.reverse(sorted);
        return sorted;
    }

    public static void createLeaderboard(Location location, String title, List<String> lines) {
        Hologram hologram = HologramsAPI.createHologram(instance, location);
        hologram.appendTextLine(title);
        hologram.appendTextLine("");
        for (String line : lines) {
            hologram.appendTextLine(line);
        }
        Bukkit.getServer().getConsoleSender().sendMessage("Created Hologram - " + title);
    }

    public void gameLoop() {
        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                // EVERY TICK
                {
                    // MOVEMENT
                    for (WarlordsPlayer warlordsPlayer : players.values()) {
                        warlordsPlayer.getSpeed().updateSpeed();
                    }

                    for (WarlordsPlayer warlordsPlayer : players.values()) {
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
                        if (warlordsPlayer.getHorseCooldown() != 0 && !warlordsPlayer.getEntity().isInsideVehicle()) {
                            warlordsPlayer.setHorseCooldown(warlordsPlayer.getHorseCooldown() - .05f);
                            if (player != null) {
                                warlordsPlayer.updateHorseItem(player);
                            }
                        }

                        warlordsPlayer.getCooldownManager().reduceCooldowns();

                        //respawn
                        if (warlordsPlayer.getRespawnTimer() == 0) {
                            warlordsPlayer.setRespawnTimer(-1);
                            warlordsPlayer.setSpawnProtection(10);
                            warlordsPlayer.setSpawnDamage(5);
                            warlordsPlayer.setDead(false);
                            Location respawnPoint = warlordsPlayer.getGame().getMap().getRespawn(warlordsPlayer.getTeam());
                            warlordsPlayer.teleport(respawnPoint);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    Location location = warlordsPlayer.getLocation();
                                    Location respawn = warlordsPlayer.getGame().getMap().getRespawn(warlordsPlayer.getTeam());
                                    if (
                                            location.getWorld() != respawn.getWorld() ||
                                                    location.distanceSquared(respawn) > SPAWN_PROTECTION_RADIUS * SPAWN_PROTECTION_RADIUS
                                    ) {
                                        warlordsPlayer.setSpawnProtection(0);
                                    }
                                    if (warlordsPlayer.getSpawnProtection() == 0) {
                                        this.cancel();
                                    }
                                }
                            }.runTaskTimer(instance, 0, 5);
                            warlordsPlayer.respawn();

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
                                if (!((UndyingArmy) cooldown.getCooldownObject()).isArmyDead()) {
                                    ((UndyingArmy) cooldown.getCooldownObject()).pop();
                                    //sending message + check if getFrom is self
                                    if (cooldown.getFrom() == warlordsPlayer) {
                                        warlordsPlayer.sendMessage("§a\u00BB§7 " + ChatColor.LIGHT_PURPLE + "Your Undying Army revived you with temporary health. Fight until your death! Your health will decay by " + ChatColor.RED + "500 " + ChatColor.LIGHT_PURPLE + "every second.");
                                    } else {
                                        warlordsPlayer.sendMessage("§a\u00BB§7 " + ChatColor.LIGHT_PURPLE + cooldown.getFrom().getName() + "'s Undying Army revived you with temporary health. Fight until your death! Your health will decay by " + ChatColor.RED + "500 " + ChatColor.LIGHT_PURPLE + "every second.");
                                    }
                                    Firework firework = warlordsPlayer.getWorld().spawn(warlordsPlayer.getLocation(), Firework.class);
                                    FireworkMeta meta = firework.getFireworkMeta();
                                    meta.addEffects(FireworkEffect.builder()
                                            .withColor(Color.LIME)
                                            .with(FireworkEffect.Type.BALL)
                                            .build());
                                    meta.setPower(0);
                                    firework.setFireworkMeta(meta);
                                    warlordsPlayer.respawn();

                                    if (player != null) {
                                        player.getWorld().spigot().strikeLightningEffect(warlordsPlayer.getLocation(), false);
                                        player.getInventory().setItem(5, UndyingArmy.BONE);
                                    }
                                    newHealth = 40;

                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            //UNDYING ARMY - dmg -500 each popped army
                                            warlordsPlayer.addHealth(warlordsPlayer, "", -500, -500, -1, 100);

                                            if (warlordsPlayer.getRespawnTimer() > 0) {
                                                this.cancel();
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
                                    if (killedBy == assisted) {
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
                            float newEnergy = warlordsPlayer.getEnergy() + warlordsPlayer.getSpec().getEnergyPerSec() / 20f;
                            if (!cooldownManager.getCooldown(AvengersWrath.class).isEmpty()) {
                                newEnergy += 1;
                            }
                            if (!cooldownManager.getCooldown(InspiringPresence.class).isEmpty()) {
                                newEnergy += .5;
                            }
                            if (!cooldownManager.getCooldown(EnergyPowerUp.class).isEmpty()) {
                                newEnergy += .35;
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
                            if (orbPosition.distanceSquared(playerPosition) < 1.75 * 1.75 && !warlordsPlayer.isDeath()) {
                                orb.remove();
                                itr.remove();

                                //504 302
                                if (Warlords.getPlayerSettings(orb.getOwner().getUuid()).classesSkillBoosts() == ClassesSkillBoosts.ORBS_OF_LIFE) {
                                    warlordsPlayer.addHealth(orb.getOwner(), "Orbs of Life", 420 * 1.2f, 420 * 1.2f, -1, 100);
                                } else {
                                    warlordsPlayer.addHealth(orb.getOwner(), "Orbs of Life", 420, 420, -1, 100);
                                }
                                for (WarlordsPlayer nearPlayer : PlayerFilter
                                        .entitiesAround(warlordsPlayer, 4, 4, 4)
                                        .aliveTeammatesOfExcludingSelf(warlordsPlayer)
                                        .limit(2)
                                ) {
                                    if (Warlords.getPlayerSettings(orb.getOwner().getUuid()).classesSkillBoosts() == ClassesSkillBoosts.ORBS_OF_LIFE) {
                                        nearPlayer.addHealth(orb.getOwner(), "Orbs of Life", 252 * 1.2f, 252 * 1.2f, -1, 100);
                                    } else {
                                        nearPlayer.addHealth(orb.getOwner(), "Orbs of Life", 252, 252, -1, 100);
                                    }
                                }
                            }
                            if (orb.getBukkitEntity().getTicksLived() > 160) {
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
                            Player player = warlordsPlayer.getEntity() instanceof Player ? (Player) warlordsPlayer.getEntity() : null;
                            if (player != null) {
                                //ACTION BAR
                                if (player.getInventory().getHeldItemSlot() != 8) {
                                    warlordsPlayer.displayActionBar();
                                } else {
                                    warlordsPlayer.displayFlagActionBar(player);
                                }
                            }
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
                            //RESPAWN
                            int respawn = warlordsPlayer.getRespawnTimer();
                            if (respawn != -1) {
                                if (respawn <= 6) {
                                    if (respawn == 1) {
                                        if (player != null) {
                                            PacketUtils.sendTitle(player, "", "", 0, 0, 0);
                                        }
                                    } else {
                                        if (player != null) {
                                            PacketUtils.sendTitle(player, "", warlordsPlayer.getTeam().teamColor() + "Respawning in... " + ChatColor.YELLOW + (respawn - 1), 0, 40, 0);
                                        }
                                    }
                                }
                                warlordsPlayer.setRespawnTimer(respawn - 1);
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
                }
                counter++;
            }

        }.runTaskTimer(this, 0, 0);
    }
}