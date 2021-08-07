package com.ebicep.warlords.powerups;

import org.bukkit.Location;

public class HealingPowerUp extends AbstractPowerUp {

    public HealingPowerUp() {
        super(null, 0, 0, 0);
    }

    public HealingPowerUp(Location location, int duration, int cooldown, int timeToSpawn) {
        super(location, duration, cooldown, timeToSpawn);
    }
}
