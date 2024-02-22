package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb;


import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.narmerstomb.DatabaseGamePlayerPvEEventNarmersTomb;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.narmerstomb.DatabaseGamePvEEventNarmersTomb;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

public class PvEEventNarmerNarmersTombDatabaseStatInformation
        extends PvEEventDatabaseStatInformation<
        DatabaseGamePvEEventNarmersTomb,
        DatabaseGamePlayerPvEEventNarmersTomb>
        implements PvEEventNarmerNarmersTombStats {

    @Field("highest_wave_cleared")
    protected int highestWaveCleared;
    @Field("total_waves_cleared")
    protected int totalWavesCleared;

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEventNarmersTomb databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventNarmersTomb gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        if (multiplier > 0) {
            this.highestWaveCleared = Math.max((((DatabaseGamePvEEventNarmersTomb) databaseGame).getWavesCleared() * multiplier), highestWaveCleared);
        } else if (this.highestWaveCleared == ((DatabaseGamePvEEventNarmersTomb) databaseGame).getWavesCleared()) {
            this.highestWaveCleared = 0;
        }
        this.totalWavesCleared += ((DatabaseGamePvEEventNarmersTomb) databaseGame).getWavesCleared() * multiplier;
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
