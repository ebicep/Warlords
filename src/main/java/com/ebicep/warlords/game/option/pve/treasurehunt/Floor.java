package com.ebicep.warlords.game.option.pve.treasurehunt;

import com.ebicep.warlords.util.java.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class Floor {

    private final List<PlacedRoom> placedRooms;
    private final int width;
    private final int length;

    public Floor(List<PlacedRoom> placedRooms, int width, int length) {
        this.placedRooms = placedRooms;
        this.width = width;
        this.length = length;
    }

    public static Floor generate(int maxWidth, int maxLength, List<Room> rooms, Random random) {
        var placedRooms = new ArrayList<PlacedRoom>();
        var grouped = rooms.stream().collect(Collectors.groupingBy(Room::getRoomType));

        var selectedRoom = grouped.get(RoomType.START).get(random.nextInt(grouped.get(RoomType.START).size()));
        var lastPlacedRoom = new PlacedRoom(
                random.nextInt(maxWidth - selectedRoom.getWidth()),
                random.nextInt(maxLength - selectedRoom.getLength()),
                selectedRoom);

        placedRooms.add(lastPlacedRoom);

        var selectedConnections = selectedRoom.getRoomConnections();

        for (int i = 0; i < 2; i++) {
            //lastPlacedRoom.getRoom().getRoomConnections().stream().flatMap(selectedConnections);
            selectedConnections = new ArrayList<>(selectedConnections);
            Collections.shuffle(selectedConnections, random);
            for (var selectedConnection : selectedConnections) {
                rooms.stream()
                        .filter(r -> r.getRoomType() == RoomType.NORMAL)
                        .flatMap(r -> r.getRoomConnections().stream().map(s -> new Pair<>(r, s)))
                        .filter(p -> p.getB().getRotation().isOpposite(selectedConnection.getRotation()))
                        .map(p -> new PlacedRoom(
                                lastPlacedRoom.getX() + selectedConnection.getX() - p.getB().getX(),
                                lastPlacedRoom.getZ() + selectedConnection.getZ() - p.getB().getZ(),
                                p.getA()
                        ));
            }
        }

        return new Floor(placedRooms, maxWidth, maxLength);
    }

    // SOUTH = +Z
    // NORTH = -Z
    // WEST = +X
    // EAST = -X

    public static void main(String[] args) {
        var rooms = new ArrayList<Room>();
        rooms.add(new Room(10, 10, RoomType.START, List.of(
                new RoomConnection(5, 0, RoomFace.NORTH_ODD_PARITY)
        )));
        rooms.add(new Room(10, 10, RoomType.END, List.of(
                new RoomConnection(5, 0, RoomFace.NORTH_ODD_PARITY),
                new RoomConnection(5, 10, RoomFace.SOUTH_ODD_PARITY)
        )));
        rooms.add(new Room(10, 10, RoomType.TREASURE, List.of(
                new RoomConnection(0, 5, RoomFace.NORTH_ODD_PARITY)
        )));
        rooms.add(new Room(10, 10, RoomType.TREASURE, List.of(
                new RoomConnection(0, 5, RoomFace.WEST_ODD_PARITY),
                new RoomConnection(10, 5, RoomFace.EAST_OOD_PARITY)
        )));
        rooms.add(new Room(10, 10, RoomType.NORMAL, List.of(
                new RoomConnection(5, 0, RoomFace.NORTH_ODD_PARITY),
                new RoomConnection(5, 10, RoomFace.SOUTH_ODD_PARITY)
        )));
        rooms.add(new Room(10, 10, RoomType.NORMAL, List.of(
                new RoomConnection(0, 5, RoomFace.WEST_ODD_PARITY),
                new RoomConnection(10, 5, RoomFace.EAST_OOD_PARITY)
        )));
        rooms.add(new Room(10, 10, RoomType.NORMAL, List.of(
                new RoomConnection(5, 0, RoomFace.NORTH_ODD_PARITY),
                new RoomConnection(0, 5, RoomFace.WEST_ODD_PARITY),
                new RoomConnection(10, 5, RoomFace.EAST_OOD_PARITY),
                new RoomConnection(5, 10, RoomFace.SOUTH_ODD_PARITY)
        )));

        var floor = generate(80, 80, rooms, new Random(50));

        System.out.println(floor);
    }

    @Override
    public String toString() {
        char[][] grid = new char[this.length][this.width];

        for (var row : grid) {
            Arrays.fill(row, ' ');
        }

        for (int i = 0; i < placedRooms.size(); i++) {
            var room = placedRooms.get(i);
            for (int x = 0; x < room.getRoom().getWidth(); x++) {
                for (int z = 0; z < room.getRoom().getLength(); z++) {
                    if (x == 0 || z == 0 || x == room.getRoom().getWidth() - 1 || z == room.getRoom().getLength() - 1) {
                        grid[room.getZ() + z][room.getX() + x] = (char) ('0' + i);
                    }
                }
            }

            for (var connection : room.getRoom().getRoomConnections()) {
                grid[room.getZ() + connection.getZ()][room.getX() + connection.getX()] = '!';
            }
        }

        StringBuilder builder = new StringBuilder(length * width + length);

        for (var row : grid) {
            builder.append(row);
            builder.append('\n');
        }

        return builder.toString();
    }
}
