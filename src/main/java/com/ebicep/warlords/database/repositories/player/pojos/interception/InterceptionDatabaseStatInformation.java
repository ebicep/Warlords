package com.ebicep.warlords.database.repositories.player.pojos.interception;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.interception.DatabaseGameInterception;
import com.ebicep.warlords.database.repositories.games.pojos.interception.DatabaseGamePlayerInterception;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

public class InterceptionDatabaseStatInformation extends AbstractDatabaseStatInformation<DatabaseGameInterception, DatabaseGamePlayerInterception> implements InterceptionStats {

    @Field("points_captured")
    private int pointsCaptured;
    @Field("points_defended")
    private int pointsDefended;
    @Field("total_time_played")
    private long totalTimePlayed = 0;

    public InterceptionDatabaseStatInformation() {
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGameInterception databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerInterception gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        super.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
        this.pointsCaptured += gamePlayer.getPointsCaptured() * multiplier;
        this.pointsDefended += gamePlayer.getPointsDefended() * multiplier;
        this.totalTimePlayed += (long) (900 - databaseGame.getTimeLeft()) * multiplier;
    }


    @Override
    public int getPointsCaptured() {
        return pointsCaptured;
    }

    @Override
    public int getPointsDefended() {
        return pointsDefended;
    }

    @Override
    public long getTotalTimePlayed() {
        return totalTimePlayed;
    }
}
