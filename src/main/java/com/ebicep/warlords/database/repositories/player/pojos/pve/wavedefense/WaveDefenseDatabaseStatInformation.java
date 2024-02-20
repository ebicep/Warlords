package com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

public class WaveDefenseDatabaseStatInformation extends PvEDatabaseStatInformation<DatabaseGamePvEWaveDefense, DatabaseGamePlayerPvEWaveDefense> implements WaveDefenseStats {

    //CUMULATIVE STATS
    @Field("total_waves_cleared")
    protected int totalWavesCleared;
    //TOP STATS
    @Field("highest_wave_cleared")
    protected int highestWaveCleared;
    @Field("most_damage_in_wave")
    protected long mostDamageInWave;
    @Field("fastest_game_finished")
    protected long fastestGameFinished = 0;

    public WaveDefenseDatabaseStatInformation() {
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEWaveDefense databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEWaveDefense gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        super.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
        if (multiplier > 0) {
            this.highestWaveCleared = Math.max(databaseGame.getWavesCleared(), this.highestWaveCleared);
        } else if (this.highestWaveCleared == databaseGame.getWavesCleared()) {
            this.highestWaveCleared = 0;
        }
        if (multiplier > 0) {
            if (databaseGame.getWavesCleared() == databaseGame.getDifficulty().getMaxWaves() &&
                    (this.fastestGameFinished == 0 || databaseGame.getTimeElapsed() < fastestGameFinished)
            ) {
                this.fastestGameFinished = databaseGame.getTimeElapsed();
            }
        } else if (this.fastestGameFinished == databaseGame.getTimeElapsed()) {
            this.fastestGameFinished = 0;
        }

        if (multiplier > 0) {
            this.mostDamageInWave = Math.max(this.mostDamageInWave, gamePlayer.getMostDamageInWave());
        } else if (this.mostDamageInWave == gamePlayer.getMostDamageInWave()) {
            this.mostDamageInWave = 0;
        }

        this.totalWavesCleared += databaseGame.getWavesCleared() * multiplier;
    }

    @Override
    public int getTotalWavesCleared() {
        return 0;
    }

    @Override
    public int highestWaveCleared() {
        return 0;
    }

    @Override
    public long mostDamageInWave() {
        return 0;
    }

    @Override
    public long fastestGameFinished() {
        return 0;
    }
}
