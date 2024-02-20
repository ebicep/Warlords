package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.tartarus;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.tartarus.DatabaseGamePlayerPvEEventTartarus;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.tartarus.DatabaseGamePvEEventTartarus;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.PvEEventGardenOfHesperidesDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

public class PvEEventGardenOfHesperidesTartarusDatabaseStatInformation extends PvEEventGardenOfHesperidesDatabaseStatInformation {

    @Field("fastest_game_finished")
    protected long fastestGameFinished = 0;

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        assert databaseGame instanceof DatabaseGamePvEEventTartarus;
        assert gamePlayer instanceof DatabaseGamePlayerPvEEventTartarus;
        super.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);

        DatabaseGamePvEEventTartarus eventTartarus = (DatabaseGamePvEEventTartarus) databaseGame;
        boolean won = eventTartarus.getWavesCleared() == 1;
        int timeElapsed = eventTartarus.getTimeElapsed();
        if (multiplier > 0) {
            if (won && (this.fastestGameFinished == 0 || timeElapsed < fastestGameFinished)) {
                this.fastestGameFinished = timeElapsed;
            }
        } else if (this.fastestGameFinished == timeElapsed) {
            this.fastestGameFinished = 0;
        }
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

    public long getFastestGameFinished() {
        return fastestGameFinished;
    }
}
