package com.ebicep.warlords.database.repositories.player.pojos.pve;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvE;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePvE;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedHashMap;
import java.util.Map;

public class PvEDatabaseStatInformation extends AbstractDatabaseStatInformation {

    //CUMULATIVE STATS
    @Field("experience_pve")
    protected long experiencePvE;
    @Field("total_waves_cleared")
    protected int totalWavesCleared;
    @Field("total_time_played")
    protected long totalTimePlayed = 0;
    @Field("mob_kills")
    protected Map<String, Long> mobKills = new LinkedHashMap<>();
    @Field("mob_assists")
    protected Map<String, Long> mobAssists = new LinkedHashMap<>();
    @Field("mob_deaths")
    protected Map<String, Long> mobDeaths = new LinkedHashMap<>();

    //TOP STATS
    @Field("highest_wave_cleared")
    protected int highestWaveCleared;
    @Field("longest_time_in_combat")
    protected int longestTimeInCombat;
    @Field("most_damage_in_round")
    protected long mostDamageInRound;
    @Field("most_damage_in_wave")
    protected long mostDamageInWave;
    @Field("fastest_normal_game_finished")
    protected long fastestGameFinished = 0;

    public PvEDatabaseStatInformation() {
    }

    @Override
    public void updateCustomStats(
            DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        assert databaseGame instanceof DatabaseGamePvE;
        assert gamePlayer instanceof DatabaseGamePlayerPvE;

        DatabaseGamePvE databaseGamePvE = (DatabaseGamePvE) databaseGame;
        DatabaseGamePlayerPvE databaseGamePlayerPvE = (DatabaseGamePlayerPvE) gamePlayer;

        if (multiplier > 0) {
            if (databaseGamePvE.getWavesCleared() > highestWaveCleared) {
                this.highestWaveCleared = databaseGamePvE.getWavesCleared();
            }
            if (databaseGamePlayerPvE.getLongestTimeInCombat() > longestTimeInCombat) {
                this.longestTimeInCombat = databaseGamePlayerPvE.getLongestTimeInCombat();
            }
            if (databaseGamePlayerPvE.getTotalDamage() > mostDamageInRound) {
                this.mostDamageInRound = databaseGamePlayerPvE.getTotalDamage();
            }
            if (databaseGamePlayerPvE.getMostDamageInWave() > mostDamageInWave) {
                this.mostDamageInWave = databaseGamePlayerPvE.getMostDamageInWave();
            }
            if (databaseGamePvE.getTimeElapsed() < fastestGameFinished) {
                this.fastestGameFinished += databaseGamePvE.getTimeElapsed();
            }
        }

        this.totalWavesCleared += databaseGamePvE.getWavesCleared();
        databaseGamePlayerPvE.getMobKills().forEach((s, aLong) -> this.mobKills.merge(s, aLong * multiplier, Long::sum));
        databaseGamePlayerPvE.getMobAssists().forEach((s, aLong) -> this.mobAssists.merge(s, aLong * multiplier, Long::sum));
        databaseGamePlayerPvE.getMobDeaths().forEach((s, aLong) -> this.mobDeaths.merge(s, aLong * multiplier, Long::sum));
    }

    public long getExperiencePvE() {
        return experiencePvE;
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

    public long getFastestGameFinished() {
        return fastestGameFinished;
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
