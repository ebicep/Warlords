package com.ebicep.warlords.database.repositories.player.pojos.cache;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.anomaly.DatabaseGamePlayerPvEAnomaly;
import com.ebicep.warlords.database.repositories.games.pojos.pve.anomaly.DatabaseGamePvEAnomaly;
import com.ebicep.warlords.database.repositories.games.pojos.pve.onslaught.DatabaseGamePlayerPvEOnslaught;
import com.ebicep.warlords.database.repositories.games.pojos.pve.onslaught.DatabaseGamePvEOnslaught;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.pojos.cache.support.PushCacheIntegrationSupport;
import com.ebicep.warlords.database.repositories.player.pojos.cache.support.PushCacheTestFixtures;
import com.ebicep.warlords.database.repositories.player.pojos.cache.support.PushCacheTestFixtures.EventCase;
import com.ebicep.warlords.database.repositories.player.pojos.cache.support.PushCacheTestFixtures.PvEStats;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.pve.DifficultyIndex;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PushCachePvEIntegrationTest {

    @AfterEach
    void tearDown() {
        PushCacheTestFixtures.clearCurrentGameEvent();
    }

    @Test
    void onslaughtUpdateMatchesTreeWalk() {
        DatabasePlayer databasePlayer = PushCacheTestFixtures.newWarmedPvePlayer();
        PvEStats stats = PvEStats.simple(6, 2);
        DatabaseGamePlayerPvEOnslaught player = PushCacheTestFixtures.onslaughtPlayer(stats);
        DatabaseGamePvEOnslaught game = PushCacheTestFixtures.pveOnslaughtGame(player, 2400);

        PushCacheIntegrationSupport.applyUpdate(
                databasePlayer,
                game,
                GameMode.ONSLAUGHT,
                player,
                DatabaseGamePlayerResult.LOST,
                1
        );

        assertEquals(6, databasePlayer.getPveStats().getKills());
        assertEquals(6, databasePlayer.getPveStats().getOnslaughtStats().getKills());
        assertEquals(6L, databasePlayer.getPveStats().getMobKills().get("zombie"));
        assertEquals(2400, databasePlayer.getPveStats().getTotalTimePlayed());
        assertEquals(6, databasePlayer.getKills());
    }

    @Test
    void anomalyUpdateMatchesTreeWalk() {
        DatabasePlayer databasePlayer = PushCacheTestFixtures.newWarmedPvePlayer();
        PvEStats stats = PvEStats.simple(5, 1);
        DatabaseGamePlayerPvEAnomaly player = PushCacheTestFixtures.anomalyPlayer(stats);
        DatabaseGamePvEAnomaly game = PushCacheTestFixtures.pveAnomalyGame(player, 1800);

        PushCacheIntegrationSupport.applyUpdate(
                databasePlayer,
                game,
                GameMode.ANOMALY,
                player,
                DatabaseGamePlayerResult.WON,
                1
        );

        assertEquals(5, databasePlayer.getPveStats().getAnomalyStats().getKills());
        assertEquals(5, databasePlayer.getPveStats().getKills());
        assertEquals(1800, databasePlayer.getPveStats().getTotalTimePlayed());
    }

    @ParameterizedTest
    @EnumSource(value = DifficultyIndex.class, names = {"EASY", "NORMAL", "HARD", "EXTREME", "ENDLESS"})
    void waveDefenseDifficultyUpdatesMatchTreeWalk(DifficultyIndex difficulty) {
        DatabasePlayer databasePlayer = PushCacheTestFixtures.newWarmedPvePlayer();
        PvEStats stats = PvEStats.simple(4, 1);
        DatabaseGamePlayerPvEWaveDefense player = PushCacheTestFixtures.waveDefensePlayer(stats);
        DatabaseGamePvEWaveDefense game = PushCacheTestFixtures.pveWaveDefenseGame(player, difficulty, 3, 1500);

        PushCacheIntegrationSupport.applyUpdate(
                databasePlayer,
                game,
                GameMode.WAVE_DEFENSE,
                player,
                DatabaseGamePlayerResult.WON,
                1
        );

        assertEquals(4, databasePlayer.getPveStats().getWaveDefenseStats().getKills());
        assertEquals(4, databasePlayer.getPveStats().getWaveDefenseStats().getDifficultyStats(difficulty).getKills());
        assertEquals(3, databasePlayer.getPveStats().getWaveDefenseStats().getTotalWavesCleared());
        assertTrue(databasePlayer.getPveStats().getWaveDefenseStats().getMobKills().containsKey("zombie"));
    }

    static Stream<EventCase> eventCases() {
        return PushCacheTestFixtures.eventCases().stream();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("eventCases")
    void eventFamilyUpdatesMatchTreeWalk(EventCase eventCase) {
        PushCacheTestFixtures.stubCurrentGameEvent(eventCase.gameEvent());
        DatabasePlayer databasePlayer = PushCacheTestFixtures.newWarmedPvePlayer();
        PvEStats stats = PvEStats.simple(7, 2);
        DatabaseGamePlayerPvEBase player = eventCase.playerFactory().get();
        PushCacheTestFixtures.configurePvEPlayer(player, stats);
        var game = PushCacheTestFixtures.pveEventGame(eventCase, player, 2000);

        PushCacheIntegrationSupport.applyUpdate(
                databasePlayer,
                game,
                GameMode.EVENT_WAVE_DEFENSE,
                player,
                DatabaseGamePlayerResult.NONE,
                1
        );

        assertEquals(7, databasePlayer.getPveStats().getEventStats().getKills());
        assertEquals(7, databasePlayer.getPveStats().getKills());
        assertEquals(7L, databasePlayer.getPveStats().getEventStats().getMobKills().get("zombie"));
    }
}
