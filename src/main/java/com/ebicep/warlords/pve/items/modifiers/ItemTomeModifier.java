package com.ebicep.warlords.pve.items.modifiers;

import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.ChatColor;

public class ItemTomeModifier {

    public static final int INCREASE_PER_TIER = 2;

    public enum Blessings implements ItemModifier<Blessings> {
        DELAYED("Delayed"),
        STRETCHED("Stretched"),
        PROLONGED("Prolonged"),
        LINGERING("Lingering"),
        ENDURING("Enduring");

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
            return ChatColor.GREEN + NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX.format((ordinal() + 1) * INCREASE_PER_TIER) + "%" + ChatColor.GRAY + " Ability Duration";
        }

    }

    public enum Curses implements ItemModifier<Curses> {
        BRIEF("Brief"),
        FLETTING("Fletting"),
        NIMBLE("Nimble"),
        ABRUPT("Abrupt"),
        INSTANTANEOUS("Instantaneous");

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
            return ChatColor.RED + NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX.format(-(ordinal() + 1) * INCREASE_PER_TIER) + "%" + ChatColor.GRAY + " Ability Duration";
        }

    }

}
