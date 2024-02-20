package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives;

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

public class DatabasePlayerPvEEventLibraryArchivesStats extends DatabasePlayerPvEEventLibraryArchivesDifficultyStats {

    @Field("events")
    private Map<Long, DatabasePlayerPvEEventLibraryArchivesDifficultyStats> eventStats = new LinkedHashMap<>();

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

    public Map<Long, DatabasePlayerPvEEventLibraryArchivesDifficultyStats> getEventStats() {
        return eventStats;
    }

    public DatabasePlayerPvEEventLibraryArchivesDifficultyStats getEvent(long epochSecond) {
        return eventStats.computeIfAbsent(epochSecond, k -> new DatabasePlayerPvEEventLibraryArchivesDifficultyStats());
    }

}
