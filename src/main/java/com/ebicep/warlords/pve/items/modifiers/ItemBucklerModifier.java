package com.ebicep.warlords.pve.items.modifiers;

import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.ChatColor;

public class ItemBucklerModifier {

    public static final int INCREASE_PER_TIER = 2;

    public enum Blessings implements ItemModifier<Blessings> {
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
        public Blessings[] getValues() {
            return VALUES;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return ChatColor.GREEN + NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX.format((ordinal() + 1) * INCREASE_PER_TIER) + "%" + ChatColor.GRAY + " Weight";
        }

    }

    public enum Curses implements ItemModifier<Curses> {
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
        public Curses[] getValues() {
            return VALUES;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return ChatColor.RED + NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX.format(-(ordinal() + 1) * INCREASE_PER_TIER) + "%" + ChatColor.GRAY + " Weight";
        }

    }

}
