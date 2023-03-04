package com.ebicep.warlords.pve.items.modifiers;

public class ItemGauntletModifier {

    public enum Blessings implements ItemModifier<Blessings> {
        STRONG,
        POWERFUL,
        EXPLOSIVE,
        VIOLENT,
        BELIGERENT;

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
        DINKY,
        MEEK,
        KIND,
        GRACFUL,
        FRIENDLY;

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
