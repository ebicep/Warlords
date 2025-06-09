package com.ebicep.warlords.player.ingame.cooldowns;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public enum CooldownTypes {

    INTERNAL("INTERNAL"),
    BUFF("BUFF"),
    LOW_LEVEL_DEBUFF("DEBUFF") {
        @Override
        public TextColor getTextColor() {
            return NamedTextColor.RED;
        }
    },
    HIGH_LEVEL_DEBUFF("TRUE DEBUFF") {
        @Override
        public TextColor getTextColor() {
            return TRUE_DEBUFF_COLOR;
        }
    },
    TRUE_DEBUFF("TRUE DEBUFF") {
        @Override
        public TextColor getTextColor() {
            return HIGH_LEVEL_DEBUFF_COLOR;
        }
    },
    ABILITY("ABILITY"),
    WEAPON("WEAPON"),
    ITEM("ITEM"),
    MASTERY("MASTERY"),
    ADDON("ADDON"),
    ASPECT("ASPECT"),
    FIELD_EFFECT("FIELD EFFECT"),
    SPEC("SPEC"),
    SPEC_BOOST("SPEC BOOST"),

    ;
    public static final TextColor HIGH_LEVEL_DEBUFF_COLOR = TextColor.color(255, 100, 100);
    public static final TextColor TRUE_DEBUFF_COLOR = TextColor.color(255, 25, 25);

    private final String name;

    CooldownTypes(String name) {
        this.name = name;
    }

    public TextColor getTextColor() {
        return NamedTextColor.GREEN;
    }

}
