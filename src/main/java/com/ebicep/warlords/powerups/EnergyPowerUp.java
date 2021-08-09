package com.ebicep.warlords.powerups;

import org.bukkit.Location;

public class EnergyPowerUp extends AbstractPowerUp {

    public EnergyPowerUp() {
        super(null, 0, 0, 0);
    }

    public EnergyPowerUp(Location location, int duration, int cooldown, int timeToSpawn) {
        super(location, duration, cooldown, timeToSpawn);
    }
}
