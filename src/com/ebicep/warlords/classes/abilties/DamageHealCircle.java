package com.ebicep.warlords.classes.abilties;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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

    public DamageHealCircle(Player player, Location location, int radius, int duration, int minDamage, int maxDamage, int critChance, int critMultiplier, String name) {
        this.player = player;
        this.location = location;
        this.radius = radius;
        this.duration = duration;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.critChance = critChance;
        this.critMultiplier = critMultiplier;
        this.name = name;
    }

    public void spawn() {
        float angle = 0;
        for (int i = 0; i < Math.PI * 20; i++) {
            float x = (float) (radius * Math.sin(angle));
            float z = (float) (radius * Math.cos(angle));
            angle += 0.2;
            location.getWorld().playEffect(new Location(location.getWorld(), location.getX() + x, location.getWorld().getHighestBlockYAt(location), location.getZ() + z), Effect.HAPPY_VILLAGER, 0);
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
