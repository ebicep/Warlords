package com.ebicep.warlords.pve.items;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public enum ItemTier {

    ALL(
            "None",
            ChatColor.BLACK,
            new ItemStack(org.bukkit.Material.STAINED_GLASS_PANE, 1, (short) 0),
            new ItemStack(Material.STAINED_CLAY, 1, (short) 0),
            0,
            null,
            null,
            0,
            0,
            0,
            0,
            null,
            0
    ),
    ALPHA(
            "Alpha",
            ChatColor.GREEN,
            new ItemStack(org.bukkit.Material.STAINED_GLASS_PANE, 1, (short) 5),
            new ItemStack(Material.STAINED_CLAY, 1, (short) 5),
            -.20f,
            new WeightRange(7, 10, 15),
            new Pair<>(1, 3),
            .001,
            .65,
            .05,
            3,
            new LinkedHashMap<>() {{
                put(Currencies.COIN, 10_000L);
            }},
            100
    ),
    BETA(
            "Beta",
            ChatColor.BLUE,
            new ItemStack(org.bukkit.Material.STAINED_GLASS_PANE, 1, (short) 3),
            new ItemStack(org.bukkit.Material.STAINED_CLAY, 1, (short) 3),
            -.10f,
            new WeightRange(15, 20, 30),
            new Pair<>(5, 10),
            .0005,
            .55,
            .10,
            2,
            new LinkedHashMap<>() {{
                put(Currencies.COIN, 25_000L);
                put(Currencies.SYNTHETIC_SHARD, 100L);
            }},
            200
    ),
    GAMMA(
            "Gamma",
            ChatColor.LIGHT_PURPLE,
            new ItemStack(org.bukkit.Material.STAINED_GLASS_PANE, 1, (short) 2),
            new ItemStack(org.bukkit.Material.STAINED_CLAY, 1, (short) 2),
            0,
            new WeightRange(22, 30, 45),
            new Pair<>(11, 20),
            .0001,
            .45,
            .10,
            2,
            new LinkedHashMap<>() {{
                put(Currencies.COIN, 75_000L);
                put(Currencies.SYNTHETIC_SHARD, 250L);
            }},
            350
    ),
    DELTA(
            "Delta",
            ChatColor.YELLOW,
            new ItemStack(org.bukkit.Material.STAINED_GLASS_PANE, 1, (short) 4),
            new ItemStack(org.bukkit.Material.STAINED_CLAY, 1, (short) 4),
            .10f,
            new WeightRange(30, 40, 60),
            new Pair<>(21, 35),
            .00001,
            .35,
            .20,
            1,
            new LinkedHashMap<>() {{
                put(Currencies.COIN, 125_000L);
                put(Currencies.LEGEND_FRAGMENTS, 50L);
            }},
            550
    ),
    OMEGA(
            "Omega",
            ChatColor.GOLD,
            new ItemStack(org.bukkit.Material.STAINED_GLASS_PANE, 1, (short) 1),
            new ItemStack(org.bukkit.Material.STAINED_CLAY, 1, (short) 1),
            .20f,
            new WeightRange(37, 50, 75),
            new Pair<>(36, 50),
            0,
            .25,
            .25,
            1,
            new LinkedHashMap<>() {{
                put(Currencies.COIN, 200_000L);
                put(Currencies.LEGEND_FRAGMENTS, 200L);
            }},
            800
    ),

    ;

    public static final ItemTier[] VALUES = values();
    public static final ItemTier[] VALID_VALUES = Arrays.stream(VALUES)
                                                        .filter(itemTier -> itemTier != ALL)
                                                        .toArray(ItemTier[]::new);

    private static Set<BasicStatPool> generateStatPoolWithSettings(
            int initialPool,
            double firstReducedPoolChance,
            double secondReducedPoolChance
    ) {
        Set<BasicStatPool> statPool = new HashSet<>();
        List<BasicStatPool> poolList = new ArrayList<>(Arrays.asList(BasicStatPool.VALUES));
        Collections.shuffle(poolList);
        for (int i = 0; i < initialPool; i++) {
            statPool.add(poolList.remove(0));
        }
        addFromReducedPool(firstReducedPoolChance, statPool, poolList);
        addFromReducedPool(secondReducedPoolChance, statPool, poolList);
        return statPool;
    }

    private static void addFromReducedPool(
            double firstReducedPoolChance,
            Set<BasicStatPool> statPool,
            List<BasicStatPool> poolList
    ) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (firstReducedPoolChance != 0 && !poolList.isEmpty() && random.nextDouble() <= firstReducedPoolChance) {
            statPool.add(poolList.remove(0));
        }
    }

    public final String name;
    public final ChatColor chatColor;
    public final ItemStack glassPane;
    public final ItemStack clayBlock;
    public final float statDistributionModifier;
    public final WeightRange weightRange;
    public final Pair<Integer, Integer> scrapValue;
    public final double dropChance;
    public final double cursedChance;
    public final double blessedChance;
    public final int maxEquipped;
    public final LinkedHashMap<Spendable, Long> removeCurseCost;
    public final int maxThornsDamage;

    ItemTier(
            String name,
            ChatColor chatColor,
            ItemStack glassPane,
            ItemStack clayBlock,
            float statDistributionModifier,
            WeightRange weightRange,
            Pair<Integer, Integer> scrapValue, double dropChance,
            double cursedChance,
            double blessedChance,
            int maxEquipped,
            LinkedHashMap<Spendable, Long> removeCurseCost,
            int maxThornsDamage
    ) {
        this.name = name;
        this.chatColor = chatColor;
        this.glassPane = glassPane;
        this.clayBlock = clayBlock;
        this.statDistributionModifier = statDistributionModifier;
        this.weightRange = weightRange;
        this.scrapValue = scrapValue;
        this.dropChance = dropChance;
        this.cursedChance = cursedChance;
        this.blessedChance = blessedChance;
        this.maxEquipped = maxEquipped;
        this.removeCurseCost = removeCurseCost;
        this.maxThornsDamage = maxThornsDamage;
    }

    public Set<BasicStatPool> generateStatPool() {
        return generateStatPoolWithSettings(ordinal(), 0, 0);
    }

    public String getColoredName() {
        return chatColor + name;
    }

    public ItemTier next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

    public static class WeightRange {
        private final int min;
        private final int normal;
        private final int max;

        public WeightRange(int min, int normal, int max) {
            this.min = min;
            this.normal = normal;
            this.max = max;
        }

        public int getMin() {
            return min;
        }

        public int getNormal() {
            return normal;
        }

        public int getMax() {
            return max;
        }
    }

}
