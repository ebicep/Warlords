package com.ebicep.warlords.game.option.pve.darkdescent;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.mobs.MobDrop;
import com.ebicep.warlords.pve.newitems.SpendableRandomNewItem;
import com.ebicep.warlords.util.java.DateUtil;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.java.RandomCollection;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum DarkDescentChest {

    LOWER(Material.CHEST, new RandomCollection<Pair<Spendable, Long>>()
            .add(25, new Pair<>(Currencies.SYNTHETIC_SHARD, 90L))
            .add(25, new Pair<>(Currencies.COIN, 700L))
            .add(25, new Pair<>(Currencies.LEGEND_FRAGMENTS, 70L))
            .add(25, new Pair<>(Currencies.FAIRY_ESSENCE, 100L))
    ),
    UPPER(Material.CHEST, new RandomCollection<Pair<Spendable, Long>>()
            .add(25, new Pair<>(Currencies.SYNTHETIC_SHARD, 150L))
            .add(25, new Pair<>(Currencies.LEGEND_FRAGMENTS, 100L))
            .add(25, new Pair<>(Currencies.ILLUSION_SHARD, 3L))
            .add(20, new Pair<>(Currencies.SUPPLY_DROP_TOKEN, 6L))
            .add(5, new Pair<>(MobDrop.ZENITH_STAR, 1L))
    ),
    MILESTONE(Material.ENDER_CHEST, new RandomCollection<Pair<Spendable, Long>>()
            .add(60, new Pair<>(SpendableRandomNewItem.EPIC, 1L))
            .add(25, new Pair<>(MobDrop.ZENITH_STAR, 1L))
            .add(10, new Pair<>(SpendableRandomNewItem.LEGENDARY, 1L))
            .add(5, new Pair<>(Currencies.ASCENDANT_STAR_PIECE, 1L))
    );

    private static final long WEEKLY_SEED_SALT = 0x4441524B44455343L;

    private final Material material;
    private final RandomCollection<Pair<Spendable, Long>> rewards;

    DarkDescentChest(Material material, RandomCollection<Pair<Spendable, Long>> rewards) {
        this.material = material;
        this.rewards = rewards;
    }

    public static boolean isRewardFloor(int floor) {
        return floor > 0 && floor % 2 == 0;
    }

    public static DarkDescentChest forFloor(int floor) {
        if (floor % 10 == 0) {
            return MILESTONE;
        }
        return floor < 10 ? LOWER : UPPER;
    }

    /**
     * @return this week's reward for every floor, index 0 being floor 1. Odd floors are null
     */
    public static List<Pair<Spendable, Long>> getWeeklyRewards() {
        Random random = new Random(DateUtil.getCurrentWeekStartEpochDay() ^ WEEKLY_SEED_SALT);
        List<Pair<Spendable, Long>> weeklyRewards = new ArrayList<>(DarkDescentOption.FLOORS);
        for (int floor = 1; floor <= DarkDescentOption.FLOORS; floor++) {
            weeklyRewards.add(isRewardFloor(floor) ? forFloor(floor).rewards.next(random) : null);
        }
        return weeklyRewards;
    }

    public Material getMaterial() {
        return material;
    }
}
