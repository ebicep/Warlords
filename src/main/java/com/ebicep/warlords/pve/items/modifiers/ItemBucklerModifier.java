package com.ebicep.warlords.pve.items.modifiers;

import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.ChatColor;

public class ItemBucklerModifier {

    public enum Blessings implements ItemModifier {
        DWARFISH("Dwarfish"),
        TINY("Tiny"),
        COMPACT("Compact"),
        FEATHERY("Feathery"),
        DIAPHANOUS("Diaphanous");

        public static final Blessings[] VALUES = values();
        public final String name;

        Blessings(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return getDescriptionCalculated((ordinal() + 1) * getIncreasePerTier());
        }

        @Override
        public String getDescriptionCalculated(float amount) {
            return ChatColor.GREEN + NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX_INVERSE.format(amount) + "%" + ChatColor.GRAY + " Weight";
        }

        @Override
        public float getIncreasePerTier() {
            return 2;
        }
    }

    public enum Curses implements ItemModifier {
        LARGE("Large"),
        MASSIVE("Massive"),
        KINGLY("Kingly"),
        COLOSSAL("Colossal"),
        GARGANTUAN("Gargantuan");

        public static final Curses[] VALUES = values();
        public final String name;

        Curses(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return getDescriptionCalculated(-(ordinal() + 1) * getIncreasePerTier());
        }

        @Override
        public String getDescriptionCalculated(float amount) {
            return ChatColor.RED + NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX_INVERSE.format(amount) + "%" + ChatColor.GRAY + " Weight";
        }

        @Override
        public float getIncreasePerTier() {
            return 4;
        }

    }

}
