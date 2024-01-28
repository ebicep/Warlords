package com.ebicep.warlords.util.warlords;

import com.ebicep.warlords.events.GeneralEvents;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.Pair;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
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
     * see org.bukkit.craftbukkit.v1_20_R2.entity.CraftLivingEntity#getLineOfSight(Set, int, int)}
     * this accounts for banners
     *
     * @param livingEntity
     * @param maxDistance
     * @return
     */
    public static Block getTargetBlock(LivingEntity livingEntity, int maxDistance) {
        if (livingEntity instanceof Player player) {
            CraftPlayer craftPlayer = (CraftPlayer) player;
            Preconditions.checkState(!craftPlayer.getHandle().generation, "Cannot get line of sight during world generation");
        }
        if (maxDistance > 120) {
            maxDistance = 120;
        }
        ArrayList<Block> blocks = new ArrayList<>();
        Iterator<Block> itr = new BlockIterator(livingEntity, maxDistance);
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

    public static Block getTargetBlock(WarlordsEntity warlordsEntity, int maxDistance) {
        return warlordsEntity.getEntity() instanceof LivingEntity livingEntity ? Utils.getTargetBlock(livingEntity,
                maxDistance
        ) : Utils.getTargetBlock(warlordsEntity.getLocation(), maxDistance);
    }


    public static Location getTargetLocation(Location location, int maxDistance) {
        return getTargetBlock(location, maxDistance).getLocation();
    }

    public static Block getTargetBlock(Location location, int maxDistance) {
        if (maxDistance > 120) {
            maxDistance = 120;
        }
        ArrayList<Block> blocks = new ArrayList<>();
        Iterator<Block> itr = new BlockIterator(location, 0, maxDistance);
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

    public static List<Block> getTargetBlockInBetween(Location location, int maxDistance) {
        if (maxDistance > 120) {
            maxDistance = 120;
        }
        ArrayList<Block> blocks = new ArrayList<>();
        Iterator<Block> itr = new BlockIterator(location, 0, maxDistance);
        while (itr.hasNext()) {
            Block block = itr.next();
            blocks.add(block);
            Material material = block.getType();
            if (!TRANSPARENT.contains(material) && !Tag.BANNERS.isTagged(material)) {
                break;
            }
        }
        return blocks;
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

    public static boolean isStrikeSlashSpike(String ability) {
        return ability.contains("Strike") ||
                ability.contains("Slash") ||
                ability.contains("Spike");
    }

    public static boolean isKnockbackAbility(String ability) {
        return ability.equals("Seismic Wave") ||
                ability.equals("Ground Slam") ||
                ability.equals("Boulder") ||
                ability.equals("Earthen Spike");
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

    public static FallingBlock addFallingBlock(Location location) {
        return addFallingBlock(location, new Vector(0, .14, 0));
    }

    public static FallingBlock addFallingBlock(Location location, Vector vector) {
        FallingBlock fallingBlock = spawnTexturedFallingBlockAt(location);
        fallingBlock.setVelocity(vector);
        fallingBlock.setDropItem(false);
        GeneralEvents.addEntityUUID(fallingBlock);
        return fallingBlock;
    }

    @Nonnull
    private static FallingBlock spawnTexturedFallingBlockAt(Location location) {
        if (location.getWorld().getBlockAt(location).getType() != Material.AIR) {
            location.add(0, 1, 0);
        }
        Location blockToGet = location.clone().add(0, -1, 0);
        if (location.getWorld().getBlockAt(blockToGet).getType() == Material.AIR) {
            blockToGet.add(0, -1, 0);
            if (location.getWorld().getBlockAt(blockToGet).getType() == Material.AIR) {
                blockToGet.add(0, -1, 0);
            }
        }
        Material type = location.getWorld().getBlockAt(blockToGet).getType();
        if (type == Material.GRASS) {
            if ((int) (Math.random() * 3) == 2) {
                type = Material.DIRT;
            }
        }
        return location.getWorld().spawnFallingBlock(location.add(0, .6, 0), type.createBlockData());
    }

    public static void spawnThrowableProjectile(
            Game game,
            ArmorStand stand,
            Vector vector,
            double gravity,
            double speed,
            BiConsumer<Location, Integer> onLast,
            Function<Location, WarlordsEntity> directHitFunction,
            BiConsumer<Location, WarlordsEntity> onImpact
    ) {
        new GameRunnable(game) {
            int ticksElapsed = 0;

            @Override
            public void run() {
                quarterStep(false);
                quarterStep(false);
                quarterStep(false);
                quarterStep(false);
                quarterStep(false);
                quarterStep(false);
                quarterStep(true);
            }

            private void quarterStep(boolean last) {
                if (!stand.isValid()) {
                    this.cancel();
                    return;
                }

                vector.add(new Vector(0, gravity * speed, 0));
                Location newLoc = stand.getLocation();
                newLoc.add(vector);
                stand.teleport(newLoc);
                newLoc.add(0, 1.75, 0);

                stand.setHeadPose(new EulerAngle(-vector.getY() * 3, 0, 0));

                boolean shouldExplode;

                ticksElapsed++;
                if (last) {
                    onLast.accept(newLoc, ticksElapsed);
                }

                WarlordsEntity directHit = null;
                Material type = newLoc.getBlock().getType();
                if (type != Material.AIR
                        && type != Material.GRASS
                        && type != Material.BARRIER
                        && type != Material.VINE
                ) {
                    // Explode based on collision
                    shouldExplode = true;
                } else {
                    directHit = directHitFunction.apply(newLoc);
                    shouldExplode = directHit != null;
                }

                if (shouldExplode) {
                    stand.remove();
                    onImpact.accept(newLoc, directHit);
                    this.cancel();
                }
            }

        }.runTaskTimer(0, 1);
    }

    public static void spawnFallingBlocks(Location impactLocation, double initialCircleRadius, int amount) {
        spawnFallingBlocks(impactLocation, initialCircleRadius, amount, -.5, .25);
    }

    public static void spawnFallingBlocks(Location impactLocation, double initialCircleRadius, int amount, double vectorMultiply, double vectorY) {
        spawnFallingBlocks(impactLocation, initialCircleRadius, amount, vectorMultiply, vectorY, Material.DIRT, Material.STONE, Material.PODZOL);
    }

    public static void spawnFallingBlocks(Location impactLocation, double initialCircleRadius, int amount, double vectorMultiply, double vectorY, Material... materials) {
        List<Material> materialList = Arrays.asList(materials);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double angle = 0;
        for (int i = 0; i < amount; i++) {
            FallingBlock fallingBlock;
            Location spawnLoc = impactLocation.clone();

            double x = initialCircleRadius * Math.cos(angle);
            double z = initialCircleRadius * Math.sin(angle);
            angle += 360.0 / amount + (int) (Math.random() * 4 - 2);
            spawnLoc.add(x, 1, z);

            if (spawnLoc.getWorld().getBlockAt(spawnLoc).getType() == Material.AIR) {
                fallingBlock = impactLocation.getWorld().spawnFallingBlock(spawnLoc, (materialList.get(random.nextInt(materialList.size())).createBlockData()));
                fallingBlock.setVelocity(impactLocation.toVector().subtract(spawnLoc.toVector()).normalize().multiply(vectorMultiply).setY(vectorY));
                fallingBlock.setDropItem(false);
                GeneralEvents.addEntityUUID(fallingBlock);
            }
        }
    }

    public static void spawnFallingBlocks(Location impactLocation, double initialCircleRadius, int amount, Material... materials) {
        spawnFallingBlocks(impactLocation, initialCircleRadius, amount, -.5, .25, materials);
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
