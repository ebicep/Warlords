package com.ebicep.warlords.database.repositories.player.pojos.pve.events;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.DatabasePlayerPvEEventBoltaroDifficultyStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.DatabasePlayerPvEEventBoltaroStats;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

public class DatabasePlayerPvEEventStats extends DatabasePlayerPvEEventDifficultyStats {

    @Field("boltaro")
    private DatabasePlayerPvEEventBoltaroStats boltaroStats = new DatabasePlayerPvEEventBoltaroStats();

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

        DatabaseGameEvent currentGameEvent = DatabaseGameEvent.currentGameEvent;
        if (currentGameEvent != null) {
            currentGameEvent.getEvent().updateStatsFuntion.apply(this).updateStats(databaseGame, gamePlayer, multiplier, playersCollection);
        }
    }

    public DatabasePlayerPvEEventBoltaroStats getBoltaroStats() {
        return boltaroStats;
    }

    public Map<Long, DatabasePlayerPvEEventBoltaroDifficultyStats> getBoltaroEventStats() {
        return boltaroStats.getEventStats();
    }
}

/*
event_stats
> total shit
  > event_1
    > date_1
    > date_2
  > event_2
 */