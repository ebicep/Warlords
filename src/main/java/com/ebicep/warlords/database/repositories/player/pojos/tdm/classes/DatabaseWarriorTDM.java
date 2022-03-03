package com.ebicep.warlords.database.repositories.player.pojos.tdm.classes;


import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.tdm.DatabaseBaseTDM;

public class DatabaseWarriorTDM extends DatabaseBaseTDM implements DatabaseWarlordsClass {

    private DatabaseBaseTDM berserker = new DatabaseBaseTDM();
    private DatabaseBaseTDM defender = new DatabaseBaseTDM();
    private DatabaseBaseTDM revenant = new DatabaseBaseTDM();

    public DatabaseWarriorTDM() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
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
