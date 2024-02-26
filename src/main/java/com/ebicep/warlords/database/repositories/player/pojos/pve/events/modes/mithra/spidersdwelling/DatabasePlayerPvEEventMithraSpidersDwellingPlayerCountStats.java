package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling;


import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.classes.*;
import com.ebicep.warlords.player.general.Classes;

public class DatabasePlayerPvEEventMithraSpidersDwellingPlayerCountStats implements PvEEventMithraSpidersDwellingStatsWarlordsClasses {

    private DatabaseMagePvEEventMithraSpidersDwelling mage = new DatabaseMagePvEEventMithraSpidersDwelling();
    private DatabaseWarriorPvEEventMithraSpidersDwelling warrior = new DatabaseWarriorPvEEventMithraSpidersDwelling();
    private DatabasePaladinPvEEventMithraSpidersDwelling paladin = new DatabasePaladinPvEEventMithraSpidersDwelling();
    private DatabaseShamanPvEEventMithraSpidersDwelling shaman = new DatabaseShamanPvEEventMithraSpidersDwelling();
    private DatabaseRoguePvEEventMithraSpidersDwelling rogue = new DatabaseRoguePvEEventMithraSpidersDwelling();
    private DatabaseArcanistPvEEventMithraSpidersDwelling arcanist = new DatabaseArcanistPvEEventMithraSpidersDwelling();

    @Override
    public PvEEventMithraSpidersDwellingStatsWarlordsSpecs getClass(Classes classes) {
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
