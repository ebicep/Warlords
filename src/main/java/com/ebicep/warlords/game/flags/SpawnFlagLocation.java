package com.ebicep.warlords.game.flags;

import com.ebicep.warlords.player.WarlordsEntity;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;

public class SpawnFlagLocation extends AbstractLocationBasedFlagLocation {

    @Nullable
    private final WarlordsEntity flagReturner;

    public SpawnFlagLocation(@Nonnull Location location, @Nullable WarlordsEntity flagReturner) {
        super(location);
        this.flagReturner = flagReturner;
    }

    /**
     * Get the player who returned the flag
     * @return the flag returner, or null is the flag automatically moved back
     */
    @Nullable
    public WarlordsEntity getFlagReturner() {
        return flagReturner;
    }

    @Override
    public FlagLocation update(FlagInfo info) {
        return null;
    }

    @Override
    public List<String> getDebugInformation() {
        return Arrays.asList(
                "Type: " + this.getClass().getSimpleName(),
                "lastToucher: " + flagReturner
        );
    }

}
