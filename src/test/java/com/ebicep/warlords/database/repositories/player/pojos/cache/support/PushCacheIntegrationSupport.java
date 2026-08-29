package com.ebicep.warlords.database.repositories.player.pojos.cache.support;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.Team;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class PushCacheIntegrationSupport {

    private PushCacheIntegrationSupport() {
    }

    public static void applyUpdate(
            DatabasePlayer databasePlayer,
            DatabaseGameBase<?> game,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier
    ) {
        int rootKillsBefore = databasePlayer.pushedStats().getKills();
        if (!GameMode.isPvE(gameMode)) {
            configureGameResult(game, gameMode, gamePlayer, result);
        }
        DatabaseGamePlayerResult effectiveResult = GameMode.isPvE(gameMode)
                ? game.getPlayerGameResult(gamePlayer)
                : result;
        databasePlayer.updateStats(
                databasePlayer,
                game,
                gameMode,
                gamePlayer,
                effectiveResult,
                multiplier,
                PlayersCollections.DAILY
        );
        assertEquals(rootKillsBefore + gamePlayer.getTotalKills() * multiplier, databasePlayer.pushedStats().getKills(),
                "root pushed cache must receive the delta before any rebuild");
        PushCacheVerifier.assertConsistent(databasePlayer);
    }

    private static void configureGameResult(
            DatabaseGameBase<?> game,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result
    ) {
        TestReflection.setField(game, "gameMode", gameMode);
        Team winner = switch (result) {
            case WON, NONE -> Team.BLUE;
            case LOST -> Team.RED;
            case DRAW -> null;
        };
        TestReflection.setField(game, "winner", winner);
        TestReflection.setField(game, "players", result == DatabaseGamePlayerResult.NONE
                ? Map.of()
                : Map.of(Team.BLUE, List.of(gamePlayer)));
    }
}
