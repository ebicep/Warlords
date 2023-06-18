package com.ebicep.warlords.game.option.pve.treasurehunt;

import com.ebicep.warlords.util.java.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Floor {

    private final List<PlacedRoom> placedRooms;
    private final int width;
    private final int length;
    private final boolean isValidPattern;

    public Floor(List<PlacedRoom> placedRooms, int width, int length, boolean isValidPattern) {
        this.placedRooms = placedRooms;
        this.width = width;
        this.length = length;
        this.isValidPattern = isValidPattern;
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

        int pathLength = 30;
        for (int i = 0; i < pathLength; i++) {
            lastPlacedRoom = generateRoom(maxWidth, maxLength, () -> rooms.stream().filter(r -> r.getRoomType() == RoomType.NORMAL), random, placedRooms, lastPlacedRoom);

            if (lastPlacedRoom == null) {
                return new Floor(placedRooms, maxWidth, maxLength, false);
            }

            placedRooms.add(lastPlacedRoom);
        }

        lastPlacedRoom = generateRoom(maxWidth, maxLength, () -> rooms.stream().filter(r -> r.getRoomType() == RoomType.END), random, placedRooms, lastPlacedRoom);

        if (lastPlacedRoom == null) {
            return new Floor(placedRooms, maxWidth, maxLength, false);
        }

        placedRooms.add(lastPlacedRoom);

        return new Floor(placedRooms, maxWidth, maxLength, true);
    }

    private static PlacedRoom generateRoom(
            int maxWidth,
            int maxLength,
            Supplier<Stream<Room>> rooms,
            Random random,
            ArrayList<PlacedRoom> placedRooms,
            PlacedRoom lastPlacedRoom
    ) {
        return lastPlacedRoom.getRoomConnections().stream().flatMap(selectedConnection -> rooms.get()
                .flatMap(
                        r -> r.getRoomConnections().stream().map(s -> new Pair<>(r, s))
                )
                .filter(
                        p -> p.getB().getRotation().isOpposite(selectedConnection.getRotation())
                )
                .map(p -> new PlacedRoom(
                        lastPlacedRoom.getX() + selectedConnection.getX() - p.getB().getX() + selectedConnection.getRotation().getX(),
                        lastPlacedRoom.getZ() + selectedConnection.getZ() - p.getB().getZ() + selectedConnection.getRotation().getZ(),
                        p.getA()
                )))
                .filter(
                        p -> p.getX() >= 0 &&
                                p.getZ() >= 0 &&
                                p.getX() + p.getWidth() < maxWidth &&
                                p.getZ() + p.getLength() < maxLength
                )
                .filter(p -> placedRooms.stream().noneMatch(p::overlaps))
                .filter(candidate -> placedRooms.stream().noneMatch(
                        candidate::checkHasValidConnections
                ))
                .collect(randomElement(random));
    }

    // SOUTH = +Z
    // NORTH = -Z
    // WEST = +X
    // EAST = -X

    public static void main(String[] args) {
        var rooms = new ArrayList<Room>();
        int roomSize = 7;
        rooms.add(makeDemoRoom(roomSize, roomSize, RoomType.START, true, false, false, false));
        rooms.add(makeDemoRoom(roomSize, roomSize, RoomType.NORMAL, true, true, true, false));
        rooms.add(makeDemoRoom(roomSize, roomSize, RoomType.NORMAL, false, true, true, true));
        rooms.add(makeDemoRoom(roomSize, roomSize, RoomType.NORMAL, true, true, true, true));
        rooms.add(makeDemoRoom(roomSize, roomSize, RoomType.NORMAL, false, false, true, true));
        rooms.add(makeDemoRoom(roomSize, roomSize, RoomType.NORMAL, true, true, false, false));
        rooms.add(makeDemoRoom(roomSize, roomSize, RoomType.END, true, true, true, true));

        var random = new Random(180);
        Floor floor;
        do {
            floor = generate(160, 160, rooms, random);
            System.out.println("Generation: " + floor.isValidPattern);
        } while (!floor.isValidPattern);

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
            grid[room.getZ() + room.getLength() / 2][room.getX() + room.getWidth() / 2] = room.getRoomType().name().charAt(0);
            for (int x = 0; x < room.getWidth(); x++) {
                for (int z = 0; z < room.getLength(); z++) {
                    if (x == 0 || z == 0 || x == room.getWidth() - 1 || z == room.getLength() - 1) {
                        grid[room.getZ() + z][room.getX() + x] = (char) ((i < 10 ? '0' : 'A' - 10) + i);
                    }
                }
            }

            for (var connection : room.getRoomConnections()) {
                grid[room.getZ() + connection.getZ()][room.getX() + connection.getX()] = ' ';
            }
        }

        StringBuilder builder = new StringBuilder(length * width + length + 6);

        builder.append(isValidPattern).append('\n');

        for (var row : grid) {
            builder.append(row);
            builder.append('\n');
        }

        return builder.toString();
    }

    private static Room makeDemoRoom(int x, int z, RoomType type, boolean north, boolean east, boolean south, boolean west) {
        var connections = new ArrayList<RoomConnection>();

        if (north) connections.add(new RoomConnection(x / 2, 0, x % 2 == 1 ? RoomFace.NORTH_ODD_PARITY : RoomFace.NORTH_EVEN_PARITY));
        if (east) connections.add(new RoomConnection(x - 1, z / 2, z % 2 == 1 ? RoomFace.EAST_ODD_PARITY : RoomFace.EAST_EVEN_PARITY));
        if (south) connections.add(new RoomConnection(x / 2, z - 1, x % 2 == 1 ? RoomFace.SOUTH_ODD_PARITY : RoomFace.SOUTH_EVEN_PARITY));
        if (west) connections.add(new RoomConnection(0, z / 2, z % 2 == 1 ? RoomFace.WEST_ODD_PARITY : RoomFace.WEST_EVEN_PARITY));

        return new Room(x, z, type, connections);
    }

    /**
     * Collector to pick a random element from a <code>Stream</code>
     * @param <T> The type of the element
     * @return A collector for picking a random element, or null if the stream is empty
     * @see Stream#collect(java.util.stream.Collector)
     */
    private static <T> Collector<T, Pair<Integer, T>, T> randomElement(Random rdn) {
        return Collector.of(
                () -> new Pair<>(0, null),
                (i, a) -> {
                    int count = i.getA();
                    if(count == 0) {
                        i.setA(1);
                        i.setB(a);
                    } else {
                        i.setA(count + 1);
                        if (rdn.nextDouble() < 1d / count) {
                            i.setB(a);
                        }
                    }
                },
                (a, b) -> {
                    int count = a.getA() + b.getA();
                    if (rdn.nextFloat() * count >= a.getA()) {
                        a.setB(b.getB());
                    }
                    a.setA(count);
                    return a;
                },
                Pair::getB,
                Collector.Characteristics.CONCURRENT,
                Collector.Characteristics.UNORDERED
        );
    }
}
