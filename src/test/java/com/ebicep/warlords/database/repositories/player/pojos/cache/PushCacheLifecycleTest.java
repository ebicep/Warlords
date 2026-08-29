package com.ebicep.warlords.database.repositories.player.pojos.cache;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.player.pojos.cache.support.PushCacheIntegrationSupport;
import com.ebicep.warlords.database.repositories.player.pojos.cache.support.PushCacheTestFixtures;
import com.ebicep.warlords.database.repositories.player.pojos.cache.support.PushCacheTestFixtures.GameStats;
import com.ebicep.warlords.database.repositories.player.pojos.cache.support.PushCacheVerifier;
import com.ebicep.warlords.database.repositories.player.pojos.cache.support.TestReflection;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PushCacheLifecycleTest {

    @Test
    void rebuildAfterManualLeafMutationResyncsAggregates() {
        DatabasePlayer databasePlayer = PushCacheTestFixtures.newWarmedPlayer();

        PushCacheIntegrationSupport.applyUpdate(
                databasePlayer,
                PushCacheTestFixtures.pubCtfGame(),
                GameMode.CAPTURE_THE_FLAG,
                PushCacheTestFixtures.ctfPlayer(GameStats.simple(5, 1)),
                DatabaseGamePlayerResult.WON,
                1
        );

        TestReflection.setField(databasePlayer.getPubStats().getCtfStats().getPyromancer(), "kills", 99);
        StatPushUp.rebuildSelectedCaches(databasePlayer);
        PushCacheVerifier.assertConsistent(databasePlayer);

        assertEquals(99, databasePlayer.getPubStats().getCtfStats().getKills());
        assertEquals(99, databasePlayer.getKills());
    }

    @Test
    void additionalUpdateAfterRebuildStaysConsistent() {
        DatabasePlayer databasePlayer = PushCacheTestFixtures.newWarmedPlayer();

        PushCacheIntegrationSupport.applyUpdate(
                databasePlayer,
                PushCacheTestFixtures.pubTdmGame(),
                GameMode.TEAM_DEATHMATCH,
                PushCacheTestFixtures.tdmPlayer(GameStats.simple(2, 0)),
                DatabaseGamePlayerResult.WON,
                1
        );

        StatPushUp.rebuildSelectedCaches(databasePlayer);

        PushCacheIntegrationSupport.applyUpdate(
                databasePlayer,
                PushCacheTestFixtures.pubTdmGame(),
                GameMode.TEAM_DEATHMATCH,
                PushCacheTestFixtures.tdmPlayer(GameStats.simple(3, 1)),
                DatabaseGamePlayerResult.LOST,
                1
        );

        assertEquals(5, databasePlayer.getKills());
        assertEquals(5, databasePlayer.getPubStats().getTdmStats().getKills());
    }
}
