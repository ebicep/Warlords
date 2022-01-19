package com.ebicep.warlords.maps.flags;

import java.util.Arrays;
import java.util.List;
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

    public boolean wasWinner() {
        return wasWinner;
    }

    @Override
    public FlagLocation update(FlagInfo info) {
        this.despawnTimer--;
        return this.despawnTimer <= 0 ? new SpawnFlagLocation(info.getSpawnLocation(), null) : null;
    }

    @Override
    public List<String> getDebugInformation() {
        return Arrays.asList("Type: " + this.getClass().getSimpleName(),
                "wasWinner: " + wasWinner(),
                "despawnTimer: " + getDespawnTimer()
        );
    }
	
}
