package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.DatabaseGamePlayerPvEEventGardenOfHesperides;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.DatabaseGamePvEEventGardenOfHesperides;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.tartarus.DatabaseGamePlayerPvEEventTartarus;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.tartarus.DatabaseGamePvEEventTartarus;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.theacropolis.DatabaseGamePlayerPvEEventTheAcropolis;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.theacropolis.DatabaseGamePvEEventTheAcropolis;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.tartarus.DatabasePlayerPvEEventGardenOfHesperidesTartarusStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.theacropolis.DatabasePlayerPvEEventGardenOfHesperidesAcropolisDifficultyStats;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.stream.Stream;

public class DatabasePlayerPvEEventGardenOfHesperidesDifficultyStats implements MultiPvEEventGardenOfHesperidesStats<
        PvEEventGardenOfHesperidesStatsWarlordsClasses<
                DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>,
                DatabaseGamePlayerPvEEventGardenOfHesperides,
                PvEEventGardenOfHesperidesStats<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides>,
                PvEEventGardenOfHesperidesStatsWarlordsSpecs<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides, PvEEventGardenOfHesperidesStats<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides>>>,
        DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>,
        DatabaseGamePlayerPvEEventGardenOfHesperides,
        PvEEventGardenOfHesperidesStats<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides>,
        PvEEventGardenOfHesperidesStatsWarlordsSpecs<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides, PvEEventGardenOfHesperidesStats<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides>>> {

    @Field("acropolis_stats")
    private DatabasePlayerPvEEventGardenOfHesperidesAcropolisDifficultyStats acropolisStats = new DatabasePlayerPvEEventGardenOfHesperidesAcropolisDifficultyStats();
    @Field("tartarus_stats")
    private DatabasePlayerPvEEventGardenOfHesperidesTartarusStats tartarusStats = new DatabasePlayerPvEEventGardenOfHesperidesTartarusStats();

    @Override
    public Collection<? extends PvEEventGardenOfHesperidesStatsWarlordsClasses<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides, PvEEventGardenOfHesperidesStats<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides>, PvEEventGardenOfHesperidesStatsWarlordsSpecs<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides, PvEEventGardenOfHesperidesStats<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides>>>> getStats() {
        return Stream.of(acropolisStats, tartarusStats) // TODO
                     .flatMap(stats -> (Stream<? extends PvEEventGardenOfHesperidesStatsWarlordsClasses<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides, PvEEventGardenOfHesperidesStats<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides>, PvEEventGardenOfHesperidesStatsWarlordsSpecs<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides, PvEEventGardenOfHesperidesStats<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides>>>>) stats.getStats()
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               .stream())
                     .toList();
    }

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
        if (databaseGame instanceof DatabaseGamePvEEventTheAcropolis databaseGamePvEEventTheAcropolis && gamePlayer instanceof DatabaseGamePlayerPvEEventTheAcropolis databaseGamePlayerPvEEventTheAcropolis) {
            acropolisStats.updateStats(databasePlayer, databaseGamePvEEventTheAcropolis, gameMode, databaseGamePlayerPvEEventTheAcropolis, result, multiplier, playersCollection);
        } else if (databaseGame instanceof DatabaseGamePvEEventTartarus databaseGamePvEEventTartarus && gamePlayer instanceof DatabaseGamePlayerPvEEventTartarus databaseGamePlayerPvEEventTartarus) {
            tartarusStats.updateStats(databasePlayer, databaseGamePvEEventTartarus, gameMode, databaseGamePlayerPvEEventTartarus, result, multiplier, playersCollection);
        } else {
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage("Invalid game or player type");
        }
    }
}
