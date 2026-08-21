package com.ebicep.warlords.database.repositories.player.pojos.pve.anomaly;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.anomaly.DatabaseGamePlayerPvEAnomaly;
import com.ebicep.warlords.database.repositories.games.pojos.pve.anomaly.DatabaseGamePvEAnomaly;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.anomaly.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;

public class DatabasePlayerPvEAnomalyPlayerCountStats implements AnomalyStatsWarlordsClasses {

    private DatabaseMagePvEAnomaly mage = new DatabaseMagePvEAnomaly();
    private DatabaseWarriorPvEAnomaly warrior = new DatabaseWarriorPvEAnomaly();
    private DatabasePaladinPvEAnomaly paladin = new DatabasePaladinPvEAnomaly();
    private DatabaseShamanPvEAnomaly shaman = new DatabaseShamanPvEAnomaly();
    private DatabaseRoguePvEAnomaly rogue = new DatabaseRoguePvEAnomaly();
    private DatabaseArcanistPvEAnomaly arcanist = new DatabaseArcanistPvEAnomaly();

    @Override
    public AnomalyStatsWarlordsSpecs getClass(Classes classes) {
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
            DatabaseGamePvEAnomaly databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEAnomaly gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        updateSpecStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
    }
}
