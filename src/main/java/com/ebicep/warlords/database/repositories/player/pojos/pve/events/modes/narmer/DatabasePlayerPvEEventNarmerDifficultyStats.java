package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.DatabaseGamePlayerPvEEventNarmer;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.DatabaseGamePvEEventNarmer;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.narmerstomb.DatabaseGamePlayerPvEEventNarmersTomb;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.narmerstomb.DatabaseGamePvEEventNarmersTomb;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.DatabasePlayerPvEEventNarmerNarmersTombDifficultyStats;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.stream.Stream;

public class DatabasePlayerPvEEventNarmerDifficultyStats implements MultiPvEEventNarmerStats<
        PvEEventNarmerStatsWarlordsClasses<
                DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>,
                DatabaseGamePlayerPvEEventNarmer,
                PvEEventNarmerStats<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer>,
                PvEEventNarmerStatsWarlordsSpecs<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer, PvEEventNarmerStats<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer>>>,
        DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>,
        DatabaseGamePlayerPvEEventNarmer,
        PvEEventNarmerStats<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer>,
        PvEEventNarmerStatsWarlordsSpecs<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer, PvEEventNarmerStats<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer>>> {

    @Field("tomb_stats")
    private DatabasePlayerPvEEventNarmerNarmersTombDifficultyStats tombStats = new DatabasePlayerPvEEventNarmerNarmersTombDifficultyStats();

    public DatabasePlayerPvEEventNarmerDifficultyStats() {
    }

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
        if (databaseGame instanceof DatabaseGamePvEEventNarmersTomb databaseGamePvEEventNarmersTomb &&
                gamePlayer instanceof DatabaseGamePlayerPvEEventNarmersTomb databaseGamePlayerPvEEventNarmer
        ) {
            this.tombStats.updateStats(databasePlayer,
                    databaseGamePvEEventNarmersTomb,
                    gameMode,
                    databaseGamePlayerPvEEventNarmer,
                    result,
                    multiplier,
                    playersCollection
            );
        }
    }

    public DatabasePlayerPvEEventNarmerNarmersTombDifficultyStats getTombStats() {
        return tombStats;
    }


    @Override
    public Collection<? extends PvEEventNarmerStatsWarlordsClasses<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer, PvEEventNarmerStats<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer>, PvEEventNarmerStatsWarlordsSpecs<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer, PvEEventNarmerStats<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer>>>> getStats() {
        return Stream.of(tombStats) // TODO
                     .flatMap(stats -> (Stream<? extends PvEEventNarmerStatsWarlordsClasses<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer, PvEEventNarmerStats<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer>, PvEEventNarmerStatsWarlordsSpecs<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer, PvEEventNarmerStats<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer>>>>) stats.getStats()
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               .stream())
                     .toList();
    }
}
