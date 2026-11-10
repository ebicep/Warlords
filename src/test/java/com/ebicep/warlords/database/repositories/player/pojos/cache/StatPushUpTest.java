package com.ebicep.warlords.database.repositories.player.pojos.cache;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.pojos.cache.support.PushCacheTestFixtures;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatPushUpTest {

    @Test
    void mapsEqualNormalizesZeroEntries() {
        Map<String, Long> left = new HashMap<>();
        left.put("zombie", 2L);
        left.put("empty", 0L);

        Map<String, Long> right = new HashMap<>();
        right.put("zombie", 2L);

        assertTrue(StatPushUp.mapsEqual(left, right));
        assertTrue(StatPushUp.mapsEqual(null, Map.of()));
    }

    @Test
    void mapsEqualDetectsDifferences() {
        assertFalse(StatPushUp.mapsEqual(Map.of("a", 1L), Map.of("a", 2L)));
    }

    @Test
    void warmAllLeavesEmptyPlayerAtZero() {
        DatabasePlayer databasePlayer = PushCacheTestFixtures.newWarmedPlayer();

        assertEquals(0, databasePlayer.getKills());
        assertEquals(0, databasePlayer.getPubStats().getCtfStats().getKills());
    }

    @Test
    void rebuildSelectedCachesResetsToTreeWalk() {
        DatabasePlayer databasePlayer = PushCacheTestFixtures.newWarmedPlayer();
        databasePlayer.pushedStats().applyGeneral(
                PushCacheTestFixtures.ctfPlayer(PushCacheTestFixtures.GameStats.simple(5, 1)),
                DatabaseGamePlayerResult.WON,
                1
        );

        StatPushUp.rebuildSelectedCaches(databasePlayer);

        assertEquals(0, databasePlayer.getKills());
        assertTrue(databasePlayer.pushedStats().isWarmed());
    }

    @Test
    void applyPvEUpdatesWarmedTotals() {
        PushedStatTotals totals = new PushedStatTotals();
        totals.warm(() -> totals.fillPvE(0, Map.of(), Map.of(), Map.of()));

        DatabaseGamePlayerPvEBase gamePlayer = PushCacheTestFixtures.onslaughtPlayer(PushCacheTestFixtures.PvEStats.simple(3, 1));
        DatabaseGamePvEWaveDefense game = PushCacheTestFixtures.pveWaveDefenseGame(
                PushCacheTestFixtures.waveDefensePlayer(PushCacheTestFixtures.PvEStats.simple(3, 1)),
                com.ebicep.warlords.pve.DifficultyIndex.NORMAL,
                2,
                1200
        );

        StatPushUp.applyPvE(totals, gamePlayer, DatabaseGamePlayerResult.WON, game, 1);

        assertEquals(1200, totals.getTotalTimePlayed());
        assertEquals(3L, totals.getMobKillsView().get("zombie"));
        assertEquals(2, totals.getTotalWavesCleared());
        assertEquals(3, totals.getKills());
        assertEquals(1, totals.getWins());
    }
}
