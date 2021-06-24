package com.ebicep.warlords.maps.flags;

import javax.annotation.Nullable;
import org.bukkit.Location;

public class SpawnFlagLocation extends AbstractLocationBasedFlagLocation {

    private final String lastToucher;
	
    public SpawnFlagLocation(Location location, @Nullable String lastToucher) {
        super(location);
        this.lastToucher = lastToucher;
    }

    public String getLastToucher() {
        return lastToucher;
    }

    @Override
    public FlagLocation update(FlagInfo info) {
        return null;
    }
	
}
