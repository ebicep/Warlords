package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza.DatabaseGamePlayerPvEEventBoltaroBonanza;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza.DatabaseGamePvEEventBoltaroBonanza;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.PvEEventBoltaroDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

public class PvEEventBoltaroBonanzaDatabaseStatInformation extends PvEEventBoltaroDatabaseStatInformation {

    @Field("highest_split")
    private int highestSplit;

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer, DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        assert databaseGame instanceof DatabaseGamePvEEventBoltaroBonanza;
        assert gamePlayer instanceof DatabaseGamePlayerPvEEventBoltaroBonanza;
        super.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);

        DatabaseGamePvEEventBoltaroBonanza databaseGamePvEEventBoltaroBonanza = (DatabaseGamePvEEventBoltaroBonanza) databaseGame;
        DatabaseGamePlayerPvEEventBoltaroBonanza databaseGamePlayerPvEEventBoltaroBonanza = (DatabaseGamePlayerPvEEventBoltaroBonanza) gamePlayer;

        int split = databaseGamePvEEventBoltaroBonanza.getHighestSplit();
        if (multiplier > 0) {
            if (this.highestSplit < split) {
                this.highestSplit = split;
            }
        } else {
            if (this.highestSplit == split) {
                this.highestSplit = 0;
            }
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

    public int getHighestSplit() {
        return highestSplit;
    }
}
