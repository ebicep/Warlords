package com.ebicep.warlords.honorifics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HonorificProfileTest {

    @Test
    void teamBossDefeatIsRecordedOncePerBoss() {
        HonorificProfile profile = new HonorificProfile();

        assertFalse(profile.hasTeamBossDefeat("ONE_OF_NINE"));
        assertFalse(profile.hasTeamBossDefeat("LILIUM"));

        assertTrue(profile.recordTeamBossDefeat("ONE_OF_NINE"));
        assertTrue(profile.hasTeamBossDefeat("ONE_OF_NINE"));
        assertFalse(profile.recordTeamBossDefeat("ONE_OF_NINE"));

        assertFalse(profile.hasTeamBossDefeat("LILIUM"));
        assertTrue(profile.recordTeamBossDefeat("LILIUM"));
        assertTrue(profile.hasTeamBossDefeat("LILIUM"));
    }

    @Test
    void otherMobsAreNotTeamBossDefeats() {
        HonorificProfile profile = new HonorificProfile();

        assertFalse(profile.recordTeamBossDefeat("ZOMBIE_LANCER"));
        assertFalse(profile.hasTeamBossDefeat("ZOMBIE_LANCER"));
        assertFalse(profile.recordTeamBossDefeat(null));
        assertFalse(profile.hasTeamBossDefeat(null));
    }

    @Test
    void combatHonorificCountersOnlyMoveForward() {
        HonorificProfile profile = new HonorificProfile();

        profile.addBurnKills(1);
        profile.addBurnKills(499);
        profile.addBurnKills(0);
        profile.addBurnKills(-3);
        assertEquals(500, profile.getBurnKills());

        profile.addFrogTransformation();
        profile.addFrogTransformation();
        assertEquals(2, profile.getFrogTransformations());

        assertFalse(profile.recordSingleHitDamage(0));
        assertTrue(profile.recordSingleHitDamage(299_999));
        assertFalse(profile.recordSingleHitDamage(299_999));
        assertTrue(profile.recordSingleHitDamage(300_000));
        assertEquals(300_000, profile.getHighestSingleHitDamage());

        assertFalse(profile.hasMaxedPveWeapon());
        profile.recordMaxedPveWeapon();
        assertTrue(profile.hasMaxedPveWeapon());

        profile.addFrozenOrSlowedKills(3_000);
        profile.addFrozenOrSlowedKills(-1);
        assertEquals(3_000, profile.getFrozenOrSlowedKills());
    }

    @Test
    void newHonorificsAreListedWithoutAPurchaseCost() {
        assertEquals("Cooked", Honorific.COOKED.getDisplayName());
        assertEquals("Kill 500 enemies with the burn effect.", Honorific.COOKED.getRequirement());
        assertEquals("Frog", Honorific.FROG.getDisplayName());
        assertEquals("Fractal", Honorific.FRACTAL.getDisplayName());
        assertEquals("Deal 300K damage in a single hit.", Honorific.FRACTAL.getRequirement());
        assertEquals("Heavyweight", Honorific.HEAVYWEIGHT.getDisplayName());
        assertEquals("You Need Me", Honorific.YOU_NEED_ME.getDisplayName());
        assertEquals("Return 100 Flags in Capture the flag.", Honorific.YOU_NEED_ME.getRequirement());
        assertEquals("Perfected", Honorific.PERFECTED.getDisplayName());
        assertEquals("Plant", Honorific.PLANT.getDisplayName());
        assertEquals("Max out the foraging life skill.", Honorific.PLANT.getRequirement());
        assertEquals("Pinguin", Honorific.PINGUIN.getDisplayName());
        assertEquals("Kill 3000 frozen or slowed enemies.", Honorific.PINGUIN.getRequirement());
        assertFalse(Honorific.PLANT.isPurchasable());
        assertFalse(Honorific.COOKED.isPurchasable());
        assertFalse(Honorific.PINGUIN.isPurchasable());
    }
}
