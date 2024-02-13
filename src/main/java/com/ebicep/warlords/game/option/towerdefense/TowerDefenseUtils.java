package com.ebicep.warlords.game.option.towerdefense;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.LocationMarker;
import com.ebicep.warlords.game.option.marker.SpawnLocationMarker;
import com.ebicep.warlords.game.option.towerdefense.towers.TowerRegistry;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.List;
import java.util.stream.Collectors;

public class TowerDefenseUtils {

    static int getFastYaw(Location from, Location to) {
        return getFastYaw(from.getX(), from.getZ(), to.getX(), to.getZ());
    }

    static int getFastYaw(double x1, double z1, double x2, double z2) {
        if (x1 > x2) {
            return 90;
        } else if (x1 < x2) {
            return -90;
        } else if (z1 > z2) {
            return 180;
        } else {
            return 0;
        }
    }

    static void alignToBottomRightCorner(TowerRegistry tower, Material type, LocationBuilder bottomRightCorner) {
        // move backwards and to the right until not same type, max tower size times
        int move = tower.getSize();
        for (int i = 0; i < move; i++) {
            bottomRightCorner.backward(1);
            if (bottomRightCorner.getBlock().getType() != type) {
                bottomRightCorner.forward(1);
                break;
            }
        }
        for (int i = 0; i < move; i++) {
            bottomRightCorner.right(1);
            if (bottomRightCorner.getBlock().getType() != type) {
                bottomRightCorner.left(1);
                break;
            }
        }
    }

    /**
     * @param location location to convert to block face
     * @return block face, NE, NW, SE, SW
     */
    static BlockFace locationToBlockFace(Location location, boolean cartesian) {
        float yaw = location.getYaw();
        if (cartesian) {
            if (yaw > 0) {
                if (67.5 <= yaw && yaw <= 112.5) {
                    return BlockFace.WEST;
                }
                if (yaw <= 90) {
                    return BlockFace.SOUTH_WEST;
                }
                return BlockFace.NORTH_WEST;
            }
            if (yaw >= -90) {
                return BlockFace.SOUTH_EAST;
            }
            return BlockFace.NORTH_EAST;
        }
        if (-67.5 <= yaw && yaw <= -22.5) {
            return BlockFace.SOUTH_EAST;
        }
        if (-22.5 < yaw && yaw < 22.5) {
            return BlockFace.SOUTH;
        }
        if (22.5 <= yaw && yaw <= 67.5) {
            return BlockFace.SOUTH_WEST;
        }
        if (67.5 < yaw && yaw < 112.5) {
            return BlockFace.WEST;
        }
        if (112.5 <= yaw && yaw <= 157.5) {
            return BlockFace.NORTH_WEST;
        }
        if (-157.5 <= yaw && yaw <= -112.5) {
            return BlockFace.NORTH_EAST;
        }
        if (-112.5 < yaw && yaw < -67.5) {
            return BlockFace.EAST;
        }
        return BlockFace.NORTH;
    }

    static List<Location> getTeamSpawnLocations(Game game, Team team) {
        return game.getMarkers(SpawnLocationMarker.class)
                   .stream()
                   .filter(marker -> marker.getPriorityTeam(team) == 0)
                   .map(LocationMarker::getLocation)
                   .collect(Collectors.toList());

    }

    static boolean insideArea(Location point, Location location1, Location location2) {
        return point.getX() >= Math.min(location1.getX(), location2.getX()) &&
                point.getX() <= Math.max(location1.getX(), location2.getX()) &&
                point.getY() >= Math.min(location1.getY(), location2.getY()) &&
                point.getY() <= Math.max(location1.getY(), location2.getY()) &&
                point.getZ() >= Math.min(location1.getZ(), location2.getZ()) &&
                point.getZ() <= Math.max(location1.getZ(), location2.getZ());
    }

}
