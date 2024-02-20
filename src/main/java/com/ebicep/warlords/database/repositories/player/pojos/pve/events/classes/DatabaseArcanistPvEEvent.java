
package com.ebicep.warlords.database.repositories.player.pojos.pve.events.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.DatabaseBasePvEEvent;

import java.util.List;

public class DatabaseArcanistPvEEvent extends DatabaseBasePvEEvent implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEvent conjurer = new DatabaseBasePvEEvent();
    private DatabaseBasePvEEvent sentinel = new DatabaseBasePvEEvent();
    private DatabaseBasePvEEvent luminary = new DatabaseBasePvEEvent();

    public DatabaseArcanistPvEEvent() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEvent[]{conjurer, sentinel, luminary};
    }


    public DatabaseBasePvEEvent getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEvent getSentinel() {
        return sentinel;
    }

    public DatabaseBasePvEEvent getLuminary() {
        return luminary;
    }
}
