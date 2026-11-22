package com.ebicep.warlords.player.ingame.motionsystem;

import com.ebicep.warlords.player.ingame.motionsystem.speed.OverrideValueModifier;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MotionSystemTest {

    @Test
    void knockbackImmunityIgnoresOtherModifiers() {
        MotionSystem knockback = new MotionSystem();
        knockback.addModifier(immunity("Stonelash"));
        knockback.addModifier(modifier("Item", -40));
        knockback.addModifier(modifier("Vulnerability", 80));
        knockback.addModifier(modifier("Reverse", -250));

        knockback.tick();

        assertEquals(0f, knockback.getLastValue());
    }

    @Test
    void knockbackImmunityIgnoresValueOverrides() {
        MotionSystem knockback = new MotionSystem();
        knockback.addModifier(immunity("Stonelash"));
        knockback.addModifier(new MotionModifierBuilder()
                .setName("Override")
                .setModifier(0)
                .setDuration(-1)
                .addAddons(new OverrideValueModifier(4f))
                .build());

        knockback.tick();

        assertEquals(0f, knockback.getLastValue());
    }

    @Test
    void knockbackImmunityIsNotRemovedWithOtherResistance() {
        MotionSystem knockback = new MotionSystem();
        knockback.addModifier(immunity("Stonelash"));
        knockback.addModifier(modifier("Item", -40));
        knockback.addModifier(modifier("Gale", -35));

        knockback.removeNegativeModifiers();
        knockback.removeModifier("Stonelash");
        knockback.tick();

        assertEquals(List.of("Stonelash"), knockback.getModifiers().stream().map(MotionModifier::getName).toList());
        assertEquals(0f, knockback.getLastValue());
    }

    @Test
    void knockbackImmunityIsNotReplacedByTheSameName() {
        MotionSystem knockback = new MotionSystem();
        knockback.addModifier(immunity("Stonelash"));
        knockback.addModifier(modifier("Stonelash", 100));
        knockback.addModifiers(List.of(modifier("STONELASH", -20)));
        knockback.tick();

        assertEquals(1, knockback.getModifiers().size());
        assertTrue(knockback.getModifiers().get(0).getAddons().stream().anyMatch(KnockbackImmunity.class::isInstance));
        assertEquals(KnockbackImmunity.MODIFIER, knockback.getModifiers().get(0).getModifier());
        assertEquals(0f, knockback.getLastValue());
    }

    @Test
    void ordinaryKnockbackStillCombinesTheStrongestSources() {
        MotionSystem knockback = new MotionSystem();
        knockback.addModifier(modifier("Item", -50));
        knockback.addModifier(modifier("Vulnerability", 100));
        knockback.tick();

        assertEquals(1f, knockback.getLastValue());

        knockback.removeNegativeModifiers();
        knockback.tick();

        assertEquals(List.of("Vulnerability"), knockback.getModifiers().stream().map(MotionModifier::getName).toList());
        assertEquals(2f, knockback.getLastValue());
    }

    @Test
    void fullResistanceWithoutImmunityCanStillBeCleared() {
        MotionSystem knockback = new MotionSystem();
        knockback.addModifier(modifier("Intervene", -100));
        knockback.tick();
        assertEquals(0f, knockback.getLastValue());

        knockback.removeNegativeModifiers();
        knockback.tick();

        assertTrue(knockback.getModifiers().isEmpty());
        assertEquals(1f, knockback.getLastValue());
    }

    private static MotionModifier immunity(String name) {
        return new MotionModifierBuilder()
                .setName(name)
                .setModifier(KnockbackImmunity.MODIFIER)
                .setDuration(-1)
                .addAddons(new KnockbackImmunity())
                .build();
    }

    private static MotionModifier modifier(String name, float value) {
        return new MotionModifierBuilder()
                .setName(name)
                .setModifier(value)
                .setDuration(-1)
                .build();
    }

}
