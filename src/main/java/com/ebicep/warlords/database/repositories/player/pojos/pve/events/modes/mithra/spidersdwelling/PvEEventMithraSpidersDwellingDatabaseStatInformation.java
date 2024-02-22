package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.spidersdwelling.DatabaseGamePlayerPvEEventSpidersDwelling;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.spidersdwelling.DatabaseGamePvEEventSpidersDwelling;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

public class PvEEventMithraSpidersDwellingDatabaseStatInformation
        extends PvEEventDatabaseStatInformation<
        DatabaseGamePvEEventSpidersDwelling,
        DatabaseGamePlayerPvEEventSpidersDwelling>
        implements PvEEventMithraSpidersDwellingStats {

    @Field("highest_wave_cleared")
    protected int highestWaveCleared;
    @Field("total_waves_cleared")
    protected int totalWavesCleared;

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEventSpidersDwelling databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventSpidersDwelling gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        if (multiplier > 0) {
            this.highestWaveCleared = Math.max((((DatabaseGamePvEEventSpidersDwelling) databaseGame).getWavesCleared() * multiplier), highestWaveCleared);
        } else if (this.highestWaveCleared == ((DatabaseGamePvEEventSpidersDwelling) databaseGame).getWavesCleared()) {
            this.highestWaveCleared = 0;
        }
        this.totalWavesCleared += ((DatabaseGamePvEEventSpidersDwelling) databaseGame).getWavesCleared() * multiplier;
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
