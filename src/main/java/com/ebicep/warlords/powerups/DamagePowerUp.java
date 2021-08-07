package com.ebicep.warlords.powerups;

import org.bukkit.Location;

public class DamagePowerUp extends AbstractPowerUp {

    public DamagePowerUp() {
        super(null, 0, 0, 0);
    }

    public DamagePowerUp(Location location, int duration, int cooldown, int timeToSpawn) {
        super(location, duration, cooldown, timeToSpawn);
    }
}
