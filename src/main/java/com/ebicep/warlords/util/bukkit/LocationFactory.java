package com.ebicep.warlords.util.bukkit;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Objects;

public class LocationFactory {
    @Nonnull
    private final Location location;

    public LocationFactory(@Nonnull Location location) {
        this.location = Objects.requireNonNull(location, "location");
    }

    public LocationFactory(@Nonnull World world) {
        this(new Location(Objects.requireNonNull(world, "world"), 0, 0, 0));
    }

    public LocationBuilder x(double x) {
        return new LocationBuilder(location.clone()).x(x);
    }

    public LocationBuilder y(double y) {
        return new LocationBuilder(location.clone()).y(y);
    }

    public LocationBuilder z(double z) {
        return new LocationBuilder(location.clone()).z(z);
    }

    public LocationBuilder addXYZ(double x, double y, double z) {
        return new LocationBuilder(location.clone()).addXYZ(x, y, z);
    }

    public LocationBuilder addXYZ(double x, double y, double z, float yaw, float pitch) {
        return new LocationBuilder(location.clone()).addXYZ(x, y, z).yaw(yaw).pitch(pitch);
    }

    public LocationBuilder addX(double amount) {
        return new LocationBuilder(location.clone()).addX(amount);
    }

    public LocationBuilder addY(double amount) {
        return new LocationBuilder(location.clone()).addY(amount);
    }

    public LocationBuilder addZ(double amount) {
        return new LocationBuilder(location.clone()).addZ(amount);
    }

    public LocationBuilder add(Vector vector) {
        return new LocationBuilder(location.clone()).add(vector);
    }

    public LocationBuilder subtract(Vector vector) {
        return new LocationBuilder(location.clone()).subtract(vector);
    }

    public LocationBuilder direction(Vector vector) {
        return new LocationBuilder(location.clone()).direction(vector);
    }

    public LocationBuilder pitch(float pitch) {
        return new LocationBuilder(location.clone()).pitch(pitch);
    }

    public LocationBuilder yaw(float yaw) {
        return new LocationBuilder(location.clone()).yaw(yaw);
    }

    public LocationBuilder forward(float amount) {
        return new LocationBuilder(location.clone()).forward(amount);
    }

    public LocationBuilder backward(float amount) {
        return new LocationBuilder(location.clone()).backward(amount);
    }

    public LocationBuilder left(float amount) {
        return new LocationBuilder(location.clone()).left(amount);
    }

    public LocationBuilder right(float amount) {
        return new LocationBuilder(location.clone()).right(amount);
    }
    
    public Location getBaseLocation() {
        return this.location;
    }

    public World getWorld() {
        return location.getWorld();
    }

    public double getX() {
        return location.getX();
    }

    public double getY() {
        return location.getY();
    }

    public double getZ() {
        return location.getZ();
    }

    public float getYaw() {
        return location.getYaw();
    }

    public float getPitch() {
        return location.getPitch();
    }

    @Override
    public String toString() {
        return "LocationFactory{" + location + '}';
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.location);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LocationFactory other = (LocationFactory) obj;
        return Objects.equals(this.location, other.location);
    }

}
