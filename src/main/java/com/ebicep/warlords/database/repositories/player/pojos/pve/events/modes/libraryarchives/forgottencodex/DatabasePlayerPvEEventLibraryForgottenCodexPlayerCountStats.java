package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex;


import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.forgottencodex.DatabaseGamePlayerPvEEventForgottenCodex;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.forgottencodex.DatabaseGamePvEEventForgottenCodex;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;

public class DatabasePlayerPvEEventLibraryForgottenCodexPlayerCountStats implements PvEEventLibraryArchivesForgottenCodexStatsWarlordsClasses {

    private DatabaseMagePvEEventLibraryForgottenCodex mage = new DatabaseMagePvEEventLibraryForgottenCodex();
    private DatabaseWarriorPvEEventLibraryForgottenCodex warrior = new DatabaseWarriorPvEEventLibraryForgottenCodex();
    private DatabasePaladinPvEEventLibraryForgottenCodex paladin = new DatabasePaladinPvEEventLibraryForgottenCodex();
    private DatabaseShamanPvEEventLibraryForgottenCodex shaman = new DatabaseShamanPvEEventLibraryForgottenCodex();
    private DatabaseRoguePvEEventLibraryForgottenCodex rogue = new DatabaseRoguePvEEventLibraryForgottenCodex();
    private DatabaseArcanistPvEEventLibraryForgottenCodex arcanist = new DatabaseArcanistPvEEventLibraryForgottenCodex();

    @Override
    public PvEEventLibraryArchivesForgottenCodexStatsWarlordsSpecs getClass(Classes classes) {
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
            DatabaseGamePvEEventForgottenCodex databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventForgottenCodex gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        updateSpecStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
    }
}
