package com.ebicep.warlords.database.repositories.player.pojos.pve.events.classes;


import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.DatabaseBasePvEEvent;

public class DatabaseWarriorPvEEvent extends DatabaseBasePvEEvent implements DatabaseWarlordsClass {

    private DatabaseBasePvEEvent berserker = new DatabaseBasePvEEvent();
    private DatabaseBasePvEEvent defender = new DatabaseBasePvEEvent();
    private DatabaseBasePvEEvent revenant = new DatabaseBasePvEEvent();

    public DatabaseWarriorPvEEvent() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEEvent[]{berserker, defender, revenant};
    }


    public DatabaseBasePvEEvent getBerserker() {
        return berserker;
    }

    public DatabaseBasePvEEvent getDefender() {
        return defender;
    }

    public DatabaseBasePvEEvent getRevenant() {
        return revenant;
    }

}
