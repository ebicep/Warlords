package com.ebicep.warlords.maps.option.flags;

import org.bukkit.Location;

import javax.annotation.Nullable;

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
