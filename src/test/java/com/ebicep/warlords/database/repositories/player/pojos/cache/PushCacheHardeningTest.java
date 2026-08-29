package com.ebicep.warlords.database.repositories.player.pojos.cache;

import com.ebicep.warlords.database.repositories.events.pojos.GameEvents;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.ctf.DatabaseGameCTF;
import com.ebicep.warlords.database.repositories.games.pojos.ctf.DatabaseGamePlayerCTF;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza.DatabaseGamePlayerPvEEventBoltaroBonanza;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza.DatabaseGamePvEEventBoltaroBonanza;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.cache.support.PushCacheIntegrationSupport;
import com.ebicep.warlords.database.repositories.player.pojos.cache.support.PushCacheTestFixtures;
import com.ebicep.warlords.database.repositories.player.pojos.cache.support.PushCacheTestFixtures.GameStats;
import com.ebicep.warlords.database.repositories.player.pojos.cache.support.PushCacheTestFixtures.PvEStats;
import com.ebicep.warlords.database.repositories.player.pojos.cache.support.PushCacheVerifier;
import com.ebicep.warlords.database.repositories.player.pojos.cache.support.TestReflection;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.DifficultyIndex;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class PushCacheHardeningTest {

    @AfterEach
    void tearDown() {
        PushCacheTestFixtures.clearCurrentGameEvent();
    }

    @Test
    void productionRoutingUpdatesNonPyromancerLeafAndRawCaches() {
        DatabasePlayer databasePlayer = PushCacheTestFixtures.newWarmedPlayer();
        DatabaseGamePlayerCTF gamePlayer = PushCacheTestFixtures.ctfPlayer(GameStats.simple(5, 1));
        TestReflection.setField(gamePlayer, "spec", Specializations.CRYOMANCER);

        PushCacheIntegrationSupport.applyUpdate(
                databasePlayer,
                PushCacheTestFixtures.pubCtfGame(),
                GameMode.CAPTURE_THE_FLAG,
                gamePlayer,
                DatabaseGamePlayerResult.WON,
                1
        );

        assertEquals(5, databasePlayer.getPubStats().getCtfStats().getSpec(Specializations.CRYOMANCER).getKills());
        assertEquals(0, databasePlayer.getPubStats().getCtfStats().getSpec(Specializations.PYROMANCER).getKills());
        assertEquals(5, databasePlayer.getPubStats().getCtfStats().pushedStats().getKills());
        assertEquals(5, databasePlayer.getPubStats().pushedStats().getKills());
        assertEquals(5, databasePlayer.pushedStats().getKills());
    }

    @Test
    void customPrivateGameDoesNotUpdateCompOrRootCache() {
        DatabasePlayer databasePlayer = PushCacheTestFixtures.newWarmedPlayer();
        DatabaseGameCTF game = PushCacheTestFixtures.compCtfGame();
        game.setGameAddons(List.of(GameAddon.PRIVATE_GAME, GameAddon.CUSTOM_GAME));

        databasePlayer.updateStats(
                databasePlayer,
                game,
                GameMode.CAPTURE_THE_FLAG,
                PushCacheTestFixtures.ctfPlayer(GameStats.simple(5, 1)),
                DatabaseGamePlayerResult.WON,
                1,
                PlayersCollections.LIFETIME
        );

        assertEquals(0, databasePlayer.getCompStats().pushedStats().getKills());
        assertEquals(0, databasePlayer.pushedStats().getKills());
        PushCacheVerifier.assertConsistent(databasePlayer);
    }

    @Test
    void nonZeroBaselineAndNegativeMultiplierStayIncremental() {
        DatabasePlayer databasePlayer = PushCacheTestFixtures.newWarmedPlayer();
        TestReflection.setField(databasePlayer.getPubStats().getCtfStats().getSpec(Specializations.PYROMANCER), "kills", 100);
        StatPushUp.rebuildSelectedCaches(databasePlayer);

        DatabaseGamePlayerCTF gamePlayer = PushCacheTestFixtures.ctfPlayer(GameStats.simple(5, 1));
        PushCacheIntegrationSupport.applyUpdate(
                databasePlayer,
                PushCacheTestFixtures.pubCtfGame(),
                GameMode.CAPTURE_THE_FLAG,
                gamePlayer,
                DatabaseGamePlayerResult.WON,
                1
        );
        assertEquals(105, databasePlayer.pushedStats().getKills());

        PushCacheIntegrationSupport.applyUpdate(
                databasePlayer,
                PushCacheTestFixtures.pubCtfGame(),
                GameMode.CAPTURE_THE_FLAG,
                gamePlayer,
                DatabaseGamePlayerResult.WON,
                -1
        );
        assertEquals(100, databasePlayer.pushedStats().getKills());
    }

    @Test
    void mixedProductionBranchesAccumulateAtRootOnlyOnce() {
        DatabasePlayer databasePlayer = PushCacheTestFixtures.newWarmedPlayer();

        PushCacheIntegrationSupport.applyUpdate(databasePlayer, PushCacheTestFixtures.pubCtfGame(), GameMode.CAPTURE_THE_FLAG,
                PushCacheTestFixtures.ctfPlayer(GameStats.simple(2, 0)), DatabaseGamePlayerResult.WON, 1);
        PushCacheIntegrationSupport.applyUpdate(databasePlayer, PushCacheTestFixtures.compTdmGame(), GameMode.TEAM_DEATHMATCH,
                PushCacheTestFixtures.tdmPlayer(GameStats.simple(3, 0)), DatabaseGamePlayerResult.LOST, 1);
        PushCacheIntegrationSupport.applyUpdate(databasePlayer, PushCacheTestFixtures.tournamentInterceptionGame(), GameMode.INTERCEPTION,
                PushCacheTestFixtures.interceptionPlayer(GameStats.simple(4, 0)), DatabaseGamePlayerResult.DRAW, 1);

        assertEquals(2, databasePlayer.getPubStats().pushedStats().getKills());
        assertEquals(3, databasePlayer.getCompStats().pushedStats().getKills());
        assertEquals(4, databasePlayer.getTournamentStats().pushedStats().getKills());
        assertEquals(9, databasePlayer.pushedStats().getKills());
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4})
    void waveDefenseUsesEveryPlayerCountBranch(int playerCount) {
        DatabasePlayer databasePlayer = PushCacheTestFixtures.newWarmedPvePlayer();
        DatabaseGamePlayerPvEWaveDefense gamePlayer = PushCacheTestFixtures.waveDefensePlayer(PvEStats.simple(4, 1));
        DatabaseGamePvEWaveDefense game = PushCacheTestFixtures.pveWaveDefenseGame(gamePlayer, DifficultyIndex.NORMAL, 3, 1500);
        for (int i = 1; i < playerCount; i++) {
            TestReflection.addToCollection(game, "players", PushCacheTestFixtures.waveDefensePlayer(PvEStats.simple(0, 0)));
        }

        PushCacheIntegrationSupport.applyUpdate(databasePlayer, game, GameMode.WAVE_DEFENSE, gamePlayer,
                DatabaseGamePlayerResult.WON, 1);

        assertEquals(4, databasePlayer.getPveStats().getWaveDefenseStats().getNormalStats()
                .getPlayerCountStats(playerCount).getKills());
        assertEquals(4, databasePlayer.getPveStats().getWaveDefenseStats().pushedStats().getKills());
    }

    @Test
    void emptyWaveDefenseGameDoesNotPushAncestors() {
        DatabasePlayer databasePlayer = PushCacheTestFixtures.newWarmedPvePlayer();
        DatabaseGamePlayerPvEWaveDefense gamePlayer = PushCacheTestFixtures.waveDefensePlayer(PvEStats.simple(4, 1));
        DatabaseGamePvEWaveDefense game = new DatabaseGamePvEWaveDefense();
        TestReflection.setField(game, "difficulty", DifficultyIndex.NORMAL);

        boolean updated = databasePlayer.getPveStats().getWaveDefenseStats().updateModeStats(
                databasePlayer, game, GameMode.WAVE_DEFENSE, gamePlayer, DatabaseGamePlayerResult.WON, 1, PlayersCollections.LIFETIME);

        assertFalse(updated);
        assertEquals(0, databasePlayer.getPveStats().getWaveDefenseStats().pushedStats().getKills());
        assertEquals(0, databasePlayer.getPveStats().getWaveDefenseStats().getNormalStats().pushedStats().getKills());
    }

    @Test
    void emptyEventGameDoesNotPushEventCache() {
        PushCacheTestFixtures.stubCurrentGameEvent(GameEvents.BOLTARO);
        DatabasePlayer databasePlayer = PushCacheTestFixtures.newWarmedPvePlayer();
        DatabaseGamePlayerPvEEventBoltaroBonanza gamePlayer = new DatabaseGamePlayerPvEEventBoltaroBonanza();
        PushCacheTestFixtures.configurePvEPlayer(gamePlayer, PvEStats.simple(4, 1));
        DatabaseGamePvEEventBoltaroBonanza game = new DatabaseGamePvEEventBoltaroBonanza();

        boolean updated = databasePlayer.getPveStats().getEventStats().updateModeStats(
                databasePlayer, game, GameMode.EVENT_WAVE_DEFENSE, gamePlayer,
                DatabaseGamePlayerResult.WON, 1, PlayersCollections.LIFETIME);

        assertFalse(updated);
        assertEquals(0, databasePlayer.getPveStats().getEventStats().pushedStats().getKills());
    }
}
