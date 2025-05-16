package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.DatabaseGamePlayerPvEEventNarmer;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.DatabaseGamePvEEventNarmer;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.TracksAbilityStats;
import com.ebicep.warlords.database.repositories.player.pojos.TracksMultiAbilityStats;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEEventNarmerStats implements MultiPvEEventNarmerStats<
        PvEEventNarmerStatsWarlordsClasses<
                DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>,
                DatabaseGamePlayerPvEEventNarmer,
                PvEEventNarmerStats<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer>,
                PvEEventNarmerStatsWarlordsSpecs<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer, PvEEventNarmerStats<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer>>>,
        DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>,
        DatabaseGamePlayerPvEEventNarmer,
        PvEEventNarmerStats<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer>,
        PvEEventNarmerStatsWarlordsSpecs<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer, PvEEventNarmerStats<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer>>>,
        TracksMultiAbilityStats {


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

    public DatabasePlayerPvEEventNarmerDifficultyStats getEvent(long epochSecond) {
        return eventStats.computeIfAbsent(epochSecond, k -> new DatabasePlayerPvEEventNarmerDifficultyStats());
    }

    public Map<Long, DatabasePlayerPvEEventNarmerDifficultyStats> getEventStats() {
        return eventStats;
    }

    @Override
    public Collection<PvEEventNarmerStatsWarlordsClasses<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer, PvEEventNarmerStats<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer>, PvEEventNarmerStatsWarlordsSpecs<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer, PvEEventNarmerStats<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer>>>> getStats() {
        return eventStats.values()
                         .stream()
                         .flatMap(stats -> stats.getStats().stream())
                         .toList();
    }

    @Override
    public Collection<TracksAbilityStats> getAllAbilityStats() {
        return eventStats.values().stream().flatMap(stats -> stats.getAllAbilityStats().stream()).toList();
    }
}
