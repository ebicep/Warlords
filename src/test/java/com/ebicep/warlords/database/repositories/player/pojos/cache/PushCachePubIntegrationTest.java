package com.ebicep.warlords.database.repositories.player.pojos.cache;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.player.pojos.cache.support.PushCacheIntegrationSupport;
import com.ebicep.warlords.database.repositories.player.pojos.cache.support.PushCacheTestFixtures;
import com.ebicep.warlords.database.repositories.player.pojos.cache.support.PushCacheTestFixtures.GameStats;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PushCachePubIntegrationTest {

    record PubCase(
            String label,
            GameMode gameMode,
            DatabaseGameBase<?> game
    ) {
        DatabaseGamePlayerBase player(GameStats stats) {
            return switch (label) {
                case "ctf" -> PushCacheTestFixtures.ctfPlayer(stats);
                case "tdm" -> PushCacheTestFixtures.tdmPlayer(stats);
                case "interception" -> PushCacheTestFixtures.interceptionPlayer(stats);
                case "duel" -> PushCacheTestFixtures.duelPlayer(stats);
                case "siege" -> PushCacheTestFixtures.siegePlayer(stats);
                default -> throw new IllegalStateException(label);
            };
        }
    }

    static Stream<PubCase> pubCases() {
        return Stream.of(
                new PubCase("ctf", GameMode.CAPTURE_THE_FLAG, PushCacheTestFixtures.pubCtfGame()),
                new PubCase("tdm", GameMode.TEAM_DEATHMATCH, PushCacheTestFixtures.pubTdmGame()),
                new PubCase("interception", GameMode.INTERCEPTION, PushCacheTestFixtures.pubInterceptionGame()),
                new PubCase("duel", GameMode.DUEL, PushCacheTestFixtures.pubDuelGame()),
                new PubCase("siege", GameMode.SIEGE, PushCacheTestFixtures.pubSiegeGame())
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("pubCases")
    void singleUpdateMatchesTreeWalk(PubCase pubCase) {
        DatabasePlayer databasePlayer = PushCacheTestFixtures.newWarmedPlayer();
        GameStats stats = GameStats.simple(7, 2);

        PushCacheIntegrationSupport.applyUpdate(
                databasePlayer,
                pubCase.game(),
                pubCase.gameMode(),
                pubCase.player(stats),
                DatabaseGamePlayerResult.WON,
                1
        );

        assertEquals(7, databasePlayer.getKills());
        assertEquals(7, databasePlayer.getPubStats().getKills());
    }

    @ParameterizedTest(name = "{0} sequential")
    @MethodSource("pubCases")
    void sequentialUpdatesAccumulate(PubCase pubCase) {
        DatabasePlayer databasePlayer = PushCacheTestFixtures.newWarmedPlayer();

        PushCacheIntegrationSupport.applyUpdate(
                databasePlayer,
                pubCase.game(),
                pubCase.gameMode(),
                pubCase.player(GameStats.simple(3, 1)),
                DatabaseGamePlayerResult.WON,
                1
        );
        PushCacheIntegrationSupport.applyUpdate(
                databasePlayer,
                pubCase.game(),
                pubCase.gameMode(),
                pubCase.player(GameStats.simple(4, 2)),
                DatabaseGamePlayerResult.LOST,
                1
        );

        assertEquals(7, databasePlayer.getKills());
        assertEquals(2, databasePlayer.getPlays());
    }

    @ParameterizedTest(name = "{0} multiplier")
    @MethodSource("pubCases")
    void multiplierDoublesDeltas(PubCase pubCase) {
        DatabasePlayer databasePlayer = PushCacheTestFixtures.newWarmedPlayer();

        PushCacheIntegrationSupport.applyUpdate(
                databasePlayer,
                pubCase.game(),
                pubCase.gameMode(),
                pubCase.player(GameStats.simple(5, 1)),
                DatabaseGamePlayerResult.WON,
                2
        );

        assertEquals(10, databasePlayer.getKills());
        assertEquals(2, databasePlayer.getWins());
    }
}
