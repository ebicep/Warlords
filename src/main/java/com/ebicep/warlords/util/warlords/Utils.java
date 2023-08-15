package com.ebicep.warlords.util.warlords;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.Pair;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class Utils {

    public static final String[] SPECS_ORDERED = {
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
            "Apothecary",
            "Conjurer",
            "Sentinel",
            "Luminary"
    };
    // Sorted wool id color
    // https://prnt.sc/UN80GeSpeyly
    private static final ItemStack[] WOOL_SORTED_BY_COLOR = {
            new ItemStack(Material.WHITE_WOOL),
            new ItemStack(Material.LIGHT_GRAY_WOOL),
            new ItemStack(Material.GRAY_WOOL),
            new ItemStack(Material.BLACK_WOOL),
            new ItemStack(Material.BROWN_WOOL),
            new ItemStack(Material.RED_WOOL),
            new ItemStack(Material.ORANGE_WOOL),
            new ItemStack(Material.YELLOW_WOOL),
            new ItemStack(Material.LIME_WOOL),
            new ItemStack(Material.GREEN_WOOL),
            new ItemStack(Material.CYAN_WOOL),
            new ItemStack(Material.LIGHT_BLUE_WOOL),
            new ItemStack(Material.BLUE_WOOL),
            new ItemStack(Material.PURPLE_WOOL),
            new ItemStack(Material.MAGENTA_WOOL),
            new ItemStack(Material.PINK_WOOL),
            new ItemStack(Material.WHITE_WOOL),
            new ItemStack(Material.LIGHT_GRAY_WOOL),
            new ItemStack(Material.GRAY_WOOL),
            new ItemStack(Material.BLACK_WOOL),
            new ItemStack(Material.BROWN_WOOL),
            new ItemStack(Material.RED_WOOL),
            new ItemStack(Material.ORANGE_WOOL),
            new ItemStack(Material.YELLOW_WOOL),
            new ItemStack(Material.LIME_WOOL),
            new ItemStack(Material.GREEN_WOOL),
            new ItemStack(Material.CYAN_WOOL),
            new ItemStack(Material.LIGHT_BLUE_WOOL),
            new ItemStack(Material.BLUE_WOOL),
            new ItemStack(Material.PURPLE_WOOL),
            new ItemStack(Material.MAGENTA_WOOL),
            new ItemStack(Material.PINK_WOOL),
    };

    private static final Set<Material> TRANSPARENT = Sets.newHashSet(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR);

    public static Location getTargetLocation(Player player, int maxDistance) {
        return getTargetBlock(player, maxDistance).getLocation();
    }

    /**
     * see org.bukkit.craftbukkit.v1_20_R1.entity.CraftLivingEntity#getLineOfSight(Set, int, int)}
     * this accounts for banners
     *
     * @param player
     * @param maxDistance
     * @return
     */
    public static Block getTargetBlock(Player player, int maxDistance) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        Preconditions.checkState(!craftPlayer.getHandle().generation, "Cannot get line of sight during world generation");

        if (maxDistance > 120) {
            maxDistance = 120;
        }
        ArrayList<Block> blocks = new ArrayList<>();
        Iterator<Block> itr = new BlockIterator(craftPlayer, maxDistance);
        while (itr.hasNext()) {
            Block block = itr.next();
            blocks.add(block);
            if (blocks.size() > 1) {
                blocks.remove(0);
            }
            Material material = block.getType();
            if (!TRANSPARENT.contains(material) && !Tag.BANNERS.isTagged(material)) {
                break;
            }
        }
        return blocks.get(0);
    }

    public static boolean isProjectile(String ability) {
        return ability.equals("Fireball") ||
                ability.equals("Frostbolt") ||
                ability.equals("Water Bolt") ||
                ability.equals("Lightning Bolt") ||
                ability.equals("Flame Burst") ||
                ability.equals("Fallen Souls") ||
                ability.equals("Soothing Elixir") ||
                ability.equals("Poisonous Hex") ||
                ability.equals("Fortifying Hex") ||
                ability.equals("Merciful Hex");
    }

    public static boolean isPrimaryProjectile(String ability) {
        return ability.equals("Fireball") ||
                ability.equals("Frostbolt") ||
                ability.equals("Water Bolt") ||
                ability.equals("Lightning Bolt") ||
                ability.equals("Fallen Souls") ||
                ability.equals("Poisonous Hex") ||
                ability.equals("Fortifying Hex") ||
                ability.equals("Merciful Hex");
    }

    public static ItemStack getWoolFromIndex(int index) {
        return WOOL_SORTED_BY_COLOR[index % WOOL_SORTED_BY_COLOR.length];
    }

    public static void resetPlayerMovementStatistics(OfflinePlayer player) {
        player.setStatistic(Statistic.WALK_ONE_CM, 0);
        player.setStatistic(Statistic.JUMP, 0);
        player.setStatistic(Statistic.FALL_ONE_CM, 0);
        player.setStatistic(Statistic.HORSE_ONE_CM, 0);
    }

    public static int getPlayerMovementStatistics(OfflinePlayer player) {
        int walkStatistic = player.getStatistic(Statistic.WALK_ONE_CM) + (player.getStatistic(Statistic.JUMP) * 200) + player.getStatistic(Statistic.FALL_ONE_CM);
        int horseStatistic = player.getStatistic(Statistic.HORSE_ONE_CM);
        return walkStatistic + horseStatistic;
    }

    /**
     * Collector to pick a random element from a <code>Stream</code>
     *
     * @param <T> The type of the element
     * @return A collector for picking a random element, or null if the stream is empty
     * @see Stream#collect(java.util.stream.Collector)
     */
    public static <T> Collector<T, Pair<Integer, T>, T> randomElement() {
        return Collector.of(
                () -> new Pair<>(0, null),
                (i, a) -> {
                    int count = i.getA();
                    if (count == 0) {
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
                Pair::getB,
                Collector.Characteristics.CONCURRENT,
                Collector.Characteristics.UNORDERED
        );
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

    public static ArmorStand spawnArmorStand(Location location) {
        return spawnArmorStand(location, null);
    }

    public static ArmorStand spawnArmorStand(Location location, @Nullable Consumer<ArmorStand> standConsumer) {
        return location.getWorld().spawn(location, ArmorStand.class, false, armorStand -> {
            armorStand.setVisible(false);
            armorStand.setGravity(false);
            armorStand.setBasePlate(false);
            armorStand.setCanPickupItems(false);
            armorStand.setArms(false);
            armorStand.setRemoveWhenFarAway(false);
            if (standConsumer != null) {
                standConsumer.accept(armorStand);
            }
        });
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
        public void setItem(@NotNull EquipmentSlot equipmentSlot, @org.jetbrains.annotations.Nullable ItemStack itemStack) {

        }

        @Override
        public void setItem(@NotNull EquipmentSlot equipmentSlot, @org.jetbrains.annotations.Nullable ItemStack itemStack, boolean b) {

        }

        @Override
        public @NotNull ItemStack getItem(@NotNull EquipmentSlot equipmentSlot) {
            return null;
        }

        @Override
        public @NotNull ItemStack getItemInMainHand() {
            return hand;
        }

        @Override
        public void setItemInMainHand(@org.jetbrains.annotations.Nullable ItemStack itemStack) {

        }

        @Override
        public void setItemInMainHand(@org.jetbrains.annotations.Nullable ItemStack itemStack, boolean b) {

        }

        @Override
        public @NotNull ItemStack getItemInOffHand() {
            return null;
        }

        @Override
        public void setItemInOffHand(@org.jetbrains.annotations.Nullable ItemStack itemStack) {

        }

        @Override
        public void setItemInOffHand(@org.jetbrains.annotations.Nullable ItemStack itemStack, boolean b) {

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
        public void setHelmet(@org.jetbrains.annotations.Nullable ItemStack itemStack, boolean b) {

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
        public void setChestplate(@org.jetbrains.annotations.Nullable ItemStack itemStack, boolean b) {

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
        public void setLeggings(@org.jetbrains.annotations.Nullable ItemStack itemStack, boolean b) {

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
        public void setBoots(@org.jetbrains.annotations.Nullable ItemStack itemStack, boolean b) {

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
        public float getItemInMainHandDropChance() {
            return 0;
        }

        @Override
        public void setItemInMainHandDropChance(float v) {

        }

        @Override
        public float getItemInOffHandDropChance() {
            return 0;
        }

        @Override
        public void setItemInOffHandDropChance(float v) {

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

        @Override
        public float getDropChance(@NotNull EquipmentSlot equipmentSlot) {
            return 0;
        }

        @Override
        public void setDropChance(@NotNull EquipmentSlot equipmentSlot, float v) {

        }
    }
}
