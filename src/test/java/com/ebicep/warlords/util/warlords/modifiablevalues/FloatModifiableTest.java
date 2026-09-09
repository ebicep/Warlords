package com.ebicep.warlords.util.warlords.modifiablevalues;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class FloatModifiableTest {

    @Test
    void getModifierFindsModifierByLogName() {
        FloatModifiable cooldown = new FloatModifiable(10);
        cooldown.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, "Item", 0.8f);

        FloatModifiable.FloatModifier modifier = cooldown.getModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, "Item");
        assertNotNull(modifier);
        assertEquals(0.8f, modifier.getModifier(), 0.0001f);
        assertEquals(8f, cooldown.getCalculatedValue(), 0.0001f);
        assertNull(cooldown.getModifier(FloatModifiable.ModifierType.ADDITIVE, "Item"));
    }

}
