package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.theacropolis;

import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.theacropolis.classes.*;
import com.ebicep.warlords.player.general.Classes;

public class DatabasePlayerPvEEventGardenOfHesperidesAcropolisPlayerCountStats implements PvEEventGardenOfHesperidesTheAcropolisStatsWarlordsClasses {

    private DatabaseMagePvEEventGardenOfHesperidesAcropolis mage = new DatabaseMagePvEEventGardenOfHesperidesAcropolis();
    private DatabaseWarriorPvEEventGardenOfHesperidesAcropolis warrior = new DatabaseWarriorPvEEventGardenOfHesperidesAcropolis();
    private DatabasePaladinPvEEventGardenOfHesperidesAcropolis paladin = new DatabasePaladinPvEEventGardenOfHesperidesAcropolis();
    private DatabaseShamanPvEEventGardenOfHesperidesAcropolis shaman = new DatabaseShamanPvEEventGardenOfHesperidesAcropolis();
    private DatabaseRoguePvEEventGardenOfHesperidesAcropolis rogue = new DatabaseRoguePvEEventGardenOfHesperidesAcropolis();
    private DatabaseArcanistPvEEventGardenOfHesperidesAcropolis arcanist = new DatabaseArcanistPvEEventGardenOfHesperidesAcropolis();

    @Override
    public PvEEventGardenOfHesperidesTheAcropolisStatsWarlordsSpecs getClass(Classes classes) {
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
