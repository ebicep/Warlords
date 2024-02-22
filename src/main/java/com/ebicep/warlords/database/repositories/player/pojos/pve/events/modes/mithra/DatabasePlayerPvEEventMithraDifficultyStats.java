package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.DatabaseGamePlayerPvEEventMithra;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.DatabaseGamePvEEventMithra;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.spidersdwelling.DatabaseGamePlayerPvEEventSpidersDwelling;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.spidersdwelling.DatabaseGamePvEEventSpidersDwelling;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.DatabasePlayerPvEEventMithraSpidersDwellingDifficultyStats;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.stream.Stream;

public class DatabasePlayerPvEEventMithraDifficultyStats implements MultiPvEEventMithraStats<
        PvEEventMithraStatsWarlordsClasses<
                DatabaseGamePvEEventMithra,
                DatabaseGamePlayerPvEEventMithra,
                PvEEventMithraStats<DatabaseGamePvEEventMithra, DatabaseGamePlayerPvEEventMithra>,
                PvEEventMithraStatsWarlordsSpecs<DatabaseGamePvEEventMithra, DatabaseGamePlayerPvEEventMithra, PvEEventMithraStats<DatabaseGamePvEEventMithra, DatabaseGamePlayerPvEEventMithra>>>,
        DatabaseGamePvEEventMithra,
        DatabaseGamePlayerPvEEventMithra,
        PvEEventMithraStats<DatabaseGamePvEEventMithra, DatabaseGamePlayerPvEEventMithra>,
        PvEEventMithraStatsWarlordsSpecs<DatabaseGamePvEEventMithra, DatabaseGamePlayerPvEEventMithra, PvEEventMithraStats<DatabaseGamePvEEventMithra, DatabaseGamePlayerPvEEventMithra>>> {

    @Field("the_borderline_of_illusion_stats")
    private DatabasePlayerPvEEventMithraSpidersDwellingDifficultyStats borderLineOfIllusionStats = new DatabasePlayerPvEEventMithraSpidersDwellingDifficultyStats();

    public DatabasePlayerPvEEventMithraDifficultyStats() {
    }

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
        if (databaseGame instanceof DatabaseGamePvEEventSpidersDwelling databaseGamePvEEventSpidersDwelling &&
                gamePlayer instanceof DatabaseGamePlayerPvEEventSpidersDwelling databaseGamePlayerPvEEventMithra
        ) {
            this.borderLineOfIllusionStats.updateStats(databasePlayer,
                    databaseGamePvEEventSpidersDwelling,
                    gameMode,
                    databaseGamePlayerPvEEventMithra,
                    result,
                    multiplier,
                    playersCollection
            );
        }
    }

    public DatabasePlayerPvEEventMithraSpidersDwellingDifficultyStats getBorderLineOfIllusionStats() {
        return borderLineOfIllusionStats;
    }

    @Override
    public Collection<? extends PvEEventMithraStatsWarlordsClasses<DatabaseGamePvEEventMithra, DatabaseGamePlayerPvEEventMithra, PvEEventMithraStats<DatabaseGamePvEEventMithra, DatabaseGamePlayerPvEEventMithra>, PvEEventMithraStatsWarlordsSpecs<DatabaseGamePvEEventMithra, DatabaseGamePlayerPvEEventMithra, PvEEventMithraStats<DatabaseGamePvEEventMithra, DatabaseGamePlayerPvEEventMithra>>>> getStats() {
        return Stream.of(borderLineOfIllusionStats) // TODO
                     .flatMap(stats -> (Stream<? extends PvEEventMithraStatsWarlordsClasses<DatabaseGamePvEEventMithra, DatabaseGamePlayerPvEEventMithra, PvEEventMithraStats<DatabaseGamePvEEventMithra, DatabaseGamePlayerPvEEventMithra>, PvEEventMithraStatsWarlordsSpecs<DatabaseGamePvEEventMithra, DatabaseGamePlayerPvEEventMithra, PvEEventMithraStats<DatabaseGamePvEEventMithra, DatabaseGamePlayerPvEEventMithra>>>>) stats.getStats()
                                                                                                                                                                                                                                                                                                                                                                                                                                       .stream())
                     .toList();
    }
}
