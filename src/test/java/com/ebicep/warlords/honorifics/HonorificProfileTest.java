package com.ebicep.warlords.honorifics;

import org.junit.jupiter.api.Test;

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
}
