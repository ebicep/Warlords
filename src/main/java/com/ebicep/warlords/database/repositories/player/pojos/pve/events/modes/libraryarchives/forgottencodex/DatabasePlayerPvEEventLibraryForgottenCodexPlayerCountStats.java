package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex;


import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex.classes.*;
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
}
