package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.util.Random;

public class DamageHealCircle {

    private Player player;
    private Location location;
    private int radius;
    private int duration;
    private int minDamage;
    private int maxDamage;
    private int critChance;
    private int critMultiplier;
    private String name;
    private ArmorStand hammer;

    public DamageHealCircle(Player player, Location location, int radius, int duration, int minDamage, int maxDamage, int critChance, int critMultiplier, String name) {
        this.player = player;
        this.location = location;
        for (int i = 0; i < 10; i++) {
            if (location.getWorld().getBlockAt(location.clone().add(0, -1, 0)).getType() == Material.AIR) {
                location.add(0, -1, 0);
            }
        }
        this.radius = radius;
        this.duration = duration;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.critChance = critChance;
        this.critMultiplier = critMultiplier;
        this.name = name;
    }

    public void spawnHammer() {
        Location newLocation = location.clone();
        for (int i = 0; i < 10; i++) {
            if (newLocation.getWorld().getBlockAt(newLocation.clone().add(0, -1, 0)).getType() == Material.AIR) {
                newLocation.add(0, -1, 0);
            }
        }
        newLocation.add(0, -1, 0);
        hammer = (ArmorStand) location.getWorld().spawnEntity(newLocation.clone().add(.25, 1.9, -.25), EntityType.ARMOR_STAND);
        hammer.setRightArmPose(new EulerAngle(20.25, 0, 0));
        hammer.setItemInHand(new ItemStack(Material.STRING));
        hammer.setGravity(false);
        hammer.setVisible(false);
        hammer.setMarker(true);
    }

    public void removeHammer() {
        hammer.remove();
    }

    private static final Random random = new Random();
    public void spawn() {
        for (int i = 0; i < 16; i++) {
            double angle = random.nextInt(360) * Math.PI / 180;
            float x = (float) (radius * Math.sin(angle));
            float z = (float) (radius * Math.cos(angle));
            if (name.contains("Hammer")) {
                location.getWorld().playEffect(new Location(location.getWorld(), location.getX() + x, location.getY() + 1, location.getZ() + z), Effect.HAPPY_VILLAGER, 0);

            } else {
                if (name.contains("Rain")) {
                    location.getWorld().playEffect(new Location(location.getWorld(), location.getX() + x, location.getY() + 1, location.getZ() + z), Effect.HAPPY_VILLAGER, 0);
                } else {
                    location.getWorld().playEffect(new Location(location.getWorld(), location.getX() + x, location.getY(), location.getZ() + z), Effect.HAPPY_VILLAGER, 0);
                }
            }

            if (name.contains("Rain")) {
                // TODO: need to revise this + ring doesn't appear at all sometimes?
                ParticleEffect.CLOUD.display(2, 0, 2, 0.01F, 1, (new Location(location.getWorld(), location.getX(), location.getY() + 6, location.getZ())), 500);
                ParticleEffect.DRIP_WATER.display(2, 0, 2, 0.01F, 1, (new Location(location.getWorld(), location.getX(), location.getY() + 6, location.getZ())), 500);
            }
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getMinDamage() {
        return minDamage;
    }

    public void setMinDamage(int minDamage) {
        this.minDamage = minDamage;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public void setMaxDamage(int maxDamage) {
        this.maxDamage = maxDamage;
    }

    public int getCritChance() {
        return critChance;
    }

    public void setCritChance(int critChance) {
        this.critChance = critChance;
    }

    public int getCritMultiplier() {
        return critMultiplier;
    }

    public void setCritMultiplier(int critMultiplier) {
        this.critMultiplier = critMultiplier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
