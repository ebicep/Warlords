package com.ebicep.warlords.maps.option.marker;
import javax.annotation.Nonnull;
import org.bukkit.Location;

/**
 * Marks a location for the debug command
 */
public interface DebugLocationMarker extends GameMarker {

    @Nonnull
    public String getName();
    
    @Nonnull
    public Class<?> getCreator();
    
    @Nonnull
    public Location getLocation();
}
