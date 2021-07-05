package com.ebicep.warlords;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.commands.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.events.WarlordsEvents;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.menu.MenuEventListener;
import com.ebicep.warlords.player.CooldownManager;
import com.ebicep.warlords.player.PlayerSettings;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.powerups.EnergyPowerUp;
import com.ebicep.warlords.util.PacketUtils;
import com.ebicep.warlords.util.RemoveEntities;
import com.ebicep.warlords.util.Utils;
import org.bukkit.*;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Warlords extends JavaPlugin {

    private static Warlords instance;

    public static Warlords getInstance() {
        return instance;
    }

    public static List<OrbsOfLife.Orb> orbs = new ArrayList<>();

    public static List<OrbsOfLife.Orb> getOrbs() {
        return orbs;
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

            wp.getCooldownManager().clear();
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

    public static int blueKills;
    public static int redKills;

    private int counter = 0;

    //TODO remove static EVERYWHERE FUCK ME
    public static Game game;
    public static DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(PlayerSettings.class);
        instance = this;
        getServer().getPluginManager().registerEvents(new WarlordsEvents(), this);
        getServer().getPluginManager().registerEvents(new MenuEventListener(this), this);

        new StartCommand().register(this);
        new EndgameCommand().register(this);
        new MenuCommand().register(this);
        new ShoutCommand().register(this);
        new HotkeyModeCommand().register(this);
        new DebugCommand().register(this);

        game = new Game();
        getData();

        startTask();
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
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[Warlords]: Plugin is disabled");
        // TODO persist this.playerSettings to a database
    }

    public void getData() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    databaseManager = new DatabaseManager();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(this);
    }

    public void startTask() {
        new BukkitRunnable() {

            @Override
            public void run() {
                RemoveEntities.removeHorsesInGame();
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
                            Location location = player.getLocation();
                            player.setCompassTarget(warlordsPlayer
                                    .getGameState()
                                    .flags()
                                    .get(warlordsPlayer.isTeamFlagCompass() ? warlordsPlayer.getTeam() : warlordsPlayer.getTeam().enemy())
                                    .getFlag()
                                    .getLocation()
                            );
                            //dismount directly downwards
                            if (player.isSneaking() && player.getVehicle() != null) {
                                player.getVehicle().remove();
                            }

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
                            Location respawnPoint = game.getMap().getRespawn(warlordsPlayer.getTeam());
                            warlordsPlayer.teleport(respawnPoint);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (player.getLocation().distanceSquared(game.getMap().getBlueRespawn()) > 5 * 5) {
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
                        if (warlordsPlayer.getCooldownManager().getCooldown(UndyingArmy.class).size() > 0 && newHealth <= 0) {
                            if (warlordsPlayer.getCooldownManager().getCooldown(UndyingArmy.class).get(0).getFrom() == warlordsPlayer) {
                                warlordsPlayer.sendMessage("§a\u00BB§7 " + ChatColor.LIGHT_PURPLE + "Your Undying Army revived you with temporary health. Fight until your death! Your health will decay by " + ChatColor.RED + "500 " + ChatColor.LIGHT_PURPLE + "every second.");

                                Firework firework = warlordsPlayer.getWorld().spawn(warlordsPlayer.getLocation(), Firework.class);
                                FireworkMeta meta = firework.getFireworkMeta();
                                meta.addEffects(FireworkEffect.builder().withColor(Color.LIME)
                                        .with(FireworkEffect.Type.BALL)
                                        .build());
                                meta.setPower(0);
                                firework.setFireworkMeta(meta);

                                player.getWorld().spigot().strikeLightningEffect(warlordsPlayer.getLocation(), false);
                            } else {
                                warlordsPlayer.sendMessage("§a\u00BB§7 " + ChatColor.LIGHT_PURPLE + warlordsPlayer.getCooldownManager().getCooldown(UndyingArmy.class).get(0).getFrom().getName() + "'s Undying Army revived you with temporary health. Fight until your death! Your health will decay by " + ChatColor.RED + "500 " + ChatColor.LIGHT_PURPLE + "every second.");

                                Firework firework = warlordsPlayer.getWorld().spawn(warlordsPlayer.getLocation(), Firework.class);
                                FireworkMeta meta = firework.getFireworkMeta();
                                meta.addEffects(FireworkEffect.builder().withColor(Color.LIME)
                                        .with(FireworkEffect.Type.BALL)
                                        .build());
                                meta.setPower(0);
                                firework.setFireworkMeta(meta);

                                player.getWorld().spigot().strikeLightningEffect(warlordsPlayer.getLocation(), false);
                            }
                            warlordsPlayer.respawn();
                            warlordsPlayer.setUndyingArmyDead(true);
                            warlordsPlayer.getCooldownManager().getCooldowns().remove(warlordsPlayer.getCooldownManager().getCooldown(UndyingArmy.class).get(0));
                            if (player != null) {
                                player.getInventory().setItem(5, UndyingArmy.BONE);
                            }
                            newHealth = 40;
                        }
                        if (newHealth <= 0) {
                            if (warlordsPlayer.isUndyingArmyDead()) {
                                warlordsPlayer.setUndyingArmyDead(false);
                                if (player != null) {
                                    player.getInventory().remove(UndyingArmy.BONE);
                                }
                            }
                            // warlordsPlayer.respawn();
                            if (player != null) {
                                player.setGameMode(GameMode.SPECTATOR);
                            }
                            //giving out assists
                            for (int i = 1; i < warlordsPlayer.getHitBy().size(); i++) {
                                WarlordsPlayer assisted = warlordsPlayer.getHitBy().get(i);
                                if (warlordsPlayer.getHitBy().get(0) == warlordsPlayer) {
                                    assisted.sendMessage(
                                            ChatColor.GRAY +
                                                    "You assisted in killing " +
                                                    warlordsPlayer.getColoredName()
                                    );
                                } else {
                                    assisted.sendMessage(
                                            ChatColor.GRAY +
                                                    "You assisted " +
                                                    ChatColor.BLUE +
                                                    warlordsPlayer.getHitBy().get(0).getColoredName() +
                                                    ChatColor.GRAY + " in killing " +
                                                    ChatColor.RED + warlordsPlayer.getName()
                                    );
                                }
                                assisted.addAssist();
                                assisted.getScoreboard().updateKillsAssists();
                            }
                            //respawn timer
                            int respawn = warlordsPlayer.getGameState().getTimerInSeconds() % 12;
                            if (respawn <= 4) {
                                respawn += 12;
                            }
                            warlordsPlayer.setRespawnTimer(respawn);
                        } else {
                            if (player != null) {
                                player.setHealth(newHealth);
                            }
                        }

                        //energy
                        if (warlordsPlayer.getEnergy() < warlordsPlayer.getMaxEnergy()) {
                            float newEnergy = warlordsPlayer.getEnergy() + warlordsPlayer.getSpec().getEnergyPerSec() / 20f;
                            if (cooldownManager.getCooldown(AvengersWrath.class).size() > 0) {
                                newEnergy += 1;
                            }
                            if (cooldownManager.getCooldown(InspiringPresence.class).size() > 0) {
                                newEnergy += .5;
                            }
                            if (cooldownManager.getCooldown(EnergyPowerUp.class).size() > 0) {
                                newEnergy += .35;
                            }
                            warlordsPlayer.setEnergy(newEnergy);
                        }

                        if (player != null) {
                            player.setLevel((int) warlordsPlayer.getEnergy());
                            player.setExp(warlordsPlayer.getEnergy() / warlordsPlayer.getMaxEnergy());
                        }

                        //melee cooldown
                        if (warlordsPlayer.getHitCooldown() > 0) {
                            warlordsPlayer.setHitCooldown(warlordsPlayer.getHitCooldown() - 1);
                        }
                        //orbs
                        Location playerPosition = warlordsPlayer.getLocation();
                        Iterator<OrbsOfLife.Orb> itr = orbs.iterator();
                        while (itr.hasNext()) {
                            OrbsOfLife.Orb orb = itr.next();
                            Location orbPosition = orb.getArmorStand().getLocation();
                            if (orb.getOwner().isTeammate(warlordsPlayer) && orbPosition.distanceSquared(playerPosition) < 1.75 * 1.75) {
                                orb.getArmorStand().remove();
                                orb.getBukkitEntity().remove();
                                itr.remove();
                                warlordsPlayer.addHealth(warlordsPlayer, "Orbs of Life", 502, 502, -1, 100);
                                Utils.filterOnlyTeammates(player, 3, 3, 3, player)
                                    .forEach((nearPlayer) -> {
                                    nearPlayer.addHealth(warlordsPlayer, "Orbs of Life", 420, 420, -1, 100);
                                });
                            }
                            if (orb.getBukkitEntity().getTicksLived() > 160) {
                                orb.getArmorStand().remove();
                                orb.getBukkitEntity().remove();
                                itr.remove();
                            }
                        }
                    }

                    //EVERY SECOND
                    if (counter % 20 == 0) {
                        for (WarlordsPlayer warlordsPlayer : players.values()) {
                            Player player = warlordsPlayer.getEntity() instanceof Player ? (Player)warlordsPlayer.getEntity() : null;
                            if (player != null) {
                                //ACTION BAR
                                if (player.getInventory().getHeldItemSlot() != 8) {
                                    warlordsPlayer.displayActionBar();
                                } else {
                                    warlordsPlayer.displayFlagActionBar(player);
                                }
                            }
                            //REGEN
                            if (warlordsPlayer.getRegenTimer() != -1) {
                                warlordsPlayer.setRegenTimer(warlordsPlayer.getRegenTimer() - 1);
                                if (warlordsPlayer.getRegenTimer() == 0) {
                                    warlordsPlayer.getHitBy().clear();
                                }
                            } else {
                                int healthToAdd = (int) (warlordsPlayer.getMaxHealth() / 55.3);
                                if (warlordsPlayer.getHealth() + healthToAdd >= warlordsPlayer.getMaxHealth()) {
                                    warlordsPlayer.setHealth(warlordsPlayer.getMaxHealth());
                                } else {
                                    warlordsPlayer.setHealth(warlordsPlayer.getHealth() + (int) (warlordsPlayer.getMaxHealth() / 55.3));
                                }
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
                            if (warlordsPlayer.isUndyingArmyDead()) {
                                warlordsPlayer.addHealth(warlordsPlayer, "", -500, -500, -1, 100);
                            }


                            for (int i = 0; i < warlordsPlayer.getSoulBindedPlayers().size(); i++) {
                                Soulbinding.SoulBoundPlayer soulBoundPlayer = warlordsPlayer.getSoulBindedPlayers().get(0);
                                soulBoundPlayer.setTimeLeft(soulBoundPlayer.getTimeLeft() - 1);
                                if (soulBoundPlayer.getTimeLeft() == 0 || (soulBoundPlayer.isHitWithLink() && soulBoundPlayer.isHitWithSoul())) {
                                    warlordsPlayer.getSoulBindedPlayers().remove(i);
                                    i--;
                                }
                            }
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
                        }

                    }
                }
                counter++;
            }

        }.runTaskTimer(this, 0, 0);
    }
}