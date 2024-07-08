package com.ebicep.warlords.sr.hypixel;

import java.util.function.Function;

public enum SpecType {
    DAMAGE(Color::darkRed), TANK(Color::gold), HEALER(Color::darkGreen);
    public static final SpecType[] VALUES = values();
    public final Function<Color, String> getColor;

    SpecType(Function<Color, String> getColor) {
        this.getColor = getColor;
    }
}
