package com.ebicep.warlords.pve.items.modifiers;

public class ItemBucklerModifier {

    public enum Blessings implements ItemModifier<Blessings> {
        DWARFISH,
        TINY,
        COMPACT,
        FEATHERY,
        DIAPHANOUS;

        public static final Blessings[] VALUES = values();

        @Override
        public Blessings[] getValues() {
            return VALUES;
        }

        @Override
        public float getIncreasePerTier() {
            return 2.5f;
        }
    }

    public enum Curses implements ItemModifier<Curses> {
        LARGE,
        MASSIVE,
        KINGLY,
        COLOSSAL,
        GARGANTUAN;

        public static final Curses[] VALUES = values();

        @Override
        public Curses[] getValues() {
            return VALUES;
        }

        @Override
        public float getIncreasePerTier() {
            return 2.5f;
        }

    }

}
