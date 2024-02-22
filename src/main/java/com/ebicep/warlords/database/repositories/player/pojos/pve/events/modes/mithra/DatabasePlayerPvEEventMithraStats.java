package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.DatabaseGamePlayerPvEEventMithra;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.DatabaseGamePvEEventMithra;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEEventMithraStats implements MultiPvEEventMithraStats<
        PvEEventMithraStatsWarlordsClasses<
                DatabaseGamePvEEventMithra<DatabaseGamePlayerPvEEventMithra>,
                DatabaseGamePlayerPvEEventMithra,
                PvEEventMithraStats<DatabaseGamePvEEventMithra<DatabaseGamePlayerPvEEventMithra>, DatabaseGamePlayerPvEEventMithra>,
                PvEEventMithraStatsWarlordsSpecs<DatabaseGamePvEEventMithra<DatabaseGamePlayerPvEEventMithra>, DatabaseGamePlayerPvEEventMithra, PvEEventMithraStats<DatabaseGamePvEEventMithra<DatabaseGamePlayerPvEEventMithra>, DatabaseGamePlayerPvEEventMithra>>>,
        DatabaseGamePvEEventMithra<DatabaseGamePlayerPvEEventMithra>,
        DatabaseGamePlayerPvEEventMithra,
        PvEEventMithraStats<DatabaseGamePvEEventMithra<DatabaseGamePlayerPvEEventMithra>, DatabaseGamePlayerPvEEventMithra>,
        PvEEventMithraStatsWarlordsSpecs<DatabaseGamePvEEventMithra<DatabaseGamePlayerPvEEventMithra>, DatabaseGamePlayerPvEEventMithra, PvEEventMithraStats<DatabaseGamePvEEventMithra<DatabaseGamePlayerPvEEventMithra>, DatabaseGamePlayerPvEEventMithra>>> {


    @Field("events")
    private Map<Long, DatabasePlayerPvEEventMithraDifficultyStats> eventStats = new LinkedHashMap<>();

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEventMithra databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventMithra gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        getEvent(DatabaseGameEvent.currentGameEvent.getStartDateSecond()).updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
    }

    public Map<Long, DatabasePlayerPvEEventMithraDifficultyStats> getEventStats() {
        return eventStats;
    }

    public DatabasePlayerPvEEventMithraDifficultyStats getEvent(long epochSecond) {
        return eventStats.computeIfAbsent(epochSecond, k -> new DatabasePlayerPvEEventMithraDifficultyStats());
    }

    @Override
    public Collection<? extends PvEEventMithraStatsWarlordsClasses<DatabaseGamePvEEventMithra<DatabaseGamePlayerPvEEventMithra>, DatabaseGamePlayerPvEEventMithra, PvEEventMithraStats<DatabaseGamePvEEventMithra<DatabaseGamePlayerPvEEventMithra>, DatabaseGamePlayerPvEEventMithra>, PvEEventMithraStatsWarlordsSpecs<DatabaseGamePvEEventMithra<DatabaseGamePlayerPvEEventMithra>, DatabaseGamePlayerPvEEventMithra, PvEEventMithraStats<DatabaseGamePvEEventMithra<DatabaseGamePlayerPvEEventMithra>, DatabaseGamePlayerPvEEventMithra>>>> getStats() {
        return eventStats.values()
                         .stream()
                         .flatMap(stats -> stats.getStats().stream())
                         .toList();
    }
}
