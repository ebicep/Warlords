package com.ebicep.warlords.database.repositories.player.pojos.general;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.mobs.MobDrop;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.EpicWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryWeaponTitleInfo;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WeaponTitleUpgradeRefundTest {

    @Test
    void levelFiveWeaponAloneRefundsFortyPercentOfOldCosts() {
        LinkedHashMap<Spendable, Long> rewards = WeaponTitleUpgradeRefund.calculate(List.of(legendary(5)));

        assertEquals(Map.of(
                Currencies.COIN, 1_540_000L,
                Currencies.SYNTHETIC_SHARD, 26_000L,
                Currencies.LEGEND_FRAGMENTS, 18_000L,
                Currencies.ASCENDANT_SHARD, 1L
        ), rewards);
    }

    @Test
    void titleLevelsAreAddedAndTheWeaponStillCountsOnce() {
        LegendaryWeapon weapon = legendary(5);
        setTitle(weapon, LegendaryTitles.REVERED, 5);

        LinkedHashMap<Spendable, Long> rewards = WeaponTitleUpgradeRefund.calculate(List.of(weapon));

        assertEquals(Map.of(
                Currencies.COIN, 7_740_000L,
                Currencies.SYNTHETIC_SHARD, 42_000L,
                Currencies.LEGEND_FRAGMENTS, 25_600L,
                Currencies.ASCENDANT_SHARD, 1L,
                MobDrop.ZENITH_STAR, 10L,
                Currencies.LIMIT_BREAKER, 2L
        ), rewards);
    }

    @Test
    void titleOnlyWeaponUsesTheSingleWeaponRate() {
        LegendaryWeapon weapon = legendary(0);
        setTitle(weapon, LegendaryTitles.REVERED, 1);

        LinkedHashMap<Spendable, Long> rewards = WeaponTitleUpgradeRefund.calculate(List.of(weapon));

        assertEquals(Map.of(
                Currencies.COIN, 200_000L,
                Currencies.SYNTHETIC_SHARD, 1_000L,
                Currencies.LEGEND_FRAGMENTS, 400L
        ), rewards);
    }

    @Test
    void secondTitleIsRefundedWithoutIncreasingTheWeaponCount() {
        LegendaryWeapon weapon = legendary(5);
        setTitle(weapon, LegendaryTitles.REVERED, 5);
        setTitle(weapon, LegendaryTitles.TITANIC, 5);

        LinkedHashMap<Spendable, Long> rewards = WeaponTitleUpgradeRefund.calculate(List.of(weapon));

        assertEquals(Map.of(
                Currencies.COIN, 13_940_000L,
                Currencies.SYNTHETIC_SHARD, 58_000L,
                Currencies.LEGEND_FRAGMENTS, 33_200L,
                Currencies.ASCENDANT_SHARD, 1L,
                MobDrop.ZENITH_STAR, 20L,
                Currencies.LIMIT_BREAKER, 5L
        ), rewards);
    }

    @Test
    void fifteenAndSixteenWeaponsBothUseTenPercent() {
        assertEquals(Map.of(
                Currencies.COIN, 150_000L,
                Currencies.SYNTHETIC_SHARD, 11_250L,
                Currencies.LEGEND_FRAGMENTS, 4_500L
        ), WeaponTitleUpgradeRefund.calculate(levelOneWeapons(15)));

        assertEquals(Map.of(
                Currencies.COIN, 160_000L,
                Currencies.SYNTHETIC_SHARD, 12_000L,
                Currencies.LEGEND_FRAGMENTS, 4_800L
        ), WeaponTitleUpgradeRefund.calculate(levelOneWeapons(16)));
    }

    @Test
    void unupgradedLegendariesAndEpicsAddNothing() {
        LegendaryWeapon plain = legendary(0);
        LegendaryWeapon titlePresentButNotUpgraded = legendary(0);
        setTitle(titlePresentButNotUpgraded, LegendaryTitles.REVERED, 0);
        EpicWeapon epic = new EpicWeapon();
        epic.upgrade();

        assertEquals(Map.of(), WeaponTitleUpgradeRefund.calculate(List.of(plain, titlePresentButNotUpgraded, epic)));
    }

    @Test
    void emptyInventoryProducesNoReward() {
        assertEquals(Map.of(), WeaponTitleUpgradeRefund.calculate(List.of()));
        assertEquals(Map.of(), WeaponTitleUpgradeRefund.calculate(null));
    }

    private static List<AbstractWeapon> levelOneWeapons(int count) {
        List<AbstractWeapon> weapons = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            weapons.add(legendary(1));
        }
        return weapons;
    }

    private static LegendaryWeapon legendary(int weaponLevel) {
        LegendaryWeapon weapon = new LegendaryWeapon();
        weapon.setUpgradeLevel(weaponLevel);
        return weapon;
    }

    private static void setTitle(LegendaryWeapon weapon, LegendaryTitles title, int titleLevel) {
        LegendaryWeaponTitleInfo titleInfo = new LegendaryWeaponTitleInfo();
        titleInfo.setUpgradeLevel(titleLevel);
        weapon.getTitles().put(title, titleInfo);
    }

}
