package com.ebicep.warlords.util.bukkit;


import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class LocationBuilder extends Location {

    public LocationBuilder(Location location) {
        super(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public LocationBuilder(World world, double x, double y, double z) {
        super(world, x, y, z);
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

    @Nonnull
    @Override
    public LocationBuilder add(@Nonnull Vector vector) {
        super.add(vector);
        return this;
    }

    @Nonnull
    @Override
    public LocationBuilder subtract(@Nonnull Vector vector) {
        super.subtract(vector);
        return this;
    }

    public LocationBuilder direction(Vector vector) {
        this.setDirection(vector);
        return this;
    }

    public LocationBuilder faceTowards(Location location) {
        if (location.getX() == getX() && location.getY() == getY() && location.getZ() == getZ()) {
            return this;
        }
        this.setDirection(getVectorTowards(location));
        return this;
    }

    public Vector getVectorTowards(Location location) {
        return location.toVector().subtract(this.toVector()).normalize();
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

    public LocationBuilder forward(double amount) {
        this.add(this.getDirection().multiply(amount));
        return this;
    }

    public LocationBuilder backward(float amount) {
        this.add(this.getDirection().multiply(-amount));
        return this;
    }

    public LocationBuilder left(float amount) {
        this.add(LocationUtils.getLeftDirection(this).multiply(amount));
        return this;
    }

    public LocationBuilder left(double amount) {
        this.add(LocationUtils.getLeftDirection(this).multiply(amount));
        return this;
    }

    public LocationBuilder right(float amount) {
        this.add(LocationUtils.getRightDirection(this).multiply(amount));
        return this;
    }

    public LocationBuilder lookLeft() {
        yaw(getYaw() - 90);
        return this;
    }

    public LocationBuilder lookRight() {
        yaw(getYaw() + 90);
        return this;
    }

    public LocationBuilder lookBackwards() {
        yaw(getYaw() + 180);
        return this;
    }

    public LocationBuilder center() {
        this.setX(this.getBlockX() + 0.5);
        this.setY(this.getBlockY() + 0.5);
        this.setZ(this.getBlockZ() + 0.5);
        return this;
    }

    public LocationBuilder centerXZBlock() {
        this.setX(this.getBlockX() + 0.5);
        this.setY(this.getBlockY());
        this.setZ(this.getBlockZ() + 0.5);
        return this;
    }

    public LocationBuilder centerXZ() {
        this.setX(this.getBlockX() + 0.5);
        this.setZ(this.getBlockZ() + 0.5);
        return this;
    }

    @Nonnull
    @Override
    public LocationBuilder clone() {
        return (LocationBuilder) super.clone();
    }

}
