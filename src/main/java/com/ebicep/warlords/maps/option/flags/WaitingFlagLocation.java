package com.ebicep.warlords.maps.option.flags;

import org.bukkit.Location;

public class WaitingFlagLocation extends AbstractLocationBasedFlagLocation {
	
    int despawnTimer;
    final boolean wasWinner;

    public WaitingFlagLocation(Location location, boolean wasWinner) {
        super(location);
        this.despawnTimer = 15 * 20;
        this.wasWinner = wasWinner;
    }

    public int getDespawnTimer() {
        return despawnTimer;
    }

    public boolean isWasWinner() {
        return wasWinner;
    }

    @Override
    public FlagLocation update(FlagInfo info) {
        this.despawnTimer--;
        return this.despawnTimer <= 0 ? new SpawnFlagLocation(info.getSpawnLocation(), null) : null;
    }

    public boolean wasWinner() {
        return wasWinner;
    }
	
}
