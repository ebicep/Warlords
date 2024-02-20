package com.ebicep.warlords.database.repositories.player.pojos.pve.events.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.DatabaseBasePvEEvent;

import java.util.List;

public class DatabasePaladinPvEEvent extends DatabaseBasePvEEvent implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEvent avenger = new DatabaseBasePvEEvent();
    private DatabaseBasePvEEvent crusader = new DatabaseBasePvEEvent();
    private DatabaseBasePvEEvent protector = new DatabaseBasePvEEvent();

    public DatabasePaladinPvEEvent() {
        super();
    }

    @Override
    public List<List> getSpecs() {
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
