package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza.DatabaseGamePlayerPvEEventBoltaroBonanza;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza.DatabaseGamePvEEventBoltaroBonanza;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

public class PvEEventBoltaroBonanzaDatabaseStatInformation extends PvEEventDatabaseStatInformation<DatabaseGamePvEEventBoltaroBonanza, DatabaseGamePlayerPvEEventBoltaroBonanza> implements PvEEventBoltaroBonanzaStats {

    @Field("highest_split")
    private int highestSplit;

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEventBoltaroBonanza databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventBoltaroBonanza gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        super.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
        int split = databaseGame.getHighestSplit();
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

    @Override
    public int getHighestSplit() {
        return highestSplit;
    }

}
