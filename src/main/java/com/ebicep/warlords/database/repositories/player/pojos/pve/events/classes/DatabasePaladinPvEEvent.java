package com.ebicep.warlords.database.repositories.player.pojos.pve.events.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.DatabaseBasePvEEvent;

public class DatabasePaladinPvEEvent extends DatabaseBasePvEEvent implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEvent avenger = new DatabaseBasePvEEvent();
    private DatabaseBasePvEEvent crusader = new DatabaseBasePvEEvent();
    private DatabaseBasePvEEvent protector = new DatabaseBasePvEEvent();

    public DatabasePaladinPvEEvent() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEvent[]{avenger, crusader, protector};
    }

    public DatabaseBasePvEEvent getAvenger() {
        return avenger;
    }

    public DatabaseBasePvEEvent getCrusader() {
        return crusader;
    }

    public DatabaseBasePvEEvent getProtector() {
        return protector;
    }

}
