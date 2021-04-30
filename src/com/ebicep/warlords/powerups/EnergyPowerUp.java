package com.ebicep.warlords.powerups;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

public class EnergyPowerUp extends AbstractPowerUp {

    public EnergyPowerUp(Location location, ArmorStand powerUp, int duration) {
        super(location, powerUp, duration);
    }

    @Override
    public void onPickUp() {

    }
}
