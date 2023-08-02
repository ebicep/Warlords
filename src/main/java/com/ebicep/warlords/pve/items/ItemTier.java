package com.ebicep.warlords.pve.items;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public enum ItemTier {

    NONE(
            "None",
            NamedTextColor.BLACK,
            new ItemStack(Material.WHITE_STAINED_GLASS_PANE),
            new ItemStack(Material.WHITE_TERRACOTTA),
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
            NamedTextColor.GREEN,
            new ItemStack(Material.LIME_STAINED_GLASS_PANE),
            new ItemStack(Material.LIME_TERRACOTTA),
            -.20f,
            new WeightRange(7, 10, 15),
            new Pair<>(1, 3),
            .0006875,
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
            NamedTextColor.BLUE,
            new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE),
            new ItemStack(Material.LIGHT_BLUE_TERRACOTTA),
            -.10f,
            new WeightRange(15, 20, 30),
            new Pair<>(5, 10),
            .00034375,
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
            NamedTextColor.LIGHT_PURPLE,
            new ItemStack(Material.MAGENTA_STAINED_GLASS_PANE),
            new ItemStack(Material.MAGENTA_TERRACOTTA),
            0,
            new WeightRange(22, 30, 45),
            new Pair<>(11, 20),
            .000103125,
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
            NamedTextColor.YELLOW,
            new ItemStack(Material.YELLOW_STAINED_GLASS_PANE),
            new ItemStack(Material.YELLOW_TERRACOTTA),
            .10f,
            new WeightRange(30, 40, 60),
            new Pair<>(21, 35),
            .00001375,
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
            NamedTextColor.GOLD,
            new ItemStack(Material.ORANGE_STAINED_GLASS_PANE),
            new ItemStack(Material.ORANGE_TERRACOTTA),
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
                                                        .filter(itemTier -> itemTier != NONE)
                                                        .toArray(ItemTier[]::new);

    private static Set<BasicStatPool> generateStatPoolWithSettings(int initialPool) {
        Set<BasicStatPool> statPool = new HashSet<>();
        List<BasicStatPool> poolList = new ArrayList<>(Arrays.asList(BasicStatPool.VALUES));
        Collections.shuffle(poolList);
        for (int i = 0; i < initialPool; i++) {
            statPool.add(poolList.remove(0));
        }
        return statPool;
    }

    public final String name;
    public final NamedTextColor textColor;
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
            NamedTextColor textColor,
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
        this.textColor = textColor;
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
        return generateStatPoolWithSettings(ordinal());
    }

    public Component getColoredName() {
        return Component.text(name, textColor);
    }

    public ItemTier next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

    public record WeightRange(int min, int normal, int max) {
    }

}
