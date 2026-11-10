package com.ebicep.warlords.pve.newitems.attributes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThornsDamageTest {

    @Test
    void baseThornsStayAtTheCap() {
        assertEquals(500f, ThornsDamage.calculate(2_000f, 0.4f, 1f, ThornsDamage.BASE_CAP));
    }

    @Test
    void crownRaisesDamageAndTheDoubledCapIsTheCeiling() {
        int doubledCap = ThornsDamage.BASE_CAP * 2;
        assertEquals(120f, ThornsDamage.calculate(400f, 0.2f, 1.5f, doubledCap));
        assertEquals(1000f, ThornsDamage.calculate(2_000f, 0.4f, 1.5f, doubledCap));
    }

    @Test
    void damageWithoutCrownCannotPassTheCap() {
        assertEquals(500f, ThornsDamage.calculate(2_000f, 1f, 1f, ThornsDamage.BASE_CAP));
    }

}
