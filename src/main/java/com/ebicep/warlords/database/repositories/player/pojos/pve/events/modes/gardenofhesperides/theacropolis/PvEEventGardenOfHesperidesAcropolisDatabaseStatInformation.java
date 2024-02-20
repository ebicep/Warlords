package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.theacropolis;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.theacropolis.DatabaseGamePlayerPvEEventTheAcropolis;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.theacropolis.DatabaseGamePvEEventTheAcropolis;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.PvEEventGardenOfHesperidesDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;

import java.util.Map;

public class PvEEventGardenOfHesperidesAcropolisDatabaseStatInformation extends PvEEventGardenOfHesperidesDatabaseStatInformation {

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer, DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        assert databaseGame instanceof DatabaseGamePvEEventTheAcropolis;
        assert gamePlayer instanceof DatabaseGamePlayerPvEEventTheAcropolis;
        super.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);

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
