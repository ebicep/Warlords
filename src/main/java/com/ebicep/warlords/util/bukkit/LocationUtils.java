package com.ebicep.warlords.util.bukkit;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import static java.lang.Math.cos;

public class LocationUtils {
    private static final Location LOCATION_CACHE_SORT = new Location(null, 0, 0, 0);
    private static final Location LOCATION_CACHE_DISTANCE = new Location(null, 0, 0, 0);

    public static List<Location> getSphereLocations(Location center, double sphereRadius) {
        return getSphereLocations(center, sphereRadius, 10);
    }

    public static List<Location> getSphereLocations(Location center, double sphereRadius, double density) {
        center = center.clone();
        List<Location> locations = new ArrayList<>();
        //center.add(0, 1, 0);
        for (double i = 0; i <= Math.PI; i += Math.PI / density) {
            double radius = Math.sin(i) * sphereRadius + 0.5;
            double y = cos(i) * sphereRadius;
            for (double a = 0; a < Math.PI * 2; a += Math.PI / density) {
                double x = cos(a) * radius;
                double z = Math.sin(a) * radius;

                center.add(x, y, z);
                locations.add(center.clone());
                center.subtract(x, y, z);
            }
        }
        return locations;
    }

    public static double getDotToPlayer(LivingEntity player1, LivingEntity player2, double yIncrease) {
        return getDotToLocation(new LocationBuilder(player1.getEyeLocation()).addY(yIncrease), player2.getEyeLocation());
    }

    public static double getDotToLocation(Location location1, Location location2) {
        Vector toEntity = location2.toVector().subtract(location1.toVector());
        return toEntity.normalize().dot(location1.getDirection());
    }

    public static double getDotToPlayerEye(LivingEntity player1, LivingEntity player2) {
        return getDotToLocation(player1.getEyeLocation(), player2.getEyeLocation());
    }

    public static double getDotToPlayerCenter(LivingEntity player1, LivingEntity player2) {
        return getDotToLocation(new LocationBuilder(player1.getEyeLocation()).addY(.7), player2.getEyeLocation());
    }

    public static boolean isLookingAt(LivingEntity player1, LivingEntity player2) {
        Location eye = new LocationBuilder(player1.getEyeLocation())
                .backward(4)
                .addY(.7);
        return lookingAt(player2, eye, 0.925);
    }

    private static boolean lookingAt(LivingEntity player2, Location eye, double dot) {
        return getDotToLocation(eye, player2.getEyeLocation()) > dot;
    }

    public static boolean isLookingAtIntervene(LivingEntity player1, LivingEntity player2) {
        Location eye = new LocationBuilder(player1.getEyeLocation())
                .backward(4)
                .addY(.7);
        return lookingAt(player2, eye, 0.96);
    }

    public static boolean isLookingAtMark(LivingEntity player1, LivingEntity player2) {
        Location eye = new LocationBuilder(player1.getEyeLocation())
                .backward(5)
                .addY(.7);
        return lookingAt(player2, eye, 0.95);
    }

    public static boolean isLineOfSightAssassin(LivingEntity player1, LivingEntity player2) {
        Location eye = new LocationBuilder(player1.getEyeLocation())
                .backward(1)
                .addY(.7);
        return lookingAt(player2, eye, 0.7);
    }

    public static boolean isLineOfSightVindicator(LivingEntity player1, LivingEntity player2) {
        Location eye = new LocationBuilder(player1.getEyeLocation())
                .backward(2)
                .addY(.7);
        return lookingAt(player2, eye, 0.78);
    }

    public static boolean isLookingAtChain(LivingEntity player1, LivingEntity player2) {
        Location eye = new LocationBuilder(player1.getEyeLocation())
                .backward(4)
                .addY(.7);
        return lookingAt(player2, eye, 0.95 + (player1.getLocation().distanceSquared(player2.getLocation()) / 10000));
    }

    public static boolean isLookingAtWave(LivingEntity player1, LivingEntity player2) {
        Location eye = new LocationBuilder(player1.getEyeLocation())
                .addY(.7)
                .pitch(0);
        return lookingAt(player2, eye, 0.91);
    }

    public static boolean hasLineOfSight(LivingEntity player, LivingEntity player2) {
        return player.hasLineOfSight(player2);
    }

    public static double getDotToPlayer(WarlordsEntity player1, WarlordsEntity player2, double yIncrease) {
        return getDotToLocation(new LocationBuilder(player1.getEyeLocation()).addY(yIncrease), player2.getEyeLocation());
    }

    public static double getDotToPlayerEye(WarlordsEntity player1, WarlordsEntity player2) {
        return getDotToLocation(player1.getEyeLocation(), player2.getEyeLocation());
    }

    public static double getDotToPlayerCenter(WarlordsEntity player1, WarlordsEntity player2) {
        return getDotToLocation(new LocationBuilder(player1.getEyeLocation()).addY(.7), player2.getEyeLocation());
    }

    public static boolean isLookingAt(WarlordsEntity player1, WarlordsEntity player2) {
        Location eye = new LocationBuilder(player1.getEyeLocation())
                .backward(4)
                .addY(.7);
        return lookingAt(player2, eye, 0.925);
    }

    private static boolean lookingAt(WarlordsEntity player2, Location eye, double dot) {
        return getDotToLocation(eye, player2.getEyeLocation()) > dot;
    }

    public static boolean isLookingAtIntervene(WarlordsEntity player1, WarlordsEntity player2) {
        Location eye = new LocationBuilder(player1.getEyeLocation())
                .backward(4)
                .addY(.7);
        return lookingAt(player2, eye, 0.96);
    }

    public static boolean isLookingAtMark(WarlordsEntity player1, WarlordsEntity player2) {
        Location eye = new LocationBuilder(player1.getEyeLocation())
                .backward(5)
                .addY(.7);
        return lookingAt(player2, eye, 0.95);
    }

    public static boolean isLineOfSightAssassin(WarlordsEntity player1, WarlordsEntity player2) {
        Location eye = new LocationBuilder(player1.getEyeLocation())
                .backward(1)
                .addY(.7);
        return lookingAt(player2, eye, 0.7);
    }

    public static boolean isLineOfSightVindicator(WarlordsEntity player1, WarlordsEntity player2) {
        Location eye = new LocationBuilder(player1.getEyeLocation())
                .backward(2)
                .addY(.7);
        return lookingAt(player2, eye, 0.78);
    }

    public static boolean isLookingAtChain(WarlordsEntity player1, WarlordsEntity player2) {
        Location eye = new LocationBuilder(player1.getEyeLocation())
                .backward(4)
                .addY(.7);
        return lookingAt(player2, eye, 0.95 + (player1.getLocation().distanceSquared(player2.getLocation()) / 10000));
    }

    public static boolean isLookingAtWave(WarlordsEntity player1, WarlordsEntity player2) {
        Location eye = new LocationBuilder(player1.getEyeLocation())
                .addY(.7)
                .pitch(0);
        return lookingAt(player2, eye, 0.91);
    }

    public static boolean hasLineOfSight(WarlordsEntity player1, WarlordsEntity player2) {
        Entity entity1 = player1.getEntity();
        Entity entity2 = player2.getEntity();
        if (entity1 instanceof LivingEntity livingEntity) {
            return livingEntity.hasLineOfSight(entity2);
        } else if (entity2 instanceof LivingEntity livingEntity) {
            return livingEntity.hasLineOfSight(entity1);
        } else {
            return false;
        }
    }

    public static Comparator<Entity> sortClosestBy(Location loc) {
        return sortClosestBy(Entity::getLocation, loc);
    }

    public static <T> Comparator<T> sortClosestBy(BiConsumer<T, Location> map, Location loc) {
        return Comparator.comparing(e -> {
            map.accept(e, LOCATION_CACHE_SORT);
            return LOCATION_CACHE_SORT.distanceSquared(loc);
        });
    }

    public static Predicate<Entity> radiusAround(Location loc, double radius) {
        return radiusAround(loc, radius, radius, radius);
    }

    public static Predicate<Entity> radiusAround(Location loc, double x, double y, double z) {
        return radiusAround(Entity::getLocation, loc, x, y, z);
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

    public static <T> Predicate<T> radiusAround(BiConsumer<T, Location> map, Location loc, double radius) {
        return radiusAround(map, loc, radius, radius, radius);
    }

    public static Vector getRightDirection(Location location) {
        Vector direction = location.getDirection().normalize();
        return new Vector(-direction.getZ(), 0.0, direction.getX()).normalize();
    }

    public static Vector getLeftDirection(Location location) {
        Vector direction = location.getDirection().normalize();
        return new Vector(direction.getZ(), 0.0, -direction.getX()).normalize();
    }

    public static double getDistance(WarlordsEntity e, double accuracy) {
        return getDistance(e.getLocation(), accuracy);
    }

    public static double getDistance(Location original, double accuracy) {
        Location loc = original.clone();
        double distance = 0;
        for (double i = loc.getY(); i >= -100; i -= accuracy) {
            loc.setY(i);
            if (loc.getBlock().getType().isSolid()) {
                break;
            }
            distance += accuracy;
        }
        distance -= accuracy;
        return distance;
    }

    public static double getDistance(Entity e, double accuracy) {
        return getDistance(e.getLocation(), accuracy);
    }

    public static boolean blocksInFrontOfLocation(Location location) {
        location = location.clone();
        location.setPitch(0);
        Location headLocationForward = location.clone().add(location.getDirection().multiply(1)).add(0, 1, 0);
        Location footLocationForward = location.clone().add(location.getDirection().multiply(1));
        return location.getWorld().getBlockAt(headLocationForward).getType() != Material.AIR &&
                location.getWorld().getBlockAt(headLocationForward).getType().data != Stairs.class &&
                location.getWorld().getBlockAt(headLocationForward).getType().data != Slab.class &&
                location.getWorld().getBlockAt(footLocationForward).getType() != Material.AIR;
    }

    public static boolean isMountableZone(Location location) {
        if (location.getWorld().getBlockAt(new LocationBuilder(location.clone()).y(2)).getType() == Material.NETHERRACK) {
            return location.getWorld().getBlockAt(new LocationBuilder(location.clone()).y(4)).getType() == Material.SOUL_SAND && !insideTunnel(location);
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

    @Nonnull
    public static Location getGroundLocation(Player player) {
        return getGroundLocation(player.getLocation());
    }

    @Nonnull
    public static Location getGroundLocation(Location startLocation) {
        //get location of block below player that isnt air using loop
        Location location = startLocation.clone();
        location.setY(location.getY() - 1);
        while (!location.getBlock().isSolid() && location.getY() > 0) {
            location.setY(location.getY() - 1);
        }
        if (location.getY() <= 0) {
            return startLocation;
        }
        location.setY(startLocation.getWorld().getBlockAt(location).getLocation().getY() + 1);
        return location;
    }

    /**
     * Return A List Of Locations That
     * Make Up A Circle Using A Provided
     * Center, Radius, And Desired Points.
     *
     * @param center
     * @param radius
     * @param amount
     * @return
     */
    public static List<Location> getCircle(Location center, float radius, int amount) {
        World world = center.getWorld();
        double increment = ((2 * Math.PI) / amount);
        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            float angle = (float) (i * increment);
            float x = (float) (center.getX() + (radius * Math.cos(angle)));
            float z = (float) (center.getZ() + (radius * Math.sin(angle)));
            Location location = new Location(world, x, center.getY(), z);
            locations.add(location);
        }
        return locations;
    }

    /**
     * Gets all the blocks in a circle centered around a location.
     *
     * @param center The center point of the circle.
     * @param radius The radius of the circle.
     * @return A list of all the blocks within the specified circle.
     */
    public static List<Block> getCircleBlocks(Location center, int radius) {
        World world = center.getWorld();
        List<Block> blocks = new ArrayList<>();
        int centerX = center.getBlockX();
        int centerY = center.getBlockY();
        int centerZ = center.getBlockZ();

        int radiusSquared = radius * radius;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z <= radiusSquared) {
                    blocks.add(world.getBlockAt(centerX + x, centerY, centerZ + z));
                }
            }
        }

        return blocks;
    }

    public static List<Location> getSquare(Location center, float radius) {
        //X--X
        //|  |
        //Y--Y
        LocationBuilder locationBuilder = new LocationBuilder(center)
                .pitch(0);
        List<Location> locations = new ArrayList<>();
        //X--X
        LocationBuilder forward = locationBuilder.clone().forward(radius);
        locations.add(forward.clone().left(radius));
        locations.add(forward.clone().right(radius));
        //Y--Y
        LocationBuilder backwards = locationBuilder.clone().backward(radius);
        locations.add(backwards.clone().left(radius));
        locations.add(backwards.clone().right(radius));
        return locations;
    }

    public static List<Location> getVerticalRectangle(Location bottomCenter, float radius, float height) {
        //X---------X
        //|         |
        //|         |
        //X----S----X
        List<Location> locations = new ArrayList<>();
        // start bottom left
        LocationBuilder start = new LocationBuilder(bottomCenter)
                .pitch(0)
                .left(radius);
        for (int i = 0; i < radius * 2 + 1; i++) {
            for (int j = 0; j < height; j++) {
                locations.add(start.clone().addY(j));
            }
            start.right(1);
        }
        return locations;
    }

    public record TimedLocationBlockHolder(LocationBlockHolder locationBlockHolder, long time) {
        public TimedLocationBlockHolder(LocationBlockHolder locationBlockHolder) {
            this(locationBlockHolder, System.currentTimeMillis());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            TimedLocationBlockHolder that = (TimedLocationBlockHolder) o;
            return locationBlockHolder.equals(that.locationBlockHolder);
        }

        @Override
        public int hashCode() {
            return Objects.hash(locationBlockHolder);
        }
    }

    public record LocationBlockHolder(World world, int x, int y, int z) {
        public LocationBlockHolder(Location location) {
            this(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        }

        public Block getBlock() {
            return world.getBlockAt(x, y, z);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final LocationBlockHolder other = (LocationBlockHolder) obj;

            if (!Objects.equals(this.world, other.world)) {
                return false;
            }
            if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
                return false;
            }
            if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
                return false;
            }
            if (Double.doubleToLongBits(this.z) != Double.doubleToLongBits(other.z)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;

            hash = 19 * hash + (world != null ? world.hashCode() : 0);
            hash = 19 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
            hash = 19 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
            hash = 19 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
            return hash;
        }
    }

    public record LocationXYZ(double x, double y, double z) {
        public LocationXYZ(Location location) {
            this(location.getX(), location.getY(), location.getZ());
        }
    }
}
