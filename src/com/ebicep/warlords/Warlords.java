package com.ebicep.warlords;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.commands.StartGame;
import com.ebicep.warlords.events.WarlordsEvents;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent;
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


    public static List<ArrayList<ArrayList<SeismicWave>>> waveArrays = new ArrayList<>();

    public static List<ArrayList<ArrayList<SeismicWave>>> getWaveArrays() {
        return waveArrays;
    }

    public static List<ArrayList<ArrayList<GroundSlam>>> groundSlamArray = new ArrayList<>();

    public static List<ArrayList<ArrayList<GroundSlam>>> getGroundSlamArray() {
        return groundSlamArray;
    }

    public static List<Orb> orbs = new ArrayList<>();

    public static List<Orb> getOrbs() {
        return orbs;
    }

    public static List<Bolt> bolts = new ArrayList<>();

    public static List<Bolt> getBolts() {
        return bolts;
    }

    public static List<EarthenSpike> spikes = new ArrayList<>();

    public static List<ArmorStand> armorStands = new ArrayList<>(new ArrayList<>());


    public static List<ConsecrateHammerCircle> consecrates = new ArrayList<>();

    private static HashMap<Player, WarlordsPlayer> players = new HashMap<>();

    public static void addPlayer(WarlordsPlayer warlordsPlayer) {
        players.put(warlordsPlayer.getPlayer(), warlordsPlayer);
    }

    public static WarlordsPlayer getPlayer(Player player) {
        return players.get(player);
    }

    public static boolean hasPlayer(Player player) {
        return players.containsKey(player);
    }


    public static World world = Bukkit.getWorld("world");


    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new WarlordsEvents(), this);
        Objects.requireNonNull(getCommand("start")).setExecutor(new StartGame());
        if (world != null) {
            runnable();
            everySecond();
            everySecondAsync();
            runnable3();
            runnable4();
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
                if (spikes.size() != 0) {
                    for (int i = 0; i < spikes.size(); i++) {
                        //earthen spike BLOCk array
                        List<ArrayList<EarthenSpikeBlock>> tempSpikes = spikes.get(i).getSpikeArrays();
                        //block
                        if (tempSpikes.size() != 0) {
                            ArrayList<EarthenSpikeBlock> spike = tempSpikes.get(0);
                            FallingBlock block = spike.get(spike.size() - 1).getBlock();
                            Player player = spike.get(spike.size() - 1).getPlayer();
                            WarlordsPlayer user = spike.get(spike.size() - 1).getUser();
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
                                spike.add(new EarthenSpikeBlock(newBlock, player, user));
                                WarlordsEvents.addEntityUUID(newBlock.getUniqueId());
                            } else if (i <= tempSpikes.size() && tempSpikes.get(i).size() > 30) {
                                spikes.remove(i);
                            } else {
                                Bukkit.broadcastMessage("HIT");
                                Location location = player.getLocation();

                                Warlords.getPlayer(player).addHealth(user, spikes.get(i).getName(), spikes.get(i).getMinDamageHeal(), spikes.get(i).getMaxDamageHeal(), spikes.get(i).getCritChance(), spikes.get(i).getCritMultiplier());

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

                                spikes.remove(i);
                                i--;
                            }
                            if (player.getGameMode() == GameMode.SPECTATOR) {
                                spikes.remove(i);
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

    public void everySecond() {
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

//                for (Player player : world.getPlayers()) {
//                    WarlordsPlayer warlordsPlayer = getPlayer(player);
//
//                    //TODO fix bug where you take two ticks of damage initally
//                    //CONSECRATE
//                    for (int i = 0; i < consecrates.size(); i++) {
//                        ConsecrateHammerCircle consecrateHammerCircle = consecrates.get(i);
//                        if (consecrateHammerCircle.getPlayer() != player) {
//                            double distance = consecrateHammerCircle.getLocation().distanceSquared(player.getLocation());
//                            if (consecrateHammerCircle.getDuration() == 3)
//                                consecrateHammerCircle.spawn();
//                            if (distance < consecrateHammerCircle.getRadius() * consecrateHammerCircle.getRadius()) {
//                                if (consecrateHammerCircle.getMinDamage() < 0) {
//                                    warlordsPlayer.addHealth(Warlords.getPlayer(consecrateHammerCircle.getPlayer()), "Consecrate", consecrateHammerCircle.getMinDamage(), consecrateHammerCircle.getMaxDamage(), consecrateHammerCircle.getCritChance(), consecrateHammerCircle.getCritMultiplier());
//                                } else {
//                                    //TODO damage/heal
//                                    warlordsPlayer.addHealth(Warlords.getPlayer(consecrateHammerCircle.getPlayer()), "Hammer of Light", consecrateHammerCircle.getMinDamage(), consecrateHammerCircle.getMaxDamage(), consecrateHammerCircle.getCritChance(), consecrateHammerCircle.getCritMultiplier());
//                                }
//                            }
//                            consecrateHammerCircle.setDuration(consecrateHammerCircle.getDuration() - 1);
//                            if (consecrateHammerCircle.getDuration() == 0) {
//                                consecrates.remove(i);
//                                i--;
//                            }
//                        }
//                    }
//
//                }

            }

        }.runTaskTimer(this, 0, 20);
    }

    public void everySecondAsync() {
        new BukkitRunnable() {
            @Override
            public void run() {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Player player : world.getPlayers()) {
                            WarlordsPlayer warlordsPlayer = getPlayer(player);

                            //REGEN
                            if (warlordsPlayer.getRegenTimer() != -1) {
                                warlordsPlayer.setRegenTimer(warlordsPlayer.getRegenTimer() - 1);
                            } else {
                                int healthToAdd = (int) (warlordsPlayer.getMaxHealth() / 55.3);
                                if (warlordsPlayer.getHealth() + healthToAdd >= warlordsPlayer.getMaxHealth()) {
                                    warlordsPlayer.setHealth(warlordsPlayer.getMaxHealth());
                                } else {
                                    warlordsPlayer.setHealth(warlordsPlayer.getHealth() + (int) (warlordsPlayer.getMaxHealth() / 55.3));
                                }
                            }
                            //RESPAWN
                            if (warlordsPlayer.getRespawnTimer() != -1) {
                                warlordsPlayer.setRespawnTimer(warlordsPlayer.getRespawnTimer() - 1);
                            }
                            //ABILITY COOLDOWN
                            if (warlordsPlayer.getSpec().getRed().getCurrentCooldown() != 0 && warlordsPlayer.getSpec().getRed().getCurrentCooldown() != warlordsPlayer.getSpec().getRed().getCooldown()) {
                                warlordsPlayer.getSpec().getRed().setCurrentCooldown(warlordsPlayer.getSpec().getRed().getCurrentCooldown() - 1);
                                warlordsPlayer.updateRedItem();
                            }
                            if (warlordsPlayer.getSpec().getPurple().getCurrentCooldown() != 0 && warlordsPlayer.getSpec().getPurple().getCurrentCooldown() != warlordsPlayer.getSpec().getPurple().getCooldown()) {
                                warlordsPlayer.getSpec().getPurple().setCurrentCooldown(warlordsPlayer.getSpec().getPurple().getCurrentCooldown() - 1);
                                warlordsPlayer.updatePurpleItem();
                            }
                            if (warlordsPlayer.getSpec().getBlue().getCurrentCooldown() != 0 && warlordsPlayer.getSpec().getBlue().getCurrentCooldown() != warlordsPlayer.getSpec().getBlue().getCooldown()) {
                                warlordsPlayer.getSpec().getBlue().setCurrentCooldown(warlordsPlayer.getSpec().getBlue().getCurrentCooldown() - 1);
                                warlordsPlayer.updateBlueItem();
                            }
                            if (warlordsPlayer.getSpec().getOrange().getCurrentCooldown() != 0 && warlordsPlayer.getSpec().getOrange().getCurrentCooldown() != warlordsPlayer.getSpec().getOrange().getCooldown()) {
                                warlordsPlayer.getSpec().getOrange().setCurrentCooldown(warlordsPlayer.getSpec().getOrange().getCurrentCooldown() - 1);
                                warlordsPlayer.updateOrangeItem();
                            }
                            if (warlordsPlayer.getHorseCooldown() != 0 && !player.isInsideVehicle()) {
                                warlordsPlayer.setHorseCooldown(warlordsPlayer.getHorseCooldown() - 1);
                                warlordsPlayer.updateHorseItem();
                            }
                            //COOLDOWNS
                            if (warlordsPlayer.getWrath() != 0) {
                                warlordsPlayer.setWrath(warlordsPlayer.getWrath() - 1);
                            }
                            //MOVEMENT
                            if (warlordsPlayer.getInfusion() != 0) {
                                player.setWalkSpeed(WarlordsPlayer.infusionSpeed);
                                warlordsPlayer.setInfusion(warlordsPlayer.getInfusion() - 1);
                            }
                            if (warlordsPlayer.getPresence() != 0) {
                                if (warlordsPlayer.getInfusion() == 0)
                                    player.setWalkSpeed(WarlordsPlayer.presenceSpeed);
                                warlordsPlayer.setPresence(warlordsPlayer.getPresence() - 1);
                                List<Entity> near = player.getNearbyEntities(6.0D, 2.0D, 6.0D);
                                near.remove(player);
                                for (Entity entity : near) {
                                    if (entity instanceof Player) {
                                        Warlords.getPlayer((Player) entity).setPresence(warlordsPlayer.getPresence());
                                    }
                                }
                            }
                            if (warlordsPlayer.getBerserk() != 0) {
                                //berserk same speed as presence 30%
                                player.setWalkSpeed(WarlordsPlayer.presenceSpeed);
                                warlordsPlayer.setBerserk(warlordsPlayer.getBerserk() - 1);
                            }
                            if (warlordsPlayer.getInfusion() == 0 && warlordsPlayer.getPresence() == 0 && warlordsPlayer.getBerserk() == 0) {
                                player.setWalkSpeed(WarlordsPlayer.defaultSpeed);
                            }
                            if (warlordsPlayer.getBloodLust() != 0) {
                                warlordsPlayer.setBloodLust(warlordsPlayer.getBloodLust() - 1);
                            }
                            if (warlordsPlayer.getIntervene() != 0) {
                                if (warlordsPlayer.getIntervene() != 1) {
                                    if (warlordsPlayer.getIntervene() == 2)
                                        warlordsPlayer.getPlayer().sendMessage("§a\u00BB§7 " + warlordsPlayer.getIntervenedBy().getName() + "'s §eIntervene §7will expire in §6" + (warlordsPlayer.getIntervene() - 1) + "§7 second!");
                                    else
                                        warlordsPlayer.getPlayer().sendMessage("§a\u00BB§7 " + warlordsPlayer.getIntervenedBy().getName() + "'s §eIntervene §7will expire in §6" + (warlordsPlayer.getIntervene() - 1) + "§7 seconds!");
                                }
                                warlordsPlayer.setIntervene(warlordsPlayer.getIntervene() - 1);
                                if (warlordsPlayer.getIntervene() == 0) {
                                    warlordsPlayer.getPlayer().sendMessage("§c\u00AB§7 " + warlordsPlayer.getIntervenedBy().getName() + "'s §eIntervene §7has expired!");
                                    //TODO add intervenedBy player no longer veneing
                                }
                            }
                            if (warlordsPlayer.getLastStand() != 0) {
                                warlordsPlayer.setLastStand(warlordsPlayer.getLastStand() - 1);
                            }
                            if (warlordsPlayer.getOrbOfLife() != 0) {
                                warlordsPlayer.setOrbOfLife(warlordsPlayer.getOrbOfLife() - 1);
                            }
                            if (warlordsPlayer.getUndyingArmy() != 0 && !warlordsPlayer.isUndyingArmyDead()) {
                                warlordsPlayer.setUndyingArmy(warlordsPlayer.getUndyingArmy() - 1);
                                if (warlordsPlayer.getUndyingArmy() == 0) {
                                    int healing = (int) ((warlordsPlayer.getMaxHealth() - warlordsPlayer.getHealth()) * .35 + 200);
                                    warlordsPlayer.addHealth(warlordsPlayer.getUndyingArmyBy(), "Undying Army", healing, healing, -1, 100);
                                }
                            } else if (warlordsPlayer.isUndyingArmyDead()) {
                                if (warlordsPlayer.getHealth() - 500 < 0) {
                                    warlordsPlayer.setHealth(0);
                                    warlordsPlayer.setUndyingArmyDead(false);
                                } else {
                                    warlordsPlayer.setHealth(warlordsPlayer.getHealth() - 500);
                                }
                            }
                            if (warlordsPlayer.getWindfury() != 0) {
                                warlordsPlayer.setWindfury(warlordsPlayer.getWindfury() - 1);
                            }
                            if (warlordsPlayer.getEarthliving() != 0) {
                                warlordsPlayer.setEarthliving(warlordsPlayer.getEarthliving() - 1);
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
                            //CONSECRATE
                            for (int i = 0; i < consecrates.size(); i++) {
                                ConsecrateHammerCircle consecrateHammerCircle = consecrates.get(i);
                                if (consecrateHammerCircle.getPlayer() != player) {
                                    double distance = consecrateHammerCircle.getLocation().distanceSquared(player.getLocation());
                                    if (consecrateHammerCircle.getDuration() == 3)
                                        consecrateHammerCircle.spawn();
                                    if (distance < consecrateHammerCircle.getRadius() * consecrateHammerCircle.getRadius()) {
                                        if (consecrateHammerCircle.getMinDamage() < 0) {
                                            warlordsPlayer.addHealth(Warlords.getPlayer(consecrateHammerCircle.getPlayer()), "Consecrate", consecrateHammerCircle.getMinDamage(), consecrateHammerCircle.getMaxDamage(), consecrateHammerCircle.getCritChance(), consecrateHammerCircle.getCritMultiplier());
                                        } else {
                                            //TODO damage/heal
                                            warlordsPlayer.addHealth(Warlords.getPlayer(consecrateHammerCircle.getPlayer()), "Hammer of Light", consecrateHammerCircle.getMinDamage(), consecrateHammerCircle.getMaxDamage(), consecrateHammerCircle.getCritChance(), consecrateHammerCircle.getCritMultiplier());
                                        }
                                    }
                                    consecrateHammerCircle.setDuration(consecrateHammerCircle.getDuration() - 1);
                                    if (consecrateHammerCircle.getDuration() == 0) {
                                        consecrates.remove(i);
                                        i--;
                                    }
                                }
                            }

                        }

                    }
                }.runTask(instance);
            }
        }.runTaskTimerAsynchronously(this, 0, 20);
    }

    public void runnable3() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player player : world.getPlayers()) {
                    WarlordsPlayer warlordsPlayer = getPlayer(player);
                    Location location = player.getLocation();

                    //to make it look like the cooldown activates super fast but isnt really fast since it updates next second tick
                    if (warlordsPlayer.getSpec().getRed().getCurrentCooldown() == warlordsPlayer.getSpec().getRed().getCooldown()) {
                        warlordsPlayer.getSpec().getRed().setCurrentCooldown(warlordsPlayer.getSpec().getRed().getCurrentCooldown() - 1);
                        warlordsPlayer.updateRedItem();
                    }
                    if (warlordsPlayer.getSpec().getPurple().getCurrentCooldown() == warlordsPlayer.getSpec().getPurple().getCooldown()) {
                        warlordsPlayer.getSpec().getPurple().setCurrentCooldown(warlordsPlayer.getSpec().getPurple().getCurrentCooldown() - 1);
                        warlordsPlayer.updatePurpleItem();
                    }
                    if (warlordsPlayer.getSpec().getBlue().getCurrentCooldown() == warlordsPlayer.getSpec().getBlue().getCooldown()) {
                        warlordsPlayer.getSpec().getBlue().setCurrentCooldown(warlordsPlayer.getSpec().getBlue().getCurrentCooldown() - 1);
                        warlordsPlayer.updateBlueItem();
                    }
                    if (warlordsPlayer.getSpec().getOrange().getCurrentCooldown() == warlordsPlayer.getSpec().getOrange().getCooldown()) {
                        warlordsPlayer.getSpec().getOrange().setCurrentCooldown(warlordsPlayer.getSpec().getOrange().getCurrentCooldown() - 1);
                        warlordsPlayer.updateOrangeItem();
                    }
                    if (warlordsPlayer.getHorseCooldown() == 15 && !player.isInsideVehicle()) {
                        warlordsPlayer.setHorseCooldown(warlordsPlayer.getHorseCooldown() - 1);
                        warlordsPlayer.updateHorseItem();
                    }

                    //respawn
                    if (warlordsPlayer.getRespawnTimer() == 0) {
                        warlordsPlayer.setRespawnTimer(-1);
                        warlordsPlayer.respawn();
                        player.setGameMode(GameMode.SURVIVAL);
                    }
                    //damage or heal
                    float newHealth = (float) warlordsPlayer.getHealth() / warlordsPlayer.getMaxHealth() * 40;
                    if (warlordsPlayer.getUndyingArmy() != 0 && newHealth <= 0) {
                        warlordsPlayer.setHealth(warlordsPlayer.getMaxHealth());
                        warlordsPlayer.setUndyingArmyDead(true);
                        warlordsPlayer.setUndyingArmy(0);
                    }
                    if (newHealth <= 0 && !warlordsPlayer.isUndyingArmyDead()) {
                        //TODO change spectator and tp to spawn point
                        player.setGameMode(GameMode.SPECTATOR);
                        warlordsPlayer.respawn();
                        warlordsPlayer.setRespawnTimer(5);
                    } else {
                        player.setHealth(newHealth);
                    }
                    if (warlordsPlayer.getIntervene() != 0 && warlordsPlayer.getInterveneDamage() >= 3600 || (warlordsPlayer.getIntervenedBy() != null && warlordsPlayer.getPlayer().getLocation().distanceSquared(warlordsPlayer.getIntervenedBy().getPlayer().getLocation()) > 15 * 15)) {
                        //TODO seperate and add why the vene broke in chat
                        warlordsPlayer.setIntervene(0);
                        warlordsPlayer.getPlayer().sendMessage("§c\u00AB§7 " + warlordsPlayer.getIntervenedBy().getName() + "'s §eIntervene §7has expired!");

                    }
                    //energy
                    if (warlordsPlayer.getEnergy() != warlordsPlayer.getMaxEnergy()) {
                        if (warlordsPlayer.getPresence() != 0) {
                            System.out.println("JUICING " + warlordsPlayer.getName());
                            warlordsPlayer.setEnergy((float) (warlordsPlayer.getEnergy() + 1.5));
                        } else {
                            warlordsPlayer.setEnergy(warlordsPlayer.getEnergy() + 1);
                        }
                    }
                    player.setLevel((int) warlordsPlayer.getEnergy());
                    player.setExp(warlordsPlayer.getEnergy() / warlordsPlayer.getMaxEnergy());
                    //melee cooldown
                    if (warlordsPlayer.getHitCooldown() != 0) {
                        warlordsPlayer.setHitCooldown(warlordsPlayer.getHitCooldown() - 1);
                    }

                    //orbs
                    for (int i = 0; i < orbs.size(); i++) {
                        Orb orb = orbs.get(i);
                        Location orbPosition = orb.getBukkitEntity().getLocation();
                        if (orbPosition.distanceSquared(location) < 2.3 * 2.3) {
                            orb.getArmorStand().remove();
                            orb.getBukkitEntity().remove();
                            orbs.remove(i);
                            i--;
                            warlordsPlayer.addHealth(warlordsPlayer, "Orbs of Life", 502, 502, -1, 100);
                            List<Entity> near = player.getNearbyEntities(3.0D, 3.0D, 3.0D);
                            near.remove(player);
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

                    for (int i = 0; i < bolts.size(); i++) {
                        Bolt bolt = bolts.get(i);
                        bolt.getArmorStand().teleport(bolt.getLocation().add(bolt.getTeleportDirection().multiply(1.1)), PlayerTeleportEvent.TeleportCause.PLUGIN);
                        //hitting player
                        //TODO fix fucked up hit detection
                        if (bolt.getShooter() != warlordsPlayer && location.distanceSquared(new Location(world, bolt.getLocation().getX(), bolt.getLocation().getY(), bolt.getLocation().getZ()).add(0, 2, 0)) < 2.5 * 2.5) {
                            warlordsPlayer.addHealth(bolt.getShooter(), bolt.getName(), bolt.getMinDmg(), bolt.getMaxDmg(), bolt.getCritChance(), bolt.getCritMultiplier());
                        }

                        if (world.getBlockAt(new Location(world, bolt.getLocation().getX(), bolt.getLocation().getY(), bolt.getLocation().getZ()).add(0, 2, 0)).getType() != Material.AIR || bolt.getArmorStand().getTicksLived() > 50) {
                            //TODO add explosion thingy
                            bolt.getArmorStand().remove();
                            bolts.remove(i);
                            i--;
                        }
                    }
                }

            }

        }.runTaskTimer(this, 0, 0);
    }

    public void runnable4() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for (ArmorStand e : world.getEntitiesByClass(ArmorStand.class)) {
                    if (e.getCustomName() != null && e.getCustomName().contains("Boulder")) {
                        Vector velocity = e.getVelocity();
                        Location location = e.getLocation();
                        double xVel = velocity.getX();
                        double yVel = velocity.getY();
                        double zVel = velocity.getZ();
                        Bukkit.broadcastMessage("" + location.getDirection());

                        if (yVel < 0) {
                            e.setHeadPose(new EulerAngle(e.getVelocity().getY() / 2 * -1, 0, 0));
                        } else {
                            e.setHeadPose(new EulerAngle(e.getVelocity().getY() * -1, 0, 0));
                        }
                        if (yVel < 0 && Math.round(Math.abs(xVel) + Math.abs(zVel)) == 0 && location.getY() < location.getWorld().getHighestBlockYAt(location) + 5) {
                            e.remove();
                            //TODO spawn boulder impact
                        }

                        //TODO fix boulder velocity stopping
//                         if (Math.abs(xVel) < .2 && Math.abs(zVel) < .2) {
//                             if(!e.getCustomName().contains("2")) {
//                                 e.setVelocity(new Vector(xVel * 1.5,yVel,zVel/2));
//                                 e.setCustomName(e.getCustomName() + "2");
//                             }
//                             if(!e.getCustomName().contains("3")) {
//                                 e.setVelocity(new Vector(zVel/2,yVel,zVel * 1.5));
//                                 e.setCustomName(e.getCustomName() + "3");
//                             }
//                         }

                    }
                }
            }

        }.runTaskTimerAsynchronously(this, 0, 2);
    }

}
