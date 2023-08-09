package com.ebicep.warlords.pve.items.modifiers;

import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

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
        public TextComponent getDescription() {
            return getDescriptionCalculated((ordinal() + 1) * getIncreasePerTier());
        }

        @Override
        public TextComponent getDescriptionCalculated(float amount) {
            return Component.textOfChildren(
                    Component.text(NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX_INVERSE.format(amount) + "%", NamedTextColor.GREEN),
                    Component.text(" Weight", NamedTextColor.GRAY)
            );
        }

        @Override
        public TextComponent getDescriptionCalculatedInverted(float amount) {
            return Component.textOfChildren(
                    Component.text("Weight: ", NamedTextColor.GRAY),
                    Component.text(NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX_INVERSE.format(amount) + "%", NamedTextColor.GREEN)
            );
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
        public TextComponent getDescription() {
            return getDescriptionCalculated(-(ordinal() + 1) * getIncreasePerTier());
        }

        @Override
        public TextComponent getDescriptionCalculated(float amount) {
            return Component.textOfChildren(
                    Component.text(NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX_INVERSE.format(amount) + "%", NamedTextColor.RED),
                    Component.text(" Weight", NamedTextColor.GRAY)
            );
        }

        @Override
        public TextComponent getDescriptionCalculatedInverted(float amount) {
            return Component.textOfChildren(
                    Component.text("Weight: ", NamedTextColor.GRAY),
                    Component.text(NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX_INVERSE.format(amount) + "%", NamedTextColor.RED)
            );
        }

        @Override
        public float getIncreasePerTier() {
            return 4;
        }

    }

}
