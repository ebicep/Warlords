package com.ebicep.warlords.powerups;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

public class DamagePowerUp extends AbstractPowerUp {

    public DamagePowerUp(Location location, ArmorStand powerUp, int duration) {
        super(location, powerUp, duration);
    }

    @Override
    public void onPickUp() {

    }
}
