package com.ebicep.warlords.pve.weapons.weaponaddons;

import com.ebicep.warlords.pve.weapons.WeaponStats;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

import static com.ebicep.warlords.util.java.Utils.generateRandomIndexFromListSize;

public interface StarPieceBonus {

    default int getStarPieceBonusCost() {
        return 10000;
    }

    int getStarPieceBonusValue();

    default float getStarPieceBonusMultiplicativeValue() {
        return 1 + getStarPieceBonusValue() / 100.0f;
    }

    default List<WeaponStats> getRandomStatBonus() {
        return Arrays.asList(
                WeaponStats.MELEE_DAMAGE,
                WeaponStats.CRIT_CHANCE,
                WeaponStats.CRIT_MULTIPLIER,
                WeaponStats.HEALTH_BONUS
        );
    }

    default WeaponStats generateRandomStatBonus() {
        List<WeaponStats> randomStatBonus = getRandomStatBonus();
        return randomStatBonus.get(generateRandomIndexFromListSize(randomStatBonus.size()));
    }

    default String getStarPieceBonusString() {
        return ChatColor.WHITE + " (+" + getStarPieceBonusValue() + "%)";
    }

    default String getStarPieceBonusMultiplicativeString(double stat) {
        //return NumberFormat.formatOptionalHundredths(stat) + " > " +  NumberFormat.formatOptionalHundredths(stat * getStarPieceBonusMultiplicativeValue());
        return NumberFormat.formatOptionalHundredths(stat * getStarPieceBonusMultiplicativeValue());
    }

}
