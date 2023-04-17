package com.ebicep.warlords.util.warlords;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class Utils {

    // ’

    public static final String[] specsOrdered = {
            "Pyromancer",
            "Cryomancer",
            "Aquamancer",
            "Berserker",
            "Defender",
            "Revenant",
            "Avenger",
            "Crusader",
            "Protector",
            "Thunderlord",
            "Spiritguard",
            "Earthwarden",
            "Assassin",
            "Vindicator",
            "Apothecary"
    };

    public static boolean isProjectile(String ability) {
        return ability.equals("Fireball") ||
                ability.equals("Frostbolt") ||
                ability.equals("Water Bolt") ||
                ability.equals("Lightning Bolt") ||
                ability.equals("Flame Burst") ||
                ability.equals("Fallen Souls") ||
                ability.equals("Soothing Elixir");
    }

    public static boolean isPrimaryProjectile(String ability) {
        return ability.equals("Fireball") ||
                ability.equals("Frostbolt") ||
                ability.equals("Water Bolt") ||
                ability.equals("Lightning Bolt") ||
                ability.equals("Fallen Souls");
    }

    // Sorted wool id color
    // https://prnt.sc/UN80GeSpeyly
    private static final ItemStack[] woolSortedByColor = {
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

    public static ItemStack getWoolFromIndex(int index) {
        return woolSortedByColor[index % woolSortedByColor.length];
    }

    private Utils() {
    }

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

    public static boolean isLineOfSightAssassin(LivingEntity player1, LivingEntity player2) {
        Location eye = new LocationBuilder(player1.getEyeLocation())
                .backward(1)
                .addY(.7)
                .get();
        return getDotToLocation(eye, player2.getEyeLocation()) > 0.7;
    }

    public static boolean isLineOfSightVindicator(LivingEntity player1, LivingEntity player2) {
        Location eye = new LocationBuilder(player1.getEyeLocation())
                .backward(2)
                .addY(.7)
                .get();
        return getDotToLocation(eye, player2.getEyeLocation()) > 0.78;
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

    // Linear Interpolation
    // https://en.wikipedia.org/wiki/Linear_interpolation
    public static double lerp(double a, double b, double target) {
        return a + target * (b - a);
    }

    public static float lerp(float point1, float point2, float alpha) {
        return point1 + alpha * (point2 - point1);
    }

    public static boolean hasLineOfSight(LivingEntity player, LivingEntity player2) {
        return player.hasLineOfSight(player2);
    }

    public static class ArmorStandComparator implements Comparator<Entity> {
        @Override
        public int compare(Entity a, Entity b) {
            return a instanceof ArmorStand && b instanceof ArmorStand ? 0 : a instanceof ArmorStand ? -1 : b instanceof ArmorStand ? 1 : 0;
        }
    }

    public static Predicate<WarlordsEntity> filterOnlyEnemies(@Nullable WarlordsEntity wp) {
        return wp == null ? (player) -> false : wp::isEnemyAlive;
    }

    public static Predicate<WarlordsEntity> filterOnlyTeammates(@Nullable WarlordsEntity wp) {
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

    public static double getDistance(WarlordsEntity e, double accuracy) {
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
            if (loc.getBlock().getType().isSolid()) {
                break;
            }
            distance += accuracy;
        }
        distance -= accuracy;
        return distance;
    }

    public static void resetPlayerMovementStatistics(Player player) {
        player.setStatistic(Statistic.WALK_ONE_CM, 0);
        player.setStatistic(Statistic.JUMP, 0);
        player.setStatistic(Statistic.FALL_ONE_CM, 0);
        player.setStatistic(Statistic.HORSE_ONE_CM, 0);
    }

    public static int getPlayerMovementStatistics(Player player) {
        int walkStatistic = player.getStatistic(Statistic.WALK_ONE_CM) + (player.getStatistic(Statistic.JUMP) * 200) + player.getStatistic(Statistic.FALL_ONE_CM);
        int horseStatistic = player.getStatistic(Statistic.HORSE_ONE_CM);
        return walkStatistic + horseStatistic;
    }

    public static boolean blocksInFrontOfLocation(Location location) {
        location = location.clone();
        location.setPitch(0);
        Location headLocationForward = location.clone().add(location.getDirection().multiply(1)).add(0, 1, 0);
        Location footLocationForward = location.clone().add(location.getDirection().multiply(1));
        return location.getWorld().getBlockAt(headLocationForward).getType() != Material.AIR &&
                location.getWorld().getBlockAt(headLocationForward).getType() != Material.WOOD_STEP &&
                location.getWorld().getBlockAt(headLocationForward).getType() != Material.STEP &&
                location.getWorld().getBlockAt(footLocationForward).getType() != Material.AIR;
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
     * Allows a Stream to be used in a for-each loop, as they do not come out of the box with support for this.
     * @param <T> The type
     * @param stream The stream
     * @return A one-time use <code>Iterable</code> for iterating over the stream
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

    public static void formatTimeLeft(StringBuilder message, long seconds) {
        long minute = seconds / 60;
        long second = seconds % 60;
        if (minute < 10) {
            message.append('0');
        }
        message.append(minute);
        message.append(':');
        if (second < 10) {
            message.append('0');
        }
        message.append(second == -1 ? 0 : second);
    }

    public static String formatTimeLeft(long seconds) {
        StringBuilder message = new StringBuilder();
        formatTimeLeft(message, seconds);
        return message.toString();
    }

    public static String toTitleCase(Object input) {
        return toTitleCase(String.valueOf(input));
    }

    public static String toTitleCase(String input) {
        return input.substring(0, 1).toUpperCase(Locale.ROOT) + input.substring(1).toLowerCase(Locale.ROOT);
    }

    public static String toTitleHumanCase(Object input) {
        return toTitleHumanCase(String.valueOf(input));
    }

    public static String toTitleHumanCase(String input) {
        return input.substring(0, 1).toUpperCase(Locale.ROOT) + input.replace('_', ' ').substring(1).toLowerCase(Locale.ROOT);
    }

    public static boolean startsWithIgnoreCase(String str, String prefix) {
        return str.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    public static void playGlobalSound(@Nonnull Location location, Sound sound, float volume, float pitch) {
        for (Player p : location.getWorld().getPlayers()) {
            p.playSound(location, sound, volume, pitch);
        }
    }

    public static void playGlobalSound(@Nonnull Location location, String soundString, float volume, float pitch) {
        for (Player p : location.getWorld().getPlayers()) {
            p.playSound(location, soundString, volume, pitch);
        }
    }

    public static void playGlobalSound(@Nonnull Location location, Instrument instrument, Note note) {
        for (Player p : location.getWorld().getPlayers()) {
            p.playNote(location, instrument, note);
        }
    }

    public static double map(double value, double min, double max) {
        return value * (max - min) + min;
    }

    public static class SimpleEntityEquipment implements EntityEquipment {

        private ItemStack helmet;
        private ItemStack chestplate;
        private ItemStack leggings;
        private ItemStack boots;
        private ItemStack hand;

        public SimpleEntityEquipment(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack hand) {
            this.helmet = helmet;
            this.chestplate = chestplate;
            this.leggings = leggings;
            this.boots = boots;
            this.hand = hand;
        }

        public SimpleEntityEquipment(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
            this.helmet = helmet;
            this.chestplate = chestplate;
            this.leggings = leggings;
            this.boots = boots;
        }

        @Override
        public ItemStack getItemInHand() {
            return hand;
        }

        @Override
        public void setItemInHand(ItemStack stack) {
            this.hand = stack;
        }

        @Override
        public ItemStack getHelmet() {
            return helmet;
        }

        @Override
        public void setHelmet(ItemStack helmet) {
            this.helmet = helmet;
        }

        @Override
        public ItemStack getChestplate() {
            return chestplate;
        }

        @Override
        public void setChestplate(ItemStack chestplate) {
            this.chestplate = chestplate;
        }

        @Override
        public ItemStack getLeggings() {
            return leggings;
        }

        @Override
        public void setLeggings(ItemStack leggings) {
            this.leggings = leggings;
        }

        @Override
        public ItemStack getBoots() {
            return boots;
        }

        @Override
        public void setBoots(ItemStack boots) {
            this.boots = boots;
        }

        @Override
        public ItemStack[] getArmorContents() {
            return new ItemStack[0];
        }

        @Override
        public void setArmorContents(ItemStack[] items) {

        }

        @Override
        public void clear() {
            helmet = null;
            chestplate = null;
            leggings = null;
            boots = null;
            hand = null;
        }

        @Override
        public float getItemInHandDropChance() {
            return 0;
        }

        @Override
        public void setItemInHandDropChance(float chance) {

        }

        @Override
        public float getHelmetDropChance() {
            return 0;
        }

        @Override
        public void setHelmetDropChance(float chance) {

        }

        @Override
        public float getChestplateDropChance() {
            return 0;
        }

        @Override
        public void setChestplateDropChance(float chance) {

        }

        @Override
        public float getLeggingsDropChance() {
            return 0;
        }

        @Override
        public void setLeggingsDropChance(float chance) {

        }

        @Override
        public float getBootsDropChance() {
            return 0;
        }

        @Override
        public void setBootsDropChance(float chance) {

        }

        @Override
        public Entity getHolder() {
            return null;
        }
    }

    /**
     * @param armor Must always be leather armor.
     * @return colored leather armor.
     */
    public static ItemStack applyColorTo(@Nonnull Material armor, int red, int green, int blue) {
        ItemStack itemStack = new ItemStack(armor);
        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
        leatherArmorMeta.setColor(Color.fromRGB(red, green, blue));
        itemStack.setItemMeta(leatherArmorMeta);
        return itemStack;
    }

    /**
     * @param from
     * @param vectorLocation initial center point
     * @param target         which target to apply the knockback on
     * @param multiplier     how much the vector should be multiplied by
     * @param yBoost         how high should the target be raised in Y level
     */
    public static void addKnockback(String from, Location vectorLocation, @Nonnull WarlordsEntity target, double multiplier, double yBoost) {
        Vector v = vectorLocation.toVector().subtract(target.getLocation().toVector()).normalize().multiply(multiplier).setY(yBoost);
        target.setVelocity(from, v, false);
    }

    public static void addKnockback(
            String from,
            Location vectorLocation,
            @Nonnull WarlordsEntity target,
            double multiplier,
            double yBoost,
            boolean ignoreModifiers
    ) {
        Vector v = vectorLocation.toVector().subtract(target.getLocation().toVector()).normalize().multiply(multiplier).setY(yBoost);
        target.setVelocity(from, v, ignoreModifiers);
    }
}
