package com.ebicep.warlords.game.option.pve.treasurehunt;

public class PlacedRoom {

    private int x;
    private int z;
    private Room room;

    public PlacedRoom(int x, int z, Room room) {
        this.x = x;
        this.z = z;
        this.room = room;
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

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
