package com.ebicep.warlords.database.repositories.player.pojos.pve.events;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.DatabasePlayerPvEEventBoltaroDifficultyStats;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEEventStats extends DatabasePlayerPvEEventDifficultyStats {

    @Field("boltaro")
    private Map<Long, DatabasePlayerPvEEventBoltaroDifficultyStats> boltaroStats = new LinkedHashMap<>();

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
            switch (currentGameEvent.getEvent()) {
                case BOLTARO:
                    boltaroStats.computeIfAbsent(currentGameEvent.getStartDate().getEpochSecond(), k -> new DatabasePlayerPvEEventBoltaroDifficultyStats())
                                .updateStats(databaseGame, gamePlayer, multiplier, playersCollection);
                    break;
            }
        }
    }

    public Map<Long, DatabasePlayerPvEEventBoltaroDifficultyStats> getBoltaroStats() {
        return boltaroStats;
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