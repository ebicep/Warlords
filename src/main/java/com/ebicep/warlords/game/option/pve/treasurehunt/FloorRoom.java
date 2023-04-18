package com.ebicep.warlords.game.option.pve.treasurehunt;

import com.ebicep.warlords.util.bukkit.LocationFactory;
import org.bukkit.Location;

public abstract class FloorRoom {

    private String floorName;
    private int floorLevel;

    private final Location beginLocation;
    private final Location endLocation;

    protected FloorRoom(String floorName, int floorLevel, LocationFactory loc, double x1, double y1, double z1, double x2, double y2, double z2) {
        this.floorName = floorName;
        this.floorLevel = floorLevel;
        this.beginLocation = new Location(
                loc.getWorld(),
                loc.getX() + Math.min(x1, x2),
                loc.getY() + Math.min(y1, y2),
                loc.getZ() + Math.min(z1, z2)
        );
        this.endLocation = new Location(
                loc.getWorld(),
                loc.getX() + Math.max(x1, x2),
                loc.getY() + Math.max(y1, y2),
                loc.getZ() + Math.max(z1, z2)
        );
    }

    public String getFloorName() {
        return floorName;
    }

    public void setFloorName(String floorName) {
        this.floorName = floorName;
    }

    public int getFloorLevel() {
        return floorLevel;
    }

    public void setFloorLevel(int floorLevel) {
        this.floorLevel = floorLevel;
    }

    public Location getBeginLocation() {
        return beginLocation;
    }

    public Location getEndLocation() {
        return endLocation;
    }
}
