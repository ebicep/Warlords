package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.classes;


import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.DatabaseBasePvEEventGardenOfHesperides;

public class DatabaseWarriorPvEEventGardenOfHesperides extends DatabaseBasePvEEventGardenOfHesperides implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventGardenOfHesperides berserker = new DatabaseBasePvEEventGardenOfHesperides();
    private DatabaseBasePvEEventGardenOfHesperides defender = new DatabaseBasePvEEventGardenOfHesperides();
    private DatabaseBasePvEEventGardenOfHesperides revenant = new DatabaseBasePvEEventGardenOfHesperides();

    public DatabaseWarriorPvEEventGardenOfHesperides() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventGardenOfHesperides[]{berserker, defender, revenant};
    }


    public DatabaseBasePvEEventGardenOfHesperides getBerserker() {
        return berserker;
    }

    public DatabaseBasePvEEventGardenOfHesperides getDefender() {
        return defender;
    }

    public DatabaseBasePvEEventGardenOfHesperides getRevenant() {
        return revenant;
    }

}
