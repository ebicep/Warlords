package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb;


import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.narmerstomb.DatabaseGamePlayerPvEEventNarmersTomb;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.narmerstomb.DatabaseGamePvEEventNarmersTomb;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;

public class DatabasePlayerPvEEventNarmerNarmersTombPlayerCountStats implements PvEEventNarmerNarmersTombStatsWarlordsClasses {

    private DatabaseMagePvEEventNarmerNarmersTomb mage = new DatabaseMagePvEEventNarmerNarmersTomb();
    private DatabaseWarriorPvEEventNarmerNarmersTomb warrior = new DatabaseWarriorPvEEventNarmerNarmersTomb();
    private DatabasePaladinPvEEventNarmerNarmersTomb paladin = new DatabasePaladinPvEEventNarmerNarmersTomb();
    private DatabaseShamanPvEEventNarmerNarmersTomb shaman = new DatabaseShamanPvEEventNarmerNarmersTomb();
    private DatabaseRoguePvEEventNarmerNarmersTomb rogue = new DatabaseRoguePvEEventNarmerNarmersTomb();
    private DatabaseArcanistPvEEventNarmerNarmersTomb arcanist = new DatabaseArcanistPvEEventNarmerNarmersTomb();

    @Override
    public PvEEventNarmerNarmersTombStatsWarlordsSpecs getClass(Classes classes) {
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
            DatabaseGamePvEEventNarmersTomb databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventNarmersTomb gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        updateSpecStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
    }
}
