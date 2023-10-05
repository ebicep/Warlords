package com.ebicep.warlords.player.ingame.cooldowns;

public enum CooldownTypes {

    BUFF("BUFF"),
    DEBUFF("DEBUFF"),
    ABILITY("ABILITY"),
    WEAPON("WEAPON"),
    ITEM("ITEM"),
    MASTERY("MASTERY"),
    ADDON("ADDON"),
    ASPECT("ASPECT"),
    FIELD_EFFECT("FIELD EFFECT"),

    ;

    private final String name;

    CooldownTypes(String name) {
        this.name = name;
    }
}
