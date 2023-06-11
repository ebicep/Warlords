
package com.ebicep.warlords.database.repositories.player.pojos.pve.events.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.DatabaseBasePvEEvent;

public class DatabaseDruidPvEEvent extends DatabaseBasePvEEvent implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEvent conjurer = new DatabaseBasePvEEvent();
    private DatabaseBasePvEEvent guardian = new DatabaseBasePvEEvent();
    private DatabaseBasePvEEvent priest = new DatabaseBasePvEEvent();

    public DatabaseDruidPvEEvent() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEEvent[]{conjurer, guardian, priest};
    }


    public DatabaseBasePvEEvent getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEvent getGuardian() {
        return guardian;
    }

    public DatabaseBasePvEEvent getPriest() {
        return priest;
    }
}
