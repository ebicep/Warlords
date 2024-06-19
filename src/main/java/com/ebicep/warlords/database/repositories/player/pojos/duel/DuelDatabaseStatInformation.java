package com.ebicep.warlords.database.repositories.player.pojos.duel;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.duel.DatabaseGameDuel;
import com.ebicep.warlords.database.repositories.games.pojos.duel.DatabaseGamePlayerDuel;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

public class DuelDatabaseStatInformation extends AbstractDatabaseStatInformation<DatabaseGameDuel, DatabaseGamePlayerDuel> implements DuelStats {

    @Field("total_time_played")
    private long totalTimePlayed = 0;

    public DuelDatabaseStatInformation() {
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGameDuel databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerDuel gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        super.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
        this.totalTimePlayed += (long) (900 - databaseGame.getTimeLeft()) * multiplier;
    }

    @Override
    public long getTotalTimePlayed() {
        return totalTimePlayed;
    }
}
