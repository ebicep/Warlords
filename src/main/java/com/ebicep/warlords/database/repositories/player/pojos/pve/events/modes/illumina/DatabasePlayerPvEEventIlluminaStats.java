package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina;


import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.illumina.DatabaseGamePlayerPvEEventIllumina;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.illumina.DatabaseGamePvEEventIllumina;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEEventIlluminaStats implements MultiPvEEventIlluminaStats<
        PvEEventIlluminaStatsWarlordsClasses<
                DatabaseGamePvEEventIllumina,
                DatabaseGamePlayerPvEEventIllumina,
                PvEEventIlluminaStats<DatabaseGamePvEEventIllumina, DatabaseGamePlayerPvEEventIllumina>,
                PvEEventIlluminaStatsWarlordsSpecs<DatabaseGamePvEEventIllumina, DatabaseGamePlayerPvEEventIllumina, PvEEventIlluminaStats<DatabaseGamePvEEventIllumina, DatabaseGamePlayerPvEEventIllumina>>>,
        DatabaseGamePvEEventIllumina,
        DatabaseGamePlayerPvEEventIllumina,
        PvEEventIlluminaStats<DatabaseGamePvEEventIllumina, DatabaseGamePlayerPvEEventIllumina>,
        PvEEventIlluminaStatsWarlordsSpecs<DatabaseGamePvEEventIllumina, DatabaseGamePlayerPvEEventIllumina, PvEEventIlluminaStats<DatabaseGamePvEEventIllumina, DatabaseGamePlayerPvEEventIllumina>>> {


    @Field("events")
    private Map<Long, DatabasePlayerPvEEventIlluminaDifficultyStats> eventStats = new LinkedHashMap<>();

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEventIllumina databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventIllumina gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        getEvent(DatabaseGameEvent.currentGameEvent.getStartDateSecond()).updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
    }

    public Map<Long, DatabasePlayerPvEEventIlluminaDifficultyStats> getEventStats() {
        return eventStats;
    }

    public DatabasePlayerPvEEventIlluminaDifficultyStats getEvent(long epochSecond) {
        return eventStats.computeIfAbsent(epochSecond, k -> new DatabasePlayerPvEEventIlluminaDifficultyStats());
    }

    @Override
    public Collection<? extends PvEEventIlluminaStatsWarlordsClasses<DatabaseGamePvEEventIllumina, DatabaseGamePlayerPvEEventIllumina, PvEEventIlluminaStats<DatabaseGamePvEEventIllumina, DatabaseGamePlayerPvEEventIllumina>, PvEEventIlluminaStatsWarlordsSpecs<DatabaseGamePvEEventIllumina, DatabaseGamePlayerPvEEventIllumina, PvEEventIlluminaStats<DatabaseGamePvEEventIllumina, DatabaseGamePlayerPvEEventIllumina>>>> getStats() {
        return eventStats.values()
                         .stream()
                         .flatMap(stats -> stats.getStats().stream())
                         .toList();
    }
}
