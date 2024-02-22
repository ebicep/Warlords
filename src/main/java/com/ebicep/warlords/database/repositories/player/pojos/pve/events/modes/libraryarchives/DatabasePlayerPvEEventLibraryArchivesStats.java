package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives;


import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.DatabaseGamePlayerPvEEventLibraryArchives;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.DatabaseGamePvEEventLibraryArchives;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEEventLibraryArchivesStats implements MultiPvEEventLibraryArchivesStats<
        PvEEventLibraryArchivesStatsWarlordsClasses<
                DatabaseGamePvEEventLibraryArchives,
                DatabaseGamePlayerPvEEventLibraryArchives,
                PvEEventLibraryArchivesStats<DatabaseGamePvEEventLibraryArchives, DatabaseGamePlayerPvEEventLibraryArchives>,
                PvEEventLibraryArchivesStatsWarlordsSpecs<DatabaseGamePvEEventLibraryArchives, DatabaseGamePlayerPvEEventLibraryArchives, PvEEventLibraryArchivesStats<DatabaseGamePvEEventLibraryArchives, DatabaseGamePlayerPvEEventLibraryArchives>>>,
        DatabaseGamePvEEventLibraryArchives,
        DatabaseGamePlayerPvEEventLibraryArchives,
        PvEEventLibraryArchivesStats<DatabaseGamePvEEventLibraryArchives, DatabaseGamePlayerPvEEventLibraryArchives>,
        PvEEventLibraryArchivesStatsWarlordsSpecs<DatabaseGamePvEEventLibraryArchives, DatabaseGamePlayerPvEEventLibraryArchives, PvEEventLibraryArchivesStats<DatabaseGamePvEEventLibraryArchives, DatabaseGamePlayerPvEEventLibraryArchives>>> {


    @Field("events")
    private Map<Long, DatabasePlayerPvEEventLibraryArchivesDifficultyStats> eventStats = new LinkedHashMap<>();

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEventLibraryArchives databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventLibraryArchives gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        getEvent(DatabaseGameEvent.currentGameEvent.getStartDateSecond()).updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
    }

    public Map<Long, DatabasePlayerPvEEventLibraryArchivesDifficultyStats> getEventStats() {
        return eventStats;
    }

    public DatabasePlayerPvEEventLibraryArchivesDifficultyStats getEvent(long epochSecond) {
        return eventStats.computeIfAbsent(epochSecond, k -> new DatabasePlayerPvEEventLibraryArchivesDifficultyStats());
    }

    @Override
    public Collection<? extends PvEEventLibraryArchivesStatsWarlordsClasses<DatabaseGamePvEEventLibraryArchives, DatabaseGamePlayerPvEEventLibraryArchives, PvEEventLibraryArchivesStats<DatabaseGamePvEEventLibraryArchives, DatabaseGamePlayerPvEEventLibraryArchives>, PvEEventLibraryArchivesStatsWarlordsSpecs<DatabaseGamePvEEventLibraryArchives, DatabaseGamePlayerPvEEventLibraryArchives, PvEEventLibraryArchivesStats<DatabaseGamePvEEventLibraryArchives, DatabaseGamePlayerPvEEventLibraryArchives>>>> getStats() {
        return eventStats.values()
                         .stream()
                         .flatMap(stats -> stats.getStats().stream())
                         .toList();
    }

}
