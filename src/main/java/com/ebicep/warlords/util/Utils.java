package com.ebicep.warlords.util;

import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public class Utils {

    public static final String[] specsOrdered = {"Pyromancer", "Cryomancer", "Aquamancer", "Berserker", "Defender", "Revenant", "Avenger", "Crusader", "Protector", "Thunderlord", "Spiritguard", "Earthwarden"};

    public static final ItemStack[] woolSortedByColor = {
            new ItemStack(Material.WOOL, 1, (byte) 0),
            new ItemStack(Material.WOOL, 1, (byte) 8),
            new ItemStack(Material.WOOL, 1, (byte) 7),
            new ItemStack(Material.WOOL, 1, (byte) 15),
            new ItemStack(Material.WOOL, 1, (byte) 12),
            new ItemStack(Material.WOOL, 1, (byte) 14),
            new ItemStack(Material.WOOL, 1, (byte) 1),
            new ItemStack(Material.WOOL, 1, (byte) 4),
            new ItemStack(Material.WOOL, 1, (byte) 5),
            new ItemStack(Material.WOOL, 1, (byte) 13),
            new ItemStack(Material.WOOL, 1, (byte) 9),
            new ItemStack(Material.WOOL, 1, (byte) 3),
            new ItemStack(Material.WOOL, 1, (byte) 11),
            new ItemStack(Material.WOOL, 1, (byte) 10),
            new ItemStack(Material.WOOL, 1, (byte) 2),
            new ItemStack(Material.WOOL, 1, (byte) 6),
    };

    public static final Object OVERHEAL_MARKER = new Object();
    public static final int OVERHEAL_DURATION = 15;

    public static double getDotToPlayer(LivingEntity player1, LivingEntity player2, double yIncrease) {
        return getDotToLocation(new LocationBuilder(player1.getEyeLocation()).addY(yIncrease).get(), player2.getEyeLocation());
    }

    public static double getDotToPlayerEye(LivingEntity player1, LivingEntity player2) {
        return getDotToLocation(player1.getEyeLocation(), player2.getEyeLocation());
    }

    public static double getDotToPlayerCenter(LivingEntity player1, LivingEntity player2) {
        return getDotToLocation(new LocationBuilder(player1.getEyeLocation()).addY(.7).get(), player2.getEyeLocation());
    }

    public static double getDotToLocation(Location location1, Location location2) {
        Vector toEntity = location2.toVector().subtract(location1.toVector());
        return toEntity.normalize().dot(location1.getDirection());
    }

    public static boolean isLookingAt(LivingEntity player1, LivingEntity player2) {
        Location eye = new LocationBuilder(player1.getEyeLocation())
                .backward(4)
                .addY(.7)
                .get();
        return getDotToLocation(eye, player2.getEyeLocation()) > 0.925;
    }

    public static boolean isLookingAtIntervene(LivingEntity player1, LivingEntity player2) {
        Location eye = new LocationBuilder(player1.getEyeLocation())
                .backward(4)
                .addY(.7)
                .get();
        return getDotToLocation(eye, player2.getEyeLocation()) > 0.96;
    }

    public static boolean isLookingAtMark(LivingEntity player1, LivingEntity player2) {
        Location eye = new LocationBuilder(player1.getEyeLocation())
                .backward(5)
                .addY(.7)
                .get();
        return getDotToLocation(eye, player2.getEyeLocation()) > 0.95;
    }

    public static boolean isLookingAtChain(LivingEntity player1, LivingEntity player2) {
        Location eye = new LocationBuilder(player1.getEyeLocation())
                .backward(4)
                .addY(.7)
                .get();
        return getDotToLocation(eye, player2.getEyeLocation()) > 0.95 + (player1.getLocation().distanceSquared(player2.getLocation()) / 10000);
    }

    public static boolean isLookingAtWave(LivingEntity player1, LivingEntity player2) {
        Location eye = new LocationBuilder(player1.getEyeLocation())
                .addY(.7)
                .pitch(0)
                .get();
        return getDotToLocation(eye, player2.getEyeLocation()) > 0.91;
    }

    public static boolean hasLineOfSight(LivingEntity player, LivingEntity player2) {
        return player.hasLineOfSight(player2);
    }

    @Nullable
    public static ArmorStand getTotemDownAndClose(WarlordsPlayer warlordsPlayer, Entity searchNearby) {
        for (Entity entity : searchNearby.getNearbyEntities(5, 3, 5)) {
            if (entity instanceof ArmorStand && (entity.hasMetadata("capacitor-totem-" + warlordsPlayer.getName().toLowerCase()) || entity.hasMetadata("healing-totem-" + warlordsPlayer.getName().toLowerCase()))) {
                return (ArmorStand) entity;
            }
        }
        return null;
    }

    public static List<ArmorStand> getCapacitorTotemDownAndClose(WarlordsPlayer warlordsPlayer, Entity searchNearby) {
        List<ArmorStand> totems = new ArrayList<>();
        for (Entity entity : searchNearby.getNearbyEntities(5, 3, 5)) {
            if (entity instanceof ArmorStand && (entity.hasMetadata("capacitor-totem-" + warlordsPlayer.getName().toLowerCase()) || entity.hasMetadata("healing-totem-" + warlordsPlayer.getName().toLowerCase()))) {
                totems.add((ArmorStand) entity);
            }
        }
        return totems;
    }


    public static class ArmorStandComparator implements Comparator<Entity> {
        @Override
        public int compare(Entity a, Entity b) {
            return a instanceof ArmorStand && b instanceof ArmorStand ? 0 : a instanceof ArmorStand ? -1 : b instanceof ArmorStand ? 1 : 0;
        }
    }

    private static final Location LOCATION_CONTAINER = new Location(null, 0, 0, 0);

    public static Predicate<WarlordsPlayer> filterOnlyEnemies(@Nullable WarlordsPlayer wp) {
        return wp == null ? (player) -> false : wp::isEnemyAlive;
    }

    public static Predicate<WarlordsPlayer> filterOnlyTeammates(@Nullable WarlordsPlayer wp) {
        return wp == null ? (player) -> false : wp::isTeammateAlive;
    }

    private static final Location LOCATION_CACHE_SORT = new Location(null, 0, 0, 0);

    public static Comparator<Entity> sortClosestBy(Location loc) {
        return sortClosestBy(Entity::getLocation, loc);
    }

    public static <T> Comparator<T> sortClosestBy(BiConsumer<T, Location> map, Location loc) {
        return Comparator.comparing(e -> {
            map.accept(e, LOCATION_CACHE_SORT);
            return LOCATION_CACHE_SORT.distanceSquared(loc);
        });
    }

    private static final Location LOCATION_CACHE_DISTANCE = new Location(null, 0, 0, 0);

    public static Predicate<Entity> radiusAround(Location loc, double radius) {
        return radiusAround(loc, radius, radius, radius);
    }

    public static Predicate<Entity> radiusAround(Location loc, double x, double y, double z) {
        return radiusAround(Entity::getLocation, loc, x, y, z);
    }

    public static <T> Predicate<T> radiusAround(BiConsumer<T, Location> map, Location loc, double radius) {
        return radiusAround(map, loc, radius, radius, radius);
    }

    public static <T> Predicate<T> radiusAround(BiConsumer<T, Location> map, Location loc, double x, double y, double z) {
        return entity -> {
            map.accept(entity, LOCATION_CACHE_DISTANCE);
            double xDif = (loc.getX() - LOCATION_CACHE_DISTANCE.getX()) / x;
            double yDif = (loc.getY() - LOCATION_CACHE_DISTANCE.getY()) / y;
            double zDif = (loc.getZ() - LOCATION_CACHE_DISTANCE.getZ()) / z;
            return xDif * xDif + yDif * yDif + zDif * zDif <= 1;
        };
    }

    public static Vector getRightDirection(Location location) {
        Vector direction = location.getDirection().normalize();
        return new Vector(-direction.getZ(), 0.0, direction.getX()).normalize();
    }

    public static Vector getLeftDirection(Location location) {
        Vector direction = location.getDirection().normalize();
        return new Vector(direction.getZ(), 0.0, -direction.getX()).normalize();
    }

    public static double getDistance(WarlordsPlayer e, double accuracy) {
        return getDistance(e.getLocation(), accuracy);
    }

    public static double getDistance(Entity e, double accuracy) {
        return getDistance(e.getLocation(), accuracy);
    }

    public static double getDistance(Location original, double accuracy) {
        Location loc = original.clone();
        double distance = 0;
        for (double i = loc.getY(); i >= -100; i -= accuracy) {
            loc.setY(i);
            distance += accuracy;
            if (loc.getBlock().getType().isSolid()) {
                break;
            }
        }
        return distance;
    }

    public static void resetPlayerMovementStatistics(Player player) {
        player.setStatistic(Statistic.WALK_ONE_CM, 0);
        player.setStatistic(Statistic.HORSE_ONE_CM, 0);
    }

    public static int getPlayerMovementStatistics(Player player) {
        int walkStatistic = player.getStatistic(Statistic.WALK_ONE_CM);
        int horseStatistic = player.getStatistic(Statistic.HORSE_ONE_CM);
        return walkStatistic + horseStatistic;
    }

    public static boolean blocksInFrontOfLocation(Location location) {
        location = location.clone();
        location.setPitch(0);
        Location headLocationForward = location.clone().add(location.getDirection().multiply(1)).add(0, 1, 0);
        Location footLocationForward = location.clone().add(location.getDirection().multiply(1));
        return location.getWorld().getBlockAt(headLocationForward).getType() != Material.AIR && location.getWorld().getBlockAt(footLocationForward).getType() != Material.AIR;
    }

    public static boolean isMountableZone(Location location) {
        if (location.getWorld().getBlockAt(new LocationBuilder(location.clone()).y(2).get()).getType() == Material.NETHERRACK) {
            return location.getWorld().getBlockAt(new LocationBuilder(location.clone()).y(4).get()).getType() == Material.SOUL_SAND && !insideTunnel(location);
        }
        return true;
    }

    public static boolean insideTunnel(Location location) {
        Location aboveLocation = location.clone().add(0, 2, 0);
        for (int i = 0; i < 10; i++) {
            if (aboveLocation.getBlock().getType() != Material.AIR) {
                return true;
            }
            aboveLocation.add(0, 1, 0);
        }
        return false;

//        location.setPitch(0);
//        LocationBuilder aboveLocation = new LocationBuilder(location.clone());
//        LocationBuilder leftLocation = new LocationBuilder(location.clone());
//        LocationBuilder leftFrontLocation = new LocationBuilder(location.clone());
//        LocationBuilder leftBackLocation = new LocationBuilder(location.clone());
//        LocationBuilder rightLocation = new LocationBuilder(location.clone());
//        LocationBuilder rightFrontLocation = new LocationBuilder(location.clone());
//        LocationBuilder rightBackLocation = new LocationBuilder(location.clone());
//        boolean blocksAbove = false;
//        boolean blocksToLeft = false;
//        boolean blocksToLeftFront = false;
//        boolean blocksToLeftBack = false;
//        boolean blocksToRight = false;
//        boolean blocksToRightFront = false;
//        boolean blocksToRightBack = false;
//        for (int i = 0; i < 10; i++) {
//            if (!blocksAbove && aboveLocation.addY(1).get().getBlock().getType() != Material.AIR) {
//                blocksAbove = true;
//            }
//        }
//        for (int i = 0; i < 5; i++) {
//            if (!blocksToLeft && leftLocation.left(1).get().getBlock().getType() != Material.AIR) {
//                blocksToLeft = true;
//            }
//            if (!blocksToLeftFront && leftFrontLocation.left(1).forward(1).get().getBlock().getType() != Material.AIR) {
//                blocksToLeftFront = true;
//            }
//            if (!blocksToLeftBack && leftBackLocation.left(1).backward(1).get().getBlock().getType() != Material.AIR) {
//                blocksToLeftBack = true;
//            }
//            if (!blocksToRight && rightLocation.right(1).get().getBlock().getType() != Material.AIR) {
//                blocksToRight = true;
//            }
//            if (!blocksToRightFront && rightFrontLocation.right(1).forward(1).get().getBlock().getType() != Material.AIR) {
//                blocksToRightFront = true;
//            }
//            if (!blocksToRightBack && rightBackLocation.right(1).backward(1).get().getBlock().getType() != Material.AIR) {
//                blocksToRightBack = true;
//            }
//        }
        //0000x
        //xxxxx
        //0000x
//        boolean right = blocksToRight && blocksToRightFront && blocksToRightBack;
        //x0000
        //xxxxx
        //x0000
//        boolean left = blocksToLeft && blocksToLeftFront && blocksToLeftBack;
//        System.out.println(right);
//        System.out.println(left);
//        System.out.println("------");
//        return blocksAbove && ((right && !left) || (!right && left));
    }

    public static boolean isInCircleRadiusFast(Location locA, Location locB, double radius) {
        double radiusMin = -radius;
        double diffX = locA.getX() - locB.getX();
        if (diffX < radiusMin || diffX > radius) {
            return false;
        }
        double diffY = locA.getY() - locB.getY();
        if (diffY < radiusMin || diffY > radius) {
            return false;
        }
        double diffZ = locA.getZ() - locB.getZ();
        if (diffZ < radiusMin || diffZ > radius) {
            return false;
        }
        return diffX * diffX + diffY * diffY + diffZ * diffZ < radius * radius;
    }

    /**
     * Checks if an <code>Iterable</code> contains an item matched by the given
     * predicate.
     *
     * @param <T> The type of the items
     * @param iterable The list of items
     * @param matcher The matcher
     * @return return true if any item matches, false otherwise. Empty iterables return false.
     */
    public static <T> boolean collectionHasItem(@Nonnull Iterable<T> iterable, @Nonnull Predicate<? super T> matcher) {
        for (T item : iterable) {
            if (matcher.test(item)) {
                return true;
            }
        }
        return false;
    }
    @Nullable
    public static <T> T arrayGetItem(@Nonnull T[] iterable, @Nonnull Predicate<? super T> matcher) {
        for (T item : iterable) {
            if (matcher.test(item)) {
                return item;
            }
        }
        return null;
    }
    
    /**
     * Allows an Stream to be used in a for-each loop, as they do not come out of the box with support for this.
     * @param <T> The type
     * @param stream The stream
     * @return An one-time use <code>Iterable</code> for iterating over the stream 
     */
    @Nonnull
    public static <T> Iterable<T> iterable(@Nonnull Stream<T> stream) {
        return stream::iterator;
    }
    
    /**
     * Collector to pick a random element from a <code>Stream</code>
     * @param <T> The type of the element
     * @return A collector for picking a random element, or null if the stream is empty
     * @see Stream#collect(java.util.stream.Collector) 
     */
    public static <T> Collector<T, Pair<Integer, T>, T> randomElement() {
        return Collector.of(
                () -> new Pair<>(0, null),
                (i, a) -> {
                    int count = i.getA();
                    if(count == 0) {
                        i.setA(1);
                        i.setB(a);
                    } else {
                        i.setA(count + 1);
                        if (Math.random() < 1d / count) {
                            i.setB(a);
                        }
                    }
                },
                (a, b) -> {
                    int count = a.getA() + b.getA();
                    if (Math.random() * count >= a.getA()) {
                        a.setB(b.getB());
                    }
                    a.setA(count);
                    return a;
                },
                (i) -> {
                    return i.getB();
                },
                Collector.Characteristics.CONCURRENT,
                Collector.Characteristics.UNORDERED
        );
    }
    
}
