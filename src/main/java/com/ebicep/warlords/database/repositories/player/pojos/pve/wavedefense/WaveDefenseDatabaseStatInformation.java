package com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.Difficulty;
import com.ebicep.warlords.database.repositories.games.pojos.pve.MostDamageInWave;
import com.ebicep.warlords.database.repositories.games.pojos.pve.TimeElapsed;
import com.ebicep.warlords.database.repositories.games.pojos.pve.WavesCleared;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

public class WaveDefenseDatabaseStatInformation extends PvEDatabaseStatInformation {

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
            DatabasePlayer databasePlayer, DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        assert gamePlayer instanceof DatabaseGamePlayerPvEWaveDefense;
        if (databaseGame instanceof WavesCleared wavesCleared) {
            if (multiplier > 0) {
                this.highestWaveCleared = Math.max(wavesCleared.getWavesCleared(), this.highestWaveCleared);
            } else if (this.highestWaveCleared == wavesCleared.getWavesCleared()) {
                this.highestWaveCleared = 0;
            }
            if (databaseGame instanceof TimeElapsed timeElapsed && databaseGame instanceof Difficulty difficulty) {
                if (multiplier > 0) {
                    if (wavesCleared.getWavesCleared() == difficulty.getDifficulty().getMaxWaves() &&
                            (this.fastestGameFinished == 0 || timeElapsed.getTimeElapsed() < fastestGameFinished)
                    ) {
                        this.fastestGameFinished = timeElapsed.getTimeElapsed();
                    }
                } else if (this.fastestGameFinished == timeElapsed.getTimeElapsed()) {
                    this.fastestGameFinished = 0;
                }
            }
        }
        if (gamePlayer instanceof MostDamageInWave mostDamageInWave) {
            if (multiplier > 0) {
                this.mostDamageInWave = Math.max(this.mostDamageInWave, mostDamageInWave.getMostDamageInWave());
            } else if (this.mostDamageInWave == mostDamageInWave.getMostDamageInWave()) {
                this.mostDamageInWave = 0;
            }
        }

        if (databaseGame instanceof WavesCleared wavesCleared) {
            this.totalWavesCleared += wavesCleared.getWavesCleared() * multiplier;
        }
    }

    public void merge(WaveDefenseDatabaseStatInformation other) {
        super.merge(other);
        this.totalWavesCleared += other.totalWavesCleared;
        this.highestWaveCleared = Math.max(this.highestWaveCleared, other.highestWaveCleared);
        this.mostDamageInWave = Math.max(this.mostDamageInWave, other.mostDamageInWave);
        this.fastestGameFinished = Math.min(this.fastestGameFinished, other.fastestGameFinished);
    }

    public int getHighestWaveCleared() {
        return highestWaveCleared;
    }

    public void setHighestWaveCleared(int highestWaveCleared) {
        this.highestWaveCleared = highestWaveCleared;
    }

    public long getMostDamageInWave() {
        return mostDamageInWave;
    }

    public void setMostDamageInWave(long mostDamageInWave) {
        this.mostDamageInWave = mostDamageInWave;
    }

    public int getTotalWavesCleared() {
        return totalWavesCleared;
    }

    public long getFastestGameFinished() {
        return fastestGameFinished;
    }

    public void setFastestGameFinished(long fastestGameFinished) {
        this.fastestGameFinished = fastestGameFinished;
    }

}
