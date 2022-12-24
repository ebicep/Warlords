package com.ebicep.warlords.database.repositories.player.pojos.pve.events;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvE;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePlayerPvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

public class PvEEventDatabaseStatInformation extends PvEDatabaseStatInformation {

    @Field("event_points_cum")
    private long eventPointsCumulative;
    @Field("event_points")
    private long eventPoints;

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

        this.eventPointsCumulative += databaseGamePlayerPvEEvent.getPoints() * multiplier;
        this.eventPoints += databaseGamePlayerPvEEvent.getPoints() * multiplier;
    }
}
