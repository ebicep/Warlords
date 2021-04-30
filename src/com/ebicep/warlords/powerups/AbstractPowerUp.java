package com.ebicep.warlords.powerups;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

public abstract class AbstractPowerUp {

    protected Location location;
    protected ArmorStand powerUp;
    protected int duration;

    public AbstractPowerUp(Location location, ArmorStand powerUp, int duration) {
        this.location = location;
        this.powerUp = powerUp;
        this.duration = duration;
    }

    public abstract void onPickUp();

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
