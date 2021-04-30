package com.ebicep.warlords.powerups;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

public class HealingPowerUp extends AbstractPowerUp {


    public HealingPowerUp(Location location, ArmorStand powerUp, int duration) {
        super(location, powerUp, duration);
    }

    @Override
    public void onPickUp() {

    }
}
