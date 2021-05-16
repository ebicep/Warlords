package com.ebicep.warlords.powerups;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HealingPowerUp extends AbstractPowerUp {

    public HealingPowerUp(Location location, int duration, int cooldown, int timeToSpawn) {
        super(location, duration, cooldown, timeToSpawn);
    }
}
