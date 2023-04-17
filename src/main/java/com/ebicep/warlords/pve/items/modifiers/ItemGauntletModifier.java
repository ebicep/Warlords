package com.ebicep.warlords.pve.items.modifiers;

import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.ChatColor;

public class ItemGauntletModifier {

    public enum Blessings implements ItemModifier {
        STRONG("Strong"),
        POWERFUL("Powerful"),
        EXPLOSIVE("Explosive"),
        VIOLENT("Violent"),
        BELIGERENT("Beligerent");

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
            return ChatColor.GREEN + NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX.format(amount) + "%" + ChatColor.GRAY + " Mob Drop Chance";
        }

        @Override
        public String getDescriptionCalculatedInverted(float amount) {
            return ChatColor.GRAY + "Mob Drop Chance: " + ChatColor.GREEN + NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX.format(amount) + "%";
        }

        @Override
        public float getIncreasePerTier() {
            return 2.5f;
        }
    }

    public enum Curses implements ItemModifier {
        DINKY("Dinky"),
        MEEK("Meek"),
        KIND("Kind"),
        GRACEFUL("Graceful"),
        FRIENDLY("Friendly");

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
            return ChatColor.RED + NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX.format(amount) + "%" + ChatColor.GRAY + " Mob Drop Chance";
        }

        @Override
        public String getDescriptionCalculatedInverted(float amount) {
            return ChatColor.GRAY + "Mob Drop Chance: " + ChatColor.RED + NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX.format(amount) + "%";
        }

        @Override
        public float getIncreasePerTier() {
            return 5;
        }

    }

}
