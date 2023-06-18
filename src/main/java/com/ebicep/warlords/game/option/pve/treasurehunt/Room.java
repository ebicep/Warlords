package com.ebicep.warlords.game.option.pve.treasurehunt;

import java.util.List;

public class Room {

    private int width;
    private int length;
    private RoomType roomType;
    private List<RoomConnection> roomConnections;

    public Room(int width, int length, RoomType roomType, List<RoomConnection> roomConnections) {
        this.width = width;
        this.length = length;
        this.roomType = roomType;
        this.roomConnections = roomConnections;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public List<RoomConnection> getRoomConnections() {
        return roomConnections;
    }

    public void setRoomConnections(List<RoomConnection> roomConnections) {
        this.roomConnections = roomConnections;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }
}
