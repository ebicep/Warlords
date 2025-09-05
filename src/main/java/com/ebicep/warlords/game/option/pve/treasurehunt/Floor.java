package com.ebicep.warlords.game.option.pve.treasurehunt;

import com.ebicep.warlords.util.java.Pair;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Floor {

    private final List<PlacedRoom> placedRooms;
    private final int width;
    private final int length;
    private final boolean isValidPattern;

    private static final int KEEP_OUT = 3;       // cells of air around every room/corridor
    private static final int MAX_FRONTIER = 16;  // cap branching queue size
    private static final double SPREAD_BIAS = 0.7; // 70% pick farthest frontier to spread out
    private static final double TURN_BIAS = 0.5; // 70% prefer corners when possible
    private static final double JUNCTION_BIAS = 0.35;
    private static final int EDGE_TURN_MARGIN = 6; // tune 4–10 as you like
    private static final double TURN_AFTER_STRAIGHT = 0.70;

    public Floor(List<PlacedRoom> placedRooms, int width, int length, boolean isValidPattern) {
        this.placedRooms = placedRooms;
        this.width = width;
        this.length = length;
        this.isValidPattern = isValidPattern;
    }

    public static Floor generate(int maxWidth, int maxLength, List<Room> rooms, Random random, int amountOfRooms) {
        var placedRooms = new ArrayList<PlacedRoom>();
        var grouped = rooms.stream().collect(Collectors.groupingBy(Room::getRoomType));

        var selectedRoom = grouped.get(RoomType.START).get(random.nextInt(grouped.get(RoomType.START).size()));
        var lastPlacedRoom = new PlacedRoom(
                (maxWidth  - selectedRoom.getWidth())  / 2,
                (maxLength - selectedRoom.getLength()) / 2,
                selectedRoom);

        placedRooms.add(lastPlacedRoom);

        var newRooms = generateHallwayWithRoom(
                maxWidth,
                maxLength,
                () -> rooms.stream().filter(r -> r.getRoomType() == RoomType.NORMAL),
                () -> rooms.stream().filter(r -> r.getRoomType() == RoomType.END),
                random,
                placedRooms::stream,
                placedRooms.stream(),
                amountOfRooms
        );

        if (newRooms == null) {
            return new Floor(placedRooms, maxWidth, maxLength, false);
        }

        placedRooms.addAll(newRooms);

        int attempts = 0;
        int generatedTreasureRooms = 0;

        while (generatedTreasureRooms < 1) {
            attempts++;

            if (attempts > 10) {
                return new Floor(placedRooms, maxWidth, maxLength, false);
            }

            var newGeneratedHallway = generateHallwayWithRoom(
                    maxWidth,
                    maxLength,
                    () -> rooms.stream()
                            .filter(r -> r.getRoomType() == RoomType.NORMAL)
                            .filter(r -> r.getRoomConnections().size() >= 2),
                    () -> rooms.stream().filter(r -> r.getRoomType() == RoomType.TREASURE),
                    random,
                    placedRooms::stream,
                    placedRooms.stream(),
                    amountOfRooms
            );

            if (newGeneratedHallway == null) {
                continue;
            }

            placedRooms.addAll(newGeneratedHallway);

            generatedTreasureRooms++;
        }

        capCorridorEnds(placedRooms);
        trimUnmatchedConnectors(placedRooms);
        return new Floor(placedRooms, maxWidth, maxLength, true);
    }

    private static List<PlacedRoom> generateHallwayWithRoom(
            int maxWidth,
            int maxLength,
            Supplier<Stream<Room>> rooms,
            Supplier<Stream<Room>> endingRoom,
            Random random,
            Supplier<Stream<PlacedRoom>> placedRooms,
            Stream<PlacedRoom> startingPoint,
            int hallwayLength
    ) {
        var generatedHallway = generateHallway(
                maxWidth,
                maxLength,
                rooms,
                random,
                placedRooms,
                startingPoint,
                hallwayLength
        );

        if (generatedHallway == null) {
            return null;
        }

        var generatedTreasureRoom = generateRoom(
                maxWidth,
                maxLength,
                endingRoom,
                random,
                () -> Stream.concat(placedRooms.get(), generatedHallway.stream()),
                Stream.of(generatedHallway.get(generatedHallway.size() - 1))
        );

        if (generatedTreasureRoom == null) {
            return null;
        }

        generatedHallway.add(generatedTreasureRoom);

        return generatedHallway;
    }

    private static List<PlacedRoom> generateHallway(
            int maxWidth,
            int maxLength,
            Supplier<Stream<Room>> rooms,
            Random random,
            Supplier<Stream<PlacedRoom>> placedRooms,
            Stream<PlacedRoom> startingPoint,
            int hallwayLength
    ) {
        List<PlacedRoom> built = new ArrayList<>();
        List<PlacedRoom> frontier = startingPoint.collect(Collectors.toCollection(ArrayList::new));
        if (frontier.isEmpty()) return null;

        for (int i = 0; i < hallwayLength; i++) {
            // pick the farthest open end most of the time -> spreads out
            PlacedRoom pivot;
            if (random.nextDouble() < SPREAD_BIAS) {
                var all = Stream.concat(placedRooms.get(), built.stream()).collect(Collectors.toList());
                double cx = all.stream().mapToDouble(r -> r.getX() + r.getWidth()/2.0).average().orElse(0);
                double cz = all.stream().mapToDouble(r -> r.getZ() + r.getLength()/2.0).average().orElse(0);
                pivot = frontier.stream().max(Comparator.comparingDouble(f -> {
                    double fx = f.getX() + f.getWidth()/2.0;
                    double fz = f.getZ() + f.getLength()/2.0;
                    double dx = fx - cx, dz = fz - cz;
                    return dx*dx + dz*dz;
                })).orElse(frontier.get(0));
            } else {
                pivot = frontier.get(random.nextInt(frontier.size()));
            }

            var newRoom = generateRoom(
                    maxWidth, maxLength, rooms, random,
                    () -> Stream.concat(built.stream(), placedRooms.get()),
                    Stream.of(pivot)
            );

            if (newRoom == null) {
                frontier.remove(pivot);
                if (frontier.isEmpty()) return built.isEmpty() ? null : built;
                i--; // try again this step with a different pivot
                continue;
            }

            built.add(newRoom);
            frontier.add(newRoom);
            int extraOpen = (int) newRoom.getRoomConnections().stream()
                    .filter(con -> isOpenConnector(newRoom, con, () -> Stream.concat(built.stream(), placedRooms.get())))
                    .count() - 1; // minus the side we just used

            for (int k = 0; k < Math.max(0, extraOpen); k++) {
                frontier.add(newRoom); // duplicates = higher chance to pick soon
            }
            if (frontier.size() > MAX_FRONTIER) frontier.remove(0);
        }

        return built;
    }

    private static PlacedRoom generateRoom(
            int maxWidth,
            int maxLength,
            Supplier<Stream<Room>> rooms,
            Random random,
            Supplier<Stream<PlacedRoom>> placedRooms,
            Stream<PlacedRoom> startingPoint
    ) {
        return startingPoint.flatMap(lastPlacedRoom -> {
                    final boolean prevStraight = wasPrevStraight(lastPlacedRoom); // capture once

                    return lastPlacedRoom.getRoomConnections().stream()
                            .filter(con -> isOpenConnector(lastPlacedRoom, con, placedRooms))
                            .flatMap(selectedConnection -> rooms.get()
                            .flatMap(r -> r.getRoomConnections().stream().map(s -> new Pair<>(r, s)))
                            .filter(p -> p.getB().getRotation().isOpposite(selectedConnection.getRotation()))
                            .map(p -> {
                                PlacedRoom pr = new PlacedRoom(
                                        lastPlacedRoom.getX() + selectedConnection.getX() - p.getB().getX() + selectedConnection.getRotation().getX(),
                                        lastPlacedRoom.getZ() + selectedConnection.getZ() - p.getB().getZ() + selectedConnection.getRotation().getZ(),
                                        p.getA()
                                );
                                return new Pair<>(pr, prevStraight);
                            })
                            );
                })
                .filter(pair -> { var p = pair.getA();
                    return p.getX() >= 0 && p.getZ() >= 0 &&
                                p.getX() + p.getWidth() <= maxWidth &&
                                p.getZ() + p.getLength() <= maxLength; })
                .filter(pair -> placedRooms.get().noneMatch(other -> pair.getA().overlaps(other)))
                .filter(pair -> placedRooms.get().allMatch(other -> {
                    var p = pair.getA();
                    if (connectsTo(p, other)) return true;
                    if (isCorridor(p) && isCorridor(other)) return true;
                    return boxDistanceChebyshev(p, other) >= KEEP_OUT;
                }))
                .filter(pair -> placedRooms.get().noneMatch(other -> pair.getA().checkHasValidConnections(other)))
                .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                    if (list.isEmpty()) return null;

                    // unpack
                    java.util.function.Function<Pair<PlacedRoom,Boolean>, PlacedRoom> R = Pair::getA;
                    boolean prevStraight = list.stream().anyMatch(Pair::getB);

                    var junctions = list.stream().map(R).filter(pr -> pr.getRoomConnections().size() >= 3).toList();
                    var corners   = list.stream().map(R).filter(Floor::isCornerPiece).toList();

                    List<PlacedRoom> pool = list.stream().map(R).collect(Collectors.toList());
                    if (prevStraight && !corners.isEmpty() && random.nextDouble() < TURN_AFTER_STRAIGHT) {
                        pool = corners;
                    } else if (!junctions.isEmpty() && random.nextDouble() < JUNCTION_BIAS) {
                        pool = junctions;
                    } else if (!corners.isEmpty() && random.nextDouble() < TURN_BIAS) {
                        pool = corners;
                    }

                    return pool.stream()
                            .max(Comparator.comparingDouble(pr -> spreadScore(pr, () -> Stream.concat(placedRooms.get(), Stream.empty()))))
                            .orElse(pool.get(random.nextInt(pool.size())));
                }));
    }

    // SOUTH = +Z
    // NORTH = -Z
    // WEST = +X
    // EAST = -X

    public static void main(String[] args) {
        var rooms = new ArrayList<Room>();
        int roomSize = 7;
        rooms.add(makeDemoRoom(roomSize, roomSize, RoomType.START, true, false, false, false));
        // T junction
        rooms.add(makeDemoRoom(roomSize, roomSize, RoomType.NORMAL, true, true, true, false));
        rooms.add(makeDemoRoom(roomSize, roomSize, RoomType.NORMAL, false, true, true, true));
        rooms.add(makeDemoRoom(roomSize, roomSize, RoomType.NORMAL, true, false, true, true));
        rooms.add(makeDemoRoom(roomSize, roomSize, RoomType.NORMAL, true, true, false, true));

        rooms.add(makeDemoRoom(roomSize, roomSize, RoomType.NORMAL, true, true, true, true));
        rooms.add(makeDemoRoom(roomSize, roomSize, RoomType.NORMAL, false, false, true, true));
        rooms.add(makeDemoRoom(roomSize, roomSize, RoomType.NORMAL, true, true, false, false));
        rooms.add(makeDemoRoom(roomSize, roomSize, RoomType.TREASURE, true, false, false, false));
        rooms.add(makeDemoRoom(roomSize, roomSize, RoomType.TREASURE, false, true, false, false));
        rooms.add(makeDemoRoom(roomSize, roomSize, RoomType.TREASURE, false, false, true, false));
        rooms.add(makeDemoRoom(roomSize, roomSize, RoomType.TREASURE, false, false, false, true));
        rooms.add(makeDemoRoom(roomSize, roomSize, RoomType.END, true, true, true, true));

        var random = new Random();
        Floor floor;
        do {
            floor = generate(160, 160, rooms, random, 40);
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
                int cx = room.getX() + connection.getX();
                int cz = room.getZ() + connection.getZ();
                grid[cz][cx] = ' ';
                int bx = cx + connection.getRotation().getX();
                int bz = cz + connection.getRotation().getZ();
                if (bz >= 0 && bz < this.length && bx >= 0 && bx < this.width) {
                    if (grid[bz][bx] == ' ') grid[bz][bx] = '.';
                }
            }

            drawCorridorInterior(grid, room);
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

        if (north) connections.add(new RoomConnection((x - 1) / 2, 0, x % 2 == 1 ? RoomFace.NORTH_ODD_PARITY : RoomFace.NORTH_EVEN_PARITY));
        if (east) connections.add(new RoomConnection(x - 1, z / 2, z % 2 == 1 ? RoomFace.EAST_ODD_PARITY : RoomFace.EAST_EVEN_PARITY));
        if (south) connections.add(new RoomConnection(x / 2, z - 1, x % 2 == 1 ? RoomFace.SOUTH_ODD_PARITY : RoomFace.SOUTH_EVEN_PARITY));
        if (west) connections.add(new RoomConnection(0, (z - 1) / 2, z % 2 == 1 ? RoomFace.WEST_ODD_PARITY : RoomFace.WEST_EVEN_PARITY));

        return new Room(x, z, type, connections, RoomSize.S_16X16X16, false, false, false);
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

    private static boolean overlapsWithMargin(PlacedRoom a, PlacedRoom b, int m) {
        int ax1 = a.getX() - m,                az1 = a.getZ() - m;
        int ax2 = a.getX() + a.getWidth() - 1 + m;
        int az2 = a.getZ() + a.getLength() - 1 + m;

        int bx1 = b.getX() - m,                bz1 = b.getZ() - m;
        int bx2 = b.getX() + b.getWidth() - 1 + m;
        int bz2 = b.getZ() + b.getLength() - 1 + m;

        return ax1 <= bx2 && ax2 >= bx1 && az1 <= bz2 && az2 >= bz1;
    }

    // corridors = NORMAL with exactly 2 connectors
    private static boolean isCorridor(PlacedRoom r) {
        return r.getRoomType() == RoomType.NORMAL && r.getRoomConnections().size() == 2;
    }

    private static void trimUnmatchedConnectors(List<PlacedRoom> placed) {
        for (int i = 0; i < placed.size(); i++) {
            PlacedRoom pr = placed.get(i);

            // compute which connectors actually have an opposing neighbor
            var matched = pr.getRoomConnections().stream().filter(rc -> hasOpposing(pr, rc, placed)).toList();
            if (matched.size() == pr.getRoomConnections().size()) continue; // already perfect
            if (matched.isEmpty()) continue; // isolated (rare), leave as-is or remove if you want

            boolean n=false,e=false,s=false,w=false;
            for (var rc : matched) {
                var f = rc.getRotation();
                if (isNorth(f)) n = true; else if (isEast(f)) e = true;
                else if (isSouth(f)) s = true; else if (isWest(f)) w = true;
            }

            RoomType type = (matched.size() == 1) ? RoomType.DEAD_END : RoomType.NORMAL;
            Room replacement = Room.makeSimpleRoom(pr.getWidth(), pr.getLength(), type, n, e, s, w);
            placed.set(i, new PlacedRoom(pr.getX(), pr.getZ(), replacement));
        }
    }

    // Is the given connector on 'room' still unused (no opposing connector already there)?
    private static boolean isOpenConnector(PlacedRoom room, RoomConnection con, Supplier<Stream<PlacedRoom>> placedRooms) {
        int tx = room.getX() + con.getX() + con.getRotation().getX();
        int tz = room.getZ() + con.getZ() + con.getRotation().getZ();

        return placedRooms.get()
                .filter(other -> other != room)
                .noneMatch(other ->
                        other.getRoomConnections().stream().anyMatch(oc ->
                                tx == other.getX() + oc.getX() + oc.getRotation().getX() &&
                                        tz == other.getZ() + oc.getZ() + oc.getRotation().getZ()
                        )
                );
    }

    // Does this 2-connector corridor bend (corner) instead of straight?
    private static boolean isCornerPiece(PlacedRoom pr) {
        if (!isCorridor(pr)) return false;
        var a = pr.getRoomConnections().get(0).getRotation();
        var b = pr.getRoomConnections().get(1).getRotation();
        return !a.isOpposite(b); // opposite = straight; otherwise = corner
    }

    // Does 'candidate' connect to 'other' via any of other's connectors?
    private static boolean connectsTo(PlacedRoom candidate, PlacedRoom other) {
        return other.getRoomConnections().stream().anyMatch(con ->
                candidate.contains(
                        other.getX() + con.getX() + con.getRotation().getX(),
                        other.getZ() + con.getZ() + con.getRotation().getZ()
                )
        );
    }

    private static int sep1D(int a1,int a2,int b1,int b2){ if(a2<b1) return b1-a2-1; if(b2<a1) return a1-b2-1; return 0; }
    private static int boxDistanceChebyshev(PlacedRoom a, PlacedRoom b){
        int ax1=a.getX(), ax2=a.getX()+a.getWidth()-1,  az1=a.getZ(), az2=a.getZ()+a.getLength()-1;
        int bx1=b.getX(), bx2=b.getX()+b.getWidth()-1,  bz1=b.getZ(), bz2=b.getZ()+b.getLength()-1;
        return Math.max(sep1D(ax1,ax2,bx1,bx2), sep1D(az1,az2,bz1,bz2));
    }

    private static double spreadScore(PlacedRoom candidate, Supplier<Stream<PlacedRoom>> placedRooms) {
        var existing = placedRooms.get().collect(Collectors.toList());
        if (existing.isEmpty()) return 0;

        double cx = existing.stream().mapToDouble(r -> r.getX()+r.getWidth()/2.0).average().orElse(0);
        double cz = existing.stream().mapToDouble(r -> r.getZ()+r.getLength()/2.0).average().orElse(0);
        double mx = candidate.getX()+candidate.getWidth()/2.0;
        double mz = candidate.getZ()+candidate.getLength()/2.0;
        double dCentroid = Math.hypot(mx - cx, mz - cz);

        double dNearest = existing.stream()
                .mapToDouble(r -> boxDistanceChebyshev(candidate, r))
                .min().orElse(0);

        return 1.0 * dCentroid + 2.0 * dNearest;
    }

    private void drawCorridorInterior(char[][] grid, PlacedRoom room) {
        if (room.getRoomType() != RoomType.NORMAL || room.getRoomConnections().size() != 2) return;

        var a = room.getRoomConnections().get(0);
        var b = room.getRoomConnections().get(1);

        int ax = room.getX() + a.getX() - a.getRotation().getX();
        int az = room.getZ() + a.getZ() - a.getRotation().getZ();
        int bx = room.getX() + b.getX() - b.getRotation().getX();
        int bz = room.getZ() + b.getZ() - b.getRotation().getZ();

        // straight hallway
        if (ax == bx) {
            for (int z = Math.min(az, bz); z <= Math.max(az, bz); z++) grid[z][ax] = '.';
            return;
        }
        if (az == bz) {
            for (int x = Math.min(ax, bx); x <= Math.max(ax, bx); x++) grid[az][x] = '.';
            return;
        }

        // corner hallway: go via the room center
        int mx = room.getX() + room.getWidth()  / 2;
        int mz = room.getZ() + room.getLength() / 2;
        for (int x = Math.min(ax, mx); x <= Math.max(ax, mx); x++) grid[az][x] = '.';
        for (int z = Math.min(az, mz); z <= Math.max(az, mz); z++) grid[z][mx] = '.';
        for (int x = Math.min(mx, bx); x <= Math.max(mx, bx); x++) grid[bz][x] = '.';
    }

    private static boolean isStraightCandidate(Room candidateRoom, RoomConnection candidateConn) {
        return candidateRoom.getRoomConnections().stream()
                .anyMatch(rc -> rc != candidateConn && rc.getRotation().isOpposite(candidateConn.getRotation()));
    }

    private static boolean hasOpposing(PlacedRoom a, RoomConnection ac, List<PlacedRoom> others) {
        int tx = a.getX() + ac.getX() + ac.getRotation().getX();
        int tz = a.getZ() + ac.getZ() + ac.getRotation().getZ();
        return others.stream()
                .filter(o -> o != a)
                .anyMatch(o -> o.getRoomConnections().stream().anyMatch(oc ->
                        oc.getRotation().isOpposite(ac.getRotation()) &&
                                tx == o.getX() + oc.getX() + oc.getRotation().getX() &&
                                tz == o.getZ() + oc.getZ() + oc.getRotation().getZ()
                ));
    }

    private static boolean isNorth(RoomFace f){return f==RoomFace.NORTH_EVEN_PARITY||f==RoomFace.NORTH_ODD_PARITY;}
    private static boolean isEast (RoomFace f){return f==RoomFace.EAST_EVEN_PARITY ||f==RoomFace.EAST_ODD_PARITY;}
    private static boolean isSouth(RoomFace f){return f==RoomFace.SOUTH_EVEN_PARITY||f==RoomFace.SOUTH_ODD_PARITY;}
    private static boolean isWest (RoomFace f){return f==RoomFace.WEST_EVEN_PARITY ||f==RoomFace.WEST_ODD_PARITY;}

    /** Turn any 2-connector corridor with only one actual neighbor into a DEAD_END that faces inward. */
    private static void capCorridorEnds(List<PlacedRoom> placed) {
        for (int i = 0; i < placed.size(); i++) {
            PlacedRoom pr = placed.get(i);
            if (!isCorridor(pr)) continue;

            var a = pr.getRoomConnections().get(0);
            var b = pr.getRoomConnections().get(1);
            boolean aMatched = hasOpposing(pr, a, placed);
            boolean bMatched = hasOpposing(pr, b, placed);

            if ((aMatched && bMatched) || (!aMatched && !bMatched)) continue;

            // Keep only the matched (inward) side
            RoomFace keep = aMatched ? a.getRotation() : b.getRotation();
            boolean n=false,e=false,s=false,w=false;
            if (isNorth(keep)) n=true; else if (isEast(keep)) e=true;
            else if (isSouth(keep)) s=true; else if (isWest(keep)) w=true;

            // Build a DEAD_END of the same footprint pointing inward
            Room cap = Room.makeSimpleRoom(pr.getWidth(), pr.getLength(), RoomType.DEAD_END, n, e, s, w);
            placed.set(i, new PlacedRoom(pr.getX(), pr.getZ(), cap));
        }
    }

    private static boolean wasPrevStraight(PlacedRoom last) {
        if (!isCorridor(last)) return false;
        var rc = last.getRoomConnections();
        if (rc.size() != 2) return false;
        return rc.get(0).getRotation().isOpposite(rc.get(1).getRotation());
    }

    public boolean isValidPattern() {
        return isValidPattern;
    }

    public List<PlacedRoom> getPlacedRooms() {
        return placedRooms;
    }
}
