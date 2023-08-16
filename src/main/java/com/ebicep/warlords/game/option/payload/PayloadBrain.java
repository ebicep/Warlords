package com.ebicep.warlords.game.option.payload;

import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

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
    private static final double MOVE_PER_TICK = 0.15;//25; // 1 block per 2 seconds = .5 blocks per second = .025 blocks per tick

    private final Location start;
    private final List<Location> path = new ArrayList<>();
    private final Location currentLocation;
    private double currentPathIndex = 0;

    public PayloadBrain(Location start) {
        this.start = start;
        this.currentLocation = start.clone();
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
     * @return true if the path is complete / reached end
     */
    public boolean tick() {
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
        currentLocation.add(direction.multiply(MOVE_PER_TICK));
        currentPathIndex += MOVE_PER_TICK;
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
}
