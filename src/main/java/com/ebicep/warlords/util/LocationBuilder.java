package com.ebicep.warlords.util;


import org.bukkit.Location;
import org.bukkit.util.Vector;

public class LocationBuilder {

    private final Location location;

    public LocationBuilder(Location location) {
        this.location = location;
    }

    public LocationBuilder addXYZ(double x, double y, double z) {
        location.add(x, y, z);
        return this;
    }

    public LocationBuilder addX(double amount) {
        location.add(amount, 0, 0);
        return this;
    }

    public LocationBuilder addY(double amount) {
        location.add(0, amount, 0);
        return this;
    }

    public LocationBuilder addZ(double amount) {
        location.add(0, 0, amount);
        return this;
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

    public LocationBuilder forward(float amount) {
        location.add(location.getDirection().multiply(amount));
        return this;
    }

    public LocationBuilder backward(float amount) {
        location.add(location.getDirection().multiply(-amount));
        return this;
    }

    public LocationBuilder left(float amount) {
        location.add(Utils.getLeftDirection(location).multiply(amount));
        return this;
    }

    public LocationBuilder right(float amount) {
        location.add(Utils.getRightDirection(location).multiply(amount));
        return this;
    }

    public Location get() {
        return this.location;
    }

}
