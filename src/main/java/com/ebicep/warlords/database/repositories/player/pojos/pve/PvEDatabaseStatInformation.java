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
    @Field("total_time_played")
    private long totalTimePlayed = 0;
    @Field("mob_kills")
    private Map<String, Long> mobKills = new LinkedHashMap<>();
    @Field("mob_assists")
    private Map<String, Long> mobAssists = new LinkedHashMap<>();
    @Field("mob_deaths")
    private Map<String, Long> mobDeaths = new LinkedHashMap<>();

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

        if (multiplier > 0 && databaseGamePvE.getWavesCleared() > highestWaveCleared) {
            this.highestWaveCleared = databaseGamePvE.getWavesCleared();
        }
        if (multiplier > 0 && databaseGamePlayerPvE.getLongestTimeInCombat() > longestTimeInCombat) {
            this.longestTimeInCombat = databaseGamePlayerPvE.getLongestTimeInCombat();
        }
        if (multiplier > 0 && databaseGamePlayerPvE.getMostDamageInRound() > mostDamageInRound) {
            this.mostDamageInRound = databaseGamePlayerPvE.getMostDamageInRound();
        }
        if (multiplier > 0 && databaseGamePlayerPvE.getMostDamageInWave() > mostDamageInWave) {
            this.mostDamageInWave = databaseGamePlayerPvE.getMostDamageInWave();
        }
        this.totalWavesCleared += databaseGamePvE.getWavesCleared();
        databaseGamePlayerPvE.getMobKills().forEach((s, aLong) -> this.mobKills.merge(s, aLong * multiplier, Long::sum));
        databaseGamePlayerPvE.getMobAssists().forEach((s, aLong) -> this.mobAssists.merge(s, aLong * multiplier, Long::sum));
        databaseGamePlayerPvE.getMobDeaths().forEach((s, aLong) -> this.mobDeaths.merge(s, aLong * multiplier, Long::sum));
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
