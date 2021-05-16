package com.ebicep.warlords.powerups;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DamagePowerUp extends AbstractPowerUp {

    public DamagePowerUp(Location location, int duration, int cooldown, int timeToSpawn) {
        super(location, duration, cooldown, timeToSpawn);
    }
}
