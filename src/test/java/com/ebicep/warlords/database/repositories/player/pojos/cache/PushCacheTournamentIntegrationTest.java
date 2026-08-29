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

class PushCacheTournamentIntegrationTest {

    record TournamentCase(String label, GameMode gameMode, DatabaseGameBase<?> game) {
        DatabaseGamePlayerBase player(GameStats stats) {
            return switch (label) {
                case "ctf" -> PushCacheTestFixtures.ctfPlayer(stats);
                case "tdm" -> PushCacheTestFixtures.tdmPlayer(stats);
                case "interception" -> PushCacheTestFixtures.interceptionPlayer(stats);
                case "duel" -> PushCacheTestFixtures.duelPlayer(stats);
                default -> throw new IllegalStateException(label);
            };
        }
    }

    static Stream<TournamentCase> tournamentCases() {
        return Stream.of(
                new TournamentCase("ctf", GameMode.CAPTURE_THE_FLAG, PushCacheTestFixtures.tournamentCtfGame()),
                new TournamentCase("tdm", GameMode.TEAM_DEATHMATCH, PushCacheTestFixtures.tournamentTdmGame()),
                new TournamentCase("interception", GameMode.INTERCEPTION, PushCacheTestFixtures.tournamentInterceptionGame()),
                new TournamentCase("duel", GameMode.DUEL, PushCacheTestFixtures.tournamentDuelGame())
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("tournamentCases")
    void singleUpdateMatchesTreeWalk(TournamentCase tournamentCase) {
        DatabasePlayer databasePlayer = PushCacheTestFixtures.newWarmedPlayer();

        PushCacheIntegrationSupport.applyUpdate(
                databasePlayer,
                tournamentCase.game(),
                tournamentCase.gameMode(),
                tournamentCase.player(GameStats.simple(9, 2)),
                DatabaseGamePlayerResult.WON,
                1
        );

        assertEquals(9, databasePlayer.getKills());
        assertEquals(9, databasePlayer.getTournamentStats().getCurrentTournamentStats().getKills());
        assertEquals(9, databasePlayer.getTournamentStats().getKills());
    }

    @ParameterizedTest(name = "{0} sequential")
    @MethodSource("tournamentCases")
    void sequentialUpdatesAccumulate(TournamentCase tournamentCase) {
        DatabasePlayer databasePlayer = PushCacheTestFixtures.newWarmedPlayer();

        PushCacheIntegrationSupport.applyUpdate(
                databasePlayer,
                tournamentCase.game(),
                tournamentCase.gameMode(),
                tournamentCase.player(GameStats.simple(1, 0)),
                DatabaseGamePlayerResult.WON,
                1
        );
        PushCacheIntegrationSupport.applyUpdate(
                databasePlayer,
                tournamentCase.game(),
                tournamentCase.gameMode(),
                tournamentCase.player(GameStats.simple(3, 1)),
                DatabaseGamePlayerResult.LOST,
                1
        );

        assertEquals(4, databasePlayer.getTournamentStats().getCurrentTournamentStats().getKills());
        assertEquals(4, databasePlayer.getKills());
    }

    @ParameterizedTest(name = "{0} multiplier")
    @MethodSource("tournamentCases")
    void multiplierDoublesDeltas(TournamentCase tournamentCase) {
        DatabasePlayer databasePlayer = PushCacheTestFixtures.newWarmedPlayer();

        PushCacheIntegrationSupport.applyUpdate(
                databasePlayer,
                tournamentCase.game(),
                tournamentCase.gameMode(),
                tournamentCase.player(GameStats.simple(4, 1)),
                DatabaseGamePlayerResult.WON,
                2
        );

        assertEquals(8, databasePlayer.getTournamentStats().getCurrentTournamentStats().getKills());
        assertEquals(8, databasePlayer.getKills());
    }
}
