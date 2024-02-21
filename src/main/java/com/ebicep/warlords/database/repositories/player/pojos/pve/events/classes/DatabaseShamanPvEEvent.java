package com.ebicep.warlords.database.repositories.player.pojos.pve.events.classes;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePlayerPvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventStatsWarlordsSpecs;

public class DatabaseShamanPvEEvent implements PvEEventStatsWarlordsSpecs<DatabaseGamePvEEvent, DatabaseGamePlayerPvEEvent, DatabaseBasePvEEvent> {

    private DatabaseBasePvEEvent thunderlord = new DatabaseBasePvEEvent();
    private DatabaseBasePvEEvent spiritguard = new DatabaseBasePvEEvent();
    private DatabaseBasePvEEvent earthwarden = new DatabaseBasePvEEvent();

    public DatabaseShamanPvEEvent() {
        super();
    }

    @Override
    public DatabaseBasePvEEvent[] getSpecs() {
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
