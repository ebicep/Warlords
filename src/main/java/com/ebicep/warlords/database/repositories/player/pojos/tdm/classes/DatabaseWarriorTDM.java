package com.ebicep.warlords.database.repositories.player.pojos.tdm.classes;


import com.ebicep.warlords.database.repositories.player.pojos.tdm.TDMStatsWarlordsSpecs;

public class DatabaseWarriorTDM implements TDMStatsWarlordsSpecs {

    private DatabaseBaseTDM berserker = new DatabaseBaseTDM();
    private DatabaseBaseTDM defender = new DatabaseBaseTDM();
    private DatabaseBaseTDM revenant = new DatabaseBaseTDM();

    public DatabaseWarriorTDM() {
        super();
    }

    @Override
    public DatabaseBaseTDM[] getSpecs() {
        return new DatabaseBaseTDM[]{berserker, defender, revenant};
    }


    public DatabaseBaseTDM getBerserker() {
        return berserker;
    }

    public DatabaseBaseTDM getDefender() {
        return defender;
    }

    public DatabaseBaseTDM getRevenant() {
        return revenant;
    }

}
