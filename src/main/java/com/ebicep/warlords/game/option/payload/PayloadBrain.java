package com.ebicep.warlords.game.option.payload;

import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class PayloadBrain {

    public static final double DEFAULT_FORWARD_MOVE_PER_TICK = 0.15;//25; // 1 block per 2 seconds = .5 blocks per second = .025 blocks per tick
    private static final double DIAGONAL_DISTANCE = 1.41421356237;
    private static final Material TARGET_MATERIAL = Material.BEDROCK;
    private static final Material END_MATERIAL = Material.SCULK;
    private static final List<UnaryOperator<LocationBuilder>> NEXT_PATH_CHECKS = new ArrayList<>() {{
        add(locationBuilder -> locationBuilder.forward(1));
        add(locationBuilder -> locationBuilder.left(1));
        add(locationBuilder -> locationBuilder.right(1));
        // diagonal forward
        add(locationBuilder -> locationBuilder.forward(1).left(1));
        add(locationBuilder -> locationBuilder.forward(1).right(1));
        // diagonal up and down
        add(locationBuilder -> locationBuilder.forward(1).addY(1));
        add(locationBuilder -> locationBuilder.forward(1).addY(-1));
    }};
    private final Location start;
    private final List<PathEntry> path = new ArrayList<>();
    private final Location currentLocation;
    private final double forwardMovePerTick;
    private final double backwardMovePerTick;
    private double mappedPathIndex = 0;

    public PayloadBrain(@Nonnull Location start) {
        this(start, DEFAULT_FORWARD_MOVE_PER_TICK, 0);
    }

    public PayloadBrain(@Nonnull Location start, double forwardMovePerTick, double backwardMovePerTick) {
        this.start = start;
        findPath();
        boolean pathEmpty = path.isEmpty();
        if (!pathEmpty) {
            PathEntry pathEntry = path.get(path.size() - 1);
            path.set(path.size() - 1, new PathEntry(pathEntry.mappedIndex, new LocationBuilder(pathEntry.location).centerXZ()));
        }
        this.currentLocation = pathEmpty ? start.clone() : path.get(0).location.clone();
        this.forwardMovePerTick = forwardMovePerTick;
        this.backwardMovePerTick = backwardMovePerTick;
    }

    private void findPath() {
        // check in a cross shape in front and check for end material then target material
        // if end material, add to path then end
        // if target material, add to path and set current then continue
        // if none found, end
        LocationBuilder current = new LocationBuilder(start.toCenterLocation());
        while (true) {
            boolean found = false;
            for (UnaryOperator<LocationBuilder> check : NEXT_PATH_CHECKS) {
                LocationBuilder nextLocation = check.apply(current.clone()).centerXZ();
                // prevent recursion
                PathEntry lastPath = path.isEmpty() ? null : path.get(path.size() - 1);
                Location lastLocation = lastPath == null ? null : lastPath.location;
                if (lastLocation != null && lastLocation.getX() == nextLocation.getX() && lastLocation.getY() == nextLocation.getY() && lastLocation.getZ() == nextLocation.getZ()) {
                    continue;
                }
                Material nextLocationMaterial = nextLocation.getBlock().getType();
                if (nextLocationMaterial == TARGET_MATERIAL || nextLocationMaterial == END_MATERIAL) {
                    Vector newDirection = current.getVectorTowards(nextLocation);
                    boolean isDiagonal = newDirection.getX() != 0 && newDirection.getZ() != 0;
                    double mappedIndex = lastPath == null ? 0 : lastPath.mappedIndex + (isDiagonal ? DIAGONAL_DISTANCE : 1);
                    path.add(new PathEntry(mappedIndex, nextLocation));
                    current = nextLocation;
                    current.setDirection(newDirection.setY(0));
                    if (nextLocationMaterial == END_MATERIAL) {
                        return;
                    }
                    found = true;
                    break;
                }
            }
            if (!found) {
                return;
            }
        }
    }

    public void reset() {
        mappedPathIndex = 0;
        path.clear();
        findPath();
        resetCurrentLocation();
    }

    private void resetCurrentLocation() {
        Location pathStart = path.get(0).location;
        this.currentLocation.set(pathStart.getX(), pathStart.getY(), pathStart.getZ());
        this.currentLocation.setYaw(pathStart.getYaw());
        this.currentLocation.setPitch(pathStart.getPitch());
    }

    /**
     * @param netEscorting if 0 then no movement, if positive then forward, if negative then backward
     * @return true if the path is complete / reached end
     */
    public boolean tick(int netEscorting) {
        return tick(netEscorting > 0 ? this.forwardMovePerTick : -this.backwardMovePerTick);
    }

    /**
     * @param movePerTick how much the payload should move
     * @return true if the path is complete / reached end
     */
    public boolean tick(double movePerTick) {
        if (movePerTick == 0) {
            return false;
        }
        if (mappedPathIndex < 0) {
            return false;
        }
        mappedPathIndex += movePerTick;
        // at start
        if (mappedPathIndex < 0) {
            if (path.isEmpty()) {
                return false;
            }
            Location start = path.get(0).location;
            currentLocation.set(start.getX(), start.getY(), start.getZ());
            currentLocation.setYaw(start.getYaw());
            currentLocation.setPitch(start.getPitch());
            mappedPathIndex = 0;
            return false;
        }
        Pair<PathEntry, Integer> currentPathLocationMapped = getPathLocationMapped();
        int pathIndex = currentPathLocationMapped.getB();
        // reached end
        boolean goingForwards = movePerTick > 0;
        if (goingForwards && pathIndex >= path.size() - 1) {
            return true;
        }
        PathEntry currentPathEntry = currentPathLocationMapped.getA();
        PathEntry nextPathEntry = path.get(pathIndex + 1);
        LocationBuilder currentPathLoc = new LocationBuilder(currentPathEntry.location);
        currentPathLoc.faceTowards(nextPathEntry.location);
        currentPathLoc.forward(mappedPathIndex - currentPathEntry.mappedIndex);
        currentLocation.set(currentPathLoc.getX(), currentPathLoc.getY(), currentPathLoc.getZ());
        currentLocation.setDirection(currentPathLoc.getDirection());
        return pathIndex >= path.size();
    }

    private Pair<PathEntry, Integer> getPathLocationMapped() {
        return getPathLocationMapped(path, mappedPathIndex, start);
    }

    public static Pair<PathEntry, Integer> getPathLocationMapped(List<PayloadBrain.PathEntry> path, double mappedPathIndex, Location fallBack) {
        for (int i = 0; i < path.size(); i++) {
            PathEntry pathEntry = path.get(i);
            if (pathEntry.mappedIndex > mappedPathIndex) {
                int index = i - 1;
                return new Pair<>(path.get(index), index);
            }
        }
        if (path.isEmpty()) {
            return new Pair<>(new PathEntry(0, fallBack), 0);
        }
        return new Pair<>(path.get(path.size() - 1), path.size() - 1);
    }

    public Location getStart() {
        return start;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public List<PathEntry> getPath() {
        return path;
    }

    public double getMappedPathIndex() {
        return mappedPathIndex;
    }

    public void setMappedPathIndex(double mappedPathIndex) {
        this.mappedPathIndex = mappedPathIndex;
    }

    public double getForwardMovePerTick() {
        return forwardMovePerTick;
    }

    public double getBackwardMovePerTick() {
        return backwardMovePerTick;
    }

    public record PathEntry(double mappedIndex, Location location) {
    }

}
