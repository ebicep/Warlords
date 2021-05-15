package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.util.ParticleEffect;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Particle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;

public class Boulder extends AbstractAbility {

    public Boulder() {
        super("Boulder", -588, -877, 8, 80, 15, 175,
                "§7Launch a giant boulder that shatters\n" +
                "§7and deals §c%dynamic.value% §7- §c%dynamic.value% §7damage\n" +
                "§7to all enemies near the impact point\n" +
                "§7and knocks them back slightly.");
    }

    @Override
    public void onActivate(Player player) {

        Location location = player.getLocation();
        Vector speed = player.getLocation().getDirection().multiply(2.5);
        ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0, 0, 0), EntityType.ARMOR_STAND);
        stand.setHelmet(new ItemStack(Material.LONG_GRASS, 1, (short) 2));
        stand.setCustomName("Boulder");
        stand.setCustomNameVisible(false);
        stand.setGravity(false);
        stand.setVisible(false);

        Warlords.getPlayer(player).subtractEnergy(energyCost);

        new BukkitRunnable() {

            @Override
            public void run() {

                if (!stand.isValid()) {
                    this.cancel();
                }

                speed.multiply(0.97);
                speed.add(new Vector(0, -0.09, 0));
                Location newLoc = stand.getLocation();
                newLoc.add(speed);
                stand.teleport(newLoc);

                if (speed.getY() < 0) {
                    stand.setHeadPose(new EulerAngle(speed.getY() / 2 * -1, 0, 0));
                } else {
                    stand.setHeadPose(new EulerAngle(speed.getY() * -1, 0, 0));
                }

                boolean boulderExplode = false;

                if (!newLoc.add(0, 2, 0).getBlock().isEmpty()) {
                    boulderExplode = true;
                    for (Player player1 : player.getWorld().getPlayers()) {
                        player1.playSound(player.getLocation(), "shaman.boulder.impact", 1, 1);
                    }
                }

                Collection<Entity> nearbyEntities = stand.getWorld().getNearbyEntities(stand.getLocation(), 1, 1, 1);
                for (Entity entity : nearbyEntities) {
                    if (entity instanceof Player) {
                        if (!Warlords.getInstance().game.onSameTeam(player, (Player) entity)) {
                            boulderExplode = true;

                        }
                    }
                }

                if (boulderExplode) {
                    stand.remove();
                }
            }

        }.runTaskTimer(Warlords.getInstance(), 0, 1);


        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "shaman.boulder.activation", 1, 1);
        }
    }
}

/*
                    for (World world : Bukkit.getWorlds()) {
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
        if (location.getY() <= 6) {
        e.remove();

        location.setPitch(-10);
        for (int i = 0; i < 20; i++) {
        Location tempLocation = location.clone();
        while (location.getWorld().getBlockAt(tempLocation).getType() == Material.AIR) {
        tempLocation.add(0, -1, 0);
        }

        FallingBlock fallingBlock = world.spawnFallingBlock(location.clone().add(0, 1, 0),
        location.getWorld().getBlockAt(tempLocation).getType(),
        location.getWorld().getBlockAt(tempLocation).getData());
        fallingBlock.setVelocity(location.getDirection().normalize().multiply(.75));
        fallingBlock.setDropItem(false);
        location.setYaw((float) (location.getYaw() + Math.random() * 25 + 12));
        WarlordsEvents.addEntityUUID(fallingBlock.getUniqueId());
        }
        List<Entity> near = (List<Entity>) world.getNearbyEntities(location, 5, 5, 5);
        for (Entity entity : near) {
        if (entity instanceof Player) {
        Player nearPlayer = (Player) entity;
        if (nearPlayer.getGameMode() != GameMode.SPECTATOR) {
//Warlords.getPlayer(nearPlayer).addHealth();
final Vector v = nearPlayer.getLocation().toVector().subtract(location.toVector()).normalize().multiply(1.5).setY(0.4);

        nearPlayer.setVelocity(v);
        }
        }
        }
        //TODO spawn boulder impact + remove teammates
        }*/