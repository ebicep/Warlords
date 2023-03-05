package com.ebicep.warlords.pve.items.statpool;

import org.bukkit.ChatColor;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public interface ItemStatPool<T extends Enum<T>> {

    DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##") {{
        setDecimalSeparatorAlwaysShown(false);
        setRoundingMode(RoundingMode.HALF_UP);
        setPositivePrefix("+");
        setNegativePrefix("-");
    }};

    T[] getPool();

    String getName();

    default String getValueFormatted(float value) {
        return ChatColor.GREEN + formatValue(value) + getOperation().prepend + " " + ChatColor.GRAY + getName();
    }

    Operation getOperation();

    default String formatValue(float value) {
        return DECIMAL_FORMAT.format(value);
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
