package com.ebicep.warlords.player;

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
