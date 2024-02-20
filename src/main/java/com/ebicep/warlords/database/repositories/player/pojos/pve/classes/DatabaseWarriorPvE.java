package com.ebicep.warlords.database.repositories.player.pojos.pve.classes;


import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePvEBase;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEStatsWarlordsSpecs;

public class DatabaseWarriorPvE implements PvEStatsWarlordsSpecs<DatabaseGamePvEBase, DatabaseGamePlayerPvEBase, DatabaseBasePvE> {

    private DatabaseBasePvE berserker = new DatabaseBasePvE();
    private DatabaseBasePvE defender = new DatabaseBasePvE();
    private DatabaseBasePvE revenant = new DatabaseBasePvE();

    public DatabaseWarriorPvE() {
        super();
    }

    @Override
    public DatabaseBasePvE[] getSpecs() {
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
