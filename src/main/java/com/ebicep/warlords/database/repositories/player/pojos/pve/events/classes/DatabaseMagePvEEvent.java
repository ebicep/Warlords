package com.ebicep.warlords.database.repositories.player.pojos.pve.events.classes;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePlayerPvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventStatsWarlordsSpecs;

public class DatabaseMagePvEEvent implements PvEEventStatsWarlordsSpecs<DatabaseGamePvEEvent, DatabaseGamePlayerPvEEvent, DatabaseBasePvEEvent> {

    protected DatabaseBasePvEEvent pyromancer = new DatabaseBasePvEEvent();
    protected DatabaseBasePvEEvent cryomancer = new DatabaseBasePvEEvent();
    protected DatabaseBasePvEEvent aquamancer = new DatabaseBasePvEEvent();

    public DatabaseMagePvEEvent() {
        super();
    }

    @Override
    public DatabaseBasePvEEvent[] getSpecs() {
        return new DatabaseBasePvEEvent[]{pyromancer, cryomancer, aquamancer};
    }

    public DatabaseBasePvEEvent getPyromancer() {
        return pyromancer;
    }

    public DatabaseBasePvEEvent getCryomancer() {
        return cryomancer;
    }

    public DatabaseBasePvEEvent getAquamancer() {
        return aquamancer;
    }

}
