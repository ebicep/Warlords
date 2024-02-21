package com.ebicep.warlords.database.repositories.player.pojos.pve.events.classes;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePlayerPvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventStatsWarlordsSpecs;

public class DatabasePaladinPvEEvent implements PvEEventStatsWarlordsSpecs<DatabaseGamePvEEvent, DatabaseGamePlayerPvEEvent, DatabaseBasePvEEvent> {

    private DatabaseBasePvEEvent avenger = new DatabaseBasePvEEvent();
    private DatabaseBasePvEEvent crusader = new DatabaseBasePvEEvent();
    private DatabaseBasePvEEvent protector = new DatabaseBasePvEEvent();

    public DatabasePaladinPvEEvent() {
        super();
    }

    @Override
    public DatabaseBasePvEEvent[] getSpecs() {
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
