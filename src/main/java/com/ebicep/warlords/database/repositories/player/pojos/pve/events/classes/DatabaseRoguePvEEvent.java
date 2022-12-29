package com.ebicep.warlords.database.repositories.player.pojos.pve.events.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.DatabaseBasePvEEvent;

public class DatabaseRoguePvEEvent extends DatabaseBasePvEEvent implements DatabaseWarlordsClass {

    private DatabaseBasePvEEvent assassin = new DatabaseBasePvEEvent();
    private DatabaseBasePvEEvent vindicator = new DatabaseBasePvEEvent();
    private DatabaseBasePvEEvent apothecary = new DatabaseBasePvEEvent();

    public DatabaseRoguePvEEvent() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEEvent[]{assassin, vindicator, apothecary};
    }


    public DatabaseBasePvEEvent getAssassin() {
        return assassin;
    }

    public DatabaseBasePvEEvent getVindicator() {
        return vindicator;
    }

    public DatabaseBasePvEEvent getApothecary() {
        return apothecary;
    }
}
