package com.ebicep.warlords.game.option.pve.treasurehunt;

import java.util.List;

public class PlacedRoom {

    private final int x;
    private final int z;
    private final Room room;

    public PlacedRoom(int x, int z, Room room) {
        this.x = x;
        this.z = z;
        this.room = room;
    }

    boolean contains(int x, int z) {
        return isBetween(this.getX(), this.getRoom().getWidth(), x) &&
                isBetween(this.getZ(), this.getRoom().getLength(), z);
    }

    private boolean hasValidConnections(PlacedRoom other) {
        return other.getRoomConnections().stream().anyMatch(existingRoomCon ->
                other.contains(
                        existingRoomCon.getX() + existingRoomCon.getRotation().getX() + other.getX(),
                        existingRoomCon.getZ() + existingRoomCon.getRotation().getZ() + other.getZ()
                ) &&
                this.getRoomConnections().stream().noneMatch(candidateRoomCon ->
                        candidateRoomCon.getX() + this.getX() ==
                        existingRoomCon.getX() + existingRoomCon.getRotation().getX() + other.getX() &&
                        candidateRoomCon.getZ() + this.getZ() ==
                        existingRoomCon.getZ() + existingRoomCon.getRotation().getZ() + other.getZ()
                )
        );
    }

    boolean checkHasValidConnections(PlacedRoom other) {
        return this.hasValidConnections(other) && other.hasValidConnections(this);
    }

    boolean overlaps(PlacedRoom other) {
        return this.getX() < other.getX() + other.getWidth() &&
                other.getX() < this.getX() + this.getWidth() &&
                this.getZ() < other.getZ() + other.getLength() &&
                other.getZ() < this.getZ() + this.getLength();
    }

    private static boolean isBetween(int rangeStart, int rangeLength, int point) {
        return point >= rangeStart && point < rangeStart + rangeLength;
    }

    public int getX() {
        return x;
    }

    public RoomType getRoomType() {
        return room.getRoomType();
    }

    public List<RoomConnection> getRoomConnections() {
        return room.getRoomConnections();
    }

    public int getWidth() {
        return room.getWidth();
    }

    public int getLength() {
        return room.getLength();
    }

    public int getZ() {
        return z;
    }

    public Room getRoom() {
        return room;
    }
}
