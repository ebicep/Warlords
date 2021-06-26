package com.ebicep.warlords;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.commands.Commands;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.events.WarlordsEvents;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.menu.MenuEventListener;
import com.ebicep.warlords.player.CooldownManager;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.powerups.EnergyPowerUp;
import com.ebicep.warlords.util.PacketUtils;
import com.ebicep.warlords.util.RemoveEntities;
import com.ebicep.warlords.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private static final HashMap<Player, WarlordsPlayer> players = new HashMap<>();

    public static void addPlayer(WarlordsPlayer warlordsPlayer) {
        players.put(warlordsPlayer.getPlayer(), warlordsPlayer);
    }

    public static WarlordsPlayer getPlayer(Player player) {
        return players.get(player);
    }

    public static boolean hasPlayer(Player player) {
        return players.containsKey(player);
    }

    public static HashMap<Player, WarlordsPlayer> getPlayers() {
        return players;
    }

    public static int blueKills;
    public static int redKills;

    private int counter = 0;

    //TODO remove static EVERYWHERE FUCK ME
    public static Game game;
    public static DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new WarlordsEvents(), this);
        getServer().getPluginManager().registerEvents(new MenuEventListener(this), this);
        Commands commands = new Commands();
        getCommand("start").setExecutor(commands);
        getCommand("endgame").setExecutor(commands);
        getCommand("class").setExecutor(commands);
        getCommand("menu").setExecutor(commands);
        getCommand("shout").setExecutor(commands);
        getCommand("hotkeymode").setExecutor(commands);
        getCommand("hitbox").setExecutor(commands);
        getCommand("speed").setExecutor(commands);

        getCommand("start").setTabCompleter(commands);
        getCommand("class").setTabCompleter(commands);

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
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[Warlords]: Plugin is disabled");
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
                if (game.getState() == Game.State.GAME) {
                    // EVERY TICK

                    // MOVEMENT
                    for (WarlordsPlayer warlordsPlayer : players.values()) {
                        warlordsPlayer.getSpeed().updateSpeed();
                        warlordsPlayer.getScoreboard().updateHealths();
                    }

                    for (WarlordsPlayer warlordsPlayer : players.values()) {
                        Player player = warlordsPlayer.getPlayer();
                        Location location = player.getLocation();
                        CooldownManager cooldownManager = warlordsPlayer.getCooldownManager();

                        //dismount directly downwards
                        if (player.isSneaking() && player.getVehicle() != null) {
                            player.getVehicle().remove();
                        }

                        if (game.isRedTeam(player)) {
                            if (warlordsPlayer.isTeamFlagCompass()) {
                                player.setCompassTarget(game.getFlags().getRed().getFlag().getLocation());
                            } else {
                                player.setCompassTarget(game.getFlags().getBlue().getFlag().getLocation());
                            }
                        } else if (game.isBlueTeam(player)) {
                            if (warlordsPlayer.isTeamFlagCompass()) {
                                player.setCompassTarget(game.getFlags().getBlue().getFlag().getLocation());
                            } else {
                                player.setCompassTarget(game.getFlags().getRed().getFlag().getLocation());
                            }
                        }


                        //ABILITY COOLDOWN
                        if (warlordsPlayer.getSpec().getRed().getCurrentCooldown() > 0) {
                            warlordsPlayer.getSpec().getRed().subtractCooldown(.05f);
                            warlordsPlayer.updateRedItem();
                        }
                        if (warlordsPlayer.getSpec().getPurple().getCurrentCooldown() > 0) {
                            warlordsPlayer.getSpec().getPurple().subtractCooldown(.05f);
                            warlordsPlayer.updatePurpleItem();
                        }
                        if (warlordsPlayer.getSpec().getBlue().getCurrentCooldown() > 0) {
                            warlordsPlayer.getSpec().getBlue().subtractCooldown(.05f);
                            warlordsPlayer.updateBlueItem();
                        }
                        if (warlordsPlayer.getSpec().getOrange().getCurrentCooldown() > 0) {
                            warlordsPlayer.getSpec().getOrange().subtractCooldown(.05f);
                            warlordsPlayer.updateOrangeItem();
                        }
                        if (warlordsPlayer.getHorseCooldown() > 0 && !player.isInsideVehicle()) {
                            warlordsPlayer.setHorseCooldown(warlordsPlayer.getHorseCooldown() - .05f);
                            warlordsPlayer.updateHorseItem();
                        }

                        warlordsPlayer.getCooldownManager().reduceCooldowns();

                        //respawn
                        if (warlordsPlayer.getRespawnTimer() == 0) {
                            warlordsPlayer.setRespawnTimer(-1);
                            warlordsPlayer.setSpawnProtection(10);
                            warlordsPlayer.setSpawnDamage(5);
                            warlordsPlayer.setDead(false);

                            if (game.getTeamBlueProtected().contains(warlordsPlayer.getPlayer())) {
                                warlordsPlayer.getPlayer().teleport(game.getMap().getBlueRespawn());
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
                            } else if (game.getTeamRedProtected().contains(warlordsPlayer.getPlayer())) {
                                warlordsPlayer.getPlayer().teleport(game.getMap().getRedRespawn());
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        if (player.getLocation().distanceSquared(game.getMap().getRedRespawn()) > 5 * 5) {
                                            warlordsPlayer.setSpawnProtection(0);
                                        }
                                        if (warlordsPlayer.getSpawnProtection() == 0) {
                                            this.cancel();
                                        }
                                    }
                                }.runTaskTimer(instance, 0, 5);
                            }
                            warlordsPlayer.respawn();
                            if (warlordsPlayer.getDeathStand() != null) {
                                warlordsPlayer.getDeathStand().remove();
                                warlordsPlayer.setDeathStand(null);
                            }
                            Location deathLocation = warlordsPlayer.getDeathLocation();
                            if (deathLocation != null) {
                                Block deathBlock = deathLocation.getBlock();
                                if (deathBlock.getType() == Material.SAPLING) {
                                    deathBlock.setType(Material.AIR);
                                }
                                warlordsPlayer.setDeathLocation(null);
                            }
                            player.setGameMode(GameMode.ADVENTURE);
                        }
                        //damage or heal
                        float newHealth = (float) warlordsPlayer.getHealth() / warlordsPlayer.getMaxHealth() * 40;
                        if (warlordsPlayer.getCooldownManager().getCooldown(UndyingArmy.class).size() > 0 && newHealth <= 0) {
                            if (warlordsPlayer.getCooldownManager().getCooldown(UndyingArmy.class).get(0).getFrom() == warlordsPlayer) {
                                warlordsPlayer.getPlayer().sendMessage("§a\u00BB§7 " + ChatColor.LIGHT_PURPLE + "Your Undying Army revived you with temporary health. Fight until your death! Your health will decay by " + ChatColor.RED + "500 " + ChatColor.LIGHT_PURPLE + "every second.");
                            } else {
                                warlordsPlayer.getPlayer().sendMessage("§a\u00BB§7 " + ChatColor.LIGHT_PURPLE + warlordsPlayer.getCooldownManager().getCooldown(UndyingArmy.class).get(0).getFrom().getName() + "'s Undying Army revived you with temporary health. Fight until your death! Your health will decay by " + ChatColor.RED + "500 " + ChatColor.LIGHT_PURPLE + "every second.");
                            }
                            warlordsPlayer.respawn();
                            warlordsPlayer.setUndyingArmyDead(true);
                            warlordsPlayer.getCooldownManager().getCooldowns().remove(warlordsPlayer.getCooldownManager().getCooldown(UndyingArmy.class).get(0));
                            warlordsPlayer.getPlayer().getInventory().setItem(5, UndyingArmy.BONE);
                            newHealth = 40;
                        }
                        if (newHealth <= 0) {
                            if (warlordsPlayer.isUndyingArmyDead()) {
                                warlordsPlayer.setUndyingArmyDead(false);
                                warlordsPlayer.getPlayer().getInventory().remove(UndyingArmy.BONE);
                            }
                            warlordsPlayer.respawn();
                            player.setGameMode(GameMode.SPECTATOR);
                            //giving out assists
                            for (int i = 1; i < warlordsPlayer.getHitBy().size(); i++) {
                                WarlordsPlayer assisted = warlordsPlayer.getHitBy().get(i);
                                if (warlordsPlayer.getHitBy().get(0).getPlayer() == player) {
                                    if (game.isBlueTeam(player)) {
                                        assisted.getPlayer().sendMessage(ChatColor.GRAY + "You assisted in killing " + ChatColor.BLUE + warlordsPlayer.getName());
                                    } else if (game.isRedTeam(player)) {
                                        assisted.getPlayer().sendMessage(ChatColor.GRAY + "You assisted in killing " + ChatColor.RED + warlordsPlayer.getName());
                                    }
                                } else {
                                    if (game.isBlueTeam(warlordsPlayer.getHitBy().get(0).getPlayer())) {
                                        assisted.getPlayer().sendMessage(ChatColor.GRAY + "You assisted " + ChatColor.BLUE + warlordsPlayer.getHitBy().get(0).getName() + ChatColor.GRAY + " in killing " + ChatColor.RED + warlordsPlayer.getName());
                                    } else if (game.isRedTeam(warlordsPlayer.getHitBy().get(0).getPlayer())) {
                                        assisted.getPlayer().sendMessage(ChatColor.GRAY + "You assisted " + ChatColor.RED + warlordsPlayer.getHitBy().get(0).getName() + ChatColor.GRAY + " in killing " + ChatColor.BLUE + warlordsPlayer.getName());
                                    }
                                }
                                assisted.addAssist();
                                assisted.getScoreboard().updateKillsAssists();
                            }
                            //respawn timer
                            int respawn = game.getScoreboardSecond() % 12;
                            if (respawn <= 4) {
                                respawn += 12;
                            }
                            warlordsPlayer.setRespawnTimer(respawn);
                        } else {
                            player.setHealth(newHealth);
                        }

                        //energy
                        if (warlordsPlayer.getPlayer().getGameMode() != GameMode.CREATIVE) {
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
                            player.setLevel((int) warlordsPlayer.getEnergy());
                            player.setExp(warlordsPlayer.getEnergy() / warlordsPlayer.getMaxEnergy());
                        }
                        //melee cooldown
                        if (warlordsPlayer.getHitCooldown() > 0) {
                            warlordsPlayer.setHitCooldown(warlordsPlayer.getHitCooldown() - 1);
                        }
                        //orbs
                        for (int i = 0; i < orbs.size(); i++) {
                            OrbsOfLife.Orb orb = orbs.get(i);
                            Location orbPosition = orb.getArmorStand().getLocation();
                            if (game.onSameTeam(orb.getOwner(), warlordsPlayer) && orbPosition.distanceSquared(location) < 1.5 * 1.5) {
                                orb.getArmorStand().remove();
                                orb.getBukkitEntity().remove();
                                orbs.remove(i);
                                i--;
                                warlordsPlayer.addHealth(warlordsPlayer, "Orbs of Life", 502, 502, -1, 100);
                                List<Entity> near = player.getNearbyEntities(3.0D, 3.0D, 3.0D);
                                near = Utils.filterOnlyTeammates(near, player);
                                for (Entity entity : near) {
                                    if (entity instanceof Player) {
                                        Player nearPlayer = (Player) entity;
                                        if (nearPlayer.getGameMode() != GameMode.SPECTATOR) {
                                            getPlayer(nearPlayer).addHealth(warlordsPlayer, "Orbs of Life", 420, 420, -1, 100);
                                        }
                                    }
                                }
                            }
                            if (orb.getBukkitEntity().getTicksLived() > 160) {
                                orb.getArmorStand().remove();
                                orb.getBukkitEntity().remove();
                                orbs.remove(i);
                                i--;
                            }
                        }
                    }

                    //EVERY SECOND
                    if (counter % 20 == 0) {
                        for (WarlordsPlayer warlordsPlayer : players.values()) {
                            warlordsPlayer.getScoreboard().updateTime();
                            //ACTION BAR
                            if (warlordsPlayer.getPlayer().getInventory().getHeldItemSlot() != 8) {
                                warlordsPlayer.displayActionBar();
                            } else {
                                warlordsPlayer.displayFlagActionBar();
                            }
                            Player player = warlordsPlayer.getPlayer();
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
                                        PacketUtils.sendTitle(player, "", "", 0, 0, 0);
                                    } else {
                                        if (game.isBlueTeam(player)) {
                                            PacketUtils.sendTitle(player, "", ChatColor.BLUE + "Respawning in... " + ChatColor.YELLOW + (respawn - 1), 0, 40, 0);
                                        } else {
                                            PacketUtils.sendTitle(player, "", ChatColor.RED + "Respawning in... " + ChatColor.YELLOW + (respawn - 1), 0, 40, 0);
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
                                warlordsPlayer.getPlayer().sendMessage("§a\u00BB §7Healed §a" + heal + " §7health.");

                                if (warlordsPlayer.getHealth() == warlordsPlayer.getMaxHealth()) {
                                    warlordsPlayer.setPowerUpHeal(false);
                                }
                            }

                        }
                    }
                    counter++;
                }
            }

        }.runTaskTimer(this, 0, 0);
    }
}