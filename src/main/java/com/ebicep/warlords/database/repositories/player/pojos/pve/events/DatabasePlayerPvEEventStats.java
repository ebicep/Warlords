package com.ebicep.warlords.database.repositories.player.pojos.pve.events;

import com.ebicep.warlords.database.repositories.events.pojos.GameEvents;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.DatabasePlayerPvEEventBoltaroDifficultyStats;
import com.ebicep.warlords.game.GameMode;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEEventStats extends DatabasePlayerPvEEventDifficultyStats {

    private Map<GameEvents, Map<Integer, DatabasePlayerPvEEventBoltaroDifficultyStats>> events = new LinkedHashMap<>() {{
        put(GameEvents.BOLTARO, new LinkedHashMap<>() {{
            put(Instant.now().getNano(), new DatabasePlayerPvEEventBoltaroDifficultyStats());
        }});
    }};

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

        //TODO
    }

//    public Map<Instant, DatabasePlayerEventBoltaroStats> getBoltaroStats() {
//        return boltaroStats;
//    }

}

/*
event_stats
> total shit
  > event_1
    > date_1
    > date_2
  > event_2
 */