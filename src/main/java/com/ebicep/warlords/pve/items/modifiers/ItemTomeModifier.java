package com.ebicep.warlords.pve.items.modifiers;

public class ItemTomeModifier {

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
        public float getIncreasePerTier() {
            return 5;
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
        public float getIncreasePerTier() {
            return 5;
        }

    }

}
