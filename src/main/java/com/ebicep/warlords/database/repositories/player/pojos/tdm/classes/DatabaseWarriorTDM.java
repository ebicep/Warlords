package com.ebicep.warlords.database.repositories.player.pojos.tdm.classes;


import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.tdm.DatabaseBaseTDM;

public class DatabaseWarriorTDM extends DatabaseBaseTDM implements DatabaseWarlordsSpecs {

    private DatabaseBaseTDM berserker = new DatabaseBaseTDM();
    private DatabaseBaseTDM defender = new DatabaseBaseTDM();
    private DatabaseBaseTDM revenant = new DatabaseBaseTDM();

    public DatabaseWarriorTDM() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
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
