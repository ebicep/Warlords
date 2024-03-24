package com.ebicep.warlords.sr.hypixel;

import java.util.function.Function;

enum Team {
    BLUE(Color::blue), RED(Color::red);
    public static final Team[] VALUES = values();
    public final Function<Color, String> getColor;

    Team(Function<Color, String> getColor) {
        this.getColor = getColor;
    }

    public Team next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

}
