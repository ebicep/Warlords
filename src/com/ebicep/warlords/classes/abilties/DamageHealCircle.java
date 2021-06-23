package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.effects.circle.AreaEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.effects.circle.LineEffect;
import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;

import org.bukkit.Bukkit;

public class DamageHealCircle {
    private final WarlordsPlayer warlordsPlayer;
    private Location location;
    private int radius;
    private int duration;
    private int minDamage;
    private int maxDamage;
    private int critChance;
    private int critMultiplier;
    private String name;
    private ArmorStand hammer;
    private final CircleEffect circle;

    public DamageHealCircle(WarlordsPlayer warlordsPlayer, Location location, int radius, int duration, int minDamage, int maxDamage, int critChance, int critMultiplier, String name) {
        this.warlordsPlayer = warlordsPlayer;
        this.location = location;
        if (!location.getWorld().getBlockAt(location).getType().isSolid()) {
            location.add(0, -1, 0);
        }
        this.radius = radius;
        this.duration = duration;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.critChance = critChance;
        this.critMultiplier = critMultiplier;
        this.name = name;
        this.circle = new CircleEffect(warlordsPlayer.getGame(), warlordsPlayer.getTeam(), location, radius);
        if (name.contains("Healing Rain")) {
            this.circle.addEffect(new CircumferenceEffect(ParticleEffect.VILLAGER_HAPPY, ParticleEffect.REDSTONE));
            this.circle.addEffect(new AreaEffect(5, ParticleEffect.CLOUD).particlesPerSurface(0.05));
            this.circle.addEffect(new AreaEffect(5, ParticleEffect.DRIP_WATER).particlesPerSurface(0.05));
        } else if (name.equals("Consecrate")) {
            this.circle.addEffect(new CircumferenceEffect(ParticleEffect.VILLAGER_HAPPY, ParticleEffect.REDSTONE));
            this.circle.addEffect(new DoubleLineEffect(ParticleEffect.SPELL));
        } else if (name.equals("Hammer of Light")) {
            this.circle.addEffect(new CircumferenceEffect(ParticleEffect.VILLAGER_HAPPY, ParticleEffect.REDSTONE));
            this.circle.addEffect(new LineEffect(this.location.clone().add(0, 2.3, 0), ParticleEffect.SPELL));
        } else {
            Bukkit.broadcastMessage("Notice, no particle effect definition for " + this.name + ", no effect is played!!");
        }

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
        hammer.setMetadata("Hammer of Light - " + getWarlordsPlayer().getName(), new FixedMetadataValue(Warlords.getInstance(), true));
        hammer.setRightArmPose(new EulerAngle(20.25, 0, 0));
        hammer.setItemInHand(new ItemStack(Material.STRING));
        hammer.setGravity(false);
        hammer.setVisible(false);
        hammer.setMarker(true);
    }

    public void removeHammer() {
        hammer.remove();
    }

    public void spawn() {
        this.circle.playEffects();
    }

    public WarlordsPlayer getWarlordsPlayer() {
        return warlordsPlayer;
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