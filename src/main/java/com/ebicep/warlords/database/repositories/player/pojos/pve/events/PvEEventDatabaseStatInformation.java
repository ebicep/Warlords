package com.ebicep.warlords.database.repositories.player.pojos.pve.events;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePlayerPvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

public class PvEEventDatabaseStatInformation<
        DatabaseGameT extends DatabaseGamePvEEvent<DatabaseGamePlayerT>,
        DatabaseGamePlayerT extends DatabaseGamePlayerPvEEvent>
        extends PvEDatabaseStatInformation<DatabaseGameT, DatabaseGamePlayerT> implements PvEEventStats<DatabaseGameT, DatabaseGamePlayerT> {

    @Field("event_points_cum")
    private long eventPointsCumulative;
    @Field("highest_event_points_game")
    private long highestEventPointsGame;

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGameT databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerT gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        super.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
        this.eventPointsCumulative += Math.min(gamePlayer.getPoints(), databaseGame.getPointLimit()) * multiplier;
        if (multiplier > 0) {
            this.highestEventPointsGame = Math.max(this.highestEventPointsGame, gamePlayer.getPoints());
        } else if (highestEventPointsGame == gamePlayer.getPoints()) {
            this.highestEventPointsGame = 0;
        }
    }

    public long getEventPointsCumulative() {
        return eventPointsCumulative;
    }

    public long getHighestEventPointsGame() {
        return highestEventPointsGame;
    }
}
