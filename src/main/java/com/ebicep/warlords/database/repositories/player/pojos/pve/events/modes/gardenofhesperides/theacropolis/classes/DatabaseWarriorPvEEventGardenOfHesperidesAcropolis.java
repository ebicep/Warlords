package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.theacropolis.classes;


import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.theacropolis.DatabaseBasePvEEventGardenOfHesperidesAcropolis;

public class DatabaseWarriorPvEEventGardenOfHesperidesAcropolis extends DatabaseBasePvEEventGardenOfHesperidesAcropolis implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventGardenOfHesperidesAcropolis berserker = new DatabaseBasePvEEventGardenOfHesperidesAcropolis();
    private DatabaseBasePvEEventGardenOfHesperidesAcropolis defender = new DatabaseBasePvEEventGardenOfHesperidesAcropolis();
    private DatabaseBasePvEEventGardenOfHesperidesAcropolis revenant = new DatabaseBasePvEEventGardenOfHesperidesAcropolis();

    public DatabaseWarriorPvEEventGardenOfHesperidesAcropolis() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
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
