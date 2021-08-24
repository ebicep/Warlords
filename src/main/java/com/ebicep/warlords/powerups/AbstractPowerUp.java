package com.ebicep.warlords.powerups;

import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public abstract class AbstractPowerUp {

    protected Location location;
    protected ArmorStand powerUp;
    protected int duration;
    protected int cooldown;
    protected int maxCooldown;
    protected int timeToSpawn;

    public AbstractPowerUp(Location location, int duration, int cooldown, int timeToSpawn) {
        this.location = location;
        this.duration = duration;
        this.cooldown = 0;
        this.maxCooldown = cooldown;
        this.timeToSpawn = timeToSpawn;
    }

    public abstract void onPickUp(WarlordsPlayer warlordsPlayer);

    public abstract void setNameAndItem(ArmorStand armorStand);

    public void spawn() {
        powerUp = location.getWorld().spawn(location.clone().add(0, -1.5, 0), ArmorStand.class);

        setNameAndItem(powerUp);

        powerUp.setGravity(false);
        powerUp.setVisible(false);
        powerUp.setCustomNameVisible(true);

        for (Player player1 : powerUp.getWorld().getPlayers()) {
            player1.playSound(powerUp.getLocation(), "ctf.powerup.spawn", 2, 1);
        }

    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ArmorStand getPowerUp() {
        return powerUp;
    }

    public void setPowerUp(ArmorStand powerUp) {
        this.powerUp = powerUp;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public int getMaxCooldown() {
        return maxCooldown;
    }

    public void setMaxCooldown(int maxCooldown) {
        this.maxCooldown = maxCooldown;
    }
}
