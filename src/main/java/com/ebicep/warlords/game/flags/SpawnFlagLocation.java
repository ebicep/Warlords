package com.ebicep.warlords.game.flags;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class SpawnFlagLocation extends AbstractLocationBasedFlagLocation {

    @Nullable
    private final WarlordsEntity flagReturner;

    public SpawnFlagLocation(@Nonnull Location location, @Nullable WarlordsEntity flagReturner) {
        super(location);
        this.flagReturner = flagReturner;
    }

    /**
     * Get the player who returned the flag
     *
     * @return the flag returner, or null is the flag automatically moved back
     */
    @Nullable
    public WarlordsEntity getFlagReturner() {
        return flagReturner;
    }

    @Override
    public FlagLocation update(@Nonnull FlagInfo info) {
        return null;
    }

    @Nonnull
    @Override
    public List<TextComponent> getDebugInformation() {
        return Arrays.asList(
                Component.text("Type: " + this.getClass().getSimpleName()),
                Component.text("lastToucher: " + flagReturner)
        );
    }

}
