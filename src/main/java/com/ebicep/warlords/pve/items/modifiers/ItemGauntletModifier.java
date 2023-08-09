package com.ebicep.warlords.pve.items.modifiers;

import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

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
        public TextComponent getDescription() {
            return getDescriptionCalculated((ordinal() + 1) * getIncreasePerTier());
        }

        @Override
        public TextComponent getDescriptionCalculated(float amount) {
            return Component.textOfChildren(
                    Component.text(NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX.format(amount) + "%", NamedTextColor.GREEN),
                    Component.text(" Mob Drop Chance", NamedTextColor.GRAY)
            );
        }

        @Override
        public TextComponent getDescriptionCalculatedInverted(float amount) {
            return Component.textOfChildren(
                    Component.text("Mob Drop Chance: ", NamedTextColor.GRAY),
                    Component.text(NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX.format(amount) + "%", NamedTextColor.GREEN)
            );
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
        public TextComponent getDescription() {
            return getDescriptionCalculated(-(ordinal() + 1) * getIncreasePerTier());
        }

        @Override
        public TextComponent getDescriptionCalculated(float amount) {
            return Component.textOfChildren(
                    Component.text(NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX.format(amount) + "%", NamedTextColor.RED),
                    Component.text(" Mob Drop Chance", NamedTextColor.GRAY)
            );
        }

        @Override
        public TextComponent getDescriptionCalculatedInverted(float amount) {
            return Component.textOfChildren(
                    Component.text("Mob Drop Chance: ", NamedTextColor.GRAY),
                    Component.text(NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX.format(amount) + "%", NamedTextColor.RED)
            );
        }

        @Override
        public float getIncreasePerTier() {
            return 5;
        }

    }

}
