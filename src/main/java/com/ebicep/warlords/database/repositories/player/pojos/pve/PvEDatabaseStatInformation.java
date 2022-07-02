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

    //TODO KILLS ASSISTS DEATH PER MOB


    @Field("total_time_played")
    private long totalTimePlayed = 0;

    public PvEDatabaseStatInformation() {
    }

    @Override
    public void updateCustomStats(DatabaseGameBase databaseGame, GameMode gameMode, DatabaseGamePlayerBase gamePlayer, DatabaseGamePlayerResult result, boolean add) {
        assert databaseGame instanceof DatabaseGamePvE;
        assert gamePlayer instanceof DatabaseGamePlayerPvE;

        if (((DatabaseGamePvE) databaseGame).getWavesCleared() > highestWaveCleared) {
            this.highestWaveCleared = ((DatabaseGamePvE) databaseGame).getWavesCleared();
        }
        if (((DatabaseGamePlayerPvE) gamePlayer).getLongestTimeInCombat() > longestTimeInCombat) {
            this.longestTimeInCombat = ((DatabaseGamePlayerPvE) gamePlayer).getLongestTimeInCombat();
        }
        if (((DatabaseGamePlayerPvE) gamePlayer).getMostDamageInRound() > mostDamageInRound) {
            this.mostDamageInRound = ((DatabaseGamePlayerPvE) gamePlayer).getMostDamageInRound();
        }
        if (((DatabaseGamePlayerPvE) gamePlayer).getMostDamageInWave() > mostDamageInWave) {
            this.mostDamageInWave = ((DatabaseGamePlayerPvE) gamePlayer).getMostDamageInWave();
        }
    }


}
