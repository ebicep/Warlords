package com.ebicep.warlords.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class LocationFactory {

    private final Location location;

    public LocationFactory(Location location) {
        this.location = location;
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

}
