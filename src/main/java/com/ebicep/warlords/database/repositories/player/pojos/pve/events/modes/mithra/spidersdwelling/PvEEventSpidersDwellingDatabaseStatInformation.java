package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.spidersdwelling.DatabaseGamePlayerPvEEventSpidersDwelling;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.spidersdwelling.DatabaseGamePvEEventSpidersDwelling;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.PvEEventBoltaroDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

public class PvEEventSpidersDwellingDatabaseStatInformation extends PvEEventBoltaroDatabaseStatInformation {

    @Field("highest_wave_cleared")
    protected int highestWaveCleared;
    @Field("total_waves_cleared")
    protected int totalWavesCleared;

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer, DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        assert databaseGame instanceof DatabaseGamePvEEventSpidersDwelling;
        assert gamePlayer instanceof DatabaseGamePlayerPvEEventSpidersDwelling;
        super.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);

        if (multiplier > 0) {
            this.highestWaveCleared = Math.max((((DatabaseGamePvEEventSpidersDwelling) databaseGame).getWavesCleared() * multiplier), highestWaveCleared);
        } else if (this.highestWaveCleared == ((DatabaseGamePvEEventSpidersDwelling) databaseGame).getWavesCleared()) {
            this.highestWaveCleared = 0;
        }
        this.totalWavesCleared += ((DatabaseGamePvEEventSpidersDwelling) databaseGame).getWavesCleared() * multiplier;
    }

    public void setHighestWaveCleared(int highestWaveCleared) {
        this.highestWaveCleared = highestWaveCleared;
    }

    public long getExperiencePvE() {
        return experiencePvE;
    }

    public long getTotalTimePlayed() {
        return totalTimePlayed;
    }

    public Map<String, Long> getMobKills() {
        return mobKills;
    }

    public Map<String, Long> getMobAssists() {
        return mobAssists;
    }

    public Map<String, Long> getMobDeaths() {
        return mobDeaths;
    }

}
