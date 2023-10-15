package com.ebicep.warlords.game.option.payload;

import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class PayloadBrain {

    private static final Material TARGET_MATERIAL = Material.BEDROCK;
    private static final Material END_MATERIAL = Material.SCULK;
    private static final List<UnaryOperator<LocationBuilder>> NEXT_PATH_CHECKS = new ArrayList<>() {{
        add(locationBuilder -> locationBuilder.forward(1));
        add(locationBuilder -> locationBuilder.left(1));
        add(locationBuilder -> locationBuilder.right(1));
        add(locationBuilder -> locationBuilder.forward(1).addY(1));
        add(locationBuilder -> locationBuilder.forward(1).addY(-1));
    }};
    public static final double DEFAULT_FORWARD_MOVE_PER_TICK = 0.15;//25; // 1 block per 2 seconds = .5 blocks per second = .025 blocks per tick

    private final Location start;
    private final List<Location> path = new ArrayList<>();
    private final Location currentLocation;
    private final double forwardMovePerTick;
    private final double backwardMovePerTick;
    private double currentPathIndex = 0;

    public PayloadBrain(@Nonnull Location start) {
        this(start, DEFAULT_FORWARD_MOVE_PER_TICK, 0);
    }

    public PayloadBrain(@Nonnull Location start, double forwardMovePerTick, double backwardMovePerTick) {
        this.start = start;
        this.currentLocation = start.clone();
        this.forwardMovePerTick = forwardMovePerTick;
        this.backwardMovePerTick = backwardMovePerTick;
        ChatUtils.MessageType.WARLORDS.sendMessage("Start: " + start);
        findPath();
//        for (Location location : path) {
//            ChatUtils.MessageType.WARLORDS.sendMessage(location.toString());
//        }
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
                LocationBuilder nextLocation = check.apply(current.clone());
                // prevent recursion
                if (!path.isEmpty()) {
                    Location lastLocation = path.get(path.size() - 1);
                    if (lastLocation.getX() == nextLocation.getX() && lastLocation.getY() == nextLocation.getY() && lastLocation.getZ() == nextLocation.getZ()) {
                        continue;
                    }
                }
                Material nextLocationMaterial = nextLocation.getBlock().getType();
                if (nextLocationMaterial == TARGET_MATERIAL || nextLocationMaterial == END_MATERIAL) {
                    path.add(nextLocation);
                    Vector newDirection = current.getVectorTowards(nextLocation);
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
        if (currentPathIndex >= path.size()) {
            return true;
        }
        Location nextPathLocation;
        if (currentPathIndex >= path.size() - 1) {
            nextPathLocation = path.get(path.size() - 1);
        } else {
            nextPathLocation = path.get((int) (currentPathIndex + 1));
        }
        // set currentLocation facing nextPathLocation
        LocationBuilder location = new LocationBuilder(currentLocation);
        Vector direction = nextPathLocation.toVector().subtract(location.toVector()).normalize();
        currentLocation.setDirection(direction);
        currentLocation.add(direction.multiply(movePerTick));
        currentPathIndex += movePerTick;
        return false;
    }

    public Location getStart() {
        return start;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public List<Location> getPath() {
        return path;
    }

    public double getCurrentPathIndex() {
        return currentPathIndex;
    }

    public double getBackwardMovePerTick() {
        return backwardMovePerTick;
    }
}
