package com.ebicep.warlords.pve.newitems.setbonus.sets;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OverflowTest {

    private static final int THRESHOLD = 90;
    private static final int CONVERSION = 10;
    private static final int CAP = 5;

    @Test
    void healingBelowTheThresholdGrantsNoEnergy() {
        assertEquals(0f, energy(800f, 1000f, 50f));
    }

    @Test
    void onlyThePortionAboveTheThresholdIsConverted() {
        assertEquals(1f, energy(890f, 1000f, 20f));
        assertEquals(4f, energy(950f, 1000f, 40f));
    }

    @Test
    void overhealAboveTheThresholdCountsAndIsCapped() {
        assertEquals(3f, energy(1000f, 1000f, 30f));
        assertEquals(5f, energy(950f, 1000f, 200f));
        assertEquals(5f, energy(800f, 1000f, 250f));
    }

    @Test
    void nonPositiveHealingOrHealthGrantsNoEnergy() {
        assertEquals(0f, energy(950f, 1000f, 0f));
        assertEquals(0f, energy(950f, 0f, 40f));
    }

    private static float energy(float currentHealth, float maxHealth, float healing) {
        return Overflow.energyFromExcessHealing(healing, currentHealth, maxHealth, THRESHOLD, CONVERSION, CAP);
    }

}
