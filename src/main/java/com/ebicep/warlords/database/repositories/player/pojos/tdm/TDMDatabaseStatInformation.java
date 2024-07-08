package com.ebicep.warlords.database.repositories.player.pojos.tdm;


import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.tdm.DatabaseGamePlayerTDM;
import com.ebicep.warlords.database.repositories.games.pojos.tdm.DatabaseGameTDM;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

public class TDMDatabaseStatInformation extends AbstractDatabaseStatInformation<DatabaseGameTDM, DatabaseGamePlayerTDM> implements TDMStats {

    @Field("total_time_played")
    private long totalTimePlayed = 0;

    public TDMDatabaseStatInformation() {
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGameTDM databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerTDM gamePlayer,
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
