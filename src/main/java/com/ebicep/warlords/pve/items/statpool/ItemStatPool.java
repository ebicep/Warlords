package com.ebicep.warlords.pve.items.statpool;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.ChatColor;

public interface ItemStatPool<T extends Enum<T>> {

    default void applyToAbility(AbstractAbility ability, int value) {

    }

    default void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, int value) {

    }

    T[] getPool();

    String getName();

    default String getValueFormatted(float value) {
        return ChatColor.GREEN + formatValue(value) + getOperation().prepend + " " + ChatColor.GRAY + getName();
    }

    Operation getOperation();

    default String formatValue(float value) {
        return NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX.format(value);
    }

    enum Operation {
        ADD(""),
        MULTIPLY("%");

        public final String prepend;

        Operation(String prepend) {
            this.prepend = prepend;
        }
    }


}
