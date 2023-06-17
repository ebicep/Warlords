package com.ebicep.warlords.game.option.pve.treasurehunt;

public class RoomConnection {

    private int x;
    private int z;
    private RoomFace rotation;

    public RoomConnection(int x, int z, RoomFace rotation) {
        this.x = x;
        this.z = z;
        this.rotation = rotation;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public RoomFace getRotation() {
        return rotation;
    }

    public void setRotation(RoomFace rotation) {
        this.rotation = rotation;
    }
}
