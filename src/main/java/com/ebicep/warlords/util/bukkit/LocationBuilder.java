package com.ebicep.warlords.util.bukkit;


import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class LocationBuilder extends Location {

    public LocationBuilder(Location location) {
        super(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public LocationBuilder x(double x) {
        this.setX(x);
        return this;
    }

    public LocationBuilder y(double y) {
        this.setY(y);
        return this;
    }

    public LocationBuilder z(double z) {
        this.setZ(z);
        return this;
    }

    public LocationBuilder addXYZ(double x, double y, double z) {
        this.add(x, y, z);
        return this;
    }

    public LocationBuilder addX(double amount) {
        this.add(amount, 0, 0);
        return this;
    }

    public LocationBuilder addY(double amount) {
        this.add(0, amount, 0);
        return this;
    }

    public LocationBuilder addZ(double amount) {
        this.add(0, 0, amount);
        return this;
    }

    @Override
    public LocationBuilder add(Vector vector) {
        super.add(vector);
        return this;
    }

    @Override
    public LocationBuilder subtract(Vector vector) {
        super.subtract(vector);
        return this;
    }

    public LocationBuilder direction(Vector vector) {
        this.setDirection(vector);
        return this;
    }

    public LocationBuilder pitch(float pitch) {
        this.setPitch(pitch);
        return this;
    }

    public LocationBuilder yaw(float yaw) {
        this.setYaw(yaw);
        return this;
    }

    public LocationBuilder forward(float amount) {
        this.add(this.getDirection().multiply(amount));
        return this;
    }

    public LocationBuilder backward(float amount) {
        this.add(this.getDirection().multiply(-amount));
        return this;
    }

    public LocationBuilder left(float amount) {
        this.add(Utils.getLeftDirection(this).multiply(amount));
        return this;
    }

    public LocationBuilder right(float amount) {
        this.add(Utils.getRightDirection(this).multiply(amount));
        return this;
    }

    /**
     * Gets a location
     * @return A location object
     * @deprecated The new <code>LocationBuilder</code> instances are locations, this method is no longer needed
     */
    @Deprecated
    public Location get() {
        return this;
    }

    @Override
    public LocationBuilder clone() {
        return (LocationBuilder) super.clone();
    }

}
