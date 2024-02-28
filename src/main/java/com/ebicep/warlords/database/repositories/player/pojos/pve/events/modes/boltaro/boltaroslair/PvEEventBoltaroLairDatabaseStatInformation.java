package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltaroslair.DatabaseGamePlayerPvEEventBoltarosLair;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltaroslair.DatabaseGamePvEEventBoltaroLair;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

public class PvEEventBoltaroLairDatabaseStatInformation extends PvEEventDatabaseStatInformation<DatabaseGamePvEEventBoltaroLair, DatabaseGamePlayerPvEEventBoltarosLair> implements PvEEventBoltaroLairStats {

    @Field("highest_wave_cleared")
    protected int highestWaveCleared;
    @Field("total_waves_cleared")
    protected int totalWavesCleared;

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEventBoltaroLair databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventBoltarosLair gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        super.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
        if (multiplier > 0) {
            this.highestWaveCleared = Math.max((databaseGame.getWavesCleared() * multiplier), highestWaveCleared);
        } else if (this.highestWaveCleared == databaseGame.getWavesCleared()) {
            this.highestWaveCleared = 0;
        }
        this.totalWavesCleared += databaseGame.getWavesCleared() * multiplier;
    }

    @Override
    public int getHighestWaveCleared() {
        return highestWaveCleared;
    }

    @Override
    public int getTotalWavesCleared() {
        return totalWavesCleared;
    }

}
