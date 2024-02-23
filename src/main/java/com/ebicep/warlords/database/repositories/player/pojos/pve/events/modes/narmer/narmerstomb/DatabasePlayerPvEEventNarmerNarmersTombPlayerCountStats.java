package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb;


import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.classes.*;
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
}
