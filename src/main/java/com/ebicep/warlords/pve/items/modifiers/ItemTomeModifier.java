package com.ebicep.warlords.pve.items.modifiers;

import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.ChatColor;

public class ItemTomeModifier {

    public enum Blessings implements ItemModifier {
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
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return getDescriptionCalculated((ordinal() + 1) * getIncreasePerTier());
        }

        @Override
        public String getDescriptionCalculated(float amount) {
            return ChatColor.GREEN + NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX.format(amount) + "%" + ChatColor.GRAY + " Ability Duration";
        }

        @Override
        public float getIncreasePerTier() {
            return 1;
        }
    }

    public enum Curses implements ItemModifier {
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
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return getDescriptionCalculated(-(ordinal() + 1) * getIncreasePerTier());
        }

        @Override
        public String getDescriptionCalculated(float amount) {
            return ChatColor.RED + NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX.format(amount) + "%" + ChatColor.GRAY + " Ability Duration";
        }

        @Override
        public float getIncreasePerTier() {
            return 2;
        }

    }

}
