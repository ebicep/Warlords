package com.ebicep.warlords.database.repositories.player.pojos.pve.classes;


import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabaseBasePvE;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEStatsWarlordsSpecs;

import java.util.List;

public class DatabaseWarriorPvE implements PvEStatsWarlordsSpecs<DatabaseBasePvE> {

    private DatabaseBasePvE berserker = new DatabaseBasePvE();
    private DatabaseBasePvE defender = new DatabaseBasePvE();
    private DatabaseBasePvE revenant = new DatabaseBasePvE();

    public DatabaseWarriorPvE() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvE[]{berserker, defender, revenant};
    }


    public DatabaseBasePvE getBerserker() {
        return berserker;
    }

    public DatabaseBasePvE getDefender() {
        return defender;
    }

    public DatabaseBasePvE getRevenant() {
        return revenant;
    }

}
