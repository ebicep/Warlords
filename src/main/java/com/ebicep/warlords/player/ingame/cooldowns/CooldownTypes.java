package com.ebicep.warlords.player.ingame.cooldowns;

public enum CooldownTypes {

    BUFF("BUFF"),
    DEBUFF("DEBUFF"),
    ABILITY("ABILITY"),
    WEAPON("WEAPON"),

    ;

    private final String name;

    CooldownTypes(String name) {
        this.name = name;
    }
}
