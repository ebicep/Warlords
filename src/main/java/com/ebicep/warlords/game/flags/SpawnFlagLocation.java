package com.ebicep.warlords.game.flags;

import com.ebicep.warlords.player.ingame.AbstractWarlordsEntity;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class SpawnFlagLocation extends AbstractLocationBasedFlagLocation {

    @Nullable
    private final AbstractWarlordsEntity flagReturner;

    public SpawnFlagLocation(@Nonnull Location location, @Nullable AbstractWarlordsEntity flagReturner) {
        super(location);
        this.flagReturner = flagReturner;
    }

    /**
     * Get the player who returned the flag
     *
     * @return the flag returner, or null is the flag automatically moved back
     */
    @Nullable
    public AbstractWarlordsEntity getFlagReturner() {
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
