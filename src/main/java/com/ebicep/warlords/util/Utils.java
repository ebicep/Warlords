package com.ebicep.warlords.util;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.WarlordsPlayer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {

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
        return getDotToLocation(eye, player2.getEyeLocation()) > 0.965;
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


    public static class ArmorStandComparator implements Comparator<Entity> {
        @Override
        public int compare(Entity a, Entity b) {
            return a instanceof ArmorStand && b instanceof ArmorStand ? 0 : a instanceof ArmorStand ? -1 : b instanceof ArmorStand ? 1 : 0;
        }
    }

    private static final Location LOCATION_CONTAINER = new Location(null, 0, 0, 0);

    @Deprecated
    public static Stream<WarlordsPlayer> filterOnlyEnemiesSorted(WarlordsPlayer entity, double x, double y, double z, Entity player) {
        return filterOnlyEnemiesSorted(entity.getLocation(LOCATION_CONTAINER), x, y, z, player);
    }

    @Deprecated
    public static Stream<WarlordsPlayer> filterOnlyEnemiesSorted(Entity entity, double x, double y, double z, Entity player) {
        return filterOnlyEnemiesSorted(entity.getLocation(LOCATION_CONTAINER), x, y, z, player);
    }

    @Deprecated
    public static Stream<WarlordsPlayer> filterOnlyEnemiesSorted(Location loc, double x, double y, double z, Entity player) {
        return filterOnlyEnemies(loc.getWorld().getNearbyEntities(loc, x, y, z), player)
                .sorted(sortClosestBy(WarlordsPlayer::getLocation, loc));
    }

    @Deprecated
    public static Stream<WarlordsPlayer> filterOnlyEnemies(Entity entity, double x, double y, double z, Entity player) {
        return filterOnlyEnemies(entity.getLocation(LOCATION_CONTAINER), x, y, z, player);
    }

    @Deprecated
    public static Stream<WarlordsPlayer> filterOnlyEnemies(Location loc, double x, double y, double z, Entity player) {
        return filterOnlyEnemies(loc.getWorld().getNearbyEntities(loc, x, y, z), player)
                .filter(radiusAround(WarlordsPlayer::getLocation, loc, x, y, z));
    }

    /**
     * Map the list of entities to valid WarlordPlayer who are part off the enemy of the team of the player
     *
     * @param entities
     * @param player
     * @return
     */
    @Deprecated
    public static Stream<WarlordsPlayer> filterOnlyEnemies(Collection<Entity> entities, Entity player) {
        WarlordsPlayer wp = Warlords.getPlayer(player);
        return wp == null ? Stream.empty() : entities.stream()
                .map(Warlords::getPlayer)
                .filter(filterOnlyEnemies(wp));
    }

    public static Predicate<WarlordsPlayer> filterOnlyEnemies(@Nullable WarlordsPlayer wp) {
        return wp == null ? (player) -> false : wp::isEnemyAlive;
    }


    @Deprecated
    public static Stream<WarlordsPlayer> filterOnlyTeammatesSorted(WarlordsPlayer entity, double x, double y, double z, Entity player) {
        return filterOnlyTeammatesSorted(entity.getLocation(LOCATION_CONTAINER), x, y, z, player);
    }

    @Deprecated
    public static Stream<WarlordsPlayer> filterOnlyTeammatesSorted(Entity entity, double x, double y, double z, Entity player) {
        return filterOnlyTeammatesSorted(entity.getLocation(LOCATION_CONTAINER), x, y, z, player);
    }

    @Deprecated
    public static Stream<WarlordsPlayer> filterOnlyTeammatesSorted(Location loc, double x, double y, double z, Entity player) {
        return filterOnlyTeammates(loc.getWorld().getNearbyEntities(loc, x, y, z), player)
                .sorted(sortClosestBy(WarlordsPlayer::getLocation, loc));
    }

    @Deprecated
    public static Stream<WarlordsPlayer> filterOnlyTeammates(Entity entity, double x, double y, double z, Entity player) {
        return filterOnlyTeammates(entity.getLocation(LOCATION_CONTAINER), x, y, z, player);
    }

    @Deprecated
    public static Stream<WarlordsPlayer> filterOnlyTeammates(WarlordsPlayer entity, double x, double y, double z, Entity player) {
        return filterOnlyTeammates(entity.getLocation(LOCATION_CONTAINER), x, y, z, player);
    }

    @Deprecated
    public static Stream<WarlordsPlayer> filterOnlyTeammates(Location loc, double x, double y, double z, Entity player) {
        return filterOnlyTeammates(loc.getWorld().getNearbyEntities(loc, x, y, z), player)
                .filter(radiusAround(WarlordsPlayer::getLocation, loc, x, y, z));
    }

    /**
     * Map the list of entities to valid WarlordPlayer who are part on the team of the player
     *
     * @param entities
     * @param player
     * @return
     */
    @Deprecated
    public static Stream<WarlordsPlayer> filterOnlyTeammates(Collection<Entity> entities, Entity player) {
        WarlordsPlayer wp = Warlords.getPlayer(player);
        return wp == null ? Stream.empty() : entities.stream()
                .map(e -> Warlords.getPlayer(e))
                .filter(filterOnlyTeammates(wp));
    }

    public static Predicate<WarlordsPlayer> filterOnlyTeammates(@Nullable WarlordsPlayer wp) {
        return wp == null ? (player) -> false : wp::isTeammateAlive;
    }

    // Old methods:
    @Deprecated
    public static List<Entity> filterOutTeammates(Collection<Entity> entities, Player player) {
        WarlordsPlayer wp = Warlords.getPlayer(player);
        return wp == null ? Collections.emptyList() : entities.stream()
                .filter(e -> wp.isEnemyAlive(e))
                .collect(Collectors.toList());
    }

    @Deprecated
    public static List<Entity> filterTeammates(Collection<Entity> entities, Player player) {
        WarlordsPlayer wp = Warlords.getPlayer(player);
        return wp == null ? Collections.emptyList() : entities.stream()
                .filter(e -> wp.isTeammateAlive(e))
                .collect(Collectors.toList());
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
        Location headLocationForward = location.clone().add(location.clone().getDirection().multiply(1)).add(0, 1, 0);
        Location footLocationForward = location.clone().add(location.clone().getDirection().multiply(1));
        return location.getWorld().getBlockAt(headLocationForward).getType() != Material.AIR && location.getWorld().getBlockAt(footLocationForward).getType() != Material.AIR;
    }

    public static boolean isMountableZone(Location location) {
        if (location.getWorld().getBlockAt((int) location.getX(), 2, (int) location.getZ()).getType() == Material.NETHERRACK) {
            return location.getWorld().getBlockAt((int) location.getX(), 4, (int) location.getZ()).getType() == Material.SOUL_SAND && !insideTunnel(location);
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

    private final static int CENTER_PX = 154;


    public static void sendMessage(Player player, boolean centered, String message) {
        if (centered) {
            Utils.sendCenteredMessage(player, message);
        } else {
            player.sendMessage(message);
        }
    }

    public static void sendCenteredMessage(Player player, String message) {
        if (message == null || message.equals("")) player.sendMessage("");
        message = ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        player.sendMessage(sb.toString() + message);
    }

    public static void sendCenteredHoverableMessage(Player player, List<TextComponent> textComponents) {
        if (textComponents == null || textComponents.size() == 0) ;
        String message = "";
        for (TextComponent textComponent : textComponents) {
            message += textComponent.getText();
        }
        message = ChatColor.translateAlternateColorCodes('&', message);
        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }
        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        ComponentBuilder componentBuilder = new ComponentBuilder(sb.toString());
        for (TextComponent textComponent : textComponents) {
            componentBuilder.append(textComponent.getText());
            componentBuilder.event(textComponent.getHoverEvent());
        }
        player.spigot().sendMessage(componentBuilder.create());
    }

    public static String addCommaAndRound(double amount) {
        amount = Math.round(amount);
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount);
    }

    /**
     * Converts an {@link org.bukkit.inventory.ItemStack} to a Json string
     * for sending with {@link net.md_5.bungee.api.chat.BaseComponent}'s.
     *
     * @param itemStack the item to convert
     * @return the Json string representation of the item
     */
    public static String convertItemStackToJsonRegular(ItemStack itemStack) {
        // First we convert the item stack into an NMS itemstack
        net.minecraft.server.v1_8_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        net.minecraft.server.v1_8_R3.NBTTagCompound compound = new NBTTagCompound();
        nmsItemStack.save(compound);
        return compound.toString();
    }

    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        Set<T> keys = new HashSet<T>();
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }
}
