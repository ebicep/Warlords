package com.ebicep.warlords;

import com.ebicep.warlords.classes.abilties.OrbsOfLife;
import com.ebicep.warlords.classes.abilties.Soulbinding;
import com.ebicep.warlords.classes.abilties.UndyingArmy;
import com.ebicep.warlords.commands.Commands;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.events.WarlordsEvents;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.menu.MenuEventListener;
import com.ebicep.warlords.util.PacketUtils;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.RemoveEntities;
import com.ebicep.warlords.util.Utils;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
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
                    player.setSaturation(1);
                }
            }

        }.runTaskTimer(this, 50, 200);

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
                RemoveEntities.onRemove();
                if (game.getState() == Game.State.GAME) {
                    // EVERY TICK

                    // MOVEMENT
                    for (WarlordsPlayer warlordsPlayer : players.values()) {
                        Player player = warlordsPlayer.getPlayer();
                        warlordsPlayer.getSpeed().updateSpeed();

                        // light infusion
                        if (warlordsPlayer.getInfusion() != 0) {
                            warlordsPlayer.setInfusion((int) (warlordsPlayer.getInfusion() - 0.05));
                        }

                        // presence
                        if (warlordsPlayer.getPresence() != 0) {
                            if (warlordsPlayer.getInfusion() == 0)
                                warlordsPlayer.setPresence((int) (warlordsPlayer.getPresence() - 0.05));
                            List<Entity> near = player.getNearbyEntities(6.0D, 2.0D, 6.0D);
                            near = Utils.filterOnlyTeammates(near, player);
                            for (Entity entity : near) {
                                if (entity instanceof Player) {
                                    Player nearPlayer = (Player) entity;
                                }
                            }
                        }

                        //freezingbreath
                        if (warlordsPlayer.getBreathSlowness() != 0) {
                            warlordsPlayer.setBreathSlowness((int) (warlordsPlayer.getBreathSlowness() - 0.05));
                            List<Entity> near = player.getNearbyEntities(6.0D, 2.0D, 6.0D);
                            near = Utils.filterOutTeammates(near, player);
                            for (Entity entity : near) {
                                if (entity instanceof Player) {
                                    Player nearPlayer = (Player) entity;
                                }
                            }
                        }

                        // frostbolt
                        if (warlordsPlayer.getFrostbolt() != 0) {
                            warlordsPlayer.setFrostbolt((int) (warlordsPlayer.getFrostbolt() - 0.05));
                            List<Entity> near = player.getNearbyEntities(6.0D, 2.0D, 6.0D);
                            near = Utils.filterOutTeammates(near, player);
                            for (Entity entity : near) {
                                if (entity instanceof Player) {
                                    Player nearPlayer = (Player) entity;
                                }
                            }
                        }

                        // berserk
                        if (warlordsPlayer.getBerserk() != 0) {
                            //berserk same speed as presence 30%
                            warlordsPlayer.setBerserk((int) (warlordsPlayer.getBerserk() - 0.05));
                        }

                        // spiritlink
                        if (warlordsPlayer.getSpiritLink() != 0) {
                            warlordsPlayer.setSpiritLink((int) (warlordsPlayer.getSpiritLink() - 0.05));
                        }

                        // ice barrier
                        if (warlordsPlayer.getIceBarrier() != 0) {
                            warlordsPlayer.setIceBarrier((int) (warlordsPlayer.getIceBarrier() - 0.05));
                        }

                        // ice barrier slowness duration
                        if (warlordsPlayer.getIceBarrierSlowness() != 0) {
                            warlordsPlayer.setIceBarrierSlowness((int) (warlordsPlayer.getIceBarrierSlowness() - 0.05));
                            warlordsPlayer.getSpeed().changeCurrentSpeed("Ice Barrier", -20, 2 * 20);
                        }
                    }

                    for (WarlordsPlayer warlordsPlayer : players.values()) {
                        Player player = warlordsPlayer.getPlayer();
                        Location location = player.getLocation();

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
                        if (warlordsPlayer.getSpec().getRed().getCurrentCooldown() != 0) {
                            warlordsPlayer.getSpec().getRed().subtractCooldown(.05f);
                            warlordsPlayer.updateRedItem();
                        }
                        if (warlordsPlayer.getSpec().getPurple().getCurrentCooldown() != 0) {
                            warlordsPlayer.getSpec().getPurple().subtractCooldown(.05f);
                            warlordsPlayer.updatePurpleItem();
                        }
                        if (warlordsPlayer.getSpec().getBlue().getCurrentCooldown() != 0) {
                            warlordsPlayer.getSpec().getBlue().subtractCooldown(.05f);
                            warlordsPlayer.updateBlueItem();
                        }
                        if (warlordsPlayer.getSpec().getOrange().getCurrentCooldown() != 0) {
                            warlordsPlayer.getSpec().getOrange().subtractCooldown(.05f);
                            warlordsPlayer.updateOrangeItem();
                        }
                        if (warlordsPlayer.getHorseCooldown() != 0 && !player.isInsideVehicle()) {
                            warlordsPlayer.setHorseCooldown(warlordsPlayer.getHorseCooldown() - .05f);
                            warlordsPlayer.updateHorseItem();
                        }

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
                        if (warlordsPlayer.getUndyingArmyDuration() != 0 && newHealth <= 0) {
                            if (warlordsPlayer.getUndyingArmyBy() == warlordsPlayer) {
                                warlordsPlayer.getPlayer().sendMessage("§a\u00BB§7 " + ChatColor.LIGHT_PURPLE + "Your Undying Army revived you with temporary health. Fight until your death! Your health will decay by " + ChatColor.RED + "500 " + ChatColor.LIGHT_PURPLE + "every second.");
                            } else {
                                warlordsPlayer.getPlayer().sendMessage("§a\u00BB§7 " + ChatColor.LIGHT_PURPLE + warlordsPlayer.getUndyingArmyBy().getName() + "'s Undying Army revived you with temporary health. Fight until your death! Your health will decay by " + ChatColor.RED + "500 " + ChatColor.LIGHT_PURPLE + "every second.");
                            }
                            warlordsPlayer.respawn();
                            warlordsPlayer.setUndyingArmyDead(true);
                            warlordsPlayer.setUndyingArmyDuration(0);
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

                        if (warlordsPlayer.getInterveneDuration() != 0 && (warlordsPlayer.getInterveneDamage() >= 3600 || warlordsPlayer.getIntervenedBy().isDead() || (warlordsPlayer.getIntervenedBy() != null && warlordsPlayer.getPlayer().getLocation().distanceSquared(warlordsPlayer.getIntervenedBy().getPlayer().getLocation()) > 15 * 15))) {
                            //TODO seperate and add why the vene broke in chat
                            warlordsPlayer.setInterveneDuration(0);
                            warlordsPlayer.getPlayer().sendMessage("§c\u00AB§7 " + warlordsPlayer.getIntervenedBy().getName() + "'s " + ChatColor.YELLOW + "Intervene " + ChatColor.GRAY + "has expired!");

                        }
                        //energy
                        if (warlordsPlayer.getPlayer().getGameMode() != GameMode.CREATIVE) {
                            if (warlordsPlayer.getEnergy() < warlordsPlayer.getMaxEnergy()) {
                                float newEnergy = warlordsPlayer.getEnergy() + warlordsPlayer.getSpec().getEnergyPerSec() / 20f;
                                if (warlordsPlayer.getWrathDuration() != 0) {
                                    newEnergy += 1;
                                }
                                if (warlordsPlayer.getPresence() != 0) {
                                    newEnergy += .5;
                                }
                                if (warlordsPlayer.getPowerUpEnergy() != 0) {
                                    newEnergy += .35;
                                }
                                warlordsPlayer.setEnergy(newEnergy);
                            }
                            player.setLevel((int) warlordsPlayer.getEnergy());
                            player.setExp(warlordsPlayer.getEnergy() / warlordsPlayer.getMaxEnergy());
                        }
                        //melee cooldown
                        if (warlordsPlayer.getHitCooldown() != 0) {
                            warlordsPlayer.setHitCooldown(warlordsPlayer.getHitCooldown() - 1);
                        }
                        //orbs
                        for (int i = 0; i < orbs.size(); i++) {
                            OrbsOfLife.Orb orb = orbs.get(i);
                            Location orbPosition = orb.getBukkitEntity().getLocation();
                            if (game.onSameTeam(orb.getOwner(), warlordsPlayer) && orbPosition.distanceSquared(location) < 1.75 * 1.75) {
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

                    // PARTICLES - FOUR TICK MODULE
                    if (counter % 4 == 0) {
                        for (WarlordsPlayer warlordsPlayer : players.values()) {
                            Player player = warlordsPlayer.getPlayer();

                            // Arcane Shield
                            if (warlordsPlayer.getArcaneShield() != 0) {
                                Location location = player.getLocation();
                                location.add(0, 1.5, 0);
                                ParticleEffect.CLOUD.display(0.15F, 0.3F, 0.15F, 0.01F, 2, location, 500);
                                ParticleEffect.FIREWORKS_SPARK.display(0.3F, 0.3F, 0.3F, 0.0001F, 1, location, 500);
                                ParticleEffect.SPELL_WITCH.display(0.3F, 0.3F, 0.3F, 0.001F, 1, location, 500);
                            }

                            // Blood Lust
                            if (warlordsPlayer.getBloodLustDuration() != 0) {
                                Location location = player.getLocation();
                                location.add((Math.random() - 0.5) * 1, 1.2, (Math.random() - 0.5) * 1);
                                ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(255, 0, 0), location, 500);
                            }

                            // Earthliving
                            if (warlordsPlayer.getEarthlivingDuration() != 0) {
                                Location location = player.getLocation();
                                location.add(0, 1.2, 0);
                                ParticleEffect.VILLAGER_HAPPY.display(0.3F, 0.3F, 0.3F, 0.1F, 3, location, 500);
                            }

                            // Wrath
                            if (warlordsPlayer.getWrathDuration() != 0) {
                                Location location = player.getLocation();
                                location.add(0, 1.2, 0);
                                ParticleEffect.SPELL.display(0.3F, 0.1F, 0.3F, 0.2F, 6, location, 500);
                            }

                            // Windfury
                            if (warlordsPlayer.getWindfuryDuration() != 0) {
                                Location location = player.getLocation();
                                location.add(0, 1.2, 0);
                                ParticleEffect.CRIT.display(0.2F, 0F, 0.2F, 0.1F, 3, location, 500);
                            }

                            // Soulbinding Weapon
                            if (warlordsPlayer.getSoulBindCooldown() != 0) {
                                Location location = player.getLocation();
                                location.add(0, 1.2, 0);
                                ParticleEffect.SPELL_WITCH.display(0.2F, 0F, 0.2F, 0.1F, 1, location, 500);
                            }
                        }
                    }

                    // PARTICLES - TWO TICK MODULE
                    if (counter % 2 == 0) {
                        for (WarlordsPlayer warlordsPlayer : players.values()) {
                            //UPDATES SCOREBOARD HEALTHS
                            warlordsPlayer.getScoreboard().updateHealths();
                            Player player = warlordsPlayer.getPlayer();

                            // Inferno
                            if (warlordsPlayer.getInferno() != 0) {
                                Location location = player.getLocation();
                                location.add(0, 1.2, 0);
                                ParticleEffect.DRIP_LAVA.display(0.5F, 0.3F, 0.5F, 0.4F, 1, location, 500);
                                ParticleEffect.FLAME.display(0.5F, 0.3F, 0.5F, 0.0001F, 1, location, 500);
                                ParticleEffect.CRIT.display(0.5F, 0.3F, 0.5F, 0.0001F, 1, location, 500);
                            }

                            // Ice Barrier
                            if (warlordsPlayer.getIceBarrier() != 0) {
                                Location location = player.getLocation();
                                location.add(0, 1.5, 0);
                                ParticleEffect.CLOUD.display(0.2F, 0.2F, 0.2F, 0.001F, 1, location, 500);
                                ParticleEffect.FIREWORKS_SPARK.display(0.3F, 0.2F, 0.3F, 0.0001F, 1, location, 500);
                            }

                            // Berserk
                            if (warlordsPlayer.getBerserk() != 0) {
                                Location location = player.getLocation();
                                location.add(0, 2.1, 0);
                                ParticleEffect.VILLAGER_ANGRY.display(0, 0, 0, 0.1F, 1, location, 500);
                            }

                            // Infusion
                            if (warlordsPlayer.getInfusion() != 0) {
                                Location location = player.getLocation();
                                location.add(0, 1.2, 0);
                                ParticleEffect.SPELL.display(0.3F, 0.1F, 0.3F, 0.2F, 2, location, 500);
                            }

                            // Presence
                            if (warlordsPlayer.getPresence() != 0) {
                                Location location = player.getLocation();
                                location.add(0, 1.5, 0);
                                ParticleEffect.SMOKE_NORMAL.display(0.3F, 0.3F, 0.3F, 0.02F, 1, location, 500);
                                ParticleEffect.SPELL.display(0.3F, 0.3F, 0.3F, 0.5F, 2, location, 500);
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
                            if (warlordsPlayer.getSpawnProtection() != 0) {
                                warlordsPlayer.setSpawnProtection(warlordsPlayer.getSpawnProtection() - 1);
                            }
                            if (warlordsPlayer.getSpawnDamage() != 0) {
                                warlordsPlayer.setSpawnDamage(warlordsPlayer.getSpawnDamage() - 1);
                            }
                            if (warlordsPlayer.getWrathDuration() != 0) {
                                warlordsPlayer.setWrathDuration(warlordsPlayer.getWrathDuration() - 1);
                            }
                            if (warlordsPlayer.getFlagCooldown() != 0) {
                                warlordsPlayer.setFlagCooldown(warlordsPlayer.getFlagCooldown() - 1);
                            }
                            if (warlordsPlayer.getBloodLustDuration() != 0) {
                                warlordsPlayer.setBloodLustDuration(warlordsPlayer.getBloodLustDuration() - 1);
                            }
                            if (warlordsPlayer.getInterveneDuration() != 0) {
                                if (warlordsPlayer.getInterveneDuration() != 1) {
                                    if (warlordsPlayer.getInterveneDuration() == 2)
                                        warlordsPlayer.getPlayer().sendMessage("§a\u00BB§7 " + warlordsPlayer.getIntervenedBy().getName() + "'s §eIntervene §7will expire in §6" + (warlordsPlayer.getInterveneDuration() - 1) + "§7 second!");
                                    else
                                        warlordsPlayer.getPlayer().sendMessage("§a\u00BB§7 " + warlordsPlayer.getIntervenedBy().getName() + "'s §eIntervene §7will expire in §6" + (warlordsPlayer.getInterveneDuration() - 1) + "§7 seconds!");
                                }
                                warlordsPlayer.setInterveneDuration(warlordsPlayer.getInterveneDuration() - 1);
                                if (warlordsPlayer.getInterveneDuration() == 0) {
                                    warlordsPlayer.getPlayer().sendMessage("§c\u00AB§7 " + warlordsPlayer.getIntervenedBy().getName() + "'s §eIntervene §7has expired!");
                                }
                            }
                            if (warlordsPlayer.getLastStandDuration() != 0) {
                                warlordsPlayer.setLastStandDuration(warlordsPlayer.getLastStandDuration() - 1);
                            }
                            if (warlordsPlayer.getOrbsOfLifeDuration() != 0) {
                                warlordsPlayer.setOrbsOfLifeDuration(warlordsPlayer.getOrbsOfLifeDuration() - 1);
                            }
                            if (warlordsPlayer.getUndyingArmyDuration() != 0 && !warlordsPlayer.isUndyingArmyDead()) {
                                warlordsPlayer.setUndyingArmyDuration(warlordsPlayer.getUndyingArmyDuration() - 1);
                                if (warlordsPlayer.getUndyingArmyDuration() == 0) {
                                    int healing = (int) ((warlordsPlayer.getMaxHealth() - warlordsPlayer.getHealth()) * .35 + 200);
                                    warlordsPlayer.addHealth(warlordsPlayer.getUndyingArmyBy(), "Undying Army", healing, healing, -1, 100);
                                }
                            } else if (warlordsPlayer.isUndyingArmyDead()) {
                                warlordsPlayer.addHealth(warlordsPlayer, "", -500, -500, -1, 100);
                            }
                            if (warlordsPlayer.getWindfuryDuration() != 0) {
                                warlordsPlayer.setWindfuryDuration(warlordsPlayer.getWindfuryDuration() - 1);
                            }
                            if (warlordsPlayer.getEarthlivingDuration() != 0) {
                                warlordsPlayer.setEarthlivingDuration(warlordsPlayer.getEarthlivingDuration() - 1);
                            }

                            if (warlordsPlayer.getBerserkerWounded() != 0) {
                                warlordsPlayer.setBerserkerWounded(warlordsPlayer.getBerserkerWounded() - 1);
                            }
                            if (warlordsPlayer.getDefenderWounded() != 0) {
                                warlordsPlayer.setDefenderWounded(warlordsPlayer.getDefenderWounded() - 1);
                            }
                            if (warlordsPlayer.getCrippled() != 0) {
                                warlordsPlayer.setCrippled(warlordsPlayer.getCrippled() - 1);
                            }
                            if (warlordsPlayer.getRepentanceDuration() != 0) {
                                warlordsPlayer.setRepentanceDuration(warlordsPlayer.getRepentanceDuration() - 1);
                            }
                            if (warlordsPlayer.getRepentanceCounter() != 0) {
                                int newRepentanceCounter = (int) (warlordsPlayer.getRepentanceCounter() * .8 - 60);
                                warlordsPlayer.setRepentanceCounter(Math.max(newRepentanceCounter, 0));
                            }
                            if (warlordsPlayer.getArcaneShield() != 0) {
                                warlordsPlayer.setArcaneShield(warlordsPlayer.getArcaneShield() - 1);
                                if (warlordsPlayer.getArcaneShield() == 0) {
                                    ((EntityLiving) ((CraftPlayer) player).getHandle()).setAbsorptionHearts(0);
                                }
                            }
                            if (warlordsPlayer.getInferno() != 0) {
                                warlordsPlayer.setInferno(warlordsPlayer.getInferno() - 1);
                            }
                            if (warlordsPlayer.getChainLightningCooldown() != 0) {
                                warlordsPlayer.setChainLightningCooldown(warlordsPlayer.getChainLightningCooldown() - 1);
                            }
                            if (warlordsPlayer.getSoulBindCooldown() != 0) {
                                warlordsPlayer.setSoulBindCooldown(warlordsPlayer.getSoulBindCooldown() - 1);
                            }
                            for (int i = 0; i < warlordsPlayer.getSoulBindedPlayers().size(); i++) {
                                Soulbinding.SoulBoundPlayer soulBoundPlayer = warlordsPlayer.getSoulBindedPlayers().get(0);
                                soulBoundPlayer.setTimeLeft(soulBoundPlayer.getTimeLeft() - 1);
                                if (soulBoundPlayer.getTimeLeft() == 0 || (soulBoundPlayer.isHitWithLink() && soulBoundPlayer.isHitWithSoul())) {
                                    warlordsPlayer.getSoulBindedPlayers().remove(i);
                                    i--;
                                }
                            }
                            if (warlordsPlayer.getPowerUpDamage() != 0) {
                                warlordsPlayer.setPowerUpDamage(warlordsPlayer.getPowerUpDamage() - 1);
                            }
                            if (warlordsPlayer.getPowerUpEnergy() != 0) {
                                warlordsPlayer.setPowerUpEnergy(warlordsPlayer.getPowerUpEnergy() - 1);
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
                            if (warlordsPlayer.getPowerUpSpeed() != 0) {
                                warlordsPlayer.setPowerUpSpeed(warlordsPlayer.getPowerUpSpeed() - 1);
                            }

                        }
                    }
                    counter++;
                }
            }

        }.runTaskTimer(this, 0, 0);
    }
}