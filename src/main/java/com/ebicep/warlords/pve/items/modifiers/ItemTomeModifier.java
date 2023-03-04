package com.ebicep.warlords.pve.items.modifiers;

public class ItemTomeModifier {

    public enum Blessings implements ItemModifier<Blessings> {
        DELAYED,
        STRETCHED,
        PROLONGED,
        LINGERING,
        ENDURING;

        public static final Blessings[] VALUES = values();

        @Override
        public Blessings[] getValues() {
            return VALUES;
        }

        @Override
        public float getIncreasePerTier() {
            return 5;
        }
    }

    public enum Curses implements ItemModifier<Curses> {
        BRIEF,
        FLETTING,
        NIMBLE,
        ABRUPT,
        INSTANTANEOUS;

        public static final Curses[] VALUES = values();

        @Override
        public Curses[] getValues() {
            return VALUES;
        }

        @Override
        public float getIncreasePerTier() {
            return 5;
        }

    }

}
