package com.ebicep.warlords.database.repositories.player.pojos.pve.events.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.DatabaseBasePvEEvent;

public class DatabaseShamanPvEEvent extends DatabaseBasePvEEvent implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEvent thunderlord = new DatabaseBasePvEEvent();
    private DatabaseBasePvEEvent spiritguard = new DatabaseBasePvEEvent();
    private DatabaseBasePvEEvent earthwarden = new DatabaseBasePvEEvent();

    public DatabaseShamanPvEEvent() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEvent[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseBasePvEEvent getThunderlord() {
        return thunderlord;
    }

    public DatabaseBasePvEEvent getSpiritguard() {
        return spiritguard;
    }

    public DatabaseBasePvEEvent getEarthwarden() {
        return earthwarden;
    }

}
