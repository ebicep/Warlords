package com.ebicep.warlords.game.flags;

import org.bukkit.Location;

public abstract class AbstractLocationBasedFlagLocation implements FlagLocation {
	
    protected final Location location;

    public AbstractLocationBasedFlagLocation(Location location) {
        this.location = location.clone();
        this.location.setX(location.getBlockX() + 0.5);
        this.location.setY(location.getBlockY());
        this.location.setZ(location.getBlockZ() + 0.5);
    }

    @Override
    public Location getLocation() {
        return location;
    }
	
}
