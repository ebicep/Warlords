package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina;


import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.illumina.DatabaseGamePlayerPvEEventIllumina;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.illumina.DatabaseGamePvEEventIllumina;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.illumina.theborderlineofillusion.DatabaseGamePlayerPvEEventTheBorderlineOfIllusion;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.illumina.theborderlineofillusion.DatabaseGamePvEEventTheBorderlineOfIllusion;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.theborderlineofillusion.DatabasePlayerPvEEventTheBorderLineOfIllusionDifficultyStats;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.stream.Stream;

public class DatabasePlayerPvEEventIlluminaDifficultyStats implements MultiPvEEventIlluminaStats<
        PvEEventIlluminaStatsWarlordsClasses<
                DatabaseGamePvEEventIllumina,
                DatabaseGamePlayerPvEEventIllumina,
                PvEEventIlluminaStats<DatabaseGamePvEEventIllumina, DatabaseGamePlayerPvEEventIllumina>,
                PvEEventIlluminaStatsWarlordsSpecs<DatabaseGamePvEEventIllumina, DatabaseGamePlayerPvEEventIllumina, PvEEventIlluminaStats<DatabaseGamePvEEventIllumina, DatabaseGamePlayerPvEEventIllumina>>>,
        DatabaseGamePvEEventIllumina,
        DatabaseGamePlayerPvEEventIllumina,
        PvEEventIlluminaStats<DatabaseGamePvEEventIllumina, DatabaseGamePlayerPvEEventIllumina>,
        PvEEventIlluminaStatsWarlordsSpecs<DatabaseGamePvEEventIllumina, DatabaseGamePlayerPvEEventIllumina, PvEEventIlluminaStats<DatabaseGamePvEEventIllumina, DatabaseGamePlayerPvEEventIllumina>>> {

    @Field("the_borderline_of_illusion_stats")
    private DatabasePlayerPvEEventTheBorderLineOfIllusionDifficultyStats borderLineOfIllusionStats = new DatabasePlayerPvEEventTheBorderLineOfIllusionDifficultyStats();

    public DatabasePlayerPvEEventIlluminaDifficultyStats() {
    }

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
        if (databaseGame instanceof DatabaseGamePvEEventTheBorderlineOfIllusion databaseGamePvEEventTheBorderlineOfIllusion &&
                gamePlayer instanceof DatabaseGamePlayerPvEEventTheBorderlineOfIllusion databaseGamePlayerPvEEventIllumina
        ) {
            this.borderLineOfIllusionStats.updateStats(databasePlayer,
                    databaseGamePvEEventTheBorderlineOfIllusion,
                    gameMode,
                    databaseGamePlayerPvEEventIllumina,
                    result,
                    multiplier,
                    playersCollection
            );
        }
    }

    public DatabasePlayerPvEEventTheBorderLineOfIllusionDifficultyStats getBorderLineOfIllusionStats() {
        return borderLineOfIllusionStats;
    }

    @Override
    public Collection<? extends PvEEventIlluminaStatsWarlordsClasses<DatabaseGamePvEEventIllumina, DatabaseGamePlayerPvEEventIllumina, PvEEventIlluminaStats<DatabaseGamePvEEventIllumina, DatabaseGamePlayerPvEEventIllumina>, PvEEventIlluminaStatsWarlordsSpecs<DatabaseGamePvEEventIllumina, DatabaseGamePlayerPvEEventIllumina, PvEEventIlluminaStats<DatabaseGamePvEEventIllumina, DatabaseGamePlayerPvEEventIllumina>>>> getStats() {
        return Stream.of(borderLineOfIllusionStats) // TODO
                     .flatMap(stats -> (Stream<? extends PvEEventIlluminaStatsWarlordsClasses<DatabaseGamePvEEventIllumina, DatabaseGamePlayerPvEEventIllumina, PvEEventIlluminaStats<DatabaseGamePvEEventIllumina, DatabaseGamePlayerPvEEventIllumina>, PvEEventIlluminaStatsWarlordsSpecs<DatabaseGamePvEEventIllumina, DatabaseGamePlayerPvEEventIllumina, PvEEventIlluminaStats<DatabaseGamePvEEventIllumina, DatabaseGamePlayerPvEEventIllumina>>>>) stats.getStats()
                                                                                                                                                                                                                                                                                                                                                                                                                                                               .stream())
                     .toList();
    }
}
