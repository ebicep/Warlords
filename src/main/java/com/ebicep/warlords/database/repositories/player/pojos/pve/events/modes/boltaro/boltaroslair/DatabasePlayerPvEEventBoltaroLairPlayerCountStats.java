package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltaroslair.DatabaseGamePlayerPvEEventBoltarosLair;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltaroslair.DatabaseGamePvEEventBoltaroLair;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;

public class DatabasePlayerPvEEventBoltaroLairPlayerCountStats implements PvEEventBoltaroLairStatsWarlordsClasses {

    private DatabaseMagePvEEventBoltaroLair mage = new DatabaseMagePvEEventBoltaroLair();
    private DatabaseWarriorPvEEventBoltaroLair warrior = new DatabaseWarriorPvEEventBoltaroLair();
    private DatabasePaladinPvEEventBoltaroLair paladin = new DatabasePaladinPvEEventBoltaroLair();
    private DatabaseShamanPvEEventBoltaroLair shaman = new DatabaseShamanPvEEventBoltaroLair();
    private DatabaseRoguePvEEventBoltaroLair rogue = new DatabaseRoguePvEEventBoltaroLair();
    private DatabaseArcanistPvEEventBoltaroLair arcanist = new DatabaseArcanistPvEEventBoltaroLair();

    @Override
    public PvEEventBoltaroLairStatsWarlordsSpecs getClass(Classes classes) {
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
            DatabaseGamePvEEventBoltaroLair databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventBoltarosLair gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        updateSpecStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
    }
}
