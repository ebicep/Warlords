package com.ebicep.warlords.game.option.pve.treasurehunt;

import java.util.ArrayList;
import java.util.List;

public class Room {

    private final int width;
    private final int length;
    private final RoomType roomType;
    private final List<RoomConnection> roomConnections;
    private final RoomSize size;
    private final boolean treasure;
    private final boolean boss;
    private final boolean intermission;

    public Room(int width, int length, RoomType roomType, List<RoomConnection> roomConnections, RoomSize size, boolean treasure, boolean boss, boolean intermission) {
        this.width = width;
        this.length = length;
        this.roomType = roomType;
        this.roomConnections = roomConnections;
        this.size = size;
        this.treasure = treasure;
        this.boss = boss;
        this.intermission = intermission;
    }

    public static Room makeSimpleRoom(int x, int z, RoomType type, boolean north, boolean east, boolean south, boolean west) {
        var connections = new ArrayList<RoomConnection>();

        if (north) connections.add(new RoomConnection((x - 1) / 2, 0, x % 2 == 1 ? RoomFace.NORTH_ODD_PARITY : RoomFace.NORTH_EVEN_PARITY));
        if (east) connections.add(new RoomConnection(x - 1, z / 2, z % 2 == 1 ? RoomFace.EAST_ODD_PARITY : RoomFace.EAST_EVEN_PARITY));
        if (south) connections.add(new RoomConnection(x / 2, z - 1, x % 2 == 1 ? RoomFace.SOUTH_ODD_PARITY : RoomFace.SOUTH_EVEN_PARITY));
        if (west) connections.add(new RoomConnection(0, (z - 1) / 2, z % 2 == 1 ? RoomFace.WEST_ODD_PARITY : RoomFace.WEST_EVEN_PARITY));

        return new Room(x, z, type, connections, RoomSize.S_16X16X16, false, false, false);
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    public List<RoomConnection> getRoomConnections() {
        return roomConnections;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public RoomSize getSize() { return size; }

    public boolean isTreasure() { return treasure; }

    public boolean isBoss() { return boss; }

    public boolean isIntermission() { return intermission; }
}
