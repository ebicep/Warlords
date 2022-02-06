package com.ebicep.warlords.player.cooldowns;

public enum CooldownTypes {

    BUFF("BUFF"),
    DEBUFF("DEBUFF"),
    ABILITY("ABILITY"),

    ;

    private final String name;

    CooldownTypes(String name) {
        this.name = name;
    }
}
