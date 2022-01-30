package com.ebicep.warlords.maps.flags;

import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;

public class SpawnFlagLocation extends AbstractLocationBasedFlagLocation {

    @Nullable
    private final String lastToucher;

    public SpawnFlagLocation(@Nonnull Location location, @Nullable String lastToucher) {
        super(location);
        this.lastToucher = lastToucher;
    }

    /**
     * Get the name of the player who returned the flag
     * @return the name of the returned, or null is the flag automatically moved back
     */
    @Nullable
    public String getLastToucher() {
        return lastToucher;
    }

    @Override
    public FlagLocation update(FlagInfo info) {
        return null;
    }

    @Override
    public List<String> getDebugInformation() {
        return Arrays.asList(
                "Type: " + this.getClass().getSimpleName(),
                "lastToucher: " + lastToucher
        );
    }

}
