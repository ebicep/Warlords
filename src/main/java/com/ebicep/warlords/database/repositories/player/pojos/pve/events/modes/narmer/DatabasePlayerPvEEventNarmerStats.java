package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.DatabaseGamePlayerPvEEventNarmer;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.DatabaseGamePvEEventNarmer;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEEventNarmerStats implements MultiPvEEventNarmerStats<
        PvEEventNarmerStatsWarlordsClasses<
                DatabaseGamePvEEventNarmer,
                DatabaseGamePlayerPvEEventNarmer,
                PvEEventNarmerStats<DatabaseGamePvEEventNarmer, DatabaseGamePlayerPvEEventNarmer>,
                PvEEventNarmerStatsWarlordsSpecs<DatabaseGamePvEEventNarmer, DatabaseGamePlayerPvEEventNarmer, PvEEventNarmerStats<DatabaseGamePvEEventNarmer, DatabaseGamePlayerPvEEventNarmer>>>,
        DatabaseGamePvEEventNarmer,
        DatabaseGamePlayerPvEEventNarmer,
        PvEEventNarmerStats<DatabaseGamePvEEventNarmer, DatabaseGamePlayerPvEEventNarmer>,
        PvEEventNarmerStatsWarlordsSpecs<DatabaseGamePvEEventNarmer, DatabaseGamePlayerPvEEventNarmer, PvEEventNarmerStats<DatabaseGamePvEEventNarmer, DatabaseGamePlayerPvEEventNarmer>>> {


    @Field("events")
    private Map<Long, DatabasePlayerPvEEventNarmerDifficultyStats> eventStats = new LinkedHashMap<>();

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEventNarmer databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventNarmer gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        getEvent(DatabaseGameEvent.currentGameEvent.getStartDateSecond()).updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
    }

    public Map<Long, DatabasePlayerPvEEventNarmerDifficultyStats> getEventStats() {
        return eventStats;
    }

    public DatabasePlayerPvEEventNarmerDifficultyStats getEvent(long epochSecond) {
        return eventStats.computeIfAbsent(epochSecond, k -> new DatabasePlayerPvEEventNarmerDifficultyStats());
    }

    @Override
    public Collection<? extends PvEEventNarmerStatsWarlordsClasses<DatabaseGamePvEEventNarmer, DatabaseGamePlayerPvEEventNarmer, PvEEventNarmerStats<DatabaseGamePvEEventNarmer, DatabaseGamePlayerPvEEventNarmer>, PvEEventNarmerStatsWarlordsSpecs<DatabaseGamePvEEventNarmer, DatabaseGamePlayerPvEEventNarmer, PvEEventNarmerStats<DatabaseGamePvEEventNarmer, DatabaseGamePlayerPvEEventNarmer>>>> getStats() {
        return eventStats.values()
                         .stream()
                         .flatMap(stats -> stats.getStats().stream())
                         .toList();
    }
}
