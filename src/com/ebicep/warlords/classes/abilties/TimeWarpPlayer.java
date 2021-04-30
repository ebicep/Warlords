package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.WarlordsPlayer;
import org.bukkit.Location;
import org.bukkit.util.Vector;


public class TimeWarpPlayer {

    private WarlordsPlayer warlordsPlayer;
    private Location location;
    private Vector facing;
    private int time;

    public TimeWarpPlayer(WarlordsPlayer warlordsPlayer, Location location, Vector facing, int time) {
        this.warlordsPlayer = warlordsPlayer;
        this.location = location;
        this.facing = facing;
        this.time = time;
    }

    public WarlordsPlayer getWarlordsPlayer() {
        return warlordsPlayer;
    }

    public void setWarlordsPlayer(WarlordsPlayer warlordsPlayer) {
        this.warlordsPlayer = warlordsPlayer;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Vector getFacing() {
        return facing;
    }

    public void setFacing(Vector facing) {
        this.facing = facing;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
