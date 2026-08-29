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

class PushCacheCompIntegrationTest {

    record CompCase(String label, GameMode gameMode, DatabaseGameBase<?> game) {
        DatabaseGamePlayerBase player(GameStats stats) {
            return switch (label) {
                case "ctf" -> PushCacheTestFixtures.ctfPlayer(stats);
                case "tdm" -> PushCacheTestFixtures.tdmPlayer(stats);
                case "interception" -> PushCacheTestFixtures.interceptionPlayer(stats);
                case "siege" -> PushCacheTestFixtures.siegePlayer(stats);
                default -> throw new IllegalStateException(label);
            };
        }
    }

    static Stream<CompCase> compCases() {
        return Stream.of(
                new CompCase("ctf", GameMode.CAPTURE_THE_FLAG, PushCacheTestFixtures.compCtfGame()),
                new CompCase("tdm", GameMode.TEAM_DEATHMATCH, PushCacheTestFixtures.compTdmGame()),
                new CompCase("interception", GameMode.INTERCEPTION, PushCacheTestFixtures.compInterceptionGame()),
                new CompCase("siege", GameMode.SIEGE, PushCacheTestFixtures.compSiegeGame())
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("compCases")
    void singleUpdateMatchesTreeWalk(CompCase compCase) {
        DatabasePlayer databasePlayer = PushCacheTestFixtures.newWarmedPlayer();

        PushCacheIntegrationSupport.applyUpdate(
                databasePlayer,
                compCase.game(),
                compCase.gameMode(),
                compCase.player(GameStats.simple(8, 3)),
                DatabaseGamePlayerResult.WON,
                1
        );

        assertEquals(8, databasePlayer.getKills());
        assertEquals(8, databasePlayer.getCompStats().getKills());
    }

    @ParameterizedTest(name = "{0} sequential")
    @MethodSource("compCases")
    void sequentialUpdatesAccumulate(CompCase compCase) {
        DatabasePlayer databasePlayer = PushCacheTestFixtures.newWarmedPlayer();

        PushCacheIntegrationSupport.applyUpdate(
                databasePlayer,
                compCase.game(),
                compCase.gameMode(),
                compCase.player(GameStats.simple(2, 1)),
                DatabaseGamePlayerResult.WON,
                1
        );
        PushCacheIntegrationSupport.applyUpdate(
                databasePlayer,
                compCase.game(),
                compCase.gameMode(),
                compCase.player(GameStats.simple(5, 2)),
                DatabaseGamePlayerResult.LOST,
                1
        );

        assertEquals(7, databasePlayer.getCompStats().getKills());
        assertEquals(7, databasePlayer.getKills());
    }

    @ParameterizedTest(name = "{0} multiplier")
    @MethodSource("compCases")
    void multiplierDoublesDeltas(CompCase compCase) {
        DatabasePlayer databasePlayer = PushCacheTestFixtures.newWarmedPlayer();

        PushCacheIntegrationSupport.applyUpdate(
                databasePlayer,
                compCase.game(),
                compCase.gameMode(),
                compCase.player(GameStats.simple(6, 1)),
                DatabaseGamePlayerResult.WON,
                2
        );

        assertEquals(12, databasePlayer.getCompStats().getKills());
        assertEquals(12, databasePlayer.getKills());
    }
}
