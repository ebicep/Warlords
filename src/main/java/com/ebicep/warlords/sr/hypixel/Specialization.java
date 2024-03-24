package com.ebicep.warlords.sr.hypixel;

import java.util.concurrent.ThreadLocalRandom;

enum Specialization {
    PYROMANCER(SpecType.DAMAGE), CRYOMANCER(SpecType.TANK), AQUAMANCER(SpecType.HEALER),
    BERSERKER(SpecType.DAMAGE), DEFENDER(SpecType.TANK), REVENANT(SpecType.HEALER),
    AVENGER(SpecType.DAMAGE), CRUSADER(SpecType.TANK), PROTECTOR(SpecType.HEALER),
    THUNDERLORD(SpecType.DAMAGE), SPIRITGUARD(SpecType.TANK), EARTHWARDEN(SpecType.HEALER);
    public static final Specialization[] VALUES = values();

    public static Specialization getRandomSpec() {
        return VALUES[ThreadLocalRandom.current().nextInt(VALUES.length)];
    }

    public final SpecType specType;

    Specialization(SpecType specType) {
        this.specType = specType;
    }
}
