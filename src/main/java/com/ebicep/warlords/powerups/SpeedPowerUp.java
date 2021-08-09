package com.ebicep.warlords.powerups;

import org.bukkit.Location;

public class SpeedPowerUp extends AbstractPowerUp {

    public SpeedPowerUp() {
        super(null, 0, 0, 0);
    }

    public SpeedPowerUp(Location location, int duration, int cooldown, int timeToSpawn) {
        super(location, duration, cooldown, timeToSpawn);
    }
}
