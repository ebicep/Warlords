package com.ebicep.warlords.database.repositories.player.pojos.interception;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.interception.DatabaseGameInterception;
import com.ebicep.warlords.database.repositories.games.pojos.interception.DatabaseGamePlayersInterception;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

public class InterceptionDatabaseStatInformation extends AbstractDatabaseStatInformation {

    @Field("points_captured")
    private int pointsCaptured;
    @Field("points_defended")
    private int pointsDefended;
    @Field("total_time_played")
    private long totalTimePlayed = 0;

    public InterceptionDatabaseStatInformation() {
    }

    @Override
    public void updateCustomStats(
            DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        assert databaseGame instanceof DatabaseGameInterception;
        assert gamePlayer instanceof DatabaseGamePlayersInterception.DatabaseGamePlayerInterception;

        this.pointsCaptured += ((DatabaseGamePlayersInterception.DatabaseGamePlayerInterception) gamePlayer).getPointsCaptured() * multiplier;
        this.pointsDefended += ((DatabaseGamePlayersInterception.DatabaseGamePlayerInterception) gamePlayer).getPointsDefended() * multiplier;
        this.totalTimePlayed += (long) (900 - ((DatabaseGameInterception) databaseGame).getTimeLeft()) * multiplier;
    }

    public int getPointsCaptured() {
        return pointsCaptured;
    }

    public int getPointsDefended() {
        return pointsDefended;
    }

    public long getTotalTimePlayed() {
        return totalTimePlayed;
    }
}
