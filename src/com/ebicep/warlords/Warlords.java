package com.ebicep.warlords;

import com.ebicep.warlords.classes.abilties.EarthenSpikeBlock;
import com.ebicep.warlords.commands.StartGame;
import com.ebicep.warlords.events.WarlordsEvents;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.text.SimpleDateFormat;
import java.util.*;

public class Warlords extends JavaPlugin {

    private static Warlords instance;

    public static Warlords getInstance() {
        return instance;
    }

    static ArrayList<ArmorStand> armorStands = new ArrayList<>(new ArrayList<>());

    private static HashMap<Player, WarlordsPlayer> players = new HashMap<>();
    public static void addPlayer(WarlordsPlayer warlordsPlayer) {
        players.put(warlordsPlayer.getPlayer(),warlordsPlayer);
    }
    public static WarlordsPlayer getPlayer(Player player) {
        return players.get(player);
    }
    public static boolean hasPlayer(Player player) {
        return players.containsKey(player);
    }


    static World world = Bukkit.getWorld("world");

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new WarlordsEvents(), this);
        Objects.requireNonNull(getCommand("start")).setExecutor(new StartGame());

        if (world != null) {
            runnable();
            runnable2();
            runnable3();
        }
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords]: Plugin is enabled");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[Warlords]: Plugin is disabled");

    }

    public void runnable() {
        new BukkitRunnable() {

            @Override
            public void run() {
                //all earthen spikes +1 when right click
                if(WarlordsEvents.getSpikes().size() != 0) {
                    for (int i = 0; i < WarlordsEvents.getSpikes().size(); i++) {
                        //earthen spike BLOCk array
                        List<ArrayList<EarthenSpikeBlock>> spikes = WarlordsEvents.getSpikes().get(i).getSpikeArrays();
                        //block
                        if(spikes.size() != 0) {
                            ArrayList<EarthenSpikeBlock> spike = spikes.get(0);
                            Player player = spike.get(spike.size() - 1).getPlayer();
                            FallingBlock block = spike.get(spike.size() - 1).getBlock();
                            if (Math.abs(player.getLocation().getX() - block.getLocation().getX()) + Math.abs(player.getLocation().getX() - block.getLocation().getX()) > 1) {
                                Location location = block.getLocation();
                                if (Math.abs(player.getLocation().getX() - location.getX()) >= Math.abs(player.getLocation().getZ() - location.getZ())) {
                                    if (player.getLocation().getX() < block.getLocation().getX()) {
                                        location.add(-1, 0, 0);
                                    } else {
                                        location.add(1, 0, 0);
                                    }
                                } else {
                                    if (player.getLocation().getZ() < block.getLocation().getZ()) {
                                        location.add(0, 0, -1);
                                    } else {
                                        location.add(0, 0, 1);
                                    }
                                }
                                location.setY(location.getWorld().getHighestBlockYAt(location));

                                FallingBlock newBlock = player.getWorld().spawnFallingBlock(location, location.getWorld().getBlockAt((int) location.getX(), location.getWorld().getHighestBlockYAt(location) - 1, (int) location.getZ()).getType(), location.getWorld().getBlockAt((int) location.getX(), location.getWorld().getHighestBlockYAt(location) - 1, (int) location.getZ()).getData());
                                newBlock.setVelocity(new Vector(0, .2, 0));
                                newBlock.setDropItem(false);
                                spike.add(new EarthenSpikeBlock(newBlock, player));
                                WarlordsEvents.addEntityUUID(newBlock.getUniqueId());
                            } else if (i <= spikes.size() && spikes.get(i).size() > 30) {
                                WarlordsEvents.getSpikes().remove(i);
                            } else {
                                Bukkit.broadcastMessage("HIT");
                                Location location = player.getLocation();
                                location.setYaw(0);
                                location.setY(player.getWorld().getHighestBlockYAt(location));
                                ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location.add(0, -.6, 0), EntityType.ARMOR_STAND);
                                //stand.setRightArmPose(new EulerAngle(100, 4.7, 3.675));
                                stand.setHelmet(new ItemStack(Material.BROWN_MUSHROOM));
                                stand.setGravity(false);
                                stand.setVisible(false);

                                armorStands.add(stand);
                                if (armorStands.size() == 1) {
                                    player.setVelocity(new Vector(0, .6, 0));
                                }

                                WarlordsEvents.getSpikes().remove(i);
                                i--;
                            }
                        }
                    }
                }
                if (armorStands.size() != 0) {
                    for (int i = 0; i < armorStands.size(); i++) {
                        ArmorStand armorStand = armorStands.get(i);
                        if (armorStand.getTicksLived() > 10) {
                            armorStand.remove();
                            armorStands.remove(i);
                            i--;
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0, 2);
    }

    public void runnable2() {
        new BukkitRunnable() {

            @Override
            public void run() {
                ScoreboardManager manager = Bukkit.getScoreboardManager();
                Scoreboard board = manager.getNewScoreboard();


                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                String dateString = format.format(new Date());

                Objective objective = board.registerNewObjective(dateString, "");
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                objective.setDisplayName("§e§lWARLORDS");
                objective.getScore(ChatColor.GRAY + dateString).setScore(15);
                objective.getScore(" ").setScore(14);
                objective.getScore(ChatColor.BLUE + "BLU: " + ChatColor.AQUA + "1000" + ChatColor.GOLD + "/1000").setScore(13);
                objective.getScore(ChatColor.RED + "RED: " + ChatColor.AQUA + "800" + ChatColor.GOLD + "/1000").setScore(12);
                objective.getScore("  ").setScore(11);
                objective.getScore(ChatColor.BLUE + "BLU " + ChatColor.GOLD + "Wins in: " + ChatColor.GREEN + "10:00").setScore(10);
                objective.getScore("   ").setScore(9);
                objective.getScore(ChatColor.RED + "RED Flag: " + ChatColor.GREEN + "Safe").setScore(8);
                objective.getScore(ChatColor.BLUE + "BLU Flag: " + ChatColor.GREEN + "Safe").setScore(7);
                objective.getScore("    ").setScore(6);
                objective.getScore(ChatColor.GOLD + "Lv90 " + ChatColor.GREEN + "Berserker").setScore(5);
                objective.getScore("     ").setScore(4);
                objective.getScore(ChatColor.GREEN + "150 " + ChatColor.RESET + "Kills " + ChatColor.GREEN + "50 " + ChatColor.RESET + "Assists").setScore(3);
                objective.getScore("      ").setScore(2);
                objective.getScore(ChatColor.YELLOW + "localhost").setScore(1);

                world.getPlayers().forEach(player -> player.setScoreboard(board));
            }

        }.runTaskTimer(this, 0, 20);
    }

    public void runnable3() {
        new BukkitRunnable() {

            @Override
            public void run() {
                 for(ArmorStand e : world.getEntitiesByClass(ArmorStand.class)) {
                     if(e.getVelocity().getX() != 0 && e.getCustomName()!= null && e.getCustomName().contains("Boulder")) {
                         Bukkit.broadcastMessage("" + e.getVelocity());
                         Vector velocity = e.getVelocity();
                         double xVel = velocity.getX();
                         double yVel = velocity.getY();
                         double zVel = velocity.getZ();
                         if(yVel < 0) {
                             e.setHeadPose(new EulerAngle(e.getVelocity().getY()/2 * -1, 0, 0));
                         } else {
                             e.setHeadPose(new EulerAngle(e.getVelocity().getY() * -1, 0, 0));
                         }
                         if (Math.abs(xVel) < .2 && Math.abs(zVel) < .2) {
                             if(!e.getCustomName().contains("2")) {
                                 e.setVelocity(new Vector(xVel * 1.5,yVel,zVel/2));
                                 e.setCustomName(e.getCustomName() + "2");
                             }
                             if(!e.getCustomName().contains("3")) {
                                 e.setVelocity(new Vector(zVel/2,yVel,zVel * 1.5));
                                 e.setCustomName(e.getCustomName() + "3");
                             }
                         }

                     }
                 }
            }

        }.runTaskTimer(this,0,0);
    }
}
