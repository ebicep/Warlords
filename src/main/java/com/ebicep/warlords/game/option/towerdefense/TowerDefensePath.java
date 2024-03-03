package com.ebicep.warlords.game.option.towerdefense;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class TowerDefensePath {

    enum PathDirection {
        X {
            @Override
            public Location getForwardLocation(Location current, Location target) {
                Location location = target.clone();
                location.setZ(current.getZ());
                return location;
            }
        },
        Z {
            @Override
            public Location getForwardLocation(Location current, Location target) {
                Location location = target.clone();
                location.setX(current.getX());
                return location;
            }
        },
        UNKNOWN {
            @Override
            public Location getForwardLocation(Location current, Location target) {
                return target;
            }
        },
        ;

        public abstract Location getForwardLocation(Location current, Location target);
    }

    public record PathLocation(Location location, PathDirection pathDirection) {

    }

    private final Location spawn;
    private final List<PathLocation> path = new ArrayList<>();

    public TowerDefensePath(Location spawn, List<Location> path) {
        this.spawn = spawn;
        for (int i = 0; i < path.size(); i++) {
            Location previous = i == 0 ? spawn : path.get(i - 1);
            Location current = path.get(i);
            PathDirection direction = getPathDirection(previous, current);
            this.path.add(new PathLocation(current, direction));
        }
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

    private static double getXZDistance(Location loc1, Location loc2) {
        double x1 = loc1.getX();
        double x2 = loc2.getX();
        double z1 = loc1.getZ();
        double z2 = loc2.getZ();
        if (x1 == x2 && z1 == z2) {
            return 0;
        }
        // use abs since path direction will always be forward
        if (x1 == x2) {
            return Math.abs(z1 - z2);
        }
        if (z1 == z2) {
            return Math.abs(x1 - x2);
        }
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(z1 - z2, 2));
    }

    private static double getYDistance(Location loc1, Location loc2) {
        return loc1.getY() - loc2.getY();
    }

    public Location getSpawn() {
        return spawn;
    }

    public List<PathLocation> getPath() {
        return path;
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

}
