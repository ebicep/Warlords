package com.ebicep.warlords.game.option.towerdefense;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class TowerDefensePath {

    private final Location spawn;
    private final List<PathLocation> path = new ArrayList<>();
    private final double totalDistance;

    public TowerDefensePath(Location spawn, List<Location> path) {
        this.spawn = spawn;
        double totalDistance = 0;
        for (int i = 0; i < path.size(); i++) {
            Location previous = i == 0 ? spawn : path.get(i - 1);
            Location current = path.get(i);
            PathDirection direction = getPathDirection(previous, current);
            totalDistance += previous.distance(current);
            this.path.add(new PathLocation(current, direction, totalDistance));
        }
        this.totalDistance = totalDistance;
    }

    private static PathDirection getPathDirection(Location loc1, Location loc2) {
        double x1 = loc1.getX();
        double x2 = loc2.getX();
        double z1 = loc1.getZ();
        double z2 = loc2.getZ();
        if (x1 == x2) {
            return PathDirection.Z;
        }
        if (z1 == z2) {
            return PathDirection.X;
        }
        return PathDirection.UNKNOWN;
    }

    public Location getSpawn() {
        return spawn;
    }

    public List<PathLocation> getPath() {
        return path;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    // generate rgb color object for path based on hash
    public int getRed() {
        return computeColorComponent(0);
    }

    public int getGreen() {
        return computeColorComponent(1);
    }

    public int getBlue() {
        return computeColorComponent(2);
    }

    // Helper method to compute the color component (red, green, or blue)
    private int computeColorComponent(int componentIndex) {
        // Extract the component by bit shifting
        int component = (path.hashCode() >> (8 * componentIndex)) & 0xFF;
        return component % 256; // Ensure the value falls within the range 0-255
    }

    enum PathDirection {
        X {
            @Override
            public Location getForwardLocation(Location current, Location target) {
                Location location = target.clone();
                location.setZ(current.getZ());
                return location;
            }

            @Override
            public int compare(Location loc1, Location loc2, Location target) {
                double targetX = target.getX();
                // return x is closer - return int
                if (Math.abs(loc1.getX() - targetX) < Math.abs(loc2.getX() - targetX)) {
                    return -1;
                }
                return 1;
            }
        },
        Z {
            @Override
            public Location getForwardLocation(Location current, Location target) {
                Location location = target.clone();
                location.setX(current.getX());
                return location;
            }

            @Override
            public int compare(Location loc1, Location loc2, Location target) {
                double targetZ = target.getZ();
                // return z is closer
                if (Math.abs(loc1.getZ() - targetZ) < Math.abs(loc2.getZ() - targetZ)) {
                    return -1;
                }
                return 1;
            }
        },
        UNKNOWN {
            @Override
            public Location getForwardLocation(Location current, Location target) {
                return target;
            }

            @Override
            public int compare(Location loc1, Location loc2, Location target) {
                return 0;
            }
        },
        ;

        public abstract Location getForwardLocation(Location current, Location target);

        public abstract int compare(Location loc1, Location loc2, Location target);
    }

    public record PathLocation(Location location, PathDirection pathDirection, double distance) {

    }

}
