package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEEventIlluminaStats extends DatabasePlayerPvEEventIlluminaDifficultyStats {

    @Field("events")
    private Map<Long, DatabasePlayerPvEEventIlluminaDifficultyStats> eventStats = new LinkedHashMap<>();

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

        getEvent(DatabaseGameEvent.currentGameEvent.getStartDateSecond()).updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
    }

    public Map<Long, DatabasePlayerPvEEventIlluminaDifficultyStats> getEventStats() {
        return eventStats;
    }

    public DatabasePlayerPvEEventIlluminaDifficultyStats getEvent(long epochSecond) {
        return eventStats.computeIfAbsent(epochSecond, k -> new DatabasePlayerPvEEventIlluminaDifficultyStats());
    }

}
