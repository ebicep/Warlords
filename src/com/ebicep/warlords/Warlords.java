package com.ebicep.warlords;

import com.ebicep.customentities.CustomFallingBlock;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.abilties.Projectile;
import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.commands.StartGame;
import com.ebicep.warlords.events.WarlordsEvents;
import com.ebicep.warlords.util.ParticleEffect;
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

    public static List<CustomFallingBlock> customFallingBlocks = new ArrayList<>();

    public static List<CustomFallingBlock> getFallingBlocks() {
        return customFallingBlocks;
    }

    public static List<SeismicWave> waveArrays = new ArrayList<>();

    public static List<SeismicWave> getWaveArrays() {
        return waveArrays;
    }

    public static List<GroundSlam> groundSlamArray = new ArrayList<>();

    public static List<GroundSlam> getGroundSlamArray() {
        return groundSlamArray;
    }

    public static List<OrbsOfLife.Orb> orbs = new ArrayList<>();

    public static List<OrbsOfLife.Orb> getOrbs() {
        return orbs;
    }

    public static List<LightningBolt.Bolt> bolts = new ArrayList<>();

    public static List<LightningBolt.Bolt> getBolts() {
        return bolts;
    }

    public static List<EarthenSpike> spikes = new ArrayList<>();

    public static List<ArmorStand> armorStands = new ArrayList<>(new ArrayList<>());

    public static List<Totem> totems = new ArrayList<>();

    public static List<Totem> getTotems() {
        return totems;
    }

    public static List<DamageHealCircle> damageHealCircles = new ArrayList<>();

    public static List<Projectile.CustomProjectile> customProjectiles = new ArrayList<>();

    public static List<Projectile.CustomProjectile> getCustomProjectiles() {
        return customProjectiles;
    }

    public static List<Breath> breaths = new ArrayList<>();

    public static List<Breath> getBreaths() {
        return breaths;
    }

    public static List<TimeWarp.TimeWarpPlayer> timeWarpPlayers = new ArrayList<>();

    public static List<TimeWarp.TimeWarpPlayer> getTimeWarpPlayers() {
        return timeWarpPlayers;
    }

    public static List<FallenSouls.FallenSoul> fallenSouls = new ArrayList<>();

    public static List<FallenSouls.FallenSoul> getFallenSouls() {
        return fallenSouls;
    }

    public static List<ArmorStand> chains = new ArrayList<>();

    public static List<ArmorStand> getChains() {
        return chains;
    }

    public static List<Boulder.SnowballBoulder> snowballBoulders = new ArrayList<>();

    public static List<Boulder.SnowballBoulder> getSnowballBoulders() {
        return snowballBoulders;
    }

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


    public static World world = Bukkit.getWorld("TestWorld");

    private int counter = 0;

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new WarlordsEvents(), this);
        Objects.requireNonNull(getCommand("start")).setExecutor(new StartGame());
        if (world != null) {
            runnable();
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

                //EVERY TWO TICKS
                // 1 tick is a lot smoother, idk about performance impact
                if (counter % 1 == 0) {

                    // speed every 1 tick updated so it activates instantly
                    // MOVEMENT
                    for (Player player : world.getPlayers()) {
                        WarlordsPlayer warlordsPlayer = getPlayer(player);
                        player.sendMessage(String.valueOf(player.getWalkSpeed()));

                        // light infusion
                        if (warlordsPlayer.getInfusion() != 0) {
                            player.setWalkSpeed(WarlordsPlayer.currentSpeed);
                            warlordsPlayer.setInfusion((int) (warlordsPlayer.getInfusion() - 0.05));
                        }

                        // presence
                        if (warlordsPlayer.getPresence() != 0) {
                            if (warlordsPlayer.getInfusion() == 0)
                                player.setWalkSpeed(WarlordsPlayer.currentSpeed);
                            warlordsPlayer.setPresence((int) (warlordsPlayer.getPresence() - 0.05));
                            List<Entity> near = player.getNearbyEntities(6.0D, 2.0D, 6.0D);
//                            near.remove(player);
                            for (Entity entity : near) {
                                if (entity instanceof Player) {
                                    Player nearPlayer = (Player) entity;
                                    nearPlayer.setWalkSpeed(WarlordsPlayer.currentSpeed);
                                }
                            }
                        }

                        //freezingbreath
                        if (warlordsPlayer.getBreathSlowness() != 0) {
                            warlordsPlayer.setBreathSlowness((int) (warlordsPlayer.getBreathSlowness() - 0.05));
                            List<Entity> near = player.getNearbyEntities(6.0D, 2.0D, 6.0D);
//                            near1.remove(player);
                            for (Entity entity : near) {
                                if (entity instanceof Player) {
                                    Player nearPlayer = (Player) entity;
                                    nearPlayer.setWalkSpeed(WarlordsPlayer.currentSpeed);
                                }
                            }
                        }

                        // frostbolt
                        if (warlordsPlayer.getFrostbolt() != 0) {
                            warlordsPlayer.setFrostbolt((int) (warlordsPlayer.getFrostbolt() - 0.05));
                            List<Entity> near = player.getNearbyEntities(6.0D, 2.0D, 6.0D);

                            // TODO: fix shooting getting all players + shooter instead of just impact location bolt
                            for (Entity entity : near) {
                                if (entity instanceof Player) {
                                    Player nearPlayer = (Player) entity;
                                    nearPlayer.setWalkSpeed(WarlordsPlayer.currentSpeed);
                                }
                            }
                        }

                        // berserk
                        if (warlordsPlayer.getBerserk() != 0) {
                            //berserk same speed as presence 30%
                            player.setWalkSpeed(WarlordsPlayer.currentSpeed);
                            warlordsPlayer.setBerserk((int) (warlordsPlayer.getBerserk() - 0.05));
                        }

                        // spiritlink
                        if (warlordsPlayer.getSpiritLink() != 0) {
                            player.setWalkSpeed(WarlordsPlayer.currentSpeed);
                            warlordsPlayer.setSpiritLink((int) (warlordsPlayer.getSpiritLink() - 0.05));
                        }

                        // ice barrier
                        if (warlordsPlayer.getIceBarrier() != 0) {
                            warlordsPlayer.setIceBarrier((int) (warlordsPlayer.getIceBarrier() - 0.05));
                        }

                        // ice barrier slowness duration
                        if (warlordsPlayer.getIceBarrierSlowness() != 0) {
                            player.setWalkSpeed(WarlordsPlayer.currentSpeed);
                            warlordsPlayer.setIceBarrierSlowness((int) (warlordsPlayer.getIceBarrierSlowness() - 0.05));
                        }
                    }




                    for (int i = 0; i < customProjectiles.size(); i++) {
                        Projectile.CustomProjectile customProjectile = customProjectiles.get(i);
                        Location location = customProjectile.getCurrentLocation();
                        boolean hitPlayer = false;
                        //TODO get confirm actual speeds
                        //BALLS
                        if (customProjectile.getBall().getName().contains("Fire")) {
                            // TODO: use setVelocity instead of multiply to avoid acceleration/startup time
                            location.add(customProjectile.getDirection().multiply(1.15));
                            location.add(0, 1.5, 0);
                            ParticleEffect.DRIP_LAVA.display(0, 0, 0, 0.35F, 7, location, 500);
                            ParticleEffect.SMOKE_NORMAL.display(0, 0, 0, 0.1F, 4, location, 500);
                            ParticleEffect.FLAME.display(0, 0, 0, 0.06F, 1, location, 500);
                            for (Entity entity : location.getWorld().getEntities()) {
                                if (entity instanceof Player && entity != customProjectile.getShooter()) {
                                    if (entity.getLocation().distanceSquared(location) < 2 * 2) {
                                        hitPlayer = true;
                                        ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0.5F, 1, entity.getLocation().add(0, 1, 0), 500);
                                        Player victim = (Player) entity;
                                        // TODO: fix sounds only playing on direct hit
                                        for (Player player1 : Bukkit.getOnlinePlayers()) {
                                            player1.playSound(entity.getLocation(), "mage.fireball.impact", 1, 1);
                                        }
                                        getPlayer(victim).addHealth(
                                                getPlayer(customProjectile.getShooter()),
                                                customProjectile.getBall().getName(),
                                                (int) (customProjectile.getBall().getMinDamageHeal() * 1.15),
                                                (int) (customProjectile.getBall().getMaxDamageHeal() * 1.15),
                                                customProjectile.getBall().getCritChance(),
                                                customProjectile.getBall().getCritMultiplier()
                                        );
                                        List<Entity> near = victim.getNearbyEntities(3.5D, 3.5D, 3.5D);
                                        for (Entity nearEntity : near) {
                                            if (nearEntity instanceof Player) {
                                                getPlayer((Player) nearEntity).addHealth(
                                                        getPlayer(customProjectile.getShooter()),
                                                        customProjectile.getBall().getName(),
                                                        customProjectile.getBall().getMinDamageHeal(),
                                                        customProjectile.getBall().getMaxDamageHeal(),
                                                        customProjectile.getBall().getCritChance(),
                                                        customProjectile.getBall().getCritMultiplier()
                                                );
                                            }
                                        }
                                        customProjectiles.remove(i);
                                        i--;
                                    }
                                }
                            }
                        } else if (customProjectile.getBall().getName().contains("Frost")) {
                            location.add(customProjectile.getDirection().multiply(1.05));
                            location.add(0, 1.5, 0);
                            //TODO add slowness
                            ParticleEffect.CLOUD.display(0, 0, 0, 0F, 1, location, 500);
                            //ParticleEffect.FLAME.display(0, 0, 0, 0.1F, 3, location, 500);
                            for (Entity entity : location.getWorld().getEntities()) {
                                if (entity instanceof Player && entity != customProjectile.getShooter()) {
                                    if (entity.getLocation().distanceSquared(location) < 2 * 2) {
                                        hitPlayer = true;
                                        ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0.0F, 1, entity.getLocation().add(0, 1, 0), 500);
                                        Player victim = (Player) entity;
                                        for (Player player1 : Bukkit.getOnlinePlayers()) {
                                            player1.playSound(entity.getLocation(), "mage.frostbolt.impact", 1, 1);
                                        }
                                        getPlayer(victim).addHealth(
                                                getPlayer(customProjectile.getShooter()),
                                                customProjectile.getBall().getName(),
                                                (int) (customProjectile.getBall().getMinDamageHeal() * 1.3),
                                                (int) (customProjectile.getBall().getMaxDamageHeal() * 1.3),
                                                customProjectile.getBall().getCritChance(),
                                                customProjectile.getBall().getCritMultiplier()
                                        );
                                        List<Entity> near = victim.getNearbyEntities(3.5D, 3.5D, 3.5D);
                                        for (Entity nearEntity : near) {
                                            if (nearEntity instanceof Player) {
                                                getPlayer((Player) nearEntity).addHealth(
                                                        getPlayer(customProjectile.getShooter()),
                                                        customProjectile.getBall().getName(),
                                                        customProjectile.getBall().getMinDamageHeal(),
                                                        customProjectile.getBall().getMaxDamageHeal(),
                                                        customProjectile.getBall().getCritChance(),
                                                        customProjectile.getBall().getCritMultiplier()
                                                );
                                            }
                                        }
                                        customProjectiles.remove(i);
                                        i--;
                                    }
                                }
                            }
                        } else if (customProjectile.getBall().getName().contains("Water")) {
                            location.add(customProjectile.getDirection().multiply(1));
                            location.add(0, 1.5, 0);
                            //TODO add damage
                            ParticleEffect.DRIP_WATER.display(0.3f, 0.3f, 0.3f, 0.1F, 5, location, 500);
                            ParticleEffect.ENCHANTMENT_TABLE.display(0, 0, 0, 0.1F, 1, location, 500);
                            ParticleEffect.VILLAGER_HAPPY.display(0, 0, 0, 0.1F, 1, location, 500);
                            ParticleEffect.CLOUD.display(0, 0, 0, 0F, 1, location, 500);
                            //ParticleEffect.FLAME.display(0, 0, 0, 0.1F, 3, location, 500);
                            for (Entity entity : location.getWorld().getEntities()) {
                                if (entity instanceof Player && entity != customProjectile.getShooter()) {
                                    if (entity.getLocation().distanceSquared(location) < 2 * 2) {
                                        ParticleEffect.HEART.display(3, 3, 3, 0.2F, 5, entity.getLocation().add(0, 1, 0), 500);
                                        ParticleEffect.VILLAGER_HAPPY.display(4, 4, 4, 0.2F, 5, entity.getLocation().add(0, 1, 0), 500);
                                        Player victim = (Player) entity;
                                        for (Player player1 : Bukkit.getOnlinePlayers()) {
                                            player1.playSound(entity.getLocation(), "mage.waterbolt.impact", 1, 1);
                                        }
                                        getPlayer(victim).addHealth(
                                                getPlayer(customProjectile.getShooter()),
                                                customProjectile.getBall().getName(),
                                                (int) (customProjectile.getBall().getMinDamageHeal() * 1.15),
                                                (int) (customProjectile.getBall().getMaxDamageHeal() * 1.15),
                                                customProjectile.getBall().getCritChance(),
                                                customProjectile.getBall().getCritMultiplier()
                                        );
                                        List<Entity> near = victim.getNearbyEntities(3.5D, 3.5D, 3.5D);
                                        for (Entity nearEntity : near) {
                                            if (nearEntity instanceof Player) {
                                                getPlayer((Player) nearEntity).addHealth(
                                                        getPlayer(customProjectile.getShooter()),
                                                        customProjectile.getBall().getName(),
                                                        customProjectile.getBall().getMinDamageHeal(),
                                                        customProjectile.getBall().getMaxDamageHeal(),
                                                        customProjectile.getBall().getCritChance(),
                                                        customProjectile.getBall().getCritMultiplier()
                                                );
                                            }
                                        }
                                        customProjectiles.remove(i);
                                        i--;
                                    }
                                }
                            }


                        } else if (customProjectile.getBall().getName().contains("Flame")) {
                            location.add(customProjectile.getDirection().multiply(1.1));
                            location.add(0, 1.5, 0);
                            //TODO add flameburst animation

                            // Equation for spiral animation
                            int radius = 2;
                            for (double x = 0; x <= 50; x += 0.05) { // Set for vertical, need to change
                                double y = radius * Math.cos(x);
                                double z = radius * Math.sin(x);
                            }

                            ParticleEffect.FLAME.display(0.2F, 0, 0.2F, 0F, 4, location, 500);
                            for (Entity entity : location.getWorld().getEntities()) {
                                if (entity instanceof Player && entity != customProjectile.getShooter()) {
                                    if (entity.getLocation().distanceSquared(location) < 2 * 2) {
                                        hitPlayer = true;
                                        ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0.0F, 1, entity.getLocation().add(0, 1, 0), 500);
                                        Player victim = (Player) entity;
                                        for (Player player1 : Bukkit.getOnlinePlayers()) {
                                            player1.playSound(entity.getLocation(), "mage.flameburst.impact", 1, 1);
                                        }
                                        System.out.println((int) location.distance(customProjectile.getStartingLocation()));
                                        getPlayer(victim).addHealth(
                                                getPlayer(customProjectile.getShooter()),
                                                customProjectile.getBall().getName(),
                                                customProjectile.getBall().getMinDamageHeal(),
                                                customProjectile.getBall().getMaxDamageHeal(),
                                                customProjectile.getBall().getCritChance() + (int) location.distance(customProjectile.getStartingLocation()),
                                                customProjectile.getBall().getCritMultiplier()
                                        );
                                        List<Entity> near = victim.getNearbyEntities(3.5D, 3.5D, 3.5D);
                                        for (Entity nearEntity : near) {
                                            if (nearEntity instanceof Player) {
                                                getPlayer((Player) nearEntity).addHealth(
                                                        getPlayer(customProjectile.getShooter()),
                                                        customProjectile.getBall().getName(),
                                                        customProjectile.getBall().getMinDamageHeal(),
                                                        customProjectile.getBall().getMaxDamageHeal(),
                                                        customProjectile.getBall().getCritChance() + (int) Math.pow(location.distanceSquared(customProjectile.getStartingLocation()), 2),
                                                        customProjectile.getBall().getCritMultiplier()
                                                );
                                            }
                                        }

                                        customProjectiles.remove(i);
                                        i--;
                                    }
                                }
                            }
                        }

                        //hit block or out of range
                        if (world.getBlockAt(location).getType() != Material.AIR && !hitPlayer) {
                            if (customProjectile.getBall().getName().contains("Water")) {
                                ParticleEffect.HEART.display(1, 1, 1, 0.2F, 5, location, 500);
                                ParticleEffect.VILLAGER_HAPPY.display(1, 1, 1, 0.2F, 5, location, 500);
                            } else {
                                ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0.0F, 1, location, 500);
                            }
                            List<Entity> near = (List<Entity>) location.getWorld().getNearbyEntities(location, 3.5, 3.5, 3.5);
                            for (Entity nearEntity : near) {
                                if (nearEntity instanceof Player && nearEntity != customProjectile.getShooter()) {
                                    if (customProjectile.getBall().getName().contains("Flame")) {
                                        getPlayer((Player) nearEntity).addHealth(
                                                getPlayer(customProjectile.getShooter()),
                                                customProjectile.getBall().getName(),
                                                customProjectile.getBall().getMinDamageHeal(),
                                                customProjectile.getBall().getMaxDamageHeal(),
                                                customProjectile.getBall().getCritChance() + (int) Math.pow(location.distanceSquared(customProjectile.getStartingLocation()), 2),
                                                customProjectile.getBall().getCritMultiplier()
                                        );
                                    } else {
                                        getPlayer((Player) nearEntity).addHealth(
                                                getPlayer(customProjectile.getShooter()),
                                                customProjectile.getBall().getName(),
                                                customProjectile.getBall().getMinDamageHeal(),
                                                customProjectile.getBall().getMaxDamageHeal(),
                                                customProjectile.getBall().getCritChance(),
                                                customProjectile.getBall().getCritMultiplier()
                                        );
                                    }
                                }
                            }
                            customProjectiles.remove(i);
                            i--;
                        } else if (location.distanceSquared(customProjectile.getStartingLocation()) >= customProjectile.getMaxDistance() * customProjectile.getMaxDistance()) {
//                            customProjectiles.remove(i);
//                            i--;
                        }

                        location.subtract(0, 1.5, 0);
                    }
                }

                if (counter % 2 == 0) {
                    //GROUND SLAM
                    for (int i = 0; i < groundSlamArray.size(); i++) {
                        GroundSlam groundSlam = groundSlamArray.get(i);
                        for (List<Location> fallingBlockLocation : groundSlam.getFallingBlockLocations()) {
                            for (Location location : fallingBlockLocation) {
                                if (world.getBlockAt(location.clone().add(0, 1, 0)).getType() == Material.AIR) {
                                    FallingBlock fallingBlock = addFallingBlock(location);
                                    System.out.println(fallingBlock.getLocation());
                                    customFallingBlocks.add(new CustomFallingBlock(fallingBlock, location.getY() + .25, groundSlam.getOwner(), groundSlam));
                                }
                            }
                            groundSlam.getFallingBlockLocations().remove(fallingBlockLocation);
                            break;
                        }
                        if (groundSlam.getFallingBlockLocations().size() == 0) {
                            groundSlamArray.remove(i);
                            i--;
                        }
                    }
                    //WAVE
                    for (int i = 0; i < waveArrays.size(); i++) {
                        SeismicWave seismicWave = waveArrays.get(i);
                        for (List<Location> fallingBlockLocation : seismicWave.getFallingBlockLocations()) {
                            for (Location location : fallingBlockLocation) {
                                if (world.getBlockAt(location.clone().add(0, 1, 0)).getType() == Material.AIR) {
                                    FallingBlock fallingBlock = addFallingBlock(location);
                                    customFallingBlocks.add(new CustomFallingBlock(fallingBlock, location.getY() + .25, seismicWave.getOwner(), seismicWave));
                                }
                            }
                            seismicWave.getFallingBlockLocations().remove(fallingBlockLocation);
                            break;
                        }
                        if (seismicWave.getFallingBlockLocations().size() == 0) {
                            waveArrays.remove(i);
                            i--;
                        }
                    }
                }

                // PARTICLES
                if (counter % 4 == 0) {
                    for (Player player : world.getPlayers()) {
                        WarlordsPlayer warlordsPlayer = getPlayer(player);
                        if (warlordsPlayer.getArcaneShield() != 0) {
                            for (int i = 0; i < warlordsPlayer.getArcaneShield(); i++) {
                                ParticleEffect.CLOUD.display(0.3F, 0.9F, 0.3F, 0.02F, 2, player.getLocation(), 500);
                                ParticleEffect.FIREWORKS_SPARK.display(0.3F, 1.2F, 0.3F, 0.0001F, 1, player.getLocation(), 500);
                                ParticleEffect.SPELL_WITCH.display(0.3F, 1.2F, 0.3F, 0.001F, 1, player.getLocation(), 500);
                            }
                        }
                        if (warlordsPlayer.getInferno() != 0) {
                            for (int i = 0; i < warlordsPlayer.getInferno(); i++) {
                                ParticleEffect.DRIP_LAVA.display(0.3F, 0.6F, 0.3F, 0.4F, 1, player.getLocation(), 500);
                            }
                        }

                        for (int i = 0; i < timeWarpPlayers.size(); i++) {
                            TimeWarp.TimeWarpPlayer timeWarpPlayer = timeWarpPlayers.get(i);
                            if (timeWarpPlayer.getTime() != 0) {
                                //ParticleEffect.CLOUD.display(0.4F, 0.1F, 0.4F, 0.001F, 5, timeWarpPlayer.getLocation(), 500);
                                ParticleEffect.SPELL_WITCH.display(0F, 0F, 0F, 0.001F, 6, player.getLocation(), 500);
                            }

                            int points = 20;
                            double radius = 0.5d;
                            Location origin = timeWarpPlayer.getLocation();

                            for (int e = 0; e < points; e++) {
                                double angle = 2 * Math.PI * e / points;
                                Location point = origin.clone().add(radius * Math.sin(angle), 0.0d, radius * Math.cos(angle));
                                ParticleEffect.CLOUD.display(0F, 0F, 0F, 0.001F, 2, point, 500);
                            }
                        }
                    }
                }

                //EVERY THREE TICKS
                if (counter % 3 == 0) {
                    //all earthen spikes +1 when right click
                    if (spikes.size() != 0) {
                        for (int i = 0; i < spikes.size(); i++) {
                            //earthen spike BLOCk array
                            List<ArrayList<EarthenSpike.EarthenSpikeBlock>> tempSpikes = spikes.get(i).getSpikeArrays();
                            //block
                            if (tempSpikes.size() != 0) {
                                ArrayList<EarthenSpike.EarthenSpikeBlock> spike = tempSpikes.get(0);
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
                                    spike.add(new EarthenSpike.EarthenSpikeBlock(newBlock, player, user));
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

                    //BOULDERS
                    //TODO remake boulder into a seperate class for better performance
                    for (int i = 0; i < snowballBoulders.size(); i++) {
                        Boulder.SnowballBoulder snowballBoulder = snowballBoulders.get(i);
                        snowballBoulder.getBoulder().teleport(snowballBoulder.getSnowball().getLocation().clone().add(0, 2, 0));
                        if (snowballBoulder.getSnowball().getVelocity().getY() < 0) {
                            snowballBoulder.getBoulder().setHeadPose(new EulerAngle(snowballBoulder.getSnowball().getVelocity().getY() / 2 * -1, 0, 0));
                        } else {
                            snowballBoulder.getBoulder().setHeadPose(new EulerAngle(snowballBoulder.getSnowball().getVelocity().getY() * -1, 0, 0));
                        }
                    }

                    for (ArmorStand e : world.getEntitiesByClass(ArmorStand.class)) {
                        if (e.getCustomName() != null && e.getCustomName().contains("Boulder")) {
                            Vector velocity = e.getVelocity();
                            Location location = e.getLocation();
                            double xVel = velocity.getX();
                            double yVel = velocity.getY();
                            double zVel = velocity.getZ();
                            //Bukkit.broadcastMessage("" + velocity);

                            if (yVel < 0) {
                                e.setHeadPose(new EulerAngle(e.getVelocity().getY() / 2 * -1, 0, 0));
                            } else {
                                e.setHeadPose(new EulerAngle(e.getVelocity().getY() * -1, 0, 0));
                            }
                            if (yVel < 0 && Math.round(Math.abs(xVel) + Math.abs(zVel)) == 0) {
                                e.remove();


                                //TODO spawn boulder impact
                            }

                            //TODO fix boulder velocity stopping
//                            if (Math.abs(xVel) + Math.abs(zVel) <= .25) {
//                                if (!e.getCustomName().contains("2")) {
//                                    Bukkit.broadcastMessage("HEHEXD");
//                                    Location newLocation = e.getLocation().clone();
//                                    newLocation.setPitch(newLocation.getPitch() * -1);
//                                    System.out.println(location.getPitch());
//                                    e.setVelocity(newLocation.getDirection().multiply(.5));
//                                    e.setCustomName(e.getCustomName() + "2");
//                                }
//                            }
                        }
                    }

                }


                //EVERY SECOND
                if (counter % 20 == 0) {
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
                        if (warlordsPlayer.getRepentance() != 0) {
                            warlordsPlayer.setRepentance(warlordsPlayer.getRepentance() - 1);
                        }
                        if (warlordsPlayer.getRepentanceCounter() != 0) {
                            int newRepentanceCounter = (int) (warlordsPlayer.getRepentanceCounter() * .8 - 60);
                            if (newRepentanceCounter < 0) {
                                warlordsPlayer.setRepentanceCounter(0);
                            } else {
                                warlordsPlayer.setRepentanceCounter(newRepentanceCounter);
                            }
                        }
                        if (warlordsPlayer.getArcaneShield() != 0) {
                            Bukkit.broadcastMessage("" + warlordsPlayer.getArcaneShield());
                            warlordsPlayer.setArcaneShield(warlordsPlayer.getArcaneShield() - 1);
                            /*or (int i = 0; i < warlordsPlayer.getArcaneShield(); i++) {
                                ParticleEffect.CLOUD.display(0.3F, 1.2F, 0.3F, 0.002F, 2, player.getLocation(), 500);
                                ParticleEffect.FIREWORKS_SPARK.display(0.3F, 1.2F, 0.3F, 0.0001F, 1, player.getLocation(), 500);
                                Bukkit.broadcastMessage("Arcane:" + warlordsPlayer.getArcaneShield());
                            }*/
                        }
                        if (warlordsPlayer.getInferno() != 0) {
                            warlordsPlayer.setInferno(warlordsPlayer.getInferno() - 1);
                            for (int i = 0; i < warlordsPlayer.getInferno(); i++) {
                                ParticleEffect.FLAME.display(0.3F, 0.7F, 0.3F, 0.0001F, 1, player.getLocation(), 500);
                                ParticleEffect.CRIT.display(0.4F, 0.7F, 0.4F, 0.0001F, 1, player.getLocation(), 500);
                            }
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
                            if (soulBoundPlayer.getTimeLeft() == 0) {
                                warlordsPlayer.getSoulBindedPlayers().remove(i);
                                i--;
                            }
                        }
                    }

                    //CONSECRATE
                    for (int i = 0; i < damageHealCircles.size(); i++) {
                        DamageHealCircle damageHealCircle = damageHealCircles.get(i);
                        if (damageHealCircle.getDuration() % 2 == 0) {
                            damageHealCircle.spawn();
                        }
                        List<Entity> near = (List<Entity>) damageHealCircle.getLocation().getWorld().getNearbyEntities(damageHealCircle.getLocation(), 3, 3, 3);
                        for (Entity entity : near) {
                            if (entity instanceof Player) {
                                Player player = (Player) entity;
                                WarlordsPlayer warlordsPlayer = getPlayer(player);
                                if (!damageHealCircle.getName().equals("Consecrate")) {
                                    double distance = damageHealCircle.getLocation().distanceSquared(player.getLocation());
                                    if (distance < damageHealCircle.getRadius() * damageHealCircle.getRadius()) {
                                        //TODO check team to heal or dmg for hammer
                                        warlordsPlayer.addHealth(Warlords.getPlayer(damageHealCircle.getPlayer()), damageHealCircle.getName(), damageHealCircle.getMinDamage(), damageHealCircle.getMaxDamage(), damageHealCircle.getCritChance(), damageHealCircle.getCritMultiplier());
                                    }
                                }
                            }
                        }
                        damageHealCircle.setDuration(damageHealCircle.getDuration() - 1);
                        if (damageHealCircle.getDuration() == 0) {
                            damageHealCircles.remove(i);
                            i--;
                        }

                    }
                    //TOTEMS
                    for (int i = 0; i < totems.size(); i++) {
                        Totem totem = totems.get(i);
                        if (totem.getSecondsLeft() != 0) {
                            if (totem.getOwner().getSpec().getOrange().getName().contains("Healing")) {
                                List<Entity> near = totem.getTotemArmorStand().getNearbyEntities(4.0D, 4.0D, 4.0D);
                                for (Entity entity : near) {
                                    if (entity instanceof Player) {
                                        Player nearPlayer = (Player) entity;
                                        if (nearPlayer.getGameMode() != GameMode.SPECTATOR) {
                                            getPlayer(nearPlayer).addHealth(totem.getOwner(), totem.getOwner().getSpec().getOrange().getName(), totem.getOwner().getSpec().getOrange().getMinDamageHeal(), (int) (totem.getOwner().getSpec().getOrange().getMinDamageHeal() * 1.35), totem.getOwner().getSpec().getOrange().getCritChance(), totem.getOwner().getSpec().getOrange().getCritMultiplier());
                                        }
                                    }
                                }
                            }
                            totem.setSecondsLeft(totem.getSecondsLeft() - 1);
                            if (totem.getOwner().getSpec().getOrange().getName().contains("Death")) {
                                if (totem.getSecondsLeft() == 0) {
                                    ((Totem.TotemSpiritguard) totem.getOwner().getSpec().getOrange()).setDebt(6);
                                }
                            }
                        } else {
                            if (totem.getOwner().getSpec().getOrange().getName().contains("Healing")) {
                                List<Entity> near = totem.getTotemArmorStand().getNearbyEntities(4.0D, 4.0D, 4.0D);
                                for (Entity entity : near) {
                                    if (entity instanceof Player) {
                                        Player nearPlayer = (Player) entity;
                                        if (nearPlayer.getGameMode() != GameMode.SPECTATOR) {
                                            getPlayer(nearPlayer).addHealth(totem.getOwner(), totem.getOwner().getSpec().getOrange().getName(), totem.getOwner().getSpec().getOrange().getMaxDamageHeal(), (int) (totem.getOwner().getSpec().getOrange().getMaxDamageHeal() * 1.35), totem.getOwner().getSpec().getOrange().getCritChance(), totem.getOwner().getSpec().getOrange().getCritMultiplier());
                                        }
                                    }
                                }
                                totem.getTotemArmorStand().remove();
                                totems.remove(i);
                                i--;
                            } else if (totem.getOwner().getSpec().getOrange().getName().contains("Capacitor")) {
                                totem.getTotemArmorStand().remove();
                                totems.remove(i);
                                i--;
                            } else if (totem.getOwner().getSpec().getOrange().getName().contains("Death")) {
                                //TODO check teammates for ALL THIS
                                Totem.TotemSpiritguard totemSpiritguard = ((Totem.TotemSpiritguard) totem.getOwner().getSpec().getOrange());
                                if (totemSpiritguard.getDebt() != 0) {
                                    Bukkit.broadcastMessage("" + totemSpiritguard.getDelayedDamage());
                                    //100% of damage over 6 seconds
                                    int damage = (int) (totemSpiritguard.getDelayedDamage() * .1667);
                                    System.out.println(damage);
                                    //player damage
                                    totem.getOwner().addHealth(totem.getOwner(), "",
                                            damage,
                                            damage,
                                            totem.getOwner().getSpec().getOrange().getCritChance(), totem.getOwner().getSpec().getOrange().getCritMultiplier());
                                    //teammate heal
                                    List<Entity> near = totem.getTotemArmorStand().getNearbyEntities(6.0D, 4.0D, 6.0D);
                                    for (Entity entity : near) {
                                        if (entity instanceof Player) {
                                            Player nearPlayer = (Player) entity;
                                            if (nearPlayer.getGameMode() != GameMode.SPECTATOR) {
                                                getPlayer(nearPlayer).addHealth(totem.getOwner(), totem.getOwner().getSpec().getOrange().getName(),
                                                        (int) (damage * -.15),
                                                        (int) (damage * -.15),
                                                        totem.getOwner().getSpec().getOrange().getCritChance(), totem.getOwner().getSpec().getOrange().getCritMultiplier());
                                            }
                                        }
                                    }

                                    totemSpiritguard.setDebt(totemSpiritguard.getDebt() - 1);
                                } else {
                                    List<Entity> near = totem.getTotemArmorStand().getNearbyEntities(6.0D, 4.0D, 6.0D);
                                    for (Entity entity : near) {
                                        if (entity instanceof Player) {
                                            Player nearPlayer = (Player) entity;
                                            if (nearPlayer.getGameMode() != GameMode.SPECTATOR) {
                                                getPlayer(nearPlayer).addHealth(totem.getOwner(), totem.getOwner().getSpec().getOrange().getName(),
                                                        (int) (totemSpiritguard.getDelayedDamage() * -.15),
                                                        (int) (totemSpiritguard.getDelayedDamage() * -.15),
                                                        totem.getOwner().getSpec().getOrange().getCritChance(), totem.getOwner().getSpec().getOrange().getCritMultiplier());
                                            }
                                        }
                                    }
                                    totem.getTotemArmorStand().remove();
                                    totems.remove(i);
                                    i--;
                                }
                            }
                        }
                    }

                    //TIME WARPS
                    for (int i = 0; i < timeWarpPlayers.size(); i++) {
                        TimeWarp.TimeWarpPlayer timeWarpPlayer = timeWarpPlayers.get(i);
                        if (timeWarpPlayer.getTime() != 0) {
                            timeWarpPlayer.setTime(timeWarpPlayer.getTime() - 1);
                        } else {
                            WarlordsPlayer player = timeWarpPlayer.getWarlordsPlayer();
                            player.addHealth(player, "Time Warp", (int) (player.getMaxHealth() * .3), (int) (player.getMaxHealth() * .3), -1, 100);
                                for (Player player1 : Bukkit.getOnlinePlayers()) {
                                    player1.playSound(timeWarpPlayer.getLocation(), "mage.timewarp.teleport", 1, 1);
                                }
                            player.getPlayer().teleport(timeWarpPlayer.getLocation());
                            player.getPlayer().getLocation().setDirection(timeWarpPlayer.getFacing());

                            timeWarpPlayers.remove(i);
                            i--;
                        }
                    }
                }

                //EVERY TICK
                for (int i = 0; i < customFallingBlocks.size(); i++) {
                    CustomFallingBlock customFallingBlock = customFallingBlocks.get(i);
                    customFallingBlock.setTicksLived(customFallingBlock.getTicksLived() + 1);
                    for (Player player : players.keySet()) {
                        if (player != customFallingBlock.getOwner()) {
                            AbstractAbility ability = customFallingBlock.getAbility();
                            if (ability instanceof SeismicWave && !((SeismicWave) ability).getPlayersHit().contains(player)) {
                                if (player.getLocation().distanceSquared(customFallingBlock.getFallingBlock().getLocation()) < 1.5) {
                                    ((SeismicWave) ability).getPlayersHit().add(player);
                                    final Location loc = player.getLocation();
                                    final Vector v = customFallingBlock.getOwner().getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-1.1).setY(0.25);
                                    player.setVelocity(v);
                                    getPlayer(player).addHealth(Warlords.getPlayer(customFallingBlock.getOwner()), ability.getName(), ability.getMinDamageHeal(), ability.getMaxDamageHeal(), ability.getCritChance(), ability.getCritMultiplier());
                                }
                            } else if (ability instanceof GroundSlam && !((GroundSlam) ability).getPlayersHit().contains(player)) {
                                if (player.getLocation().distanceSquared(customFallingBlock.getFallingBlock().getLocation()) < 1.5) {
                                    ((GroundSlam) ability).getPlayersHit().add(player);
                                    final Location loc = player.getLocation();
                                    final Vector v = customFallingBlock.getOwner().getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-1.1).setY(0.25);
                                    player.setVelocity(v);
                                    getPlayer(player).addHealth(Warlords.getPlayer(customFallingBlock.getOwner()), ability.getName(), ability.getMinDamageHeal(), ability.getMaxDamageHeal(), ability.getCritChance(), ability.getCritMultiplier());
                                }
                            }
                        }
                    }
                    //TODO fix bug where the blocks dont get removed if ability used near high wall - stuck in block?
                    //System.out.println(customFallingBlock.getCustomFallingBlock().getLocation().getY());
                    //System.out.println(customFallingBlock.getyLevel());
                    if (customFallingBlock.getFallingBlock().getLocation().getY() <= customFallingBlock.getyLevel() || customFallingBlock.getFallingBlock().getTicksLived() > 10 || customFallingBlock.getTicksLived() > 10) {
                        customFallingBlock.getFallingBlock().remove();
                        customFallingBlocks.remove(i);
                        i--;
                    }
                }

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
                        OrbsOfLife.Orb orb = orbs.get(i);
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

                    //BOLTS
                    for (int i = 0; i < bolts.size(); i++) {
                        LightningBolt.Bolt bolt = bolts.get(i);
                        bolt.getArmorStand().teleport(bolt.getLocation().add(bolt.getTeleportDirection().multiply(1.1)), PlayerTeleportEvent.TeleportCause.PLUGIN);
                        //hitting player
                        //TODO fix fucked up hit detection
                        if (bolt.getShooter() != warlordsPlayer && location.distanceSquared(new Location(world, bolt.getLocation().getX(), bolt.getLocation().getY(), bolt.getLocation().getZ()).add(0, 2, 0)) < 2.5 * 2.5) {
                            warlordsPlayer.addHealth(bolt.getShooter(), bolt.getLightningBolt().getName(), bolt.getLightningBolt().getMinDamageHeal(), bolt.getLightningBolt().getMaxDamageHeal(), bolt.getLightningBolt().getCritChance(), bolt.getLightningBolt().getCritMultiplier());
                        }

                        if (world.getBlockAt(new Location(world, bolt.getLocation().getX(), bolt.getLocation().getY(), bolt.getLocation().getZ()).add(0, 2, 0)).getType() != Material.AIR || bolt.getArmorStand().getTicksLived() > 50) {
                            //TODO add explosion thingy
                            bolt.getArmorStand().remove();
                            bolts.remove(i);
                            i--;
                        }
                    }

                    //FALLEN SOULS
                    for (int i = 0; i < fallenSouls.size(); i++) {
                        FallenSouls.FallenSoul fallenSoul = fallenSouls.get(i);
                        fallenSoul.getFallenSoulLeft().teleport(fallenSoul.getFallenSoulLeft().getLocation().add(fallenSoul.getDirectionLeft()));
                        fallenSoul.getFallenSoulMiddle().teleport(fallenSoul.getFallenSoulMiddle().getLocation().add(fallenSoul.getDirectionMiddle()));
                        fallenSoul.getFallenSoulRight().teleport(fallenSoul.getFallenSoulRight().getLocation().add(fallenSoul.getDirectionRight()));

                        if (!fallenSoul.getPlayersHit().contains(warlordsPlayer) &&
                                warlordsPlayer.getPlayer().getGameMode() != GameMode.SPECTATOR &&
                                (location.distanceSquared(new Location(world, fallenSoul.getFallenSoulLeft().getLocation().getX(), fallenSoul.getFallenSoulLeft().getLocation().getY(), fallenSoul.getFallenSoulLeft().getLocation().getZ()).add(0, 2, 0)) < 2 * 2
                                        || location.distanceSquared(new Location(world, fallenSoul.getFallenSoulMiddle().getLocation().getX(), fallenSoul.getFallenSoulMiddle().getLocation().getY(), fallenSoul.getFallenSoulMiddle().getLocation().getZ()).add(0, 2, 0)) < 2 * 2
                                        || location.distanceSquared(new Location(world, fallenSoul.getFallenSoulRight().getLocation().getX(), fallenSoul.getFallenSoulRight().getLocation().getY(), fallenSoul.getFallenSoulRight().getLocation().getZ()).add(0, 2, 0)) < 2 * 2
                                )
                        ) {
                            warlordsPlayer.addHealth(fallenSoul.getShooter(), fallenSoul.getFallenSouls().getName(), fallenSoul.getFallenSouls().getMinDamageHeal(), fallenSoul.getFallenSouls().getMaxDamageHeal(), fallenSoul.getFallenSouls().getCritChance(), fallenSoul.getFallenSouls().getCritMultiplier());
                            fallenSoul.getPlayersHit().add(warlordsPlayer);
                            if (fallenSoul.getShooter().getSoulBindCooldown() != 0 && fallenSoul.getShooter().hasBoundPlayer(warlordsPlayer)) {
                                System.out.println("COOLDOWN REDUCED");
                                fallenSoul.getShooter().getSpec().getRed().subtractCooldown(1.5F);
                                fallenSoul.getShooter().getSpec().getPurple().subtractCooldown(1.5F);
                                fallenSoul.getShooter().getSpec().getBlue().subtractCooldown(1.5F);
                                fallenSoul.getShooter().getSpec().getOrange().subtractCooldown(1.5F);

                                fallenSoul.getShooter().updateRedItem();
                                fallenSoul.getShooter().updatePurpleItem();
                                fallenSoul.getShooter().updateBlueItem();
                                fallenSoul.getShooter().updateOrangeItem();
                            }
                        }

                        if (!fallenSoul.isLeftRemoved() && world.getBlockAt(new Location(world, fallenSoul.getFallenSoulLeft().getLocation().getX(), fallenSoul.getFallenSoulLeft().getLocation().getY(), fallenSoul.getFallenSoulLeft().getLocation().getZ()).add(0, 2, 0)).getType() != Material.AIR || fallenSoul.getFallenSoulLeft().getTicksLived() > 50) {
                            //TODO add explosion thingy
                            fallenSoul.getFallenSoulLeft().remove();
                            fallenSoul.setLeftRemoved(true);
                        }
                        if (!fallenSoul.isMiddleRemoved() && world.getBlockAt(new Location(world, fallenSoul.getFallenSoulMiddle().getLocation().getX(), fallenSoul.getFallenSoulMiddle().getLocation().getY(), fallenSoul.getFallenSoulMiddle().getLocation().getZ()).add(0, 2, 0)).getType() != Material.AIR || fallenSoul.getFallenSoulMiddle().getTicksLived() > 50) {
                            //TODO add explosion thingy
                            fallenSoul.getFallenSoulMiddle().remove();
                            fallenSoul.setMiddleRemoved(true);
                        }
                        if (!fallenSoul.isRightRemoved() && world.getBlockAt(new Location(world, fallenSoul.getFallenSoulRight().getLocation().getX(), fallenSoul.getFallenSoulRight().getLocation().getY(), fallenSoul.getFallenSoulRight().getLocation().getZ()).add(0, 2, 0)).getType() != Material.AIR || fallenSoul.getFallenSoulRight().getTicksLived() > 50) {
                            //TODO add explosion thingy
                            fallenSoul.getFallenSoulRight().remove();
                            fallenSoul.setRightRemoved(true);
                        }

                        if (fallenSoul.isLeftRemoved() && fallenSoul.isMiddleRemoved() && fallenSoul.isRightRemoved()) {
                            fallenSouls.remove(i);
                            i--;
                        }
                    }

                    //CHAINS
                    for (int i = 0; i < chains.size(); i++) {
                        if (chains.get(i).getTicksLived() >= 15) {
                            chains.get(i).remove();
                            chains.remove(i);
                            i--;
                        }
                    }
                }
                updateScoreBoard();

                counter++;
            }

        }.runTaskTimer(this, 0, 0);
    }

    private void updateScoreBoard() {
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

    private FallingBlock addFallingBlock(Location location) {
        if (world.getBlockAt(location).getType() != Material.AIR) {
            location.add(0, 1, 0);
        }
        Location blockToGet = location.clone().add(0, -1, 0);
        if (location.getWorld().getBlockAt(location.clone().add(0, -1, 0)).getType() == Material.AIR) {
            blockToGet.add(0, -1, 0);
        }
        FallingBlock fallingBlock = world.spawnFallingBlock(location,
                location.getWorld().getBlockAt(blockToGet).getType(),
                location.getWorld().getBlockAt(blockToGet).getData());
        fallingBlock.setVelocity(new Vector(0, .1, 0));
        fallingBlock.setDropItem(false);
        return fallingBlock;

    }

}
