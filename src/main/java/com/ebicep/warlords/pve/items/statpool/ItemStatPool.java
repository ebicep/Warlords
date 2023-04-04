package com.ebicep.warlords.pve.items.statpool;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.ChatColor;

import java.util.HashMap;

public interface ItemStatPool<T extends Enum<T>> {

    default void applyToAbility(AbstractAbility ability, float value, ItemTier highestTier) {

    }

    default void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {

    }

    T[] getPool();

    HashMap<T, ItemTier.StatRange> getStatRange();

    default String getValueFormatted(float value) {
        return ChatColor.GREEN + formatValue(value) + getOperation().prepend + " " + ChatColor.GRAY + getName();
    }

    default String formatValue(float value) {
        return NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX.format(value / getDecimalPlace().value);
    }

    Operation getOperation();

    default DecimalPlace getDecimalPlace() {
        return DecimalPlace.TENTHS;
    }

    String getName();

    enum Operation {
        ADD(""),
        MULTIPLY("%");

        public final String prepend;

        Operation(String prepend) {
            this.prepend = prepend;
        }
    }

    enum DecimalPlace {

        TENTHS(10),
        ONES(1);

        public final int value;

        DecimalPlace(int value) {
            this.value = value;
        }
    }


}
