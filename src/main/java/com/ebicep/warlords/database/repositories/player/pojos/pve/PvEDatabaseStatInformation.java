package com.ebicep.warlords.database.repositories.player.pojos.pve;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvE;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePvE;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

public class PvEDatabaseStatInformation extends AbstractDatabaseStatInformation {

    @Field("highest_wave_cleared")
    private int highestWaveCleared;
    @Field("longest_time_in_combat")
    private int longestTimeInCombat;
    @Field("most_damage_in_round")
    private long mostDamageInRound;
    @Field("most_damage_in_wave")
    private long mostDamageInWave;
    @Field("total_waves_cleared")
    private int totalWavesCleared;

    //TODO KILLS ASSISTS DEATH PER MOB


    @Field("total_time_played")
    private long totalTimePlayed = 0;

    public PvEDatabaseStatInformation() {
    }

    @Override
    public void updateCustomStats(DatabaseGameBase databaseGame, GameMode gameMode, DatabaseGamePlayerBase gamePlayer, DatabaseGamePlayerResult result, boolean add) {
        assert databaseGame instanceof DatabaseGamePvE;
        assert gamePlayer instanceof DatabaseGamePlayerPvE;

        DatabaseGamePvE databaseGamePvE = (DatabaseGamePvE) databaseGame;
        DatabaseGamePlayerPvE databaseGamePlayerPvE = (DatabaseGamePlayerPvE) gamePlayer;

        if (databaseGamePvE.getWavesCleared() > highestWaveCleared) {
            this.highestWaveCleared = databaseGamePvE.getWavesCleared();
        }
        if (databaseGamePlayerPvE.getLongestTimeInCombat() > longestTimeInCombat) {
            this.longestTimeInCombat = databaseGamePlayerPvE.getLongestTimeInCombat();
        }
        if (databaseGamePlayerPvE.getMostDamageInRound() > mostDamageInRound) {
            this.mostDamageInRound = databaseGamePlayerPvE.getMostDamageInRound();
        }
        if (databaseGamePlayerPvE.getMostDamageInWave() > mostDamageInWave) {
            this.mostDamageInWave = databaseGamePlayerPvE.getMostDamageInWave();
        }
        this.totalWavesCleared += databaseGamePvE.getWavesCleared();
    }

    public int getHighestWaveCleared() {
        return highestWaveCleared;
    }

    public int getLongestTimeInCombat() {
        return longestTimeInCombat;
    }

    public long getMostDamageInRound() {
        return mostDamageInRound;
    }

    public long getMostDamageInWave() {
        return mostDamageInWave;
    }

    public int getTotalWavesCleared() {
        return totalWavesCleared;
    }

    public long getTotalTimePlayed() {
        return totalTimePlayed;
    }
}
