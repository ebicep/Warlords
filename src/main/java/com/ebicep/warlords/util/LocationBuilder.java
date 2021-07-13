package com.ebicep.warlords.util;


import org.bukkit.Location;
import org.bukkit.util.Vector;

public class LocationBuilder {

    private final Location location;

    public LocationBuilder(Location location) {
        this.location = location;
    }

    public LocationBuilder add(Vector vector) {
        location.add(vector);
        return this;
    }

    public LocationBuilder subtract(Vector vector) {
        location.subtract(vector);
        return this;
    }

    public LocationBuilder direction(Vector vector) {
        location.setDirection(vector);
        return this;
    }

    public LocationBuilder pitch(float pitch) {
        location.setPitch(pitch);
        return this;
    }

    public LocationBuilder yaw(float yaw) {
        location.setYaw(yaw);
        return this;
    }

    public Location get() {
        return this.location;
    }

}
