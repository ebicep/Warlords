package com.ebicep.warlords.database.repositories.player.pojos.general;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.mobs.MobDrop;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryWeaponTitleInfo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Partial refund of the old legendary weapon and title upgrade costs.
 * One upgraded weapon refunds 40%. Fifteen or more refund 10%. The rate is linear in between.
 */
public final class WeaponTitleUpgradeRefund {

    static final int REFUND_SCALE_CAP = 15;

    private static final long RATE_NUMERATOR_BASE = 560L;
    private static final long RATE_STEP = 30L;
    private static final long RATE_DENOMINATOR = 1400L;

    private static final long[] WEAPON_COINS = {0, 100_000, 250_000, 500_000, 1_000_000, 2_000_000};
    private static final long[] WEAPON_SYNTHETIC_SHARDS = {0, 7_500, 10_000, 12_500, 15_000, 20_000};
    private static final long[] WEAPON_LEGEND_FRAGMENTS = {0, 3_000, 6_000, 9_000, 12_000, 15_000};
    private static final long[] WEAPON_ASCENDANT_SHARDS = {0, 0, 0, 0, 0, 3};

    private static final long[] TITLE_COINS = {0, 500_000, 1_000_000, 2_000_000, 4_000_000, 8_000_000};
    private static final long[] TITLE_SYNTHETIC_SHARDS = {0, 2_500, 5_000, 7_500, 10_000, 15_000};
    private static final long[] TITLE_LEGEND_FRAGMENTS = {0, 1_000, 2_000, 3_000, 5_000, 8_000};
    private static final long[] TITLE_ZENITH_STARS = {0, 1, 3, 5, 7, 9};
    private static final long[] TITLE_LIMIT_BREAKERS = {0, 0, 0, 1, 2, 3};

    private WeaponTitleUpgradeRefund() {
    }

    public static LinkedHashMap<Spendable, Long> calculate(List<AbstractWeapon> weapons) {
        long coins = 0;
        long syntheticShards = 0;
        long legendFragments = 0;
        long ascendantShards = 0;
        long zenithStars = 0;
        long limitBreakers = 0;
        int upgradedWeapons = 0;

        if (weapons != null) {
            for (AbstractWeapon weapon : weapons) {
                if (!(weapon instanceof AbstractLegendaryWeapon legendaryWeapon)) {
                    continue;
                }
                int weaponLevel = Math.max(0, legendaryWeapon.getUpgradeLevel());
                boolean upgraded = weaponLevel > 0;
                coins += sumThroughLevel(WEAPON_COINS, weaponLevel);
                syntheticShards += sumThroughLevel(WEAPON_SYNTHETIC_SHARDS, weaponLevel);
                legendFragments += sumThroughLevel(WEAPON_LEGEND_FRAGMENTS, weaponLevel);
                ascendantShards += sumThroughLevel(WEAPON_ASCENDANT_SHARDS, weaponLevel);

                Map<?, LegendaryWeaponTitleInfo> titles = legendaryWeapon.getTitles();
                if (titles != null) {
                    for (LegendaryWeaponTitleInfo titleInfo : titles.values()) {
                        if (titleInfo == null) {
                            continue;
                        }
                        int titleLevel = Math.max(0, titleInfo.getUpgradeLevel());
                        if (titleLevel > 0) {
                            upgraded = true;
                        }
                        coins += sumThroughLevel(TITLE_COINS, titleLevel);
                        syntheticShards += sumThroughLevel(TITLE_SYNTHETIC_SHARDS, titleLevel);
                        legendFragments += sumThroughLevel(TITLE_LEGEND_FRAGMENTS, titleLevel);
                        zenithStars += sumThroughLevel(TITLE_ZENITH_STARS, titleLevel);
                        limitBreakers += sumThroughLevel(TITLE_LIMIT_BREAKERS, titleLevel);
                    }
                }
                if (upgraded) {
                    upgradedWeapons++;
                }
            }
        }

        LinkedHashMap<Spendable, Long> rewards = new LinkedHashMap<>();
        if (upgradedWeapons == 0) {
            return rewards;
        }
        putRefund(rewards, Currencies.COIN, coins, upgradedWeapons);
        putRefund(rewards, Currencies.SYNTHETIC_SHARD, syntheticShards, upgradedWeapons);
        putRefund(rewards, Currencies.LEGEND_FRAGMENTS, legendFragments, upgradedWeapons);
        putRefund(rewards, Currencies.ASCENDANT_SHARD, ascendantShards, upgradedWeapons);
        putRefund(rewards, MobDrop.ZENITH_STAR, zenithStars, upgradedWeapons);
        putRefund(rewards, Currencies.LIMIT_BREAKER, limitBreakers, upgradedWeapons);
        return rewards;
    }

    private static long sumThroughLevel(long[] costs, int level) {
        int cappedLevel = Math.min(level, costs.length - 1);
        long total = 0;
        for (int tier = 1; tier <= cappedLevel; tier++) {
            total += costs[tier];
        }
        return total;
    }

    /**
     * {@code refund = (cost * (560 - (count - 1) * 30) + 700) / 1400}, with the count clamped at 15.
     * That is 40% at one weapon and 10% at fifteen or more, rounded half up.
     */
    static long refundAmount(long cost, int upgradedWeapons) {
        if (cost <= 0 || upgradedWeapons <= 0) {
            return 0;
        }
        int count = Math.min(upgradedWeapons, REFUND_SCALE_CAP);
        long numerator = RATE_NUMERATOR_BASE - (count - 1L) * RATE_STEP;
        return (cost * numerator + RATE_DENOMINATOR / 2) / RATE_DENOMINATOR;
    }

    private static void putRefund(LinkedHashMap<Spendable, Long> rewards, Spendable spendable, long cost, int upgradedWeapons) {
        long refund = refundAmount(cost, upgradedWeapons);
        if (refund > 0) {
            rewards.put(spendable, refund);
        }
    }

}
