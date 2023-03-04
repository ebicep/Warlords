package com.ebicep.warlords.pve.items;

import com.ebicep.warlords.pve.items.statpool.ItemBucklerStatPool;
import com.ebicep.warlords.pve.items.statpool.ItemGauntletStatPool;
import com.ebicep.warlords.pve.items.statpool.ItemStatPool;
import com.ebicep.warlords.pve.items.statpool.ItemTomeStatPool;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public enum ItemTier {

    ALPHA(
            "Alpha",
            .1,
            .001,
            .55,
            0,
            new HashMap<>() {{
                put(ItemGauntletStatPool.HP, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.MAX_ENERGY, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.EPH, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.EPS, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.SPEED, new ItemTier.StatRange(1, 5));
            }},
            new HashMap<>() {{
                put(ItemTomeStatPool.DAMAGE_HEALING, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CRIT_CHANCE, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CRIT_MULTI, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CD_RED, new ItemTier.StatRange(1, 5));
            }},
            new HashMap<>() {{
                put(ItemBucklerStatPool.DAMAGE_RED, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.AGGRO_PRIO, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.THORNS, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.KB_RES, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.RES_SPEED, new ItemTier.StatRange(1, 5));
            }}
    ) {
        @Override
        public <T extends Enum<T> & ItemStatPool<T>> Set<T> generateStatPool(T[] pool) {
            return generateStatPoolWithSettings(pool, 2, .45, .10);
        }
    },
    BETA(
            "Beta",
            .05,
            .0005,
            .45,
            0,
            new HashMap<>() {{
                put(ItemGauntletStatPool.HP, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.MAX_ENERGY, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.EPH, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.EPS, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.SPEED, new ItemTier.StatRange(1, 5));
            }},
            new HashMap<>() {{
                put(ItemTomeStatPool.DAMAGE_HEALING, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CRIT_CHANCE, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CRIT_MULTI, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CD_RED, new ItemTier.StatRange(1, 5));
            }},
            new HashMap<>() {{
                put(ItemBucklerStatPool.DAMAGE_RED, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.AGGRO_PRIO, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.THORNS, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.KB_RES, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.RES_SPEED, new ItemTier.StatRange(1, 5));
            }}
    ) {
        @Override
        public <T extends Enum<T> & ItemStatPool<T>> Set<T> generateStatPool(T[] pool) {
            return generateStatPoolWithSettings(pool, 2, .75, .30);
        }
    },
    GAMMA(
            "Gamma",
            .01,
            .0001,
            .35,
            0,
            new HashMap<>() {{
                put(ItemGauntletStatPool.HP, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.MAX_ENERGY, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.EPH, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.EPS, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.SPEED, new ItemTier.StatRange(1, 5));
            }},
            new HashMap<>() {{
                put(ItemTomeStatPool.DAMAGE_HEALING, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CRIT_CHANCE, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CRIT_MULTI, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CD_RED, new ItemTier.StatRange(1, 5));
            }},
            new HashMap<>() {{
                put(ItemBucklerStatPool.DAMAGE_RED, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.AGGRO_PRIO, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.THORNS, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.KB_RES, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.RES_SPEED, new ItemTier.StatRange(1, 5));
            }}
    ) {
        @Override
        public <T extends Enum<T> & ItemStatPool<T>> Set<T> generateStatPool(T[] pool) {
            return generateStatPoolWithSettings(pool, 3, .30, 0);
        }
    },
    DELTA(
            "Delta",
            .001,
            .00001,
            .35,
            .10,
            new HashMap<>() {{
                put(ItemGauntletStatPool.HP, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.MAX_ENERGY, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.EPH, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.EPS, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.SPEED, new ItemTier.StatRange(1, 5));
            }},
            new HashMap<>() {{
                put(ItemTomeStatPool.DAMAGE_HEALING, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CRIT_CHANCE, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CRIT_MULTI, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CD_RED, new ItemTier.StatRange(1, 5));
            }},
            new HashMap<>() {{
                put(ItemBucklerStatPool.DAMAGE_RED, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.AGGRO_PRIO, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.THORNS, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.KB_RES, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.RES_SPEED, new ItemTier.StatRange(1, 5));
            }}
    ) {
        @Override
        public <T extends Enum<T> & ItemStatPool<T>> Set<T> generateStatPool(T[] pool) {
            return generateStatPoolWithSettings(pool, 4, .20, 0);
        }
    },
    OMEGA(
            "Omega",
            0,
            0,
            0,
            0,
            new HashMap<>() {{
                put(ItemGauntletStatPool.HP, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.MAX_ENERGY, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.EPH, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.EPS, new ItemTier.StatRange(1, 5));
                put(ItemGauntletStatPool.SPEED, new ItemTier.StatRange(1, 5));
            }},
            new HashMap<>() {{
                put(ItemTomeStatPool.DAMAGE_HEALING, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CRIT_CHANCE, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CRIT_MULTI, new ItemTier.StatRange(1, 5));
                put(ItemTomeStatPool.CD_RED, new ItemTier.StatRange(1, 5));
            }},
            new HashMap<>() {{
                put(ItemBucklerStatPool.DAMAGE_RED, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.AGGRO_PRIO, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.THORNS, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.KB_RES, new ItemTier.StatRange(1, 5));
                put(ItemBucklerStatPool.RES_SPEED, new ItemTier.StatRange(1, 5));
            }}
    ) {
        @Override
        public <T extends Enum<T> & ItemStatPool<T>> Set<T> generateStatPool(T[] pool) {
            return generateStatPoolWithSettings(pool, 5, 0, 0);
        }
    },

    ;

    private static <T extends Enum<T> & ItemStatPool<T>> Set<T> generateStatPoolWithSettings(
            T[] pool,
            int initialPool,
            double firstReducedPoolChance,
            double secondReducedPoolChance
    ) {
        Set<T> statPool = new HashSet<>();
        List<T> poolList = new ArrayList<>(Arrays.asList(pool));
        Collections.shuffle(poolList);
        for (int i = 0; i < initialPool; i++) {
            statPool.add(poolList.remove(0));
        }
        addFromReducedPool(firstReducedPoolChance, statPool, poolList);
        addFromReducedPool(secondReducedPoolChance, statPool, poolList);
        return statPool;
    }

    private static <T extends Enum<T> & ItemStatPool<T>> void addFromReducedPool(
            double firstReducedPoolChance,
            Set<T> statPool,
            List<T> poolList
    ) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (firstReducedPoolChance != 0 && !poolList.isEmpty() && random.nextDouble() <= firstReducedPoolChance) {
            statPool.add(poolList.remove(0));
        }
    }

    public final String name;
    public final double dropChance;
    public final double killDropChance;
    public final double cursedChance;
    public final double blessedChance;
    public final HashMap<ItemGauntletStatPool, StatRange> gauntletStatRange;
    public final HashMap<ItemTomeStatPool, StatRange> tomeStatRange;
    public final HashMap<ItemBucklerStatPool, StatRange> bucklerStatRange;

    ItemTier(
            String name, double dropChance, double killDropChance, double cursedChance, double blessedChance,
            HashMap<ItemGauntletStatPool, StatRange> gauntletStatRange,
            HashMap<ItemTomeStatPool, StatRange> tomeStatRange,
            HashMap<ItemBucklerStatPool, StatRange> bucklerStatRange
    ) {
        this.name = name;
        this.dropChance = dropChance;
        this.killDropChance = killDropChance;
        this.cursedChance = cursedChance;
        this.blessedChance = blessedChance;
        this.gauntletStatRange = gauntletStatRange;
        this.tomeStatRange = tomeStatRange;
        this.bucklerStatRange = bucklerStatRange;
    }

    public abstract <T extends Enum<T> & ItemStatPool<T>> Set<T> generateStatPool(T[] pool);

    public static class StatRange {

        public final double min;
        public final double max;

        public StatRange(double min, double max) {
            this.min = min;
            this.max = max;
        }

        public double generateValue() {
            return ThreadLocalRandom.current().nextDouble(min, max);
        }

    }

}
