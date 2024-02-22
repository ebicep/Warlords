package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.DatabaseGamePlayerPvEEventBoltaro;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.DatabaseGamePvEEventBoltaro;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEEventBoltaroStats implements MultiPvEEventBoltaroStats<
        PvEEventBoltaroStatsWarlordsClasses<
                DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>,
                DatabaseGamePlayerPvEEventBoltaro,
                PvEEventBoltaroStats<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro>,
                PvEEventBoltaroStatsWarlordsSpecs<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro, PvEEventBoltaroStats<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro>>>,
        DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>,
        DatabaseGamePlayerPvEEventBoltaro,
        PvEEventBoltaroStats<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro>,
        PvEEventBoltaroStatsWarlordsSpecs<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro, PvEEventBoltaroStats<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro>>> {

    @Field("events")
    private Map<Long, DatabasePlayerPvEEventBoltaroDifficultyStats> eventStats = new LinkedHashMap<>();


    public DatabasePlayerPvEEventBoltaroDifficultyStats getEvent(long epochSecond) {
        return eventStats.computeIfAbsent(epochSecond, k -> new DatabasePlayerPvEEventBoltaroDifficultyStats());
    }

    public Map<Long, DatabasePlayerPvEEventBoltaroDifficultyStats> getEventStats() {
        return eventStats;
    }

    @Override
    public Collection<? extends PvEEventBoltaroStatsWarlordsClasses<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro, PvEEventBoltaroStats<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro>, PvEEventBoltaroStatsWarlordsSpecs<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro, PvEEventBoltaroStats<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro>>>> getStats() {
        return eventStats.values()
                         .stream()
                         .flatMap(stats -> stats.getStats().stream())
                         .toList();
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEventBoltaro databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventBoltaro gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        getEvent(DatabaseGameEvent.currentGameEvent.getStartDateSecond()).updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
    }

}
