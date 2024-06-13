package com.ebicep.warlords.game.option.towerdefense.path;

import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.java.MathUtils;
import org.bukkit.Location;

public enum PathDirection {
    X {
        @Override
        public LocationBuilder getForwardLocation(Location current, Location target) {
            return new LocationBuilder(target).z(current.getZ());
        }

        @Override
        public int compare(Location loc1, Location loc2, Location target) {
            if (loc1 == null) {
                return 1;
            }
            if (loc2 == null) {
                return -1;
            }
            double targetX = target.getX();
            // return x is closer - return int
            if (Math.abs(loc1.getX() - targetX) < Math.abs(loc2.getX() - targetX)) {
                return -1;
            }
            return 1;
        }

        @Override
        public Location getRandomSpawnLocation(Location waypoint, Location current) {
            // varying z by +-2 of waypoint z
            double waypointZ = waypoint.getZ();
            Location location = current.clone();
            location.setZ(waypointZ + MathUtils.generateRandomValueBetweenInclusive(-2, 2));
            return location;
        }
    },
    Z {
        @Override
        public LocationBuilder getForwardLocation(Location current, Location target) {
            return new LocationBuilder(target).x(current.getX());
        }

        @Override
        public int compare(Location loc1, Location loc2, Location target) {
            if (loc1 == null) {
                return 1;
            }
            if (loc2 == null) {
                return -1;
            }
            double targetZ = target.getZ();
            // return z is closer
            if (Math.abs(loc1.getZ() - targetZ) < Math.abs(loc2.getZ() - targetZ)) {
                return -1;
            }
            return 1;
        }

        @Override
        public Location getRandomSpawnLocation(Location waypoint, Location current) {
            // varying x by +-2 of waypoint x
            double waypointX = waypoint.getX();
            Location location = current.clone();
            location.setX(waypointX + MathUtils.generateRandomValueBetweenInclusive(-2, 2));
            return location;
        }
    },
    UNKNOWN {
        @Override
        public LocationBuilder getForwardLocation(Location current, Location target) {
            return new LocationBuilder(target);
        }

        @Override
        public int compare(Location loc1, Location loc2, Location target) {
            return 0;
        }

        @Override
        public Location getRandomSpawnLocation(Location waypoint, Location current) {
            Location location = current.clone();
            location.setX(waypoint.getX() + MathUtils.generateRandomValueBetweenInclusive(-2, 2));
            location.setZ(waypoint.getZ() + MathUtils.generateRandomValueBetweenInclusive(-2, 2));
            return location;
        }
    },
    ;

    public static PathDirection getPathDirection(Location loc1, Location loc2) {
        double x1 = loc1.getX();
        double x2 = loc2.getX();
        double z1 = loc1.getZ();
        double z2 = loc2.getZ();
        if (x1 == x2) {
            return Z;
        }
        if (z1 == z2) {
            return X;
        }
        return UNKNOWN;
    }

    public abstract LocationBuilder getForwardLocation(Location current, Location target);

    public abstract int compare(Location loc1, Location loc2, Location target);

    public abstract Location getRandomSpawnLocation(Location waypoint, Location current);
}
