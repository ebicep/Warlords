package com.ebicep.warlords.game.option.towerdefense;

import com.ebicep.warlords.util.java.Pair;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class TowerDefensePath {

    private final Location spawn;
    private final List<Location> path;
    private final List<Pair<Double, Double>> forwardPath = new ArrayList<>();

    public TowerDefensePath(Location spawn, List<Location> path) {
        this.spawn = spawn;
        this.path = path;
        Location current = spawn.clone();
        for (Location location : path) {
            forwardPath.add(new Pair<>(getXZDistance(current, location), getYDistance(current, location)));
            current = location;
        }
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

    public List<Pair<Double, Double>> getForwardPath() {
        return forwardPath;
    }

    public List<Location> getPath() {
        return path;
    }
}
