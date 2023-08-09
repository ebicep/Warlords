package com.ebicep.warlords.pve.items.modifiers;

import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

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
        public TextComponent getDescription() {
            return getDescriptionCalculated((ordinal() + 1) * getIncreasePerTier());
        }

        @Override
        public TextComponent getDescriptionCalculated(float amount) {
            return Component.textOfChildren(
                    Component.text(NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX.format(amount) + "%", NamedTextColor.GREEN),
                    Component.text(" Ability Duration", NamedTextColor.GRAY)
            );
        }

        @Override
        public TextComponent getDescriptionCalculatedInverted(float amount) {
            return Component.textOfChildren(
                    Component.text("Ability Duration: ", NamedTextColor.GRAY),
                    Component.text(NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX.format(amount) + "%", NamedTextColor.GREEN)
            );
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
        public TextComponent getDescription() {
            return getDescriptionCalculated(-(ordinal() + 1) * getIncreasePerTier());
        }

        @Override
        public TextComponent getDescriptionCalculated(float amount) {
            return Component.textOfChildren(
                    Component.text(NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX.format(amount) + "%", NamedTextColor.RED),
                    Component.text(" Ability Duration", NamedTextColor.GRAY)
            );
        }

        @Override
        public TextComponent getDescriptionCalculatedInverted(float amount) {
            return Component.textOfChildren(
                    Component.text("Ability Duration: ", NamedTextColor.GRAY),
                    Component.text(NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX.format(amount) + "%", NamedTextColor.RED)
            );
        }

        @Override
        public float getIncreasePerTier() {
            return 2;
        }

    }

}
