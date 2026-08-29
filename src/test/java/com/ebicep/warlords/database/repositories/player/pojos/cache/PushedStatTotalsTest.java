package com.ebicep.warlords.database.repositories.player.pojos.cache;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePvEBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PushedStatTotalsTest {

    @Test
    void coldCacheIgnoresApply() {
        PushedStatTotals totals = new PushedStatTotals();
        DatabaseGamePlayerBase gamePlayer = gamePlayer(3, 1, 2);

        totals.applyGeneral(gamePlayer, DatabaseGamePlayerResult.WON, 1);
        totals.applyPvE(pvePlayer(Map.of("zombie", 5L)), 1200, 1);
        totals.applyTotalWavesCleared(4, 1);

        assertFalse(totals.isWarmed());
        assertEquals(0, totals.getKills());
        assertEquals(0, totals.getTotalWavesCleared());
        assertTrue(totals.getMobKillsView().isEmpty());
    }

    @Test
    void warmThenApplyGeneralIncrements() {
        PushedStatTotals totals = new PushedStatTotals();
        totals.warm(() -> totals.fillGeneral(10, 1, 2, 3, 4, 5, 100, 20, 5, 50));

        DatabaseGamePlayerBase gamePlayer = gamePlayer(4, 2, 1);
        totals.applyGeneral(gamePlayer, DatabaseGamePlayerResult.WON, 1);

        assertEquals(14, totals.getKills());
        assertEquals(3, totals.getAssists());
        assertEquals(3, totals.getDeaths());
        assertEquals(4, totals.getWins());
        assertEquals(4, totals.getLosses());
        assertEquals(6, totals.getPlays());
        assertEquals(500, totals.getDamage());
        assertEquals(100, totals.getHealing());
        assertEquals(25, totals.getAbsorbed());
        assertEquals(90, totals.getExperience());
    }

    @Test
    void applyGeneralHandlesAllResults() {
        PushedStatTotals totals = new PushedStatTotals();
        totals.warm(() -> totals.fillGeneral(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        DatabaseGamePlayerBase gamePlayer = gamePlayer(1, 0, 0);

        totals.applyGeneral(gamePlayer, DatabaseGamePlayerResult.WON, 1);
        totals.applyGeneral(gamePlayer, DatabaseGamePlayerResult.LOST, 1);
        totals.applyGeneral(gamePlayer, DatabaseGamePlayerResult.DRAW, 1);
        totals.applyGeneral(gamePlayer, DatabaseGamePlayerResult.NONE, 1);

        assertEquals(4, totals.getKills());
        assertEquals(4, totals.getPlays());
        assertEquals(1, totals.getWins());
        assertEquals(2, totals.getLosses());
    }

    @Test
    void applyGeneralRespectsMultiplier() {
        PushedStatTotals totals = new PushedStatTotals();
        totals.warm(() -> totals.fillGeneral(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));

        totals.applyGeneral(gamePlayer(2, 0, 1), DatabaseGamePlayerResult.WON, 2);

        assertEquals(4, totals.getKills());
        assertEquals(2, totals.getDeaths());
        assertEquals(2, totals.getWins());
        assertEquals(2, totals.getPlays());
    }

    @Test
    void applyPvEMergesMobMaps() {
        PushedStatTotals totals = new PushedStatTotals();
        totals.warm(() -> totals.fillPvE(1000, Map.of("zombie", 2L), Map.of(), Map.of()));

        Map<String, Long> delta = new HashMap<>();
        delta.put("zombie", 3L);
        delta.put("skeleton", 1L);
        totals.applyPvE(pvePlayer(delta), 600, 2);

        assertEquals(2200, totals.getTotalTimePlayed());
        assertEquals(8L, totals.getMobKillsView().get("zombie"));
        assertEquals(2L, totals.getMobKillsView().get("skeleton"));
    }

    @Test
    void applyPvEMergesAllMapsAndRemovesZeroEntries() {
        PushedStatTotals totals = new PushedStatTotals();
        totals.warm(() -> totals.fillPvE(
                1000,
                Map.of("zombie", 2L),
                Map.of("zombie", 3L),
                Map.of("zombie", 4L)
        ));

        totals.applyPvE(
                pvePlayer(
                        Map.of("zombie", 2L),
                        Map.of("zombie", 3L),
                        Map.of("zombie", 4L)
                ),
                1000,
                -1
        );

        assertEquals(0, totals.getTotalTimePlayed());
        assertTrue(totals.getMobKillsView().isEmpty());
        assertTrue(totals.getMobAssistsView().isEmpty());
        assertTrue(totals.getMobDeathsView().isEmpty());
    }

    @Test
    void applyTotalWavesClearedRequiresWarmCache() {
        PushedStatTotals totals = new PushedStatTotals();
        totals.applyTotalWavesCleared(5, 1);
        assertEquals(0, totals.getTotalWavesCleared());

        totals.warm(() -> totals.fillTotalWavesCleared(1));
        totals.applyTotalWavesCleared(5, 2);
        assertEquals(11, totals.getTotalWavesCleared());
    }

    @Test
    void invalidateResetsAndAllowsRewarm() {
        PushedStatTotals totals = new PushedStatTotals();
        totals.warm(() -> totals.fillGeneral(1, 0, 0, 0, 0, 1, 0, 0, 0, 0));
        totals.applyGeneral(gamePlayer(1, 0, 0), DatabaseGamePlayerResult.WON, 1);

        totals.invalidate();
        assertFalse(totals.isWarmed());
        assertEquals(0, totals.getKills());

        totals.warm(() -> totals.fillGeneral(5, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        assertTrue(totals.isWarmed());
        assertEquals(5, totals.getKills());
    }

    @Test
    void warmIsIdempotent() {
        PushedStatTotals totals = new PushedStatTotals();
        totals.warm(() -> totals.fillGeneral(3, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        totals.warm(() -> totals.fillGeneral(99, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        assertEquals(3, totals.getKills());
    }

    private static DatabaseGamePlayerBase gamePlayer(int killCount, int assistCount, int deathCount) {
        return new DatabaseGamePlayerBase() {
            @Override
            public int getTotalKills() {
                return killCount;
            }

            @Override
            public int getTotalAssists() {
                return assistCount;
            }

            @Override
            public int getTotalDeaths() {
                return deathCount;
            }

            @Override
            public long getTotalDamage() {
                return killCount * 100L;
            }

            @Override
            public long getTotalHealing() {
                return killCount * 20L;
            }

            @Override
            public long getTotalAbsorbed() {
                return killCount * 5L;
            }

            @Override
            public long getExperienceEarnedSpec() {
                return killCount * 10L;
            }
        };
    }

    private static DatabaseGamePlayerPvEBase pvePlayer(Map<String, Long> mobKills) {
        return pvePlayer(mobKills, Map.of(), Map.of());
    }

    private static DatabaseGamePlayerPvEBase pvePlayer(
            Map<String, Long> mobKills,
            Map<String, Long> mobAssists,
            Map<String, Long> mobDeaths
    ) {
        return new DatabaseGamePlayerPvEBase() {
            @Override
            public Map<String, Long> getMobKills() {
                return mobKills;
            }

            @Override
            public Map<String, Long> getMobAssists() {
                return mobAssists;
            }

            @Override
            public Map<String, Long> getMobDeaths() {
                return mobDeaths;
            }
        };
    }
}
