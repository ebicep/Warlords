package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza.DatabaseGamePlayerPvEEventBoltaroBonanza;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza.DatabaseGamePvEEventBoltaroBonanza;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;

public class DatabasePlayerPvEEventBoltaroBonanzaPlayerCountStats implements PvEEventBoltaroBonanzaStatsWarlordsClasses {

    private DatabaseMagePvEEventBoltaroBonanza mage = new DatabaseMagePvEEventBoltaroBonanza();
    private DatabaseWarriorPvEEventBoltaroBonanza warrior = new DatabaseWarriorPvEEventBoltaroBonanza();
    private DatabasePaladinPvEEventBoltaroBonanza paladin = new DatabasePaladinPvEEventBoltaroBonanza();
    private DatabaseShamanPvEEventBoltaroBonanza shaman = new DatabaseShamanPvEEventBoltaroBonanza();
    private DatabaseRoguePvEEventBoltaroBonanza rogue = new DatabaseRoguePvEEventBoltaroBonanza();
    private DatabaseArcanistPvEEventBoltaroBonanza arcanist = new DatabaseArcanistPvEEventBoltaroBonanza();

    @Override
    public PvEEventBoltaroBonanzaStatsWarlordsSpecs getClass(Classes classes) {
        return switch (classes) {
            case MAGE -> mage;
            case WARRIOR -> warrior;
            case PALADIN -> paladin;
            case SHAMAN -> shaman;
            case ROGUE -> rogue;
            case ARCANIST -> arcanist;
        };
    }

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
        updateSpecStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
    }
}
