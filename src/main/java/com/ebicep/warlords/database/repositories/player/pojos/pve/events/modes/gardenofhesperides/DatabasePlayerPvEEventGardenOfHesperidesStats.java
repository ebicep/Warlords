package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides;


import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.DatabaseGamePlayerPvEEventGardenOfHesperides;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.DatabaseGamePvEEventGardenOfHesperides;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEEventGardenOfHesperidesStats implements MultiPvEEventGardenOfHesperidesStats<
        PvEEventGardenOfHesperidesStatsWarlordsClasses<
                DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>,
                DatabaseGamePlayerPvEEventGardenOfHesperides,
                PvEEventGardenOfHesperidesStats<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides>,
                PvEEventGardenOfHesperidesStatsWarlordsSpecs<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides, PvEEventGardenOfHesperidesStats<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides>>>,
        DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>,
        DatabaseGamePlayerPvEEventGardenOfHesperides,
        PvEEventGardenOfHesperidesStats<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides>,
        PvEEventGardenOfHesperidesStatsWarlordsSpecs<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides, PvEEventGardenOfHesperidesStats<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides>>> {

    @Field("events")
    private Map<Long, DatabasePlayerPvEEventGardenOfHesperidesDifficultyStats> eventStats = new LinkedHashMap<>();
    @Field("tartarus_auto_ready")
    private boolean tartarusAutoReady = false;

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEventGardenOfHesperides databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventGardenOfHesperides gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        getEvent(DatabaseGameEvent.currentGameEvent.getStartDateSecond()).updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
    }

    public DatabasePlayerPvEEventGardenOfHesperidesDifficultyStats getEvent(long epochSecond) {
        return eventStats.computeIfAbsent(epochSecond, k -> new DatabasePlayerPvEEventGardenOfHesperidesDifficultyStats());
    }

    @Override
    public Collection<? extends PvEEventGardenOfHesperidesStatsWarlordsClasses<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides, PvEEventGardenOfHesperidesStats<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides>, PvEEventGardenOfHesperidesStatsWarlordsSpecs<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides, PvEEventGardenOfHesperidesStats<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides>>>> getStats() {
        return eventStats.values()
                         .stream()
                         .flatMap(stats -> stats.getStats().stream())
                         .toList();
    }

    public boolean isTartarusAutoReady() {
        return tartarusAutoReady;
    }

    public void setTartarusAutoReady(boolean tartarusAutoReady) {
        this.tartarusAutoReady = tartarusAutoReady;
    }

    public Map<Long, DatabasePlayerPvEEventGardenOfHesperidesDifficultyStats> getEventStats() {
        return eventStats;
    }
}
