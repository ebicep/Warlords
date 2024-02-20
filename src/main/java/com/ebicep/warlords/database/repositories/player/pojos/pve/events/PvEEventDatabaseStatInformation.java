package com.ebicep.warlords.database.repositories.player.pojos.pve.events;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePlayerPvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

public class PvEEventDatabaseStatInformation extends PvEDatabaseStatInformation implements PvEEventStats {

    @Field("event_points_cum")
    private long eventPointsCumulative;
    @Field("highest_event_points_game")
    private long highestEventPointsGame;

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer, DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        super.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);

        assert databaseGame instanceof DatabaseGamePvEEvent;
        assert gamePlayer instanceof DatabaseGamePlayerPvEWaveDefense;

        DatabaseGamePvEEvent databaseGamePvEEvent = (DatabaseGamePvEEvent) databaseGame;
        DatabaseGamePlayerPvEEvent databaseGamePlayerPvEEvent = (DatabaseGamePlayerPvEEvent) gamePlayer;

        this.eventPointsCumulative += Math.min(databaseGamePlayerPvEEvent.getPoints(), databaseGamePvEEvent.getPointLimit()) * multiplier;
        if (multiplier > 0) {
            this.highestEventPointsGame = Math.max(this.highestEventPointsGame, databaseGamePlayerPvEEvent.getPoints());
        } else if (highestEventPointsGame == databaseGamePlayerPvEEvent.getPoints()) {
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
