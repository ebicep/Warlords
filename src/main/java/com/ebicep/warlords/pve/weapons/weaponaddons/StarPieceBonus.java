package com.ebicep.warlords.pve.weapons.weaponaddons;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.StarPieces;
import com.ebicep.warlords.pve.weapons.WeaponStats;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.LinkedHashMap;
import java.util.List;

import static com.ebicep.warlords.util.java.JavaUtils.generateRandomIndexFromListSize;

public interface StarPieceBonus {

    default WeaponStats generateRandomStatBonus() {
        List<WeaponStats> randomStatBonus = getRandomStatBonus();
        return randomStatBonus.get(generateRandomIndexFromListSize(randomStatBonus.size()));
    }

    List<WeaponStats> getRandomStatBonus();

    default Component getStarPieceBonusString() {
        return Component.text(" (+" + getStarPieceBonusValue() + "% ✦)", NamedTextColor.WHITE);
    }

    int getStarPieceBonusValue();

    default String getStarPieceBonusMultiplicativeString(double stat) {
        //return NumberFormat.formatOptionalHundredths(stat) + " > " +  NumberFormat.formatOptionalHundredths(stat * getStarPieceBonusMultiplicativeValue());
        return NumberFormat.formatOptionalHundredths(stat * getStarPieceBonusMultiplicativeValue());
    }

    default float getStarPieceBonusMultiplicativeValue() {
        return 1 + getStarPieceBonusValue() / 100.0f;
    }

    default List<Component> getStarPieceCostLore(StarPieces starPieceCurrency) {
        return PvEUtils.getCostLore(getStarPieceBonusCost(starPieceCurrency), true);
    }

    default LinkedHashMap<Currencies, Long> getStarPieceBonusCost(StarPieces starPieceCurrency) {
        return new LinkedHashMap<>() {{
            put(Currencies.COIN, 10000L);
            put(Currencies.SYNTHETIC_SHARD, 50L);
            put(starPieceCurrency.currency, 1L);
        }};
    }

}
