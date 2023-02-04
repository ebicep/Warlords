package com.ebicep.warlords.database.repositories.player.pojos.pve.events;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvE;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePlayerPvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza.DatabaseGamePvEEventBoltaroBonanza;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltaroslair.DatabaseGamePvEEventBoltaroLair;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

public class PvEEventDatabaseStatInformation extends PvEDatabaseStatInformation {

    @Field("event_points_cum")
    private long eventPointsCumulative;
    @Field("highest_event_points_game")
    private long highestEventPointsGame;

    @Override
    public void updateCustomStats(
            DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        super.updateCustomStats(databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);

        assert databaseGame instanceof DatabaseGamePvEEvent;
        assert gamePlayer instanceof DatabaseGamePlayerPvE;

        DatabaseGamePvEEvent databaseGamePvEEvent = (DatabaseGamePvEEvent) databaseGame;
        DatabaseGamePlayerPvEEvent databaseGamePlayerPvEEvent = (DatabaseGamePlayerPvEEvent) gamePlayer;

        if (databaseGame instanceof DatabaseGamePvEEventBoltaroLair) {
            this.eventPointsCumulative += Math.min(databaseGamePlayerPvEEvent.getPoints(), 50_000) * multiplier;
        } else if (databaseGame instanceof DatabaseGamePvEEventBoltaroBonanza) {
            this.eventPointsCumulative += Math.min(databaseGamePlayerPvEEvent.getPoints(), 15_000) * multiplier;
        } else {
            this.eventPointsCumulative += Math.min(databaseGamePlayerPvEEvent.getPoints(), 915_000) * multiplier;
        }
        if (multiplier > 0) {
            this.highestEventPointsGame = Math.max(this.highestEventPointsGame, databaseGamePlayerPvEEvent.getPoints());
        }
    }

    public long getEventPointsCumulative() {
        return eventPointsCumulative;
    }

    public long getHighestEventPointsGame() {
        return highestEventPointsGame;
    }
}
