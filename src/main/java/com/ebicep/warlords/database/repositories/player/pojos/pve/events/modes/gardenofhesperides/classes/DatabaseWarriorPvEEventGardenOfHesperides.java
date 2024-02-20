package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.classes;


import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.DatabaseBasePvEEventGardenOfHesperides;

import java.util.List;

public class DatabaseWarriorPvEEventGardenOfHesperides extends DatabaseBasePvEEventGardenOfHesperides implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventGardenOfHesperides berserker = new DatabaseBasePvEEventGardenOfHesperides();
    private DatabaseBasePvEEventGardenOfHesperides defender = new DatabaseBasePvEEventGardenOfHesperides();
    private DatabaseBasePvEEventGardenOfHesperides revenant = new DatabaseBasePvEEventGardenOfHesperides();

    public DatabaseWarriorPvEEventGardenOfHesperides() {
        super();
    }

    @Override
    public List<List> getSpecs() {
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
