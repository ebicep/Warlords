package com.ebicep.warlords.abilities.internal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShieldTest {

    @Test
    void overkillStaysAtZeroWhenClamped() {
        Shield shield = new Shield("Overgrowth Shield", 500);
        shield.setClampHealthAtZero(true);
        shield.setShieldHealth(80);

        shield.addShieldHealth(-200);

        assertEquals(0f, shield.getShieldHealth());
        assertFalse(shield.isBroken());
    }

    @Test
    void enablingTheClampClearsAnExistingNegativeRemainder() {
        Shield shield = new Shield("Overgrowth Shield", 500);
        shield.setShieldHealth(-40);

        shield.setClampHealthAtZero(true);

        assertEquals(0f, shield.getShieldHealth());
    }

    @Test
    void unclampedShieldsStillStoreOverkillAsNegativeHealth() {
        Shield shield = new Shield("Arcane Shield", 500);
        shield.setShieldHealth(80);

        shield.addShieldHealth(-200);

        assertEquals(-120f, shield.getShieldHealth());
        assertTrue(shield.isBroken());
    }

}
