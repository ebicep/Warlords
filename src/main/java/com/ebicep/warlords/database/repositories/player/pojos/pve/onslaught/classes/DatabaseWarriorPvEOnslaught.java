package com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.classes;


import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.DatabaseBasePvEOnslaught;

import java.util.List;

public class DatabaseWarriorPvEOnslaught extends DatabaseBasePvEOnslaught implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEOnslaught berserker = new DatabaseBasePvEOnslaught();
    private DatabaseBasePvEOnslaught defender = new DatabaseBasePvEOnslaught();
    private DatabaseBasePvEOnslaught revenant = new DatabaseBasePvEOnslaught();

    public DatabaseWarriorPvEOnslaught() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEOnslaught[]{berserker, defender, revenant};
    }


    public DatabaseBasePvEOnslaught getBerserker() {
        return berserker;
    }

    public DatabaseBasePvEOnslaught getDefender() {
        return defender;
    }

    public DatabaseBasePvEOnslaught getRevenant() {
        return revenant;
    }

}
