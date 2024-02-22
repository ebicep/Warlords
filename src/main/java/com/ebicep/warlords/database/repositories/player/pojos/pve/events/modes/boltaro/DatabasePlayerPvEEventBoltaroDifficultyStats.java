package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.DatabaseGamePlayerPvEEventBoltaro;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.DatabaseGamePvEEventBoltaro;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza.DatabaseGamePlayerPvEEventBoltaroBonanza;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza.DatabaseGamePvEEventBoltaroBonanza;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltaroslair.DatabaseGamePlayerPvEEventBoltarosLair;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltaroslair.DatabaseGamePvEEventBoltaroLair;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.DatabasePlayerPvEEventBoltaroBonanzaStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.DatabasePlayerPvEEventBoltaroLairStats;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.stream.Stream;

public class DatabasePlayerPvEEventBoltaroDifficultyStats implements MultiPvEEventBoltaroStats<
        PvEEventBoltaroStatsWarlordsClasses<
                DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>,
                DatabaseGamePlayerPvEEventBoltaro,
                PvEEventBoltaroStats<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro>,
                PvEEventBoltaroStatsWarlordsSpecs<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro, PvEEventBoltaroStats<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro>>>,
        DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>,
        DatabaseGamePlayerPvEEventBoltaro,
        PvEEventBoltaroStats<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro>,
        PvEEventBoltaroStatsWarlordsSpecs<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro, PvEEventBoltaroStats<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro>>> {

    @Field("bonanza_stats")
    private DatabasePlayerPvEEventBoltaroBonanzaStats bonanzaStats = new DatabasePlayerPvEEventBoltaroBonanzaStats();
    @Field("lair_stats")
    private DatabasePlayerPvEEventBoltaroLairStats lairStats = new DatabasePlayerPvEEventBoltaroLairStats();

    public DatabasePlayerPvEEventBoltaroDifficultyStats() {
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEventBoltaro databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventBoltaro gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        if (databaseGame instanceof DatabaseGamePvEEventBoltaroBonanza databaseGamePvEEventBoltaroBonanza && gamePlayer instanceof DatabaseGamePlayerPvEEventBoltaroBonanza databaseGamePlayerPvEEventBoltaroBonanza) {
            bonanzaStats.updateStats(databasePlayer, databaseGamePvEEventBoltaroBonanza, gameMode, databaseGamePlayerPvEEventBoltaroBonanza, result, multiplier, playersCollection);
        } else if (databaseGame instanceof DatabaseGamePvEEventBoltaroLair databaseGamePvEEventBoltaroLair && gamePlayer instanceof DatabaseGamePlayerPvEEventBoltarosLair databaseGamePlayerPvEEventBoltarosLair) {
            lairStats.updateStats(databasePlayer, databaseGamePvEEventBoltaroLair, gameMode, databaseGamePlayerPvEEventBoltarosLair, result, multiplier, playersCollection);
        } else {
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage("Invalid game or player type");
        }
    }


    @Override
    public Collection<? extends PvEEventBoltaroStatsWarlordsClasses<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro, PvEEventBoltaroStats<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro>, PvEEventBoltaroStatsWarlordsSpecs<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro, PvEEventBoltaroStats<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro>>>> getStats() {
        return Stream.of(bonanzaStats, lairStats) // TODO
                     .flatMap(stats -> (Stream<? extends PvEEventBoltaroStatsWarlordsClasses<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro, PvEEventBoltaroStats<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro>, PvEEventBoltaroStatsWarlordsSpecs<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro, PvEEventBoltaroStats<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro>>>>) stats.getStats()
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               .stream())
                     .toList();
    }
}
