
package com.ebicep.warlords.maps.option.marker;

import java.util.Objects;
import javax.annotation.Nonnull;
import org.bukkit.Location;


public class SimpleDebugLocationMarker implements DebugLocationMarker {
    @Nonnull
    private Class<?> creator;
    @Nonnull
    private String name;
    @Nonnull
    private Location location;

    public SimpleDebugLocationMarker(@Nonnull Class<?> creator, @Nonnull String name, @Nonnull Location loc) {
        this.creator = Objects.requireNonNull(creator);
        this.name = Objects.requireNonNull(name);
        this.location = Objects.requireNonNull(loc);
    }

    @Override
    public Class<?> getCreator() {
        return creator;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public void setCreator(@Nonnull Class<?> creator) {
        this.creator = Objects.requireNonNull(creator);
    }

    public void setName(@Nonnull String name) {
        this.name = Objects.requireNonNull(name);
    }

    public void setLocation(@Nonnull Location location) {
        this.location = Objects.requireNonNull(location);
    }
        

}
