package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.theacropolis.classes;


import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.theacropolis.PvEEventGardenOfHesperidesTheAcropolisStatsWarlordsSpecs;

public class DatabaseWarriorPvEEventGardenOfHesperidesAcropolis implements PvEEventGardenOfHesperidesTheAcropolisStatsWarlordsSpecs {

    private DatabaseBasePvEEventGardenOfHesperidesAcropolis berserker = new DatabaseBasePvEEventGardenOfHesperidesAcropolis();
    private DatabaseBasePvEEventGardenOfHesperidesAcropolis defender = new DatabaseBasePvEEventGardenOfHesperidesAcropolis();
    private DatabaseBasePvEEventGardenOfHesperidesAcropolis revenant = new DatabaseBasePvEEventGardenOfHesperidesAcropolis();

    public DatabaseWarriorPvEEventGardenOfHesperidesAcropolis() {
        super();
    }

    @Override
    public DatabaseBasePvEEventGardenOfHesperidesAcropolis[] getSpecs() {
        return new DatabaseBasePvEEventGardenOfHesperidesAcropolis[]{berserker, defender, revenant};
    }


    public DatabaseBasePvEEventGardenOfHesperidesAcropolis getBerserker() {
        return berserker;
    }

    public DatabaseBasePvEEventGardenOfHesperidesAcropolis getDefender() {
        return defender;
    }

    public DatabaseBasePvEEventGardenOfHesperidesAcropolis getRevenant() {
        return revenant;
    }

}
