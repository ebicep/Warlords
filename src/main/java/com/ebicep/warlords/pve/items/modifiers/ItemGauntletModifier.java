package com.ebicep.warlords.pve.items.modifiers;

public class ItemGauntletModifier {

    public enum Blessings implements ItemModifier<Blessings> {
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
        DINKY("Dinky"),
        MEEK("Meek"),
        KIND("Kind"),
        GRACFUL("Gracful"),
        FRIENDLY("Friendly");

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
